/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.*;
import org.teamapps.icons.IconProvider;
import org.teamapps.icons.SessionIconProvider;
import org.teamapps.server.UxServerContext;
import org.teamapps.uisession.*;
import org.teamapps.util.threading.SequentialExecutorFactory;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.json.UxJacksonSerializationTemplate;
import org.teamapps.ux.session.ClientInfo;
import org.teamapps.ux.session.SessionConfiguration;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.webcontroller.WebController;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.teamapps.common.TeamAppsVersion.TEAMAPPS_DEV_SERVER_VERSION;
import static org.teamapps.common.TeamAppsVersion.TEAMAPPS_VERSION;

public class TeamAppsUxSessionManager implements UiSessionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamAppsUxSessionManager.class);
	public static final String TEAMAPPS_REFRESH_PARAMETER = "teamappsRefresh"; // keep in-sync with JavaScript!!!

	private final SequentialExecutorFactory sessionExecutorFactory;
	private final WebController webController;
	private final UiCommandExecutor commandExecutor;

	private final IconProvider iconProvider;
	private final TeamAppsUploadManager uploadManager;
	private final Map<QualifiedUiSessionId, SessionContext> sessionContextById = new ConcurrentHashMap<>();

	private final UxServerContext uxServerContext = new UxServerContext() {
		@Override
		public SessionContext getSessionContextById(QualifiedUiSessionId sessionId) {
			return sessionContextById.get(sessionId);
		}

		@Override
		public File getUploadedFileByUuid(String uuid) {
			return uploadManager.getUploadedFile(uuid);
		}
	};

	public TeamAppsUxSessionManager(SequentialExecutorFactory sessionExecutorFactory,
									WebController webController,
									UiCommandExecutor commandExecutor,
									IconProvider iconProvider,
									TeamAppsUploadManager uploadManager) {
		this.sessionExecutorFactory = sessionExecutorFactory;
		this.webController = webController;
		this.commandExecutor = commandExecutor;
		this.iconProvider = iconProvider;
		this.uploadManager = uploadManager;
	}

	@Override
	public void onUiSessionStarted(QualifiedUiSessionId sessionId, UiClientInfo uiClientInfo, HttpSession httpSession) {
		boolean wrongTeamappsVersion = uiClientInfo.getTeamAppsVersion() == null
				|| (!Objects.equals(uiClientInfo.getTeamAppsVersion(), TEAMAPPS_VERSION) && !Objects.equals(uiClientInfo.getTeamAppsVersion(), TEAMAPPS_DEV_SERVER_VERSION));
		boolean hasTeamAppsRefreshParameter = uiClientInfo.getClientParameters().containsKey(TEAMAPPS_REFRESH_PARAMETER);
		if (wrongTeamappsVersion) {
			LOGGER.info("Wrong TeamApps client version {} in session {}! Expected: {}!", uiClientInfo.getTeamAppsVersion(), sessionId, TEAMAPPS_VERSION);
			if (!hasTeamAppsRefreshParameter) {
				LOGGER.info("Sending redirect with {} parameter.", TEAMAPPS_REFRESH_PARAMETER);
				String clientUrl = uiClientInfo.getClientUrl();
				String separator = clientUrl.contains("?") ? "&" : "?";
				commandExecutor.sendCommand(sessionId, new UiCommandWithResultCallback<>(new UiRootPanel.GoToUrlCommand(clientUrl + separator + TEAMAPPS_REFRESH_PARAMETER + "=" + System.currentTimeMillis(), false), null)); // TODO remove this in 2022, when all clients
			}
			commandExecutor.closeSession(sessionId, UiSessionClosingReason.WRONG_TEAMAPPS_VERSION);
			return;
		}

		ClientInfo clientInfo = new ClientInfo(
				uiClientInfo.getIp(),
				uiClientInfo.getScreenWidth(),
				uiClientInfo.getScreenHeight(),
				uiClientInfo.getViewPortWidth(),
				uiClientInfo.getViewPortHeight(),
				uiClientInfo.getPreferredLanguageIso(),
				uiClientInfo.getHighDensityScreen(),
				uiClientInfo.getTimezoneIana(),
				uiClientInfo.getTimezoneOffsetMinutes(),
				uiClientInfo.getClientTokens(),
				uiClientInfo.getUserAgentString(),
				uiClientInfo.getClientUrl(),
				uiClientInfo.getClientParameters(),
				uiClientInfo.getTeamAppsVersion());

		SessionConfiguration sessionConfiguration = SessionConfiguration.createForClientInfo(clientInfo);

		SessionContext sessionContext = new SessionContext(
				sessionId,
				sessionExecutorFactory.createExecutor(),
				clientInfo,
				sessionConfiguration,
				httpSession,
				commandExecutor,
				uxServerContext,
				new SessionIconProvider(iconProvider)
		);
		sessionContextById.put(sessionId, sessionContext);

		CompletableFuture<Void> future = sessionContext.runWithContext(() -> {
			sessionContext.registerTemplates(Arrays.stream(BaseTemplate.values())
					.collect(Collectors.toMap(Enum::name, BaseTemplate::getTemplate)));
			webController.onSessionStart(sessionContext);
		});

		try {
			// TODO make non-blocking when exception handling (and thereby session invalidation) is changed
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUiSessionClientRefresh(QualifiedUiSessionId sessionId, UiClientInfo clientInfo, HttpSession httpSession) {
		this.onUiSessionStarted(sessionId, clientInfo, httpSession);
	}

	@Override
	public void onActivityStateChanged(QualifiedUiSessionId sessionId, boolean active) {
		SessionContext context = sessionContextById.get(sessionId);
		if (context != null) {
			context.handleActivityStateChangedInternal(active);
		}
	}

	@Override
	public void onUiSessionClosed(QualifiedUiSessionId sessionId, UiSessionClosingReason reason) {
		SessionContext context = sessionContextById.remove(sessionId);
		if (context != null) {
			context.handleSessionDestroyedInternal();
		}
	}

	@Override
	public CompletableFuture<Void> onUiEvent(QualifiedUiSessionId sessionId, UiEvent event) {
		SessionContext sessionContext = sessionContextById.get(sessionId);
		if (sessionContext != null) {
			return sessionContext.runWithContext(() -> {
				String uiComponentId = event.getComponentId();
				if (uiComponentId != null) {
					ClientObject clientObject = sessionContext.getClientObject(uiComponentId);
					if (clientObject != null) {
						clientObject.handleUiEvent(event);
					} else {
						throw new TeamAppsComponentNotFoundException(sessionId, uiComponentId);
					}
				} else {
					sessionContext.handleStaticEvent(event);
				}
			});
		} else {
			return CompletableFuture.failedFuture(new TeamAppsSessionNotFoundException(sessionId));
		}
	}

	@Override
	public void onUiQuery(QualifiedUiSessionId sessionId, UiQuery query, Consumer<Object> resultCallback, Consumer<Throwable> errorCallback) {
		SessionContext sessionContext = sessionContextById.get(sessionId);
		if (sessionContext != null) {
			sessionContext.runWithContext(() -> {
				String uiComponentId = query.getComponentId();
				ClientObject clientObject = sessionContext.getClientObject(uiComponentId);
				if (clientObject != null) {
					clientObject.handleUiQuery(query)
							.handle((result, throwable) -> {
								if (throwable != null) {
									errorCallback.accept(throwable);
								} else {
									new UxJacksonSerializationTemplate(sessionContext).doWithUxJacksonSerializers(() -> {
										resultCallback.accept(result);
									});
								}
								return null;
							});
				} else {
					errorCallback.accept(new TeamAppsComponentNotFoundException(sessionId, uiComponentId));
				}
			});
		} else {
			errorCallback.accept(new TeamAppsSessionNotFoundException(sessionId));
		}
	}

	public SessionContext getSessionContext(QualifiedUiSessionId sessionId) {
		return sessionContextById.get(sessionId);
	}

}
