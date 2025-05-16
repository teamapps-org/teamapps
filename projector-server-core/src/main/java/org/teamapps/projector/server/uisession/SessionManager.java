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
package org.teamapps.projector.server.uisession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Queues;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.commons.event.Event;
import org.teamapps.projector.clientobject.ComponentLibraryRegistry;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.core.div.Div;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.flexcontainer.VerticalLayout;
import org.teamapps.projector.component.core.linkbutton.LinkButton;
import org.teamapps.projector.component.core.window.Window;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.protocol.server.SessionClosingReason;
import org.teamapps.projector.icon.IconProvider;
import org.teamapps.projector.icon.SessionIconProvider;
import org.teamapps.projector.script.Script;
import org.teamapps.projector.server.UxServerContext;
import org.teamapps.projector.server.config.ProjectorConfiguration;
import org.teamapps.projector.server.core.UploadManager;
import org.teamapps.projector.server.threading.SequentialExecutorFactory;
import org.teamapps.projector.server.uisession.statistics.SessionStatsUpdatedEventData;
import org.teamapps.projector.server.webcontroller.WebController;
import org.teamapps.projector.session.ClientInfo;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.session.navigation.ParameterConverterProvider;
import org.teamapps.projector.session.uisession.CommandWithResultCallback;
import org.teamapps.projector.session.uisession.UiSession;
import org.teamapps.projector.session.uisession.UiSessionState;
import org.teamapps.projector.session.uisession.stats.UiSessionStatistics;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.teamapps.projector.common.ProjectorVersion.TEAMAPPS_DEV_SERVER_VERSION;
import static org.teamapps.projector.common.ProjectorVersion.TEAMAPPS_VERSION;
import static org.teamapps.projector.session.uisession.UiSessionState.*;

/**
 * Keeps track of all UI sessions, both the {@link UiSession} and {@link SessionContext} part.
 * <ul>
 *     <li>Instantiates {@link UiSession UiSessions} and {@link SessionContext SessionContexts}</li>
 *     <li>Provides sessions by ID</li>
 *     <li>Tracks and handles activity timeouts</li>
 *     <li>Tracks and handles session timeouts</li>
 *     <li></li>
 * </ul>
 * <p>
 * It takes care of the removal of timed-out sessions. The "last used" information is updated every time a session is retrieved.
 */
