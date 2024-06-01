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
package org.teamapps.projector.session;

import com.devskiller.friendly_id.FriendlyId;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.ULocale;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.commons.event.Event;
import org.teamapps.commons.util.ExceptionUtil;
import org.teamapps.dto.protocol.server.*;
import org.teamapps.icons.Icon;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.projector.annotation.ClientObjectTypeName;
import org.teamapps.projector.clientobject.*;
import org.teamapps.projector.clientobject.ComponentLibraryRegistry.ClientObjectLibraryInfo;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.DtoGlobals;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.i18n.ResourceBundleTranslationProvider;
import org.teamapps.projector.i18n.TranslationProvider;
import org.teamapps.projector.resource.Resource;
import org.teamapps.projector.server.UxServerContext;
import org.teamapps.projector.session.config.DateTimeFormatDescriptor;
import org.teamapps.projector.session.event.KeyboardEvent;
import org.teamapps.projector.session.uisession.ClientBackPressureInfo;
import org.teamapps.projector.session.uisession.CommandWithResultCallback;
import org.teamapps.projector.session.uisession.UiSession;
import org.teamapps.projector.session.uisession.UiSessionState;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

public class SessionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ExecutorService sessionExecutor;

	public final ProjectorEvent<KeyboardEvent> onGlobalKeyEventOccurred = new ProjectorEvent<>(hasListeners -> toggleStaticEvent(null, "globalKeyEventOccurred", hasListeners));
	public final ProjectorEvent<NavigationStateChangeEvent> onNavigationStateChange = new ProjectorEvent<>(hasListeners -> toggleStaticEvent(null, "navigationStateChange", hasListeners));
	public final ProjectorEvent<SessionActivityState> onActivityStateChanged = new ProjectorEvent<>();
	public final Event<SessionClosingReason> onDestroyed = new Event<>();

	/**
	 * Decorators around all executions inside this SessionContext. These will be invoked when the Thread is already bound to the SessionContext, so SessionContext.current() will
	 * return this instance.
	 */
	public final ExecutionDecoratorStack executionDecorators = new ExecutionDecoratorStack();

	private final UiSession uiSession;
	private UiSessionState state = UiSessionState.ACTIVE;

	private final ClientInfo clientInfo;
	private URL currentLocation;

	private final HttpSession httpSession;
	private final UxServerContext serverContext;
	private final SessionIconProvider iconProvider;
	private final ObjectMapper objectMapper;

	private final BidiMap<String, ClientObject> clientObjectsById = new DualHashBidiMap<>();
	private final HashMap<ClientObject, ClientObjectChannel> channelsByClientObject = new HashMap<>();
	private final Set<ClientObject> renderingClientObjects = new HashSet<>();
	private final Set<ClientObject> renderedClientObjects = new HashSet<>();

	private final SessionContextResourceManager sessionResourceProvider;

	private TranslationProvider translationProvider;

	/**
	 * Allows for replacing icons with other icons, globally. This comes handy if components use icons that are not compatible
	 * with the general appearance of the application.
	 */
	private Function<Icon<?, ?>, Icon<?, ?>> iconTransformer = Function.identity();

	private final InternalUiSessionListener uiSessionListener = new InternalUiSessionListener();

	private final Set<ClientObjectLibrary> loadedComponentLibraries = new HashSet<>();
	private final ComponentLibraryRegistry componentLibraryRegistry;

	private ULocale locale = ULocale.US;
	private DateTimeFormatDescriptor dateFormat = DateTimeFormatDescriptor.forDate(DateTimeFormatDescriptor.FullLongMediumShortType.SHORT);
	private DateTimeFormatDescriptor timeFormat = DateTimeFormatDescriptor.forTime(DateTimeFormatDescriptor.FullLongMediumShortType.SHORT);
	private ZoneId timeZone = ZoneId.of("Europe/Berlin");
	private DayOfWeek firstDayOfWeek; // null == determine by locale
	private boolean optimizedForTouch = false;
	private String iconBasePath = "/icons";
	private StylingTheme theme = StylingTheme.DEFAULT;

	public SessionContext(UiSession uiSession,
						  ExecutorService sessionExecutor,
						  ClientInfo clientInfo,
						  HttpSession httpSession,
						  UxServerContext serverContext,
						  SessionIconProvider iconProvider,
						  ComponentLibraryRegistry componentLibraryRegistry,
						  ObjectMapper objectMapper) {
		this.sessionExecutor = sessionExecutor;
		this.uiSession = uiSession;
		this.httpSession = httpSession;
		this.serverContext = serverContext;
		this.iconProvider = iconProvider;
		this.objectMapper = objectMapper;
		this.translationProvider = new ResourceBundleTranslationProvider("org.teamapps.ux.i18n.DefaultCaptions", Locale.ENGLISH);
		this.sessionResourceProvider = new SessionContextResourceManager(uiSession.getSessionId());
		this.componentLibraryRegistry = componentLibraryRegistry;

		this.clientInfo = clientInfo;
		this.currentLocation = clientInfo.getLocation();
		boolean optimizedForTouch = false;
		StylingTheme theme = StylingTheme.DEFAULT;
		if (clientInfo.isMobileDevice()) {
			optimizedForTouch = true;
			theme = StylingTheme.MODERN;
		}
		ULocale locale1 = ULocale.forLanguageTag(clientInfo.getPreferredLanguageIso());
		ZoneId timeZone1 = ZoneId.of(clientInfo.getTimeZone());
		this.locale = locale1;
		this.timeZone = timeZone1;
		this.theme = theme;
		this.optimizedForTouch = optimizedForTouch;
	}

	public class InternalUiSessionListener {
		public void handleEvent(String sessionId, String libraryId, String clientObjectId, String name, JsonWrapper eventObject) {
			runWithContext(() -> {
				if (clientObjectId != null) {
					ClientObject clientObject = clientObjectsById.get(clientObjectId);
					if (clientObject != null) {
						clientObject.handleEvent(name, eventObject);
					} else {
						throw new ProjectorComponentNotFoundException(sessionId, clientObjectId);
					}
				} else {
					handleStaticEvent(libraryId, name, eventObject);
				}
			});
		}

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

		public void onStateChanged(String sessionId, UiSessionState state) {
			runWithContext(() -> {
				boolean activityStateChanged = SessionContext.this.state.isActive() != state.isActive();
				SessionContext.this.state = state;
				if (activityStateChanged) {
					onActivityStateChanged.fire(new SessionActivityState(state.isActive()));
				}
			});
		}

		public void onClosed(String sessionId, SessionClosingReason reason) {
			runWithContext(() -> {
				onDestroyed.fireIgnoringExceptions(reason);
				// Enqueue this at the end, so all onDestroyed handlers have already been executed before disabling any more executions inside the context!
				sessionExecutor.submit(sessionExecutor::shutdown);
			});
		}
	}

	public Event<SessionClosingReason> onDestroyed() {
		return onDestroyed;
	}

	public void pushNavigationState(String relativeUrl) {
		sendStaticCommand(null, "pushHistoryState", relativeUrl);
	}

	public void navigateBack(int steps) {
		navigateForward(-steps);
	}

	public void navigateForward(int steps) {
		sendStaticCommand(null, "navigateForward", steps);
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

	public void setIconTransformer(Function<Icon<?, ?>, Icon<?, ?>> iconTransformer) {
		this.iconTransformer = iconTransformer;
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

	public void sendStaticCommand(Class<? extends ClientObject> clientObjectClass, String name, Object... params) {
		sendStaticCommandWithCallback(clientObjectClass, name, params, null);
	}

	/**
	 * Invokes the given command on the client side and calls the resultCallback after it has been executed on the client side.
	 * Note that the callback is also going to be called if the client side does not explicitly return a result (in which case
	 * the callback is invoked with null).
	 *
	 * @param resultCallback invoked after the command has been executed on the client side.
	 */
	public <RESULT> void sendStaticCommandWithCallback(Class<? extends ClientObject> clientObjectClass, String name, Object[] params, Consumer<RESULT> resultCallback) {
		CurrentSessionContext.throwIfNotSameAs(this);

		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;
		String componentLibraryUuid = getComponentLibraryUuidForClientObjectClass(clientObjectClass, true);

		uiSession.sendCommand(new CommandWithResultCallback(componentLibraryUuid, null, name, params, (Consumer) wrappedCallback));
	}

	private String getComponentLibraryUuidForClientObjectClass(Class<? extends ClientObject> clientObjectClass, boolean loadIfNecessary) {
		String componentLibraryUuid;
		if (clientObjectClass == null) {
			componentLibraryUuid = null;
		} else {
			ClientObjectLibraryInfo libraryInfo = componentLibraryRegistry.getComponentLibraryForClientObjectClass(clientObjectClass);
			if (!loadedComponentLibraries.contains(libraryInfo.clientObjectLibrary()) && loadIfNecessary) {
				uiSession.sendReliableServerMessage(new REGISTER_LIB(libraryInfo.uuid(), libraryInfo.mainJsUrl(), libraryInfo.mainCssUrl()));
				loadedComponentLibraries.add(libraryInfo.clientObjectLibrary());
			}
			componentLibraryUuid = libraryInfo.uuid();
		}
		return componentLibraryUuid;
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
							.createWrappedRunnable(() -> resultHolder[0] = ExceptionUtil.runWithSoftenedExceptions(callable))
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
		icon = iconTransformer.apply(icon);
		return getIconBasePath() + "/" + iconProvider.encodeIcon((Icon) icon, true);
	}

	public ClientObjectChannel registerClientObject(ClientObject clientObject) {
		Objects.requireNonNull(clientObject, "clientObject must not be null!");

		if (channelsByClientObject.containsKey(clientObject)) {
			LOGGER.warn("Attempt to register client object multiple times! {}", clientObject);
			return channelsByClientObject.get(clientObject);
		}

		String clientId = clientObjectsById.inverseBidiMap().computeIfAbsent(clientObject, co -> co.getClass().getSimpleName() + "-" + FriendlyId.createFriendlyId());

		ClientObjectChannel clientObjectChannel = new ClientObjectChannelImpl(clientObject, clientId);

		channelsByClientObject.put(clientObject, clientObjectChannel);
		return clientObjectChannel;
	}

	private void renderClientObject(ClientObject clientObject) {
		CurrentSessionContext.throwIfNotSameAs(this);

		ClientObjectChannel clientObjectChannel = channelsByClientObject.get(clientObject);

		if (clientObjectChannel == null) {
			throw new IllegalStateException("Cannot render unregistered client object! Please call registerClientObject first. If you are not the developer of this component (" + clientObject.getClass() + "), please file a bug report to them.");
		}

		clientObjectChannel.forceRender();
	}

	/*package-private*/ String ensureRenderedAndGetId(ClientObject clientObject) {
		CurrentSessionContext.throwIfNotSameAs(this);

		renderClientObject(clientObject);
		return clientObjectsById.getKey(clientObject);
	}

	private <RESULT> void sendCommandInternal(String clientObjectId, String name, Object[] params, Consumer<RESULT> resultCallback) {
		Consumer<RESULT> wrappedCallback = resultCallback != null ? result -> this.runWithContext(() -> resultCallback.accept(result)) : null;
		uiSession.sendCommand(new CommandWithResultCallback(null, clientObjectId, name, params, (Consumer) wrappedCallback));
	}

	public void toggleStaticEvent(Class<? extends ClientObject> representativeLibraryClass, String eventName, boolean enabled) {
		CurrentSessionContext.throwIfNotSameAs(this);
		String libraryUuid = getComponentLibraryUuidForClientObjectClass(representativeLibraryClass, true);
		uiSession.sendReliableServerMessage(new TOGGLE_EVT(libraryUuid, null, eventName, enabled));
	}

	public void destroyClientObject(ClientObject clientObject) {
		String id = clientObjectsById.getKey(clientObject);
		if (id != null) {
			uiSession.sendReliableServerMessage(new DESTROY_OBJ(id));
			clientObjectsById.remove(id);
			renderingClientObjects.remove(clientObject);
			renderedClientObjects.remove(clientObject);
		}
	}

	public ClientObject getClientObjectById(String id) {
		return clientObjectsById.get(id);
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

	public void download(Resource resource, String downloadFileName) {
		download(createResourceLink(resource), downloadFileName);
	}

	public void download(File file, String downloadFileName) {
		download(createFileLink(file), downloadFileName);
	}

	public void download(String url, String downloadFileName) {
		runWithContext(() -> sendStaticCommand(null, "downloadFile", url, downloadFileName));
	}

	public void exitFullScreen() {
		sendStaticCommand(null, "exitFullScreen");
	}

	public void addComponent(String containerElementSelector, Component component) {
		sendStaticCommand(null, "addRootComponent", containerElementSelector, component);
	}

	public void addRootComponent(Component component) {
		addComponent("body", component);
	}

	public void setSessionMessages(Showable sessionExpiredShowable, Showable sessionErrorShowable, Showable sessionTerminatedShowable) {
		renderClientObject(sessionExpiredShowable);
		renderClientObject(sessionExpiredShowable);
		renderClientObject(sessionExpiredShowable);
	}


	public CompletableFuture<WakeLock> requestWakeLock() {
		String uuid = UUID.randomUUID().toString();
		CompletableFuture<WakeLock> completableFuture = new CompletableFuture<>();
		runWithContext(() -> {
			this.<Boolean>sendStaticCommandWithCallback(null, "wakeLock", new Object[]{uuid}, successful -> {
				if (successful) {
					completableFuture.complete(() -> sendStaticCommand(null, "releaseWakeLock", uuid));
				} else {
					completableFuture.completeExceptionally(new RuntimeException("Could not acquire WakeLock"));
				}
			});
		});
		return completableFuture;
	}

	public void goToUrl(String url, boolean blankPage) {
		sendStaticCommand(null, "goToUrlCommand", "goToUrl", url, blankPage);
	}

	public void setFavicon(Icon<?, ?> icon) {
		setFavicon(resolveIcon(icon));
	}

	public void setFavicon(Resource resource) {
		setFavicon(createResourceLink(resource));
	}

	public void setFavicon(String url) {
		sendStaticCommand(null, "setFavicon", url);
	}

	public void setTitle(String title) {
		sendStaticCommand(null, "SetTitle", title);
	}

	public void configureGlobalKeyEvents(boolean unmodified, boolean modifiedWithAltKey, boolean modifiedWithCtrlKey, boolean modifiedWithMetaKey, boolean includeRepeats, boolean keyDown, boolean keyUp) {
		sendStaticCommand(null, "configureGlobalKeyboardEvents", new Object[]{unmodified, modifiedWithAltKey, modifiedWithCtrlKey, modifiedWithMetaKey, includeRepeats, keyDown, keyUp});
	}

	public String getSessionId() {
		return uiSession.getSessionId();
	}

	public void handleStaticEvent(String libraryId, String name, JsonWrapper eventObject) {
		LOGGER.info("static event: {}, {}, {}", libraryId, name, eventObject);
		if (libraryId == null && "globalKeyEventOccurred".equals(name)) {
			DtoGlobals.GlobalKeyEventOccurredEventWrapper e = eventObject.as(DtoGlobals.GlobalKeyEventOccurredEventWrapper::new);
			String clientObjectId = e.getSourceComponentId();
			onGlobalKeyEventOccurred.fire(new KeyboardEvent(
					e.getEventType(),
					(e.getSourceComponentId() != null ? (Component) clientObjectsById.get(clientObjectId) : null),
					e.getCode(),
					e.isComposing(),
					e.getKey(),
					e.getLocale(),
					e.getLocation(),
					e.isRepeat(),
					e.isAltKey(),
					e.isCtrlKey(),
					e.isShiftKey(),
					e.isMetaKey()
			));
		} else if (libraryId == null && "navigationStateChange".equals(name)) {
			DtoGlobals.NavigationStateChangeEventWrapper e = eventObject.as(DtoGlobals.NavigationStateChangeEventWrapper::new);
			URL url = ExceptionUtil.runWithSoftenedExceptions(() -> URI.create(e.getLocation()).toURL());
			this.currentLocation = url;
			onNavigationStateChange.fire(new NavigationStateChangeEvent(url, e.isTriggeredByUser()));
		} else {
			// TODO static event handlers on library level...
			LOGGER.error("TODO static event handling!");
		}
	}

	public URL getCurrentLocation() {
		return currentLocation;
	}

	public void setName(String name) {
		uiSession.setName(name);
	}

	public String getName() {
		return uiSession.getName();
	}

	private static DayOfWeek determineFirstDayOfWeek(ULocale locale) {
		return DayOfWeek.of(GregorianCalendar.getInstance(locale).getFirstDayOfWeek()).minus(1);
	}

	public Locale getLocale() {
		return locale.toLocale();
	}

	public ULocale getULocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		setULocale(ULocale.forLocale(locale));
	}

	public void setULocale(ULocale locale) {
		this.locale = locale;
	}

	public DateTimeFormatDescriptor getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateTimeFormatDescriptor dateFormat) {
		this.dateFormat = dateFormat;
	}

	public DateTimeFormatDescriptor getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(DateTimeFormatDescriptor timeFormat) {
		this.timeFormat = timeFormat;
	}

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}

	public DayOfWeek getFirstDayOfWeek() {
		return firstDayOfWeek != null ? firstDayOfWeek : determineFirstDayOfWeek(locale);
	}

	public void setFirstDayOfWeek(DayOfWeek firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
	}

	public String getIconBasePath() {
		return iconBasePath;
	}

	public void setIconBasePath(String iconBasePath) {
		this.iconBasePath = iconBasePath;
	}

	public <T> T getInternalApi() {
		return (T) uiSessionListener;
	}

	private class ClientObjectChannelImpl implements ClientObjectChannel {

		private final Set<String> enabledEventNames;
		private final ClientObject clientObject;
		private final String clientId;

		public ClientObjectChannelImpl(ClientObject clientObject, String clientId) {
			this.clientObject = clientObject;
			this.clientId = clientId;
			enabledEventNames = new HashSet<>();
		}

		@Override
		public void forceRender() {
			if (!renderedClientObjects.contains(clientObject) && !renderingClientObjects.contains(clientObject)) {
				renderingClientObjects.add(clientObject);
				try {
					String libraryUuid = getComponentLibraryUuidForClientObjectClass(clientObject.getClass(), true);

					// TODO cache for performance
					ClientObjectTypeName typeNameAnnotation = clientObject.getClass().getAnnotation(ClientObjectTypeName.class);
					String typeName = typeNameAnnotation != null ? typeNameAnnotation.value() : clientObject.getClass().getSimpleName();

					uiSession.sendReliableServerMessage(new CREATE_OBJ(libraryUuid, typeName, clientId, clientObject.createConfig(), List.copyOf(enabledEventNames)));
				} finally {
					renderingClientObjects.remove(clientObject);
				}
				renderedClientObjects.add(clientObject);
			}
		}

		@Override
		public boolean isRendered() {
			return SessionContext.this.isRendered(clientObject);
		}

		@Override
		public boolean sendCommandIfRendered(String name, Object[] params, Consumer<JsonWrapper> resultHandler) {
			CurrentSessionContext.throwIfNotSameAs(SessionContext.this);

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
				runWithContext(() -> sendCommandInternal(clientObjectsById.getKey(clientObject), name, params, null), true);
				return true;
			} else if (isRendered()) {
				sendCommandInternal(clientObjectsById.getKey(clientObject), name, params, null);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void toggleEvent(String eventName, boolean enabled) {
			CurrentSessionContext.throwIfNotSameAs(SessionContext.this);
			enabledEventNames.add(eventName);
			if (!renderedClientObjects.contains(clientObject)) {
				return; // will be registered when rendering, anyway.
			}
			String libraryUuid = getComponentLibraryUuidForClientObjectClass(clientObject.getClass(), true);
			uiSession.sendReliableServerMessage(new TOGGLE_EVT(libraryUuid, clientObjectsById.getKey(clientObject), eventName, enabled));
		}
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
