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

import com.devskiller.friendly_id.FriendlyId;
import com.ibm.icu.util.ULocale;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.DtoGlobals;
import org.teamapps.dto.DtoNotification;
import org.teamapps.dto.JsonWrapper;
import org.teamapps.dto.protocol.server.SessionClosingReason;
import org.teamapps.dto.protocol.server.CREATE_OBJ;
import org.teamapps.dto.protocol.server.DESTROY_OBJ;
import org.teamapps.dto.protocol.server.REGISTER_LIB;
import org.teamapps.dto.protocol.server.TOGGLE_EVT;
import org.teamapps.event.Event;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.*;
import org.teamapps.ux.component.*;
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
import org.teamapps.ux.resource.Resource;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static org.teamapps.common.util.ExceptionUtil.softenExceptions;

public class SessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ExecutorService sessionExecutor;

	public final ProjectorEvent<KeyboardEvent> onGlobalKeyEventOccurred = new ProjectorEvent<>(hasListeners -> toggleStaticEvent(Globals.class, "globalKeyEventOccurred", hasListeners));
	public final ProjectorEvent<NavigationStateChangeEvent> onNavigationStateChange = new ProjectorEvent<>(hasListeners -> toggleStaticEvent(Globals.class, "navigationStateChange", hasListeners));
	public final ProjectorEvent<UiSessionActivityState> onActivityStateChanged = new ProjectorEvent<>();
	public final Event<SessionClosingReason> onDestroyed = new Event<>();

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

	private final BidiMap<String, ClientObject> clientObjectsById = new DualHashBidiMap<>();
	private final Set<ClientObject> renderingClientObjects = new HashSet<>();
	private final Set<ClientObject> renderedClientObjects = new HashSet<>();

	private final SessionContextResourceManager sessionResourceProvider;

	private TranslationProvider translationProvider;

	private SessionConfiguration sessionConfiguration;

	private final Map<String, Icon<?, ?>> bundleIconByKey = new HashMap<>();

	private Window sessionExpiredWindow;
	private Window sessionErrorWindow;
	private Window sessionTerminatedWindow;

	private final UiSessionListener uiSessionListener = new UiSessionListener() {
		@Override
		public void handleEvent(String sessionId, String libraryId, String clientObjectId, String name, List<JsonWrapper> params) {
			runWithContext(() -> {
				if (clientObjectId != null) {
					ClientObject clientObject = clientObjectsById.get(clientObjectId);
					if (clientObject != null) {
						clientObject.handleEvent(name, params);
					} else {
						throw new ProjectorComponentNotFoundException(sessionId, clientObjectId);
					}
				} else {
					handleStaticEvent(libraryId, name, params);
				}
			});
		}

		@Override
		public void handleQuery(String sessionId, String libraryId, String clientObjectId, String name, List<JsonWrapper> params, Consumer<Object> resultCallback) {
			runWithContext(() -> {
				if (clientObjectId != null) {
					ClientObject clientObject = clientObjectsById.get(clientObjectId);
					if (clientObject != null) {
						Object result = clientObject.handleQuery(name, params);
						resultCallback.accept(result);
					} else {
						throw new ProjectorComponentNotFoundException(sessionId, clientObjectId);
					}
				} else {
					// TODO
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
		public void onClosed(String sessionId, SessionClosingReason reason) {
			runWithContext(() -> {
				onDestroyed.fireIgnoringExceptions(reason);
				// Enqueue this at the end, so all onDestroyed handlers have already been executed before disabling any more executions inside the context!
				sessionExecutor.submit(sessionExecutor::shutdown);
			});
		}
	};

	private final Set<ComponentLibrary> loadedComponentLibraries = new HashSet<>();
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
		this.translationProvider = new ResourceBundleTranslationProvider("org.teamapps.ux.i18n.DefaultCaptions", Locale.ENGLISH);
		addIconBundle(TeamAppsIconBundle.createBundle());
		runWithContext(this::updateSessionMessageWindows);
		this.sessionResourceProvider = new SessionContextResourceManager(uiSession.getSessionId());
		this.componentLibraryRegistry = componentLibraryRegistry;
	}

	public Event<SessionClosingReason> onDestroyed() {
		return onDestroyed;
	}

	public void pushNavigationState(String relativeUrl) {
		sendStaticCommand(Globals.class, DtoGlobals.PushHistoryStateCommand.CMD_NAME, new DtoGlobals.PushHistoryStateCommand(relativeUrl).getParameters());
	}

	public void navigateBack(int steps) {
		navigateForward(-steps);
	}

	public void navigateForward(int steps) {
		sendStaticCommand(Globals.class, DtoGlobals.NavigateForwardCommand.CMD_NAME, new DtoGlobals.NavigateForwardCommand(steps).getParameters());
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
		destroy(SessionClosingReason.TERMINATED_BY_APPLICATION);
	}

	private void destroy(SessionClosingReason reason) {
		uiSession.close(reason);
	}

	public void sendStaticCommand(Class<? extends ClientObject> clientObjectClass, String name, Object[] params) {
		sendStaticCommand(clientObjectClass, name, params, null);
	}

	public <RESULT> void sendStaticCommand(Class<? extends ClientObject> clientObjectClass, String name, Object[] params, Consumer<RESULT> resultCallback) {
		CurrentSessionContext.throwIfNotSameAs(this);

		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;
		String componentLibraryUuid = getComponentLibraryUuidForClientObjectClass(clientObjectClass, true);

		uiSession.sendCommand(new UiCommandWithResultCallback(componentLibraryUuid, null, name, params, (Consumer) wrappedCallback));
	}

	private String getComponentLibraryUuidForClientObjectClass(Class<? extends ClientObject> clientObjectClass, boolean loadIfNecessary) {
		Objects.requireNonNull(clientObjectClass);
		String componentLibraryUuid;
		if (clientObjectClass == Globals.class) {
			componentLibraryUuid = null;
		} else {
			ComponentLibraryInfo libraryInfo = componentLibraryRegistry.getComponentLibraryForClientObjectClass(clientObjectClass);
			if (!loadedComponentLibraries.contains(libraryInfo.componentLibrary()) && loadIfNecessary) {
				uiSession.sendServerMessage(new REGISTER_LIB(libraryInfo.uuid(), libraryInfo.mainJsUrl(), libraryInfo.mainCssUrl()));
				loadedComponentLibraries.add(libraryInfo.componentLibrary());
			}
			componentLibraryUuid = libraryInfo.uuid();
		}
		return componentLibraryUuid;
	}

	public <RESULT> void sendCommandIfRendered(ClientObject clientObject, String name, Object[] params, Consumer<RESULT> resultCallback) {
		Objects.requireNonNull(clientObject, "clientObject must not be null!");
		CurrentSessionContext.throwIfNotSameAs(this);

		if (isRendering(clientObject)) {
			/*
			This accounts for a very rare case. A component that is rendering itself may, while one of its children is rendered, be changed due to a fired event. This change must be transported to the client
			as command (since the corresponding setter of the parent's DtoComponent has possibly already been set). However, this command must be enqueued after the component is rendered on the client
			side! Therefore, sending the command must be forcibly enqueued.

			Example: A panel contains a table. The panel's title is bound to the table's "count" ObservableValue. When the panel is rendered, the table also is rendered (as part of rendering the
			panel). While rendering, the table sets its "count" value, so the panel's title is changed. However, the DtoPanel's setTitle() method already has been invoked, so the change will not have
			any effect on the initialization of the DtoPanel. Therefore, the change must be sent as a command. Sending the command directly however would make it arrive at the client before
			the panel was rendered (which is only after completing its createUiComponent() method).
			 */
			runWithContext(() -> sendCommandInternal(clientObject.getId(), name, params, resultCallback), true);
		} else if (isRendered(clientObject)) {
			sendCommandInternal(clientObject.getId(), name, params, resultCallback);
		}
	}

	private <RESULT> void sendCommandInternal(String clientObjectId, String name, Object[] params, Consumer<RESULT> resultCallback) {
		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;
		uiSession.sendCommand(new UiCommandWithResultCallback(null, clientObjectId, name, params, (Consumer) wrappedCallback));
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
					this.destroy(SessionClosingReason.SERVER_SIDE_ERROR);
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
		sendStaticCommand(Globals.class, DtoGlobals.SetConfigCommand.CMD_NAME, new DtoGlobals.SetConfigCommand(config.createUiConfiguration()).getParameters());
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

	public ClientObjectChannel createClientObject(ClientObject clientObject) {
		CurrentSessionContext.throwIfNotSameAs(this);

		if (!clientObjectsById.containsValue(clientObject)) {
			String id = FriendlyId.createFriendlyId();
			clientObjectsById.put(id, clientObject);
		}

		if (!renderedClientObjects.contains(clientObject) && !renderingClientObjects.contains(clientObject)) {
			this.renderingClientObjects.add(clientObject);
			try {
				String libraryUuid = getComponentLibraryUuidForClientObjectClass(clientObject.getClass(), true);
				uiSession.sendServerMessage(new CREATE_OBJ(libraryUuid, clientObject.getClass().getSimpleName(), clientObject.createConfig(), clientObject.getListeningEventNames()));
			} finally {
				renderingClientObjects.remove(clientObject);
			}
			renderedClientObjects.add(clientObject);
		}

		return new ClientObjectChannel() {
			@Override
			public void sendCommand(String name, Object[] params, Consumer<JsonWrapper> resultHandler) {
				sendCommandIfRendered(clientObject, name, params, null);
			}

			@Override
			public void toggleEvent(String eventName, boolean enabled) {
				SessionContext.this.toggleEvent(clientObject, eventName, enabled);
			}
		};
	}

	public void toggleEvent(ClientObject clientObject, String eventName, boolean enabled) {
		CurrentSessionContext.throwIfNotSameAs(this);
		if (!renderedClientObjects.contains(clientObject)) {
			return; // will be registered when rendering, anyway.
		}
		String libraryUuid = getComponentLibraryUuidForClientObjectClass(clientObject.getClass(), true);
		uiSession.sendServerMessage(new TOGGLE_EVT(libraryUuid, clientObjectsById.getKey(clientObject), eventName, enabled));
	}

	public void toggleStaticEvent(Class<? extends ClientObject> representativeLibraryClass, String eventName, boolean enabled) {
		CurrentSessionContext.throwIfNotSameAs(this);
		String libraryUuid = getComponentLibraryUuidForClientObjectClass(representativeLibraryClass, true);
		uiSession.sendServerMessage(new TOGGLE_EVT(libraryUuid, null, eventName, enabled));
	}

	public void destroyClientObject(ClientObject clientObject) {
		String id = clientObjectsById.getKey(clientObject);
		if (id != null) {
			uiSession.sendServerMessage(new DESTROY_OBJ(id));
			clientObjectsById.remove(id);
			renderingClientObjects.remove(clientObject);
			renderedClientObjects.remove(clientObject);
		}
	}

	public String getClientObjectId(ClientObject clientObject) {
		return clientObjectsById.getKey(clientObject);
	}

	public boolean isRendering(ClientObject clientObject) {
		return renderingClientObjects.contains(clientObject);
	}

	public boolean isRendered(ClientObject clientObject) {
		return renderedClientObjects.contains(clientObject);
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
		runWithContext(() -> sendStaticCommand(Globals.class, DtoGlobals.DownloadFileCommand.CMD_NAME, new DtoGlobals.DownloadFileCommand(url, downloadFileName).getParameters()));
	}

	public void setBackground(String backgroundImageUrl, String blurredBackgroundImageUrl, Color backgroundColor, Duration animationDuration) {
		sendStaticCommand(Globals.class, DtoGlobals.SetBackgroundCommand.CMD_NAME, new DtoGlobals.SetBackgroundCommand(backgroundImageUrl, blurredBackgroundImageUrl, backgroundColor.toHtmlColorString(), (int) animationDuration.toMillis()).getParameters());
	}

	public void exitFullScreen() {
		sendStaticCommand(Globals.class, DtoGlobals.ExitFullScreenCommand.CMD_NAME, new DtoGlobals.ExitFullScreenCommand().getParameters());
	}

	public void addRootComponent(String containerElementSelector, Component component) {
		addRootPanel(containerElementSelector, component);
	}

	public void addRootPanel(String containerElementSelector, Component rootPanel) {
		sendStaticCommand(Globals.class, DtoGlobals.AddRootComponentCommand.CMD_NAME, new DtoGlobals.AddRootComponentCommand(containerElementSelector, rootPanel).getParameters());
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
		sendStaticCommand(Globals.class, DtoGlobals.AddClientTokenCommand.CMD_NAME, new DtoGlobals.AddClientTokenCommand(token).getParameters());
	}

	public void removeClientToken(String token) {
		getClientInfo().getClientTokens().remove(token);
		sendStaticCommand(Globals.class, DtoGlobals.RemoveClientTokenCommand.CMD_NAME, new DtoGlobals.RemoveClientTokenCommand(token).getParameters());
	}

	public void clearClientTokens() {
		getClientInfo().getClientTokens().clear();
		sendStaticCommand(Globals.class, DtoGlobals.ClearClientTokensCommand.CMD_NAME, new DtoGlobals.ClearClientTokensCommand().getParameters());
	}

	public void showNotification(Notification notification, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		runWithContext(() -> {
			sendStaticCommand(Notification.class, DtoNotification.ShowNotificationCommand.CMD_NAME, new DtoNotification.ShowNotificationCommand(notification, position.toUiNotificationPosition(), entranceAnimation.toUiEntranceAnimation(), exitAnimation.toUiExitAnimation()).getParameters());
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
			cancelLink.setOnClickJavaScript("context.getClientObjectById(\"" + window.createClientReference().getId() + "\").close();");
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
			this.<Boolean>sendStaticCommand(Globals.class, DtoGlobals.RequestWakeLockCommand.CMD_NAME, new DtoGlobals.RequestWakeLockCommand(uuid).getParameters(), successful -> {
				if (successful) {
					completableFuture.complete(() -> sendStaticCommand(Globals.class, DtoGlobals.ReleaseWakeLockCommand.CMD_NAME, new DtoGlobals.ReleaseWakeLockCommand(uuid).getParameters()));
				} else {
					completableFuture.completeExceptionally(new RuntimeException("Could not acquire WakeLock"));
				}
			});
		});
		return completableFuture;
	}

	public void goToUrl(String url, boolean blankPage) {
		sendStaticCommand(Globals.class, DtoGlobals.GoToUrlCommand.CMD_NAME, new DtoGlobals.GoToUrlCommand(url, blankPage).getParameters());
	}

	public void setFavicon(Icon<?, ?> icon) {
		setFavicon(resolveIcon(icon));
	}

	public void setFavicon(Resource resource) {
		setFavicon(createResourceLink(resource));
	}

	public void setFavicon(String url) {
		sendStaticCommand(Globals.class, DtoGlobals.SetFaviconCommand.CMD_NAME, new DtoGlobals.SetFaviconCommand(url).getParameters());
	}

	public void setTitle(String title) {
		sendStaticCommand(Globals.class, DtoGlobals.SetTitleCommand.CMD_NAME, new DtoGlobals.SetTitleCommand(title).getParameters());
	}

	public void setGlobalKeyEventsEnabled(boolean unmodified, boolean modifiedWithAltKey, boolean modifiedWithCtrlKey, boolean modifiedWithMetaKey, boolean includeRepeats, boolean keyDown, boolean keyUp) {
		sendStaticCommand(Globals.class, DtoGlobals.SetGlobalKeyEventsEnabledCommand.CMD_NAME,
				new DtoGlobals.SetGlobalKeyEventsEnabledCommand(unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats, keyDown, keyUp).getParameters());
	}

	public String getSessionId() {
		return uiSession.getSessionId();
	}

	// TODO
	public void handleStaticEvent(String libraryId, String name, List<JsonWrapper> params) {
		switch (name) {
			case DtoGlobals.GlobalKeyEventOccurredEvent.TYPE_ID -> {
				DtoGlobals.GlobalKeyEventOccurredEventWrapper e = params.getFirst().as(DtoGlobals.GlobalKeyEventOccurredEventWrapper.class);
				String clientObjectId = e.getSourceComponentId();
				onGlobalKeyEventOccurred.fire(new KeyboardEvent(
						e.getEventType(),
						(e.getSourceComponentId() != null ? (Component) clientObjectsById.get(clientObjectId) : null),
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
				DtoGlobals.NavigationStateChangeEventWrapper e = params.getFirst().as(DtoGlobals.NavigationStateChangeEventWrapper.class);
				Location location = Location.fromUiLocationWrapper(e.getLocation());
				this.currentLocation = location;
				onNavigationStateChange.fire(new NavigationStateChangeEvent(location, e.getTriggeredByUser()));
			}
			default -> {
				// TODO static event handlers on library level...
				throw new TeamAppsUiApiException(getSessionId(), name);
			}
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