public class SessionManager implements HttpSessionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String TEAMAPPS_VERSION_REFRESH_PARAMETER = "teamappsRefresh"; // keep in-sync with JavaScript!!!

	public final Event<SessionStatsUpdatedEventData> onStatsUpdated = new Event<>();

	private final ScheduledExecutorService houseKeepingScheduledExecutor;
	private final ObjectMapper objectMapper;
	private final ProjectorConfiguration config;
	private final ComponentLibraryRegistry componentLibraryRegistry;

	private final Map<String, SessionPair> uiSessionsById = new ConcurrentHashMap<>();
	private final Deque<UiSessionStatistics> closedSessionsStatistics = Queues.synchronizedDeque(new ArrayDeque<>());

	private final SequentialExecutorFactory sessionExecutorFactory;
	private final WebController webController;
	private final IconProvider iconProvider;
	private final UxServerContext uxServerContext;

	public SessionManager(ProjectorConfiguration config,
						  ObjectMapper objectMapper,
						  SequentialExecutorFactory sessionExecutorFactory,
						  WebController webController,
						  IconProvider iconProvider,
						  UploadManager uploadManager,
						  ComponentLibraryRegistry componentLibraryRegistry) {
		this.config = config;
		this.componentLibraryRegistry = componentLibraryRegistry;
		if (config.getKeepaliveMessageIntervalMillis() >= config.getUiSessionInactivityTimeoutMillis() / 2) {
			LOGGER.error("keepaliveMessageIntervalMillis should be less than uiSessionInactivityTimeoutMillis / 2!");
		}
		if (config.getUiSessionPreInactivityPingMillis() > config.getUiSessionInactivityTimeoutMillis() / 2) {
			LOGGER.error("uiSessionPreInactivityPingMillis should not be larger than uiSessionInactivityTimeoutMillis / 2!");
		}
		if (config.getUiSessionInactivityTimeoutMillis() > config.getUiSessionTimeoutMillis()) {
			LOGGER.error("uiSessionInactivityTimeoutMillis must not be greater than uiSessionTimeoutMillis!");
		}
		this.objectMapper = objectMapper;
		this.houseKeepingScheduledExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
			Thread thread = new Thread(runnable);
			thread.setName("TeamAppsUiSessionManager.houseKeeping");
			thread.setDaemon(true);
			return thread;
		});

		long sessionStateHouseKeepingInterval = Math.min(config.getUiSessionPreInactivityPingMillis() / 2, config.getUiSessionInactivityTimeoutMillis() / 4);
		LOGGER.info("sessionStateHouseKeepingInterval: {}ms", sessionStateHouseKeepingInterval);
		this.houseKeepingScheduledExecutor.scheduleAtFixedRate(
				() -> {
					try {
						this.updateSessionStates();
					} catch (Exception e) {
						LOGGER.error("Exception while updating session states!", e);
					}
				},
				sessionStateHouseKeepingInterval, sessionStateHouseKeepingInterval, TimeUnit.MILLISECONDS
		);
		this.houseKeepingScheduledExecutor.scheduleAtFixedRate(
				() -> {
					try {
						uiSessionsById.values().forEach(s -> s.getUiSession().updateStats());
						onStatsUpdated.fire(new SessionStatsUpdatedEventData(getAllSessions(), getClosedSessionsStatistics()));
					} catch (Exception e) {
						LOGGER.error("Exception while flushing stats!", e);
					}
				},
				10, 10, TimeUnit.SECONDS
		);

		this.sessionExecutorFactory = sessionExecutorFactory;
		this.webController = webController;
		this.iconProvider = iconProvider;
		this.uxServerContext = uploadManager::getUploadedFile;
	}

	public UiSessionImpl getUiSessionById(String sessionId) {
		SessionPair sessionPair = uiSessionsById.get(sessionId);
		return sessionPair != null ? sessionPair.getUiSession() : null;
	}

	public SessionContext getSessionContextById(String sessionId) {
		SessionPair sessionPair = uiSessionsById.get(sessionId);
		return sessionPair != null ? sessionPair.getSessionContext() : null;
	}

	public int getNumberOfSessions() {
		return uiSessionsById.size();
	}

	public int getNumberOfSessionsByState(UiSessionState state) {
		return (int) uiSessionsById.values().stream()
				.filter(sessionPair -> sessionPair.getSessionContext().getState() == state)
				.count();
	}

	public List<SessionPair> getAllSessions() {
		return List.copyOf(uiSessionsById.values());
	}

	public int getBufferedCommandsCount() {
		return uiSessionsById.values().stream()
				.map(SessionPair::getUiSession)
				.mapToInt(uiSession -> uiSession.getClientBackPressureInfo().bufferedCommandsCount())
				.sum();
	}

	public int getUnconsumedCommandsCount() {
		return uiSessionsById.values().stream()
				.map(SessionPair::getUiSession)
				.mapToInt(uiSession -> uiSession.getClientBackPressureInfo().unconsumedCommandsCount())
				.sum();
	}

	public int getNumberOfAvailableClosedSessionStatistics() {
		return closedSessionsStatistics.size();
	}

	public List<UiSessionStatistics> getClosedSessionsStatistics() {
		synchronized (closedSessionsStatistics) {
			return List.copyOf(closedSessionsStatistics);
		}
	}

	public UiSessionImpl initSession(
			String sessionId,
			ClientInfo clientInfo,
			HttpSession httpSession,
			int maxRequestedCommandId,
			MessageSender messageSender
	) {
		LOGGER.trace("initSession: sessionId = [" + sessionId + "], clientInfo = [" + clientInfo + "], "
					 + "maxRequestedCommandId = [" + maxRequestedCommandId + "], messageSender = [" + messageSender + "]");

		UiSessionImpl uiSession = new UiSessionImpl(sessionId, System.currentTimeMillis(), config, objectMapper, messageSender);
		SessionContext sessionContext = createSessionContext(uiSession, clientInfo, httpSession, config.getNavigationPathPrefix());
		SessionContext.InternalUiSessionListener sessionUiSessionListenerImpl = sessionContext.getInternalApi();
		uiSession.addSessionListener(new UiSessionListener() {
			@Override
			public void handleEvent(String sessionId, String libraryId, String clientObjectId, String name, JsonWrapper eventObject) {
				sessionUiSessionListenerImpl.handleEvent(sessionId, libraryId, clientObjectId, name, eventObject);
			}

			@Override
			public void handleQuery(String sessionId, String libraryId, String clientObjectId, String name, List<JsonWrapper> params, Consumer<Object> resultCallback) {
				sessionUiSessionListenerImpl.handleQuery(sessionId, libraryId, clientObjectId, name, params, resultCallback);
			}

			@Override
			public void onStateChanged(String sessionId, UiSessionState state) {
				if (state == CLOSED) {
					uiSessionsById.remove(uiSession.getSessionId());
					closedSessionsStatistics.addLast(uiSession.getStatistics().immutable());
					while (closedSessionsStatistics.size() > 10_000) {
						closedSessionsStatistics.removeFirst();
					}
				}
				sessionUiSessionListenerImpl.onStateChanged(sessionId, state);
			}

			@Override
			public void onClosed(String sessionId, SessionClosingReason reason) {
				sessionUiSessionListenerImpl.onClosed(sessionId, reason);
			}
		});

		uiSession.handleCommandRequest(maxRequestedCommandId, null);

		boolean wrongTeamappsVersion = clientInfo.getTeamAppsVersion() == null
									   || (!Objects.equals(clientInfo.getTeamAppsVersion(), TEAMAPPS_VERSION) && !Objects.equals(clientInfo.getTeamAppsVersion(), TEAMAPPS_DEV_SERVER_VERSION));
		boolean hasTeamAppsRefreshParameter = clientInfo.getClientParameters().containsKey(TEAMAPPS_VERSION_REFRESH_PARAMETER);
		if (wrongTeamappsVersion) {
			LOGGER.info("Wrong TeamApps client version {} in session {}! Expected: {}!", clientInfo.getTeamAppsVersion(), sessionId, TEAMAPPS_VERSION);
			if (!hasTeamAppsRefreshParameter) {
				LOGGER.info("Sending redirect with {} parameter.", TEAMAPPS_VERSION_REFRESH_PARAMETER);
				String separator = StringUtils.isNotEmpty(clientInfo.getLocation().getQuery()) ? "&" : "?";
				String url = clientInfo.getLocation().toString() + separator + TEAMAPPS_VERSION_REFRESH_PARAMETER + "=" + System.currentTimeMillis();
				uiSession.sendCommand(new CommandWithResultCallback(null, null, "goToUrl", url, false));
			}
			uiSession.close(SessionClosingReason.WRONG_TEAMAPPS_VERSION);
			return uiSession;
		}

		uiSessionsById.put(sessionId, new SessionPair(uiSession, sessionContext));

		uiSession.sendInitOk();

		try {
			// TODO make non-blocking when exception handling (and thereby session invalidation) is changed
			sessionContext.runWithContext(() -> {
				webController.onSessionStart(sessionContext);
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}

		return uiSession;
	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
	}

	public void updateSessionStates() {
		long now = System.currentTimeMillis();
		Map<UiSessionState, List<UiSessionImpl>> sessionsByActivity;
		List<UiSessionImpl> uiSessionsToClose;
		long nearlyInactiveTimeout = config.getUiSessionInactivityTimeoutMillis() - config.getUiSessionPreInactivityPingMillis();
		sessionsByActivity = uiSessionsById.values().stream()
				.map(SessionPair::getUiSession)
				.collect(Collectors.groupingBy(session -> {
					long timeSinceLastMessage = now - session.getTimestampOfLastMessageFromClient();
					return timeSinceLastMessage > config.getUiSessionInactivityTimeoutMillis() ? INACTIVE
							: timeSinceLastMessage > nearlyInactiveTimeout ? NEARLY_INACTIVE
							: ACTIVE;
				}));
		uiSessionsToClose = uiSessionsById.values().stream()
				.map(SessionPair::getUiSession)
				.filter(session -> now - session.getTimestampOfLastMessageFromClient() > config.getUiSessionTimeoutMillis())
				.collect(Collectors.toList());
		for (UiSessionImpl inactiveSession : sessionsByActivity.getOrDefault(INACTIVE, List.of())) {
			if (inactiveSession.getState() != INACTIVE) {
				LOGGER.info("Marking session inactive: {} ({})", inactiveSession.getName(), inactiveSession.getSessionId());
				inactiveSession.setInactive();
			}
		}
		for (UiSessionImpl criticalSession : sessionsByActivity.getOrDefault(NEARLY_INACTIVE, List.of())) {
			if (criticalSession.getState() != NEARLY_INACTIVE) {
				LOGGER.info("Marking session nearly inactive and sending PING to client: {} ({})", criticalSession.getName(), criticalSession.getSessionId());
				criticalSession.setNearlyInactive();
				criticalSession.ping();
			}
		}
		for (UiSessionImpl activeSession : sessionsByActivity.getOrDefault(ACTIVE, List.of())) {
			if (activeSession.getState() != ACTIVE) {
				LOGGER.info("Marking session active: {} ({})", activeSession.getName(), activeSession.getSessionId());
				activeSession.setActive();
			}
		}
		for (UiSessionImpl uiSessionToClose : uiSessionsToClose) {
			LOGGER.info("Closing session: {} ({})", uiSessionToClose.getName(), uiSessionToClose.getSessionId());
			uiSessionToClose.close(SessionClosingReason.SESSION_TIMEOUT);
		}
	}

	public void destroy() {
		houseKeepingScheduledExecutor.shutdown();
	}

	public SessionContext createSessionContext(UiSessionImpl uiSession, ClientInfo clientInfo, HttpSession httpSession, String navigationPathPrefix) {
		SessionContext sessionContext = new SessionContext(
				uiSession,
				sessionExecutorFactory.createExecutor(uiSession.getSessionId()),
				clientInfo,
				httpSession,
				uxServerContext,
				new SessionIconProvider(iconProvider),
				componentLibraryRegistry,
				objectMapper,
				navigationPathPrefix,
				new ParameterConverterProvider()
		);

		sessionContext.runWithContext(() -> {
			var sessionExpiredWindow = createDefaultSessionMessageWindow(sessionContext.getLocalized("teamapps.common.sessionExpired"), sessionContext.getLocalized("teamapps.common.sessionExpiredText"), sessionContext.getLocalized("teamapps.common.refresh"), sessionContext.getLocalized("teamapps.common.cancel"));
			var	sessionErrorWindow = createDefaultSessionMessageWindow(sessionContext.getLocalized("teamapps.common.error"), sessionContext.getLocalized("teamapps.common.sessionErrorText"), sessionContext.getLocalized("teamapps.common.refresh"), sessionContext.getLocalized("teamapps.common.cancel"));
			var	sessionTerminatedWindow = createDefaultSessionMessageWindow(sessionContext.getLocalized("teamapps.common.sessionTerminated"), sessionContext.getLocalized("teamapps.common.sessionTerminatedText"), sessionContext.getLocalized("teamapps.common.refresh"), sessionContext.getLocalized("teamapps.common.cancel"));
			sessionContext.setSessionMessages(sessionExpiredWindow, sessionErrorWindow, sessionTerminatedWindow);
		});
		return sessionContext;
	}

	public static Window createDefaultSessionMessageWindow(String title, String message, String refreshButtonCaption, String cancelButtonCaption) {
		Window window = new Window(null, title, null, 300, 300, true, true, true);
		window.setPadding(10);

		VerticalLayout verticalLayout = new VerticalLayout();

		Div messageField = new Div(message);
		messageField.setCssStyle("font-size", "110%");
		verticalLayout.addComponentFillRemaining(messageField);

		Button refreshButton = new Button(null, refreshButtonCaption);
		refreshButton.setCssStyle("margin", "10px 0");
		refreshButton.setCssStyle(".DtoButton", "background-color", RgbaColor.MATERIAL_BLUE_600.toHtmlColorString());
		refreshButton.setCssStyle(".DtoButton", "color", RgbaColor.WHITE.toHtmlColorString());
		refreshButton.setCssStyle(".DtoButton", "font-size", "120%");
		refreshButton.setCssStyle(".DtoButton", "height", "50px");
		refreshButton.setOnClickClientSideEventHandler(new Script("export function refresh() {window.location.reload()}"), "refresh", false);
		verticalLayout.addComponentAutoSize(refreshButton);

		if (cancelButtonCaption != null) {
			LinkButton cancelLink = new LinkButton(cancelButtonCaption);
			cancelLink.setCssStyle("text-align", "center");
			// TODO cancelLink.setOnClickJavaScript("context.getClientObjectById(\"" + window.getId() + "\").close();");
			verticalLayout.addComponentAutoSize(cancelLink);
		}

		window.setContent(verticalLayout);
		window.enableAutoHeight();
		return window;
	}

}
