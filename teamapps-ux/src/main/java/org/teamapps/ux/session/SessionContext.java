/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.session;

import com.ibm.icu.util.ULocale;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.*;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.*;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.ComponentLibrary;
import org.teamapps.ux.component.ComponentLibraryRegistry;
import org.teamapps.ux.component.ComponentLibraryRegistry.ComponentLibraryInfo;
import org.teamapps.ux.component.animation.EntranceAnimation;
import org.teamapps.ux.component.animation.ExitAnimation;
import org.teamapps.ux.component.div.Div;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.linkbutton.LinkButton;
import org.teamapps.ux.component.notification.Notification;
import org.teamapps.ux.component.notification.NotificationPosition;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.rootpanel.WakeLock;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.TemplateReference;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.i18n.ResourceBundleTranslationProvider;
import org.teamapps.ux.i18n.TranslationProvider;
import org.teamapps.ux.icon.IconBundle;
import org.teamapps.ux.icon.TeamAppsIconBundle;
import org.teamapps.ux.json.UxJacksonSerializationTemplate;
import org.teamapps.ux.resource.Resource;

import java.io.File;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.teamapps.common.util.ExceptionUtil.softenExceptions;

public class SessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionContext.class);
	private static final String DEFAULT_BACKGROUND_NAME = "defaultBackground";
	private static final String DEFAULT_BACKGROUND_URL = "/resources/backgrounds/default-bl.jpg";

	private final ExecutorService sessionExecutor;

	public final ProjectorEvent<KeyboardEvent> onGlobalKeyEventOccurred = new ProjectorEvent<>(hasListeners -> sendStaticCommand(RootPanel.class, new UiRootPanel.ToggleEventListeningCommand(null, null, UiRootPanel.GlobalKeyEventOccurredEvent.NAME, hasListeners)));
	public final ProjectorEvent<NavigationStateChangeEvent> onNavigationStateChange = new ProjectorEvent<>(hasListeners -> sendStaticCommand(RootPanel.class, new UiRootPanel.ToggleEventListeningCommand(null, null, UiRootPanel.NavigationStateChangeEvent.NAME, hasListeners)));
	public final ProjectorEvent<UiSessionActivityState> onActivityStateChanged = new ProjectorEvent<>();
	public final Event<UiSessionClosingReason> onDestroyed = new Event<>();

	/**
	 * Decorators around all executions inside this SessionContext. These will be invoked when the Thread is already bound to the SessionContext, so SessionContext.current() will
	 * return this instance.
	 */
	public final ExecutionDecoratorStack executionDecorators = new ExecutionDecoratorStack();

	private UiSessionState state = UiSessionState.ACTIVE;

	private final UiSession uiSession;

	private final ClientInfo clientInfo;
	private Location currentLocation;

	private final HttpSession httpSession;
	private final UxServerContext serverContext;
	private final SessionIconProvider iconProvider;
	private final UxJacksonSerializationTemplate uxJacksonSerializationTemplate;
	private final HashMap<String, ClientObject> clientObjectsById = new HashMap<>();
	private final SessionContextResourceManager sessionResourceProvider;

	private TranslationProvider translationProvider;

	private final Map<String, Template> registeredTemplates = new ConcurrentHashMap<>();
	private SessionConfiguration sessionConfiguration;

	private final Map<String, Icon<?, ?>> bundleIconByKey = new HashMap<>();

	private boolean defaultBackgroundRegistered;

	private Window sessionExpiredWindow;
	private Window sessionErrorWindow;
	private Window sessionTerminatedWindow;

	private final UiSessionListener uiSessionListener = new UiSessionListener() {
		@Override
		public void onUiEvent(String sessionId, UiEvent event) {
			runWithContext(() -> {
				String uiComponentId = event.getComponentId();
				if (uiComponentId != null) {
					ClientObject clientObject = getClientObject(uiComponentId);
					if (clientObject != null) {
						clientObject.handleUiEvent(event);
					} else {
						throw new TeamAppsComponentNotFoundException(sessionId, uiComponentId);
					}
				} else {
					handleStaticEvent(event);
				}
			});
		}

		@Override
		public void onUiQuery(String sessionId, UiQuery query, Consumer<Object> resultCallback) {
			runWithContext(() -> {
				String uiComponentId = query.getComponentId();
				ClientObject clientObject = getClientObject(uiComponentId);
				if (clientObject != null) {
					Object result = clientObject.handleUiQuery(query);
					new UxJacksonSerializationTemplate(SessionContext.this).doWithUxJacksonSerializers(() -> {
						resultCallback.accept(result);
					});
				} else {
					throw new TeamAppsComponentNotFoundException(sessionId, uiComponentId);
				}
			});
		}

		@Override
		public void onStateChanged(String sessionId, UiSessionState state) {
			runWithContext(() -> {
				boolean activityStateChanged = SessionContext.this.state.isActive() != state.isActive();
				SessionContext.this.state = state;
				if (activityStateChanged) {
					onActivityStateChanged.fire(new UiSessionActivityState(state.isActive()));
				}
			});
		}

		@Override
		public void onClosed(String sessionId, UiSessionClosingReason reason) {
			runWithContext(() -> {
				onDestroyed.fireIgnoringExceptions(reason);
				// Enqueue this at the end, so all onDestroyed handlers have already been executed before disabling any more executions inside the context!
				sessionExecutor.submit(sessionExecutor::shutdown);
			});
		}
	};

	private final Set<ComponentLibrary> componentLibrariesLoaded = new HashSet<>();
	private final Set<Class<? extends ClientObject>> clientObjectTypesKnownToClient = new HashSet<>();
	private final ComponentLibraryRegistry componentLibraryRegistry;

	public SessionContext(UiSession uiSession,
						  ExecutorService sessionExecutor,
						  ClientInfo clientInfo,
						  SessionConfiguration sessionConfiguration,
						  HttpSession httpSession,
						  UxServerContext serverContext,
						  SessionIconProvider iconProvider,
						  ComponentLibraryRegistry componentLibraryRegistry) {
		this.sessionExecutor = sessionExecutor;
		this.uiSession = uiSession;
		this.httpSession = httpSession;
		this.clientInfo = clientInfo;
		this.currentLocation = clientInfo.getLocation();
		this.sessionConfiguration = sessionConfiguration;
		this.serverContext = serverContext;
		this.iconProvider = iconProvider;
		this.uxJacksonSerializationTemplate = new UxJacksonSerializationTemplate(this);
		this.translationProvider = new ResourceBundleTranslationProvider("org.teamapps.ux.i18n.DefaultCaptions", Locale.ENGLISH);
		addIconBundle(TeamAppsIconBundle.createBundle());
		runWithContext(this::updateSessionMessageWindows);
		this.sessionResourceProvider = new SessionContextResourceManager(uiSession.getSessionId());
		this.componentLibraryRegistry = componentLibraryRegistry;
	}

	public Event<UiSessionClosingReason> onDestroyed() {
		return onDestroyed;
	}


	public void pushNavigationState(String relativeUrl) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.PushHistoryStateCommand(relativeUrl));
	}

	public void navigateBack(int steps) {
		navigateForward(-steps);
	}

	public void navigateForward(int steps) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.NavigateForwardCommand(steps));
	}

	public static SessionContext current() {
		return CurrentSessionContext.get();
	}

	public static SessionContext currentOrNull() {
		return CurrentSessionContext.getOrNull();
	}

	public void setTranslationProvider(TranslationProvider translationProvider) {
		this.translationProvider = translationProvider;
	}

	public TranslationProvider getTranslationProvider() {
		return translationProvider;
	}

	public void addIconBundle(IconBundle iconBundle) {
		iconBundle.getEntries().forEach(entry -> bundleIconByKey.put(entry.getKey(), entry.getIcon()));
	}

	public Icon<?, ?> getIcon(String key) {
		return bundleIconByKey.get(key);
	}

	public ULocale getULocale() {
		return sessionConfiguration.getULocale();
	}

	public Locale getLocale() {
		return sessionConfiguration.getLocale();
	}

	public void setLocale(Locale locale) {
		setULocale(ULocale.forLocale(locale));
	}

	public void setULocale(ULocale locale) {
		sessionConfiguration.setULocale(locale);
		setConfiguration(sessionConfiguration);
	}

	public String getLocalized(String key, Object... parameters) {
		return translationProvider.getLocalized(getLocale(), key, parameters);
	}

	public boolean isActive() {
		return state.isActive();
	}

	public UiSessionState getState() {
		return state;
	}

	public boolean isDestroyed() {
		return state == UiSessionState.CLOSED;
	}

	public void destroy() {
		destroy(UiSessionClosingReason.TERMINATED_BY_APPLICATION);
	}

	private void destroy(UiSessionClosingReason reason) {
		uiSession.close(reason);
	}

	public <RESULT> void sendStaticCommand(Class<? extends ClientObject> clientObjectClass, UiCommand<RESULT> command, Consumer<RESULT> resultCallback) {
		CurrentSessionContext.throwIfNotSameAs(this);

		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;

		ComponentLibraryInfo componentLibraryInfo = componentLibraryRegistry.getComponentLibraryForClientObjectClass(clientObjectClass);

		uxJacksonSerializationTemplate.doWithUxJacksonSerializers(() -> uiSession.sendCommand(new UiCommandWithResultCallback<>(componentLibraryInfo.getUuid(), null, command, wrappedCallback)));
	}

	public <RESULT> void sendStaticCommand(Class<? extends ClientObject> clientObjectClass, UiCommand<RESULT> command) {
		sendStaticCommand(clientObjectClass, command, null);
	}

	public <RESULT> void sendCommand(String clientObjectId, UiCommand<RESULT> command, Consumer<RESULT> resultCallback) {
		CurrentSessionContext.throwIfNotSameAs(this);

		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;

		uxJacksonSerializationTemplate.doWithUxJacksonSerializers(() -> uiSession.sendCommand(new UiCommandWithResultCallback<>(null, clientObjectId, command, wrappedCallback)));
	}

	public void sendCommand(String clientObjectId, UiCommand<?> command) {
		this.sendCommand(clientObjectId, command, null);
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public ClientBackPressureInfo getClientBackPressureInfo() {
		return uiSession.getClientBackPressureInfo();
	}

	public String createFileLink(File file) {
		return sessionResourceProvider.createFileLink(file);
	}

	public String createResourceLink(Resource resource, String uniqueIdentifier) {
		return sessionResourceProvider.createResourceLink(resource, uniqueIdentifier);
	}

	public Resource getBinaryResource(int resourceId) {
		return sessionResourceProvider.getBinaryResource(resourceId);
	}

	public File getUploadedFileByUuid(String uuid) {
		return this.serverContext.getUploadedFileByUuid(uuid);
	}

	public TemplateReference registerTemplate(String id, Template template) {
		registeredTemplates.put(id, template);
		sendStaticCommand(RootPanel.class, new UiRootPanel.RegisterTemplateCommand(id, template.createUiTemplate()));
		return new TemplateReference(template, id);
	}

	public void registerTemplates(Map<String, Template> templates) {
		registeredTemplates.putAll(templates);
		sendStaticCommand(RootPanel.class, new UiRootPanel.RegisterTemplatesCommand(templates.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().createUiTemplate()))));
	}

	public Template getTemplate(String id) {
		return registeredTemplates.get(id);
	}

	public CompletableFuture<Void> runWithContext(Runnable runnable) {
		return this.runWithContext(runnable, false);
	}

	/**
	 * @param runnable
	 * @param forceEnqueue No synchronous execution! Enqueue this at the end of this SessionContext's work queue.
	 */
	public CompletableFuture<Void> runWithContext(Runnable runnable, boolean forceEnqueue) {
		return runWithContext(() -> {
			runnable.run();
			return null;
		}, forceEnqueue);
	}

	public <R> CompletableFuture<R> runWithContext(Callable<R> runnable) {
		return runWithContext(runnable, false);
	}

	public <R> CompletableFuture<R> runWithContext(Callable<R> callable, boolean forceEnqueue) {
		if (CurrentSessionContext.getOrNull() == this && !forceEnqueue) {
			// Fast lane! This thread is already bound to this SessionContext. Just execute the runnable.
			try {
				return CompletableFuture.completedFuture(callable.call());
			} catch (Throwable t) {
				/*
				 note that we don't return a failed completable future in this case, since we want to make sure
				 1) the exception is logged
				 2) the session is closed
				 3) the code that is calling this (which is already in a thread bound to this session context)
				    does not do any further stuff inside the sessionContext.
				*/
				throw new FastLaneExecutionException("Exception during fast lane execution!", t);
			}
		} else {
			return CompletableFuture.supplyAsync(() -> {
				CurrentSessionContext.set(this);
				try {
					Object[] resultHolder = new Object[1];
					executionDecorators
							.createWrappedRunnable(() -> resultHolder[0] = softenExceptions(callable))
							.run();
					return ((R) resultHolder[0]);
				} catch (Throwable t) {
					LOGGER.error("Exception while executing within session context", t);
					this.destroy(UiSessionClosingReason.SERVER_SIDE_ERROR);
					throw t;
				} finally {
					CurrentSessionContext.unset();
				}
			}, sessionExecutor);
		}
	}

	/**
	 * Adds a decorator that gets invoked whenever a Thread is bound to this SessionContext.
	 * The decorator will be called right <strong>after</strong> the Thread is bound to this SessionContext, so SessionContext.current() will return this instance.
	 *
	 * @param decorator
	 * @param outer     Whether to add this decorator as outermost or innermost execution wrapper.
	 */
	public void addExecutionDecorator(ExecutionDecorator decorator, boolean outer) {
		if (outer) {
			executionDecorators.addOuterDecorator(decorator);
		} else {
			executionDecorators.addInnerDecorator(decorator);
		}
	}

	/**
	 * Removes the specified execution decorator.
	 */
	public void removeExecutionDecorator(ExecutionDecorator decorator) {
		executionDecorators.removeDecorator(decorator);
	}

	/**
	 * Removes all decorators.
	 */
	public void clearExecutionDecorators() {
		executionDecorators.clear();
	}

	public SessionConfiguration getConfiguration() {
		return sessionConfiguration;
	}

	public void setConfiguration(SessionConfiguration config) {
		this.sessionConfiguration = config;
		sendStaticCommand(RootPanel.class, new UiRootPanel.SetConfigCommand(config.createUiConfiguration()));
		updateSessionMessageWindows();
	}

	public ZoneId getTimeZone() {
		return sessionConfiguration.getTimeZone();
	}

	public SessionIconProvider getIconProvider() {
		return iconProvider;
	}

	public <I extends Icon<I, S>, S> void setDefaultStyleForIconClass(Class<I> iconClass, S defaultStyle) {
		this.runWithContext(() -> {
			iconProvider.setDefaultStyleForIconClass(iconClass, defaultStyle);
		});
	}

	public String resolveIcon(Icon icon) {
		if (icon == null) {
			return null;
		}
		return sessionConfiguration.getIconPath() + "/" + iconProvider.encodeIcon(icon, true);
	}

	public void renderClientObject(ClientObject clientObject) {
		CurrentSessionContext.throwIfNotSameAs(this);

		clientObjectsById.put(clientObject.getId(), clientObject);
		UiClientObject uiComponent = clientObject.createUiClientObject();

		ComponentLibraryInfo componentLibraryInfo = componentLibraryRegistry.getComponentLibraryForClientObject(clientObject);
		loadComponentLibraryIfNecessary(clientObject, componentLibraryInfo);

		if (!clientObjectTypesKnownToClient.contains(clientObject.getClass())) {
			sendStaticCommand(RootPanel.class, new UiRootPanel.RegisterClientObjectTypeCommand(componentLibraryInfo.getUuid(), uiComponent.getClass().getSimpleName(), List.of(), List.of()));
			clientObjectTypesKnownToClient.add(clientObject.getClass());
		}

		sendStaticCommand(RootPanel.class, new UiRootPanel.RenderCommand(componentLibraryInfo.getUuid(), uiComponent));
	}

	private void loadComponentLibraryIfNecessary(ClientObject clientObject, ComponentLibraryInfo componentLibraryInfo) {
		if (!componentLibrariesLoaded.contains(componentLibraryInfo.getComponentLibrary())) {
			String mainJsUrl = componentLibraryRegistry.getMainJsUrl(clientObject.getClass());
			sendStaticCommand(RootPanel.class, new UiRootPanel.RegisterComponentLibraryCommand(componentLibraryInfo.getUuid(), mainJsUrl), null);
			componentLibrariesLoaded.add(componentLibraryInfo.getComponentLibrary());
		}
	}

	public void unrenderClientObject(ClientObject clientObject) {
		sendStaticCommand(RootPanel.class,
				// unregister only after the ui destroyed the object!
				new UiRootPanel.UnrenderCommand(clientObject.getId()), unused -> clientObjectsById.remove(clientObject.getId()));
	}

	public ClientObject getClientObject(String clientObjectId) {
		return clientObjectsById.get(clientObjectId);
	}

	public String createResourceLink(Resource resource) {
		return createResourceLink(resource, null);
	}

	public void showWindow(Window window, int animationDuration) {
		window.show(animationDuration);
	}

	public void download(Resource resource, String downloadFileName) {
		download(createResourceLink(resource), downloadFileName);
	}

	public void download(File file, String downloadFileName) {
		download(createFileLink(file), downloadFileName);
	}

	public void download(String url, String downloadFileName) {
		runWithContext(() -> sendStaticCommand(RootPanel.class, new UiRootPanel.DownloadFileCommand(url, downloadFileName)));
	}

	public void registerBackgroundImage(String id, String image, String blurredImage) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.RegisterBackgroundImageCommand(id, image, blurredImage));
	}

	public void setBackgroundImage(String id, int animationDuration) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.SetBackgroundImageCommand(id, animationDuration));
	}

	public void showDefaultBackground(int animationDuration) {
		if (!defaultBackgroundRegistered) {
			defaultBackgroundRegistered = true;
			registerBackgroundImage(DEFAULT_BACKGROUND_NAME, DEFAULT_BACKGROUND_URL, DEFAULT_BACKGROUND_URL);
		}
		setBackgroundImage(DEFAULT_BACKGROUND_NAME, animationDuration);
	}

	public void setBackgroundColor(Color color, int animationDuration) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.SetBackgroundColorCommand(color != null ? color.toHtmlColorString() : null, animationDuration));
	}

	public void exitFullScreen() {
		sendStaticCommand(RootPanel.class, new UiRootPanel.ExitFullScreenCommand());
	}

	public void addRootComponent(String containerElementSelector, Component component) {
		addRootPanel(containerElementSelector, component);
	}

	public void addRootPanel(String containerElementSelector, Component rootPanel) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.BuildRootPanelCommand(containerElementSelector, rootPanel.createUiReference()));
	}

	public RootPanel addRootPanel(String containerElementSelector) {
		RootPanel rootPanel = new RootPanel();
		addRootPanel(containerElementSelector, rootPanel);
		return rootPanel;
	}

	public RootPanel addRootPanel() {
		return addRootPanel("body");
	}

	public void addClientToken(String token) {
		getClientInfo().getClientTokens().add(token);
		sendStaticCommand(RootPanel.class, new UiRootPanel.AddClientTokenCommand(token));
	}

	public void removeClientToken(String token) {
		getClientInfo().getClientTokens().remove(token);
		sendStaticCommand(RootPanel.class, new UiRootPanel.RemoveClientTokenCommand(token));
	}

	public void clearClientTokens() {
		getClientInfo().getClientTokens().clear();
		sendStaticCommand(RootPanel.class, new UiRootPanel.ClearClientTokensCommand());
	}

	public void showNotification(Notification notification, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		runWithContext(() -> {
			sendStaticCommand(RootPanel.class, new UiRootPanel.ShowNotificationCommand(notification.createUiReference(), position.toUiNotificationPosition(), entranceAnimation.toUiEntranceAnimation(),
					exitAnimation.toUiExitAnimation()));
		});
	}

	public void showNotification(Notification notification, NotificationPosition position) {
		runWithContext(() -> {
			showNotification(notification, position, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon icon, String caption) {
		runWithContext(() -> {
			Notification notification = Notification.createWithIconAndCaption(icon, caption);
			notification.setDismissible(true);
			notification.setShowProgressBar(false);
			notification.setDisplayTimeInMillis(5000);
			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon icon, String caption, String description) {
		runWithContext(() -> {
			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
			notification.setDismissible(true);
			notification.setShowProgressBar(false);
			notification.setDisplayTimeInMillis(5000);
			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon icon, String caption, String description, boolean dismissable, int displayTimeInMillis, boolean showProgress) {
		runWithContext(() -> {
			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
			notification.setDismissible(dismissable);
			notification.setDisplayTimeInMillis(displayTimeInMillis);
			notification.setShowProgressBar(showProgress);
			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void setSessionExpiredWindow(Window sessionExpiredWindow) {
		this.sessionExpiredWindow = sessionExpiredWindow;
		updateSessionMessageWindows();
	}

	public void setSessionErrorWindow(Window sessionErrorWindow) {
		this.sessionErrorWindow = sessionErrorWindow;
		updateSessionMessageWindows();
	}

	public void setSessionTerminatedWindow(Window sessionTerminatedWindow) {
		this.sessionTerminatedWindow = sessionTerminatedWindow;
		updateSessionMessageWindows();
	}

	private void updateSessionMessageWindows() {
		sendCommand(null,
				new UiRootPanel.SetSessionMessageWindowsCommand(
						sessionExpiredWindow != null ? sessionExpiredWindow.createUiReference()
								: createDefaultSessionMessageWindow(getLocalized("teamapps.common.sessionExpired"), getLocalized("teamapps.common.sessionExpiredText"),
								getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createUiReference(),
						sessionErrorWindow != null ? sessionErrorWindow.createUiReference() : createDefaultSessionMessageWindow(getLocalized("teamapps.common.error"), getLocalized("teamapps.common.sessionErrorText"),
								getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createUiReference(),
						sessionTerminatedWindow != null ? sessionTerminatedWindow.createUiReference() : createDefaultSessionMessageWindow(getLocalized("teamapps.common.sessionTerminated"), getLocalized("teamapps.common.sessionTerminatedText"),
								getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createUiReference()));
	}

	public static Window createDefaultSessionMessageWindow(String title, String message, String refreshButtonCaption, String cancelButtonCaption) {
		Window window = new Window(null, title, null, 300, 300, false, false, false);
		window.setPadding(10);

		VerticalLayout verticalLayout = new VerticalLayout();

		Div messageField = new Div(message);
		messageField.setCssStyle("font-size", "110%");
		verticalLayout.addComponentFillRemaining(messageField);

		Button<?> refreshButton = new Button<>(null, refreshButtonCaption);
		refreshButton.setCssStyle("margin", "10px 0");
		refreshButton.setCssStyle(".UiButton", "background-color", RgbaColor.MATERIAL_BLUE_600.toHtmlColorString());
		refreshButton.setCssStyle(".UiButton", "color", RgbaColor.WHITE.toHtmlColorString());
		refreshButton.setCssStyle(".UiButton", "font-size", "120%");
		refreshButton.setCssStyle(".UiButton", "height", "50px");
		refreshButton.setOnClickJavaScript("window.location.reload()");
		verticalLayout.addComponentAutoSize(refreshButton);

		if (cancelButtonCaption != null) {
			LinkButton cancelLink = new LinkButton(cancelButtonCaption);
			cancelLink.setCssStyle("text-align", "center");
			cancelLink.setOnClickJavaScript("context.getClientObjectById(\"" + window.createUiReference().getId() + "\").close();");
			verticalLayout.addComponentAutoSize(cancelLink);
		}

		window.setContent(verticalLayout);
		window.enableAutoHeight();
		return window;
	}

	public CompletableFuture<WakeLock> requestWakeLock() {
		String uuid = UUID.randomUUID().toString();
		CompletableFuture<WakeLock> completableFuture = new CompletableFuture<>();
		runWithContext(() -> {
			sendStaticCommand(RootPanel.class, new UiRootPanel.RequestWakeLockCommand(uuid), successful -> {
				if (successful) {
					completableFuture.complete(() -> sendStaticCommand(RootPanel.class, new UiRootPanel.ReleaseWakeLockCommand(uuid)));
				} else {
					completableFuture.completeExceptionally(new RuntimeException("Could not acquire WakeLock"));
				}
			});
		});
		return completableFuture;
	}

	public void goToUrl(String url, boolean blankPage) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.GoToUrlCommand(url, blankPage));
	}

	public void setFavicon(Icon<?, ?> icon) {
		setFavicon(resolveIcon(icon));
	}

	public void setFavicon(Resource resource) {
		setFavicon(createResourceLink(resource));
	}

	public void setFavicon(String url) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.SetFaviconCommand(url));
	}

	public void setTitle(String title) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.SetTitleCommand(title));
	}

	public void setGlobalKeyEventsEnabled(boolean unmodified, boolean modifiedWithAltKey, boolean modifiedWithCtrlKey, boolean modifiedWithMetaKey, boolean includeRepeats, boolean keyDown, boolean keyUp) {
		sendStaticCommand(RootPanel.class, new UiRootPanel.SetGlobalKeyEventsEnabledCommand(unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats, keyDown, keyUp));
	}

	public String getSessionId() {
		return uiSession.getSessionId();
	}

	public void handleStaticEvent(UiEvent event) {
		if (event instanceof UiRootPanel.GlobalKeyEventOccurredEvent) {
			UiRootPanel.GlobalKeyEventOccurredEvent e = (UiRootPanel.GlobalKeyEventOccurredEvent) event;
			onGlobalKeyEventOccurred.fire(new KeyboardEvent(
					e.getEventType(),
					(e.getSourceComponentId() != null ? (Component) getClientObject(e.getSourceComponentId()) : null),
					e.getCode(),
					e.getIsComposing(),
					e.getKey(),
					e.getCharCode(),
					e.getKeyCode(),
					e.getLocale(),
					e.getLocation(),
					e.getRepeat(),
					e.getAltKey(),
					e.getCtrlKey(),
					e.getShiftKey(),
					e.getMetaKey()
			));
		} else if (event instanceof UiRootPanel.NavigationStateChangeEvent) {
			UiRootPanel.NavigationStateChangeEvent e = (UiRootPanel.NavigationStateChangeEvent) event;
			Location location = Location.fromUiLocation(e.getLocation());
			this.currentLocation = location;
			onNavigationStateChange.fire(new NavigationStateChangeEvent(location, e.getTriggeredByUser()));
		} else {
			throw new TeamAppsUiApiException(getSessionId(), event.getClass().getName());
		}
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public UiSessionListener getAsUiSessionListenerInternal() {
		return uiSessionListener;
	}

	public void setName(String name) {
		uiSession.setName(name);
	}

	public String getName() {
		return uiSession.getName();
	}
}
