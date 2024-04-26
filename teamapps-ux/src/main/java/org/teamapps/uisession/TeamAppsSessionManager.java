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
package org.teamapps.uisession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Queues;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.core.TeamAppsUploadManager;
import org.teamapps.dto.DtoGlobals;
import org.teamapps.dto.protocol.server.SessionClosingReason;
import org.teamapps.event.Event;
import org.teamapps.icons.IconProvider;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.statistics.SessionStatsUpdatedEventData;
import org.teamapps.uisession.statistics.UiSessionStats;
import org.teamapps.util.threading.SequentialExecutorFactory;
import org.teamapps.ux.component.ComponentLibraryRegistry;
import org.teamapps.ux.component.div.Div;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.linkbutton.LinkButton;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.session.ClientInfo;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.webcontroller.WebController;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.teamapps.common.TeamAppsVersion.TEAMAPPS_DEV_SERVER_VERSION;
import static org.teamapps.common.TeamAppsVersion.TEAMAPPS_VERSION;
import static org.teamapps.uisession.UiSessionState.*;

/**
 * Implements a cache for {@link UiSession} instances.
 * <p>
 * It takes care of the removal of timed-out sessions. The "last used" information is updated every time a session is retrieved.
 */
public class TeamAppsSessionManager implements HttpSessionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String TEAMAPPS_VERSION_REFRESH_PARAMETER = "teamappsRefresh"; // keep in-sync with JavaScript!!!

	public final Event<SessionStatsUpdatedEventData> onStatsUpdated = new Event<>();

	private final ScheduledExecutorService houseKeepingScheduledExecutor;
	private final ObjectMapper objectMapper;
	private final TeamAppsConfiguration config;
	private final ComponentLibraryRegistry componentLibraryRegistry;

	private final Map<String, SessionPair> sessionsById = new ConcurrentHashMap<>();
	private final Deque<UiSessionStats> closedSessionsStatistics = Queues.synchronizedDeque(new ArrayDeque<>());

	private final SequentialExecutorFactory sessionExecutorFactory;
	private final WebController webController;
	private final IconProvider iconProvider;
	private final UxServerContext uxServerContext;

	public TeamAppsSessionManager(TeamAppsConfiguration config,
								  ObjectMapper objectMapper,
								  SequentialExecutorFactory sessionExecutorFactory,
								  WebController webController,
								  IconProvider iconProvider,
								  TeamAppsUploadManager uploadManager,
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
						sessionsById.values().forEach(s -> s.getUiSession().updateStats());
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

	public UiSession getUiSessionById(String sessionId) {
		SessionPair sessionPair = sessionsById.get(sessionId);
		return sessionPair != null ? sessionPair.getUiSession() : null;
	}

	public SessionContext getSessionContextById(String sessionId) {
		SessionPair sessionPair = sessionsById.get(sessionId);
		return sessionPair != null ? sessionPair.getSessionContext() : null;
	}

	public int getNumberOfSessions() {
		return sessionsById.size();
	}

	public int getNumberOfSessionsByState(UiSessionState state) {
		return (int) sessionsById.values().stream()
				.filter(sessionPair -> sessionPair.getSessionContext().getState() == state)
				.count();
	}

	public List<SessionPair> getAllSessions() {
		return List.copyOf(sessionsById.values());
	}

	public int getBufferedCommandsCount() {
		return sessionsById.values().stream()
				.map(SessionPair::getUiSession)
				.mapToInt(uiSession -> uiSession.getClientBackPressureInfo().getBufferedCommandsCount())
				.sum();
	}

	public int getUnconsumedCommandsCount() {
		return sessionsById.values().stream()
				.map(SessionPair::getUiSession)
				.mapToInt(uiSession -> uiSession.getClientBackPressureInfo().getUnconsumedCommandsCount())
				.sum();
	}

	public int getNumberOfAvailableClosedSessionStatistics() {
		return closedSessionsStatistics.size();
	}

	public List<UiSessionStats> getClosedSessionsStatistics() {
		synchronized (closedSessionsStatistics) {
			return List.copyOf(closedSessionsStatistics);
		}
	}

	public UiSession initSession(
			String sessionId,
			ClientInfo clientInfo,
			HttpSession httpSession,
			int maxRequestedCommandId,
			MessageSender messageSender
	) {
		LOGGER.trace("initSession: sessionId = [" + sessionId + "], clientInfo = [" + clientInfo + "], "
					 + "maxRequestedCommandId = [" + maxRequestedCommandId + "], messageSender = [" + messageSender + "]");

		UiSession uiSession = new UiSession(sessionId, System.currentTimeMillis(), config, objectMapper, messageSender);
		uiSession.addSessionListener(new UiSessionListener() {
			@Override
			public void onStateChanged(String sessionId, UiSessionState state) {
				if (state == CLOSED) {
					sessionsById.remove(uiSession.getSessionId());
					closedSessionsStatistics.addLast(uiSession.getStatistics().immutableCopy());
					while (closedSessionsStatistics.size() > 10_000) {
						closedSessionsStatistics.removeFirst();
					}
				}
			}
		});
		SessionContext sessionContext = createSessionContext(uiSession, clientInfo, httpSession);
		uiSession.addSessionListener(sessionContext.getAsUiSessionListenerInternal());

		uiSession.handleCommandRequest(maxRequestedCommandId, null);

		boolean wrongTeamappsVersion = clientInfo.getTeamAppsVersion() == null
									   || (!Objects.equals(clientInfo.getTeamAppsVersion(), TEAMAPPS_VERSION) && !Objects.equals(clientInfo.getTeamAppsVersion(), TEAMAPPS_DEV_SERVER_VERSION));
		boolean hasTeamAppsRefreshParameter = clientInfo.getClientParameters().containsKey(TEAMAPPS_VERSION_REFRESH_PARAMETER);
		if (wrongTeamappsVersion) {
			LOGGER.info("Wrong TeamApps client version {} in session {}! Expected: {}!", clientInfo.getTeamAppsVersion(), sessionId, TEAMAPPS_VERSION);
			if (!hasTeamAppsRefreshParameter) {
				LOGGER.info("Sending redirect with {} parameter.", TEAMAPPS_VERSION_REFRESH_PARAMETER);
				String separator = StringUtils.isNotEmpty(clientInfo.getLocation().getSearch()) ? "&" : "?";
				String url = clientInfo.getLocation().getHref() + separator + TEAMAPPS_VERSION_REFRESH_PARAMETER + "=" + System.currentTimeMillis();
				uiSession.sendCommand(new UiCommandWithResultCallback(null, null, "goToUrl", url, false));
			}
			uiSession.close(SessionClosingReason.WRONG_TEAMAPPS_VERSION);
			return uiSession;
		}

		sessionsById.put(sessionId, new SessionPair(uiSession, sessionContext));

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
		Map<UiSessionState, List<UiSession>> sessionsByActivity;
		List<UiSession> sessionsToClose;
		long nearlyInactiveTimeout = config.getUiSessionInactivityTimeoutMillis() - config.getUiSessionPreInactivityPingMillis();
		sessionsByActivity = sessionsById.values().stream()
				.map(SessionPair::getUiSession)
				.collect(Collectors.groupingBy(session -> {
					long timeSinceLastMessage = now - session.getTimestampOfLastMessageFromClient();
					return timeSinceLastMessage > config.getUiSessionInactivityTimeoutMillis() ? INACTIVE
							: timeSinceLastMessage > nearlyInactiveTimeout ? NEARLY_INACTIVE
							: ACTIVE;
				}));
		sessionsToClose = sessionsById.values().stream()
				.map(SessionPair::getUiSession)
				.filter(session -> now - session.getTimestampOfLastMessageFromClient() > config.getUiSessionTimeoutMillis())
				.collect(Collectors.toList());
		for (UiSession inactiveSession : sessionsByActivity.getOrDefault(INACTIVE, List.of())) {
			if (inactiveSession.getState() != INACTIVE) {
				LOGGER.info("Marking session inactive: {} ({})", inactiveSession.getName(), inactiveSession.getSessionId());
				inactiveSession.setInactive();
			}
		}
		for (UiSession criticalSession : sessionsByActivity.getOrDefault(NEARLY_INACTIVE, List.of())) {
			if (criticalSession.getState() != NEARLY_INACTIVE) {
				LOGGER.info("Marking session nearly inactive and sending PING to client: {} ({})", criticalSession.getName(), criticalSession.getSessionId());
				criticalSession.setNearlyInactive();
				criticalSession.ping();
			}
		}
		for (UiSession activeSession : sessionsByActivity.getOrDefault(ACTIVE, List.of())) {
			if (activeSession.getState() != ACTIVE) {
				LOGGER.info("Marking session active: {} ({})", activeSession.getName(), activeSession.getSessionId());
				activeSession.setActive();
			}
		}
		for (UiSession sessionToClose : sessionsToClose) {
			LOGGER.info("Closing session: {} ({})", sessionToClose.getName(), sessionToClose.getSessionId());
			sessionToClose.close(SessionClosingReason.SESSION_TIMEOUT);
		}
	}

	public void destroy() {
		houseKeepingScheduledExecutor.shutdown();
	}

	public SessionContext createSessionContext(UiSession uiSession, ClientInfo clientInfo, HttpSession httpSession) {
		SessionContext sessionContext = new SessionContext(
				uiSession,
				sessionExecutorFactory.createExecutor(uiSession.getSessionId()),
				clientInfo,
				httpSession,
				uxServerContext,
				new SessionIconProvider(iconProvider),
				componentLibraryRegistry
		);

		sessionContext.runWithContext(() -> {
			var sessionExpiredWindow = createDefaultSessionMessageWindow(sessionContext.getLocalized("teamapps.common.sessionExpired"), sessionContext.getLocalized("teamapps.common.sessionExpiredText"), sessionContext.getLocalized("teamapps.common.refresh"), sessionContext.getLocalized("teamapps.common.cancel")),
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
			// TODO cancelLink.setOnClickJavaScript("context.getClientObjectById(\"" + window.createClientReference().getId() + "\").close();");
			verticalLayout.addComponentAutoSize(cancelLink);
		}

		window.setContent(verticalLayout);
		window.enableAutoHeight();
		return window;
	}

}
