/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
import jakarta.ws.rs.ext.ParamConverterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.*;
import org.teamapps.uisession.statistics.UiSessionStats;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.animation.EntranceAnimation;
import org.teamapps.ux.component.animation.ExitAnimation;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.field.DisplayField;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.linkbutton.LinkButton;
import org.teamapps.ux.component.notification.Notification;
import org.teamapps.ux.component.notification.NotificationPosition;
import org.teamapps.ux.component.popup.Popup;
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
import org.teamapps.ux.session.navigation.*;

import java.io.File;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.teamapps.common.util.ExceptionUtil.softenExceptions;
import static org.teamapps.ux.session.navigation.NavigationHistoryOperation.PUSH;
import static org.teamapps.ux.session.navigation.NavigationHistoryOperation.REPLACE;
import static org.teamapps.ux.session.navigation.RoutingUtil.isEmptyPath;
import static org.teamapps.ux.session.navigation.RoutingUtil.normalizePath;

public class SessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionContext.class);
	private static final String DEFAULT_BACKGROUND_NAME = "defaultBackground";
	private static final String DEFAULT_BACKGROUND_URL = "/resources/backgrounds/default-bl.jpg";

	private final ExecutorService sessionExecutor;

	public final Event<KeyboardEvent> onGlobalKeyEventOccurred = new Event<>();

	public final Event<UiSessionActivityState> onActivityStateChanged = new Event<>();
	public final Event<UiSessionClosingReason> onDestroyed = new Event<>();
	/**
	 * Decorators around all executions inside this SessionContext. These will be invoked when the Thread is already bound to the SessionContext, so SessionContext.current() will
	 * return this instance.
	 */
	public final ExecutionDecoratorStack executionDecorators = new ExecutionDecoratorStack();
	public final Event<NavigationStateChangeEvent> onNavigationStateChange = new Event<>();

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

	private final Map<String, Template> registeredTemplates = new HashMap<>();
	private SessionConfiguration sessionConfiguration;

	private final Map<String, Icon<?, ?>> bundleIconByKey = new HashMap<>();

	private boolean defaultBackgroundRegistered;

	private Window sessionExpiredWindow;
	private Window sessionErrorWindow;
	private Window sessionTerminatedWindow;

	private final ParamConverterProvider navigationParamConverterProvider;
	private final String navigationPathPrefix;

	private RoutingMode routingMode = RoutingMode.DISABLED;
	private final Map<String, Router> routersByPathPrefix = new HashMap<>();
	private final List<Router> routers = new ArrayList<>();
	private boolean routeHandlingDirty = false; // indicates whether the routers changed during routing
	private boolean skipAutoUpdateNavigationHistoryStateOnce = false;

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

	public SessionContext(UiSession uiSession,
						  ExecutorService sessionExecutor,
						  ClientInfo clientInfo,
						  SessionConfiguration sessionConfiguration,
						  HttpSession httpSession,
						  UxServerContext serverContext,
						  SessionIconProvider iconProvider,
						  String navigationPathPrefix,
						  ParamConverterProvider navigationParamConverterProvider // TODO #ownInterfaces
	) {
		this.sessionExecutor = sessionExecutor;
		this.uiSession = uiSession;
		this.httpSession = httpSession;
		this.clientInfo = clientInfo;
		this.currentLocation = clientInfo.getLocation();
		this.sessionConfiguration = sessionConfiguration;
		this.serverContext = serverContext;
		this.iconProvider = iconProvider;
		this.navigationPathPrefix = navigationPathPrefix;
		this.navigationParamConverterProvider = navigationParamConverterProvider;
		this.uxJacksonSerializationTemplate = new UxJacksonSerializationTemplate(this);
		this.translationProvider = new ResourceBundleTranslationProvider("org.teamapps.ux.i18n.DefaultCaptions", Locale.ENGLISH);
		addIconBundle(TeamAppsIconBundle.createBundle());
		runWithContext(this::updateSessionMessageWindows);
		this.sessionResourceProvider = new SessionContextResourceManager(uiSession.getSessionId());
	}


	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void navigateBack(int steps) {
		navigateForward(-steps);
	}

	public void navigateForward(int steps) {
		queueCommand(new UiRootPanel.NavigateForwardCommand(steps));
	}

	public Router getBaseRouter() {
		return getRouter("/");
	}

	public Router getRouter(String pathPrefix) {
		pathPrefix = normalizePath(pathPrefix);
		return this.routersByPathPrefix.computeIfAbsent(pathPrefix, prefix -> {
			Router router = new Router(prefix);
			routers.add(router);
			router.addChangeListener(() -> routeHandlingDirty = true);
			routeHandlingDirty = true;
			return router;
		});
	}

	public void updateNavigationHistoryState() {
		Route currentRoute = Route.fromLocation(getCurrentLocation());

		Router router = getBaseRouter();
		Route route = Route.create();

		NavigationHistoryOperation pathChangeOperation = REPLACE;
		Set<String> queryParamNamesWorthStatePush = new HashSet<>();

		while (router != null) {
			RelativeRouteInfo relativeRouteInfo = router.calculateRelativeRouteInfo();
			route = route.withPathSuffix(relativeRouteInfo.getRoute().getPath())
					.withQueryParams(relativeRouteInfo.getRoute().getQueryParams());
			if (relativeRouteInfo.isPathChangeWorthStatePush()) {
				pathChangeOperation = PUSH;
			}
			queryParamNamesWorthStatePush.addAll(relativeRouteInfo.getQueryParamNamesWorthStatePush());
			String pathPrefix = route.getPath();
			boolean addsToPath = !isEmptyPath(pathPrefix);
			// TODO #performance check efficiency and improve
			router = addsToPath ? this.routers.stream().filter(r -> r.matchesPathPrefix(pathPrefix)).findFirst().orElse(null) : null;
		}

		route = route.withPathPrefix(navigationPathPrefix);
		if (!route.equals(currentRoute)) {
			Route r = route;
			if (pathChangeOperation == PUSH && !currentRoute.getPath().equals(route.getPath())
					|| queryParamNamesWorthStatePush.stream().anyMatch(pName -> !Objects.equals(r.getQueryParam(pName), currentRoute.getQueryParam(pName)))) {
				pushNavigationHistoryState(route.toString(), false);
			} else {
				replaceNavigationHistoryState(route.toString(), false);
			}
		}
	}

	/**
	 * Pushes a new entry (URL) to the browser's navigation history without reloading the site.
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/pushState">JavaScript API</a>.
	 * <p>
	 * This method may or may not fire an {@link #onNavigationStateChange} event, depending on the value of the <code>fireEvent</code> parameter.
	 * Both cases are useful:
	 * <ul>
	 *     <li>When the user does some action that changes the state of the UI that needs to be reflected in the URL
	 *     (say changing a table filter parameter) the corresponding change will most likely already have been applied to the UI. So in such
	 *     a case it does not make sense to fire the event.</li>
	 *     <li>In the case that the user clicks on a LinkButton (or similar action) which will navigate to some completely different parts of the UI, it makes sense
	 *     that the event is triggered, so the routing to that UI can happen.</li>
	 * </ul>
	 * <p>
	 * Note that the {@link #onNavigationStateChange} event will not be fired asynchronously since it will be triggered
	 * by the client (browser!) in order to guarantee the correct sequence of events. For example, if the user presses a navigation
	 * button at the same time as this method is invoked, which one was first needs to be consistent on the client and server side.
	 *
	 * @param pathWithQueryParams May also be a complete URL, but this is not recommended, since the origin needs to stay the same.
	 * @param fireEvent           Indicates whether an onNavigationStateChange event should be fired as a reaction of this invocation.
	 */
	public void pushNavigationHistoryState(String pathWithQueryParams, boolean fireEvent) {
		changeNavigationHistoryState(pathWithQueryParams, fireEvent, NavigationHistoryOperation.PUSH);
	}

	/**
	 * Same as {@link #pushNavigationHistoryState(String, boolean)}, except that it replaces the current browser history entry (URL).
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/replaceState">JavaScript API</a>.
	 *
	 * @see #pushNavigationHistoryState(String, boolean)
	 */
	public void replaceNavigationHistoryState(String pathWithQueryParams, boolean fireEvent) {
		changeNavigationHistoryState(pathWithQueryParams, fireEvent, REPLACE);
	}

	/**
	 * @see #pushNavigationHistoryState(String, boolean)
	 */
	public void changeNavigationHistoryState(String pathWithQueryParams, boolean fireEvent, NavigationHistoryOperation operation) {
		if (Objects.equals(currentLocation.getPathname() + currentLocation.getSearch(), pathWithQueryParams)) {
			LOGGER.debug("Not sending same navigation history state as previous one to client.");
			return; // nothing to do here...
		}
		Location newLocation = currentLocation.withPathNameAndQueryParams(pathWithQueryParams);
		currentLocation = newLocation;
		queueCommand(new UiRootPanel.ChangeNavigationHistoryStateCommand(pathWithQueryParams, fireEvent, operation == NavigationHistoryOperation.PUSH), unused -> {
			// make sure this is the location after the browser applied it. The user might have clicked on the back button in the meantime.
			currentLocation = newLocation;
			skipAutoUpdateNavigationHistoryStateOnce = true;
		});
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

	public <RESULT> void queueCommand(UiCommand<RESULT> command, Consumer<RESULT> resultCallback) {
		if (CurrentSessionContext.get() != this) {
			String errorMessage = "Trying to queue a command for foreign/null SessionContext (CurrentSessionContext.get() != this)."
					+ " Please use SessionContext.runWithContext(Runnable). NOTE: The command will not get queued!";
			LOGGER.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;

		uxJacksonSerializationTemplate.doWithUxJacksonSerializers(() -> uiSession.sendCommand(new UiCommandWithResultCallback<>(command, wrappedCallback)));
	}

	public void queueCommand(UiCommand<?> command) {
		this.queueCommand(command, null);
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
		queueCommand(new UiRootPanel.RegisterTemplateCommand(id, template.createUiTemplate()));
		return new TemplateReference(template, id);
	}

	public void registerTemplates(Map<String, Template> templates) {
		registeredTemplates.putAll(templates);
		queueCommand(new UiRootPanel.RegisterTemplatesCommand(templates.entrySet().stream()
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
					if (routingMode == RoutingMode.AUTO && !skipAutoUpdateNavigationHistoryStateOnce) {
						updateNavigationHistoryState();
					}
					skipAutoUpdateNavigationHistoryStateOnce = false;
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
		queueCommand(new UiRootPanel.SetConfigCommand(config.createUiConfiguration()));
		updateSessionMessageWindows();
	}

	public void showPopupAtCurrentMousePosition(Popup popup) {
		queueCommand(new UiRootPanel.ShowPopupAtCurrentMousePositionCommand(popup.createUiReference()));
	}

	public void showPopup(Popup popup) {
		queueCommand(new UiRootPanel.ShowPopupCommand(popup.createUiReference()));
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

	public void registerClientObject(ClientObject clientObject) {
		CurrentSessionContext.throwIfNotSameAs(this);
		clientObjectsById.put(clientObject.getId(), clientObject);
	}

	public void unregisterClientObject(ClientObject clientObject) {
		clientObjectsById.remove(clientObject.getId());
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

	public void download(Resource resource) {
		download(resource, resource.getName());
	}

	public void download(Resource resource, String downloadFileName) {
		download(createResourceLink(resource), downloadFileName);
	}

	public void download(File file, String downloadFileName) {
		download(createFileLink(file), downloadFileName);
	}

	public void download(String url, String downloadFileName) {
		runWithContext(() -> queueCommand(new UiRootPanel.DownloadFileCommand(url, downloadFileName)));
	}

	public void registerBackgroundImage(String id, String image, String blurredImage) {
		queueCommand(new UiRootPanel.RegisterBackgroundImageCommand(id, image, blurredImage));
	}

	public void setBackgroundImage(String id, int animationDuration) {
		queueCommand(new UiRootPanel.SetBackgroundImageCommand(id, animationDuration));
	}

	public void showDefaultBackground(int animationDuration) {
		if (!defaultBackgroundRegistered) {
			defaultBackgroundRegistered = true;
			registerBackgroundImage(DEFAULT_BACKGROUND_NAME, DEFAULT_BACKGROUND_URL, DEFAULT_BACKGROUND_URL);
		}
		setBackgroundImage(DEFAULT_BACKGROUND_NAME, animationDuration);
	}

	public void setBackgroundColor(Color color, int animationDuration) {
		queueCommand(new UiRootPanel.SetBackgroundColorCommand(color != null ? color.toHtmlColorString() : null, animationDuration));
	}

	public void exitFullScreen() {
		queueCommand(new UiRootPanel.ExitFullScreenCommand());
	}

	public void addRootComponent(String containerElementSelector, Component component) {
		addRootPanel(containerElementSelector, component);
	}

	public void addRootPanel(String containerElementSelector, Component rootPanel) {
		queueCommand(new UiRootPanel.BuildRootPanelCommand(containerElementSelector, rootPanel.createUiReference()));
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
		queueCommand(new UiRootPanel.AddClientTokenCommand(token));
	}

	public void removeClientToken(String token) {
		getClientInfo().getClientTokens().remove(token);
		queueCommand(new UiRootPanel.RemoveClientTokenCommand(token));
	}

	public void clearClientTokens() {
		getClientInfo().getClientTokens().clear();
		queueCommand(new UiRootPanel.ClearClientTokensCommand());
	}

	public void showNotification(Notification notification, NotificationPosition position, EntranceAnimation entranceAnimation, ExitAnimation exitAnimation) {
		runWithContext(() -> {
			queueCommand(new UiRootPanel.ShowNotificationCommand(notification.createUiReference(), position.toUiNotificationPosition(), entranceAnimation.toUiEntranceAnimation(),
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
		queueCommand(new UiRootPanel.SetSessionMessageWindowsCommand(
				sessionExpiredWindow != null ? sessionExpiredWindow.createUiReference()
						: createDefaultSessionMessageWindow(getLocalized("teamapps.common.sessionExpired"), getLocalized("teamapps.common.sessionExpiredText"),
						getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createUiReference(),
				sessionErrorWindow != null ? sessionErrorWindow.createUiReference() : createDefaultSessionMessageWindow(getLocalized("teamapps.common.error"), getLocalized("teamapps.common.sessionErrorText"),
						getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createUiReference(),
				sessionTerminatedWindow != null ? sessionTerminatedWindow.createUiReference() : createDefaultSessionMessageWindow(getLocalized("teamapps.common.sessionTerminated"), getLocalized("teamapps.common.sessionTerminatedText"),
						getLocalized("teamapps.common.refresh"), getLocalized("teamapps.common.cancel")).createUiReference())
		);
	}

	public static Window createDefaultSessionMessageWindow(String title, String message, String refreshButtonCaption, String cancelButtonCaption) {
		Window window = new Window(null, title, null, 300, 300, false, false, false);
		window.setPadding(10);

		VerticalLayout verticalLayout = new VerticalLayout();

		DisplayField messageField = new DisplayField(false, false);
		messageField.setCssStyle("font-size", "110%");
		messageField.setValue(message);
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
			queueCommand(new UiRootPanel.RequestWakeLockCommand(uuid), successful -> {
				if (successful) {
					completableFuture.complete(() -> queueCommand(new UiRootPanel.ReleaseWakeLockCommand(uuid)));
				} else {
					completableFuture.completeExceptionally(new RuntimeException("Could not acquire WakeLock"));
				}
			});
		});
		return completableFuture;
	}

	public void goToUrl(String url, boolean blankPage) {
		queueCommand(new UiRootPanel.GoToUrlCommand(url, blankPage));
	}

	public void setFavicon(Icon<?, ?> icon) {
		setFavicon(resolveIcon(icon));
	}

	public void setFavicon(Resource resource) {
		setFavicon(createResourceLink(resource));
	}

	public void setFavicon(String url) {
		queueCommand(new UiRootPanel.SetFaviconCommand(url));
	}

	public void setTitle(String title) {
		queueCommand(new UiRootPanel.SetTitleCommand(title));
	}

	public void setGlobalKeyEventsEnabled(boolean unmodified, boolean modifiedWithAltKey, boolean modifiedWithCtrlKey, boolean modifiedWithMetaKey, boolean includeRepeats, boolean keyDown, boolean keyUp) {
		queueCommand(new UiRootPanel.SetGlobalKeyEventsEnabledCommand(unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats, keyDown, keyUp));
	}

	public String getSessionId() {
		return uiSession.getSessionId();
	}

	public void handleStaticEvent(UiEvent event) {
		UiEventType uiEventType = event.getUiEventType();
		switch (uiEventType) {
			case UI_ROOT_PANEL_GLOBAL_KEY_EVENT_OCCURRED: {
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
				break;
			}
			case UI_ROOT_PANEL_NAVIGATION_STATE_CHANGE: {
				UiRootPanel.NavigationStateChangeEvent e = (UiRootPanel.NavigationStateChangeEvent) event;
				Location location = Location.fromUiLocation(e.getLocation());
				this.currentLocation = location;
				onNavigationStateChange.fire(new NavigationStateChangeEvent(location, e.getTriggeredBrowserNavigation()));
				if (routingMode != RoutingMode.DISABLED) {
					route();
				}
				break;
			}
			default:
				throw new TeamAppsUiApiException(getSessionId(), uiEventType.toString());
		}
	}

	public void route() {
		Location location = getCurrentLocation();
		Route route = Route.fromLocation(location)
				.subRoute(navigationPathPrefix);

		do {
			routeHandlingDirty = false;
			// TODO #performance check efficiency and improve
			List<Router> matchingRouters = routers.stream()
					.filter(r -> r.matchesPath(route.getPath()))
					.sorted(Comparator.comparing(e -> e.getPathPrefix().length()))
					.collect(Collectors.toList());
			for (Router router : matchingRouters) {
				router.route(route.subRoute(router.getPathPrefix()));
			}
		} while (routeHandlingDirty); // routers or routeHandlers have been added during this execution!
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

	public ParamConverterProvider getRoutingParamConverterProvider() {
		return navigationParamConverterProvider;
	}

	public UiSessionStats getUiSessionStats() {
		return uiSession.getStatistics().immutableCopy();
	}

	@Override
	public String toString() {
		return "SessionContext: " + getName();
	}

	public RoutingMode getRoutingMode() {
		return routingMode;
	}

	public void setRoutingMode(RoutingMode routingMode) {
		this.routingMode = routingMode;
		if (routingMode != RoutingMode.DISABLED) {
			route();
		}
	}

	/**
	 * Same as {@link #onDestroyed} but mockable.
	 */
	public Event<UiSessionClosingReason> onDestroyed() {
		return onDestroyed;
	}

}
