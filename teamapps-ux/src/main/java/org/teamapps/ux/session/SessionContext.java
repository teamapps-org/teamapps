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
import org.teamapps.dto.DtoClientObject;
import org.teamapps.dto.DtoCommand;
import org.teamapps.dto.DtoGlobals;
import org.teamapps.dto.DtoNotification;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.dto.protocol.DtoQueryWrapper;
import org.teamapps.dto.protocol.DtoSessionClosingReason;
import org.teamapps.event.Event;
import org.teamapps.event.ProjectorEvent;
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
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.i18n.ResourceBundleTranslationProvider;
import org.teamapps.ux.i18n.TranslationProvider;
import org.teamapps.ux.icon.IconBundle;
import org.teamapps.ux.icon.TeamAppsIconBundle;
import org.teamapps.ux.json.UxJacksonSerializationTemplate;
import org.teamapps.ux.resource.Resource;

import java.io.File;
import java.time.Duration;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.teamapps.common.util.ExceptionUtil.softenExceptions;

public class SessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionContext.class);

	private final ExecutorService sessionExecutor;

	public final ProjectorEvent<KeyboardEvent> onGlobalKeyEventOccurred = new ProjectorEvent<>(hasListeners -> sendStaticCommand(null, new DtoGlobals.ToggleEventListeningCommand(null, null, DtoGlobals.GlobalKeyEventOccurredEvent.TYPE_ID, hasListeners)));
	public final ProjectorEvent<NavigationStateChangeEvent> onNavigationStateChange = new ProjectorEvent<>(hasListeners -> sendStaticCommand(null, new DtoGlobals.ToggleEventListeningCommand(null, null, DtoGlobals.NavigationStateChangeEvent.TYPE_ID, hasListeners)));
	public final ProjectorEvent<UiSessionActivityState> onActivityStateChanged = new ProjectorEvent<>();
	public final Event<DtoSessionClosingReason> onDestroyed = new Event<>();

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
	private Set<ClientObject> renderingClientObjects = new HashSet<>();
	private final SessionContextResourceManager sessionResourceProvider;

	private TranslationProvider translationProvider;

	private SessionConfiguration sessionConfiguration;

	private final Map<String, Icon<?, ?>> bundleIconByKey = new HashMap<>();

	private Window sessionExpiredWindow;
	private Window sessionErrorWindow;
	private Window sessionTerminatedWindow;

	private final UiSessionListener uiSessionListener = new UiSessionListener() {
		@Override
		public void onUiEvent(String sessionId, DtoEventWrapper event) {
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
		public void onUiQuery(String sessionId, DtoQueryWrapper query, Consumer<Object> resultCallback) {
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
		public void onClosed(String sessionId, DtoSessionClosingReason reason) {
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

	public Event<DtoSessionClosingReason> onDestroyed() {
		return onDestroyed;
	}

	public void pushNavigationState(String relativeUrl) {
		sendStaticCommand(null, new DtoGlobals.PushHistoryStateCommand(relativeUrl));
	}

	public void navigateBack(int steps) {
		navigateForward(-steps);
	}

	public void navigateForward(int steps) {
		sendStaticCommand(null, new DtoGlobals.NavigateForwardCommand(steps));
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
		destroy(DtoSessionClosingReason.TERMINATED_BY_APPLICATION);
	}

	private void destroy(DtoSessionClosingReason reason) {
		uiSession.close(reason);
	}

	public <RESULT> void sendStaticCommand(Class<? extends ClientObject> clientObjectClass, DtoCommand<RESULT> command, Consumer<RESULT> resultCallback) {
		CurrentSessionContext.throwIfNotSameAs(this);

		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;

		String componentLibraryUuid;
		if (clientObjectClass != null) {
			ComponentLibraryInfo componentLibraryInfo = componentLibraryRegistry.getComponentLibraryForClientObjectClass(clientObjectClass);
			componentLibraryUuid = componentLibraryInfo.getUuid();
		} else {
			componentLibraryUuid = null;
		}

		uxJacksonSerializationTemplate.doWithUxJacksonSerializers(() -> uiSession.sendCommand(new UiCommandWithResultCallback<>(componentLibraryUuid, null, command, wrappedCallback)));
	}

	public <RESULT> void sendStaticCommand(Class<? extends ClientObject> clientObjectClass, DtoCommand<RESULT> command) {
		sendStaticCommand(clientObjectClass, command, null);
	}

	public void sendCommandIfRendered(ClientObject clientObject, DtoCommand<?> command) {
		sendCommandIfRendered(clientObject, null, (Supplier) () -> command);
	}

	public <RESULT> void sendCommandIfRendered(ClientObject clientObject, Consumer<RESULT> resultCallback, Supplier<DtoCommand<RESULT>> commandSupplier) {
		Objects.requireNonNull(clientObject, "clientObject must not be null!");
		CurrentSessionContext.throwIfNotSameAs(this);

		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;

		if (isRendering(clientObject)) {
			/*
			This accounts for a very rare case. A component that is rendering itself may, while one of its children is rendered, be changed due to a thrown event. This change must be transported to the client
			as command (since the corresponding setter of the parent's DtoComponent has possibly already been set). However, this command must be enqueued after the component is rendered on the client
			side! Therefore, sending the command must be forcibly enqueued.

			Example: A panel contains a table. The panel's title is bound to the table's "count" ObservableValue. When the panel is rendered, the table also is rendered (as part of rendering the
			panel). While rendering, the table sets its "count" value, so the panel's title is changed. However, the DtoPanel's setTitle() method already has been invoked, so the change will not have
			any effect on the initialization of the DtoPanel. Therefore, the change must be sent as a command. Sending the command directly however would make it arrive at the client before
			the panel was rendered (which is only after completing its createUiComponent() method).
			 */
			runWithContext(() -> sendCommandInternal(clientObject.getId(), commandSupplier.get(), wrappedCallback), true);
		} else if (isRendered(clientObject)) {
			sendCommandInternal(clientObject.getId(), commandSupplier.get(), wrappedCallback);
		}
	}

	private <RESULT> void sendCommandInternal(String clientObjectId, DtoCommand<RESULT> command, Consumer<RESULT> wrappedCallback) {
		uxJacksonSerializationTemplate.doWithUxJacksonSerializers(() -> uiSession.sendCommand(new UiCommandWithResultCallback<>(null, clientObjectId, command, wrappedCallback)));
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
					this.destroy(DtoSessionClosingReason.SERVER_SIDE_ERROR);
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
		sendStaticCommand(null, new DtoGlobals.SetConfigCommand(config.createUiConfiguration()));
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

	public String resolveIcon(Icon<?, ?> icon) {
		if (icon == null) {
			return null;
		}
		return sessionConfiguration.getIconPath() + "/" + iconProvider.encodeIcon((Icon) icon, true);
	}

	public void renderClientObject(ClientObject clientObject) {
		CurrentSessionContext.throwIfNotSameAs(this);
		if (clientObjectsById.containsKey(clientObject.getId())) {
			return; // already rendered or currently rendering!
		}

		LOGGER.debug("Rendering: " + clientObject.getId());

		this.renderingClientObjects.add(clientObject);
		try {
			clientObjectsById.put(clientObject.getId(), clientObject);
			DtoClientObject uiClientObject = clientObject.createDto();
			if (uiClientObject.getId() == null) {
				throw new IllegalArgumentException("DtoClientObject must not have id == null!");
			}

			ComponentLibraryInfo componentLibraryInfo = componentLibraryRegistry.getComponentLibraryForClientObject(clientObject);
			loadComponentLibraryIfNecessary(clientObject, componentLibraryInfo);

			if (!clientObjectTypesKnownToClient.contains(clientObject.getClass())) {
				sendStaticCommand(null, new DtoGlobals.RegisterClientObjectTypeCommand(componentLibraryInfo.getUuid(), uiClientObject.getTypeId(), uiClientObject.getEventNames(), uiClientObject.getQueryNames()));
				clientObjectTypesKnownToClient.add(clientObject.getClass());
			}

			sendStaticCommand(null, new DtoGlobals.RenderCommand(componentLibraryInfo.getUuid(), uiClientObject));
		} finally {
			renderingClientObjects.remove(clientObject);
		}
	}

	private void loadComponentLibraryIfNecessary(ClientObject clientObject, ComponentLibraryInfo componentLibraryInfo) {
		if (!componentLibrariesLoaded.contains(componentLibraryInfo.getComponentLibrary())) {
			String mainJsUrl = componentLibraryRegistry.getMainJsUrl(clientObject.getClass());
			String mainCssUrl = componentLibraryRegistry.getMainCssUrl(clientObject.getClass());
			sendStaticCommand(null, new DtoGlobals.RegisterComponentLibraryCommand(componentLibraryInfo.getUuid(), mainJsUrl, mainCssUrl), null);
			componentLibrariesLoaded.add(componentLibraryInfo.getComponentLibrary());
		}
	}

	public void unrenderClientObject(ClientObject clientObject) {
		sendStaticCommand(null, new DtoGlobals.UnrenderCommand(clientObject.getId()));
		clientObjectsById.remove(clientObject.getId());
	}

	public boolean isRendering(ClientObject clientObject) {
		return renderingClientObjects.contains(clientObject);
	}

	public boolean isRendered(ClientObject clientObject) {
		return clientObjectsById.containsKey(clientObject.getId());
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
		runWithContext(() -> sendStaticCommand(null, new DtoGlobals.DownloadFileCommand(url, downloadFileName)));
	}

	public void setBackground(String backgroundImageUrl, String blurredBackgroundImageUrl, Color backgroundColor, Duration animationDuration) {
		sendStaticCommand(null, new DtoGlobals.SetBackgroundCommand(backgroundImageUrl, blurredBackgroundImageUrl, backgroundColor.toHtmlColorString(), (int) animationDuration.toMillis()));
	}

	public void exitFullScreen() {
		sendStaticCommand(null, new DtoGlobals.ExitFullScreenCommand());
	}

	public void addRootComponent(String containerElementSelector, Component component) {
		addRootPanel(containerElementSelector, component);
	}

	public void addRootPanel(String containerElementSelector, Component rootPanel) {
		sendStaticCommand(null, new DtoGlobals.AddRootComponentCommand(containerElementSelector, rootPanel.createDtoReference()));
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
		sendStaticCommand(null, new DtoGlobals.AddClientTokenCommand(token));
	}

	public void removeClientToken(String token) {
		getClientInfo().getClientTokens().remove(token);
		sendStaticCommand(null, new DtoGlobals.RemoveClientTokenCommand(token));
	}

	public void clearClientTokens() {
		getClientInfo().getClientTokens().clear();
		sendStaticCommand(null, new DtoGlobals.ClearClientTokensCommand());
	}

	public void showNotification(Notification notification, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		runWithContext(() -> {
			sendStaticCommand(null, new DtoNotification.ShowNotificationCommand(notification.createDtoReference(), position.toUiNotificationPosition(), entranceAnimation.toUiEntranceAnimation(),
					exitAnimation.toUiExitAnimation()));
		});
	}

	public void showNotification(Notification notification, NotificationPosition position) {
		runWithContext(() -> {
			showNotification(notification, position, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon<?, ?> icon, String caption) {
		runWithContext(() -> {
			Notification notification = Notification.createWithIconAndCaption(icon, caption);
			notification.setDismissible(true);
			notification.setShowProgressBar(false);
			notification.setDisplayTimeInMillis(5000);
			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon<?, ?> icon, String caption, String description) {
		runWithContext(() -> {
			Notification notification = Notification.createWithIconAndTextAndDescription(icon, caption, description);
			notification.setDismissible(true);
			notification.setShowProgressBar(false);
			notification.setDisplayTimeInMillis(5000);
			showNotification(notification, NotificationPosition.TOP_RIGHT, EntranceAnimation.SLIDE_IN_RIGHT, ExitAnimation.FADE_OUT);
		});
	}

	public void showNotification(Icon<?, ?> icon, String caption, String description, boolean dismissable, int displayTimeInMillis, boolean showProgress) {
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
		// TODO uncomment:
//		sendStaticCommand(null,
//				new DtoGlobals.SetSessionMessageWindowsCommand(
//						sessionExpiredWindow != null ? sessionExpiredWindow.createDtoReference()
//								: createDefaultSessionMessageWindow(getLocalized("teamapps.common.sessionExpired"), getLocalized("teamapps.common.sessionExpiredText"),
//								getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createDtoReference(),
//						sessionErrorWindow != null ? sessionErrorWindow.createDtoReference() : createDefaultSessionMessageWindow(getLocalized("teamapps.common.error"), getLocalized("teamapps.common.sessionErrorText"),
//								getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createDtoReference(),
//						sessionTerminatedWindow != null ? sessionTerminatedWindow.createDtoReference() : createDefaultSessionMessageWindow(getLocalized("teamapps.common.sessionTerminated"), getLocalized("teamapps.common.sessionTerminatedText"),
//								getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createDtoReference()));
	}

	public static Window createDefaultSessionMessageWindow(String title, String message, String refreshButtonCaption, String cancelButtonCaption) {
		Window window = new Window(null, title, null, 300, 300, true, true, true);
		window.setPadding(10);

		VerticalLayout verticalLayout = new VerticalLayout();

		Div messageField = new Div(message);
		messageField.setCssStyle("font-size", "110%");
		verticalLayout.addComponentFillRemaining(messageField);

		Button<?> refreshButton = new Button<>(null, refreshButtonCaption);
		refreshButton.setCssStyle("margin", "10px 0");
		refreshButton.setCssStyle(".DtoButton", "background-color", RgbaColor.MATERIAL_BLUE_600.toHtmlColorString());
		refreshButton.setCssStyle(".DtoButton", "color", RgbaColor.WHITE.toHtmlColorString());
		refreshButton.setCssStyle(".DtoButton", "font-size", "120%");
		refreshButton.setCssStyle(".DtoButton", "height", "50px");
		refreshButton.setOnClickJavaScript("window.location.reload()");
		verticalLayout.addComponentAutoSize(refreshButton);

		if (cancelButtonCaption != null) {
			LinkButton cancelLink = new LinkButton(cancelButtonCaption);
			cancelLink.setCssStyle("text-align", "center");
			cancelLink.setOnClickJavaScript("context.getClientObjectById(\"" + window.createDtoReference().getId() + "\").close();");
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
			sendStaticCommand(null, new DtoGlobals.RequestWakeLockCommand(uuid), successful -> {
				if (successful) {
					completableFuture.complete(() -> sendStaticCommand(null, new DtoGlobals.ReleaseWakeLockCommand(uuid)));
				} else {
					completableFuture.completeExceptionally(new RuntimeException("Could not acquire WakeLock"));
				}
			});
		});
		return completableFuture;
	}

	public void goToUrl(String url, boolean blankPage) {
		sendStaticCommand(null, new DtoGlobals.GoToUrlCommand(url, blankPage));
	}

	public void setFavicon(Icon<?, ?> icon) {
		setFavicon(resolveIcon(icon));
	}

	public void setFavicon(Resource resource) {
		setFavicon(createResourceLink(resource));
	}

	public void setFavicon(String url) {
		sendStaticCommand(null, new DtoGlobals.SetFaviconCommand(url));
	}

	public void setTitle(String title) {
		sendStaticCommand(null, new DtoGlobals.SetTitleCommand(title));
	}

	public void setGlobalKeyEventsEnabled(boolean unmodified, boolean modifiedWithAltKey, boolean modifiedWithCtrlKey, boolean modifiedWithMetaKey, boolean includeRepeats, boolean keyDown, boolean keyUp) {
		sendStaticCommand(null, new DtoGlobals.SetGlobalKeyEventsEnabledCommand(unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats, keyDown, keyUp));
	}

	public String getSessionId() {
		return uiSession.getSessionId();
	}

	public void handleStaticEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoGlobals.GlobalKeyEventOccurredEvent.TYPE_ID -> {
				DtoGlobals.GlobalKeyEventOccurredEventWrapper e = event.as(DtoGlobals.GlobalKeyEventOccurredEventWrapper.class);
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
			}
			case DtoGlobals.NavigationStateChangeEvent.TYPE_ID -> {
				DtoGlobals.NavigationStateChangeEventWrapper e = event.as(DtoGlobals.NavigationStateChangeEventWrapper.class);
				Location location = Location.fromUiLocationWrapper(e.getLocation());
				this.currentLocation = location;
				onNavigationStateChange.fire(new NavigationStateChangeEvent(location, e.getTriggeredByUser()));
			}
			default -> throw new TeamAppsUiApiException(getSessionId(), event.getClass().getName());
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
