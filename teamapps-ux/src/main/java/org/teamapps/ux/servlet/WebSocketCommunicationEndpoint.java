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
package org.teamapps.ux.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.dto.protocol.*;
import org.teamapps.uisession.*;
import org.teamapps.ux.session.ClientInfo;
import org.teamapps.ux.session.Location;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class WebSocketCommunicationEndpoint extends Endpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketCommunicationEndpoint.class);

	/**
	 * This is needed due to https://github.com/eclipse/jetty.project/issues/8151.
	 * TODO: remove once the ticket is fixed.
	 */
	private final Executor jettyWorkaroundCloseExecutor = Executors.newFixedThreadPool(5);
	private final ObjectMapper mapper;

	private final AtomicLong totalSendCount = new AtomicLong();
	private final AtomicLong totalReceiveCount = new AtomicLong();

	private final TeamAppsSessionManager sessionManager;
	private final TeamAppsConfiguration teamAppsConfig;

	public WebSocketCommunicationEndpoint(TeamAppsSessionManager sessionManager, TeamAppsConfiguration teamAppsConfig, ObjectMapper mapper) {
		this.sessionManager = sessionManager;
		this.teamAppsConfig = teamAppsConfig;
		this.mapper = mapper;
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.setMaxIdleTimeout(teamAppsConfig.getKeepaliveMessageIntervalMillis() * 3);
		session.setMaxTextMessageBufferSize(teamAppsConfig.getMaxUiClientMessageSize());
		session.addMessageHandler(new WebSocketHandler(session));
	}

	@Override
	public void onError(Session session, Throwable thr) {
		LOGGER.warn("WebSocket communication error.", thr);
		closeWebSocketSession(session);
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		// nothing to do...
	}

	private void closeWebSocketSession(Session wsSession) {
		try {
			wsSession.close();
		} catch (IOException e) {
			// ignore
		}
	}

	public long getTotalSendCount() {
		return totalSendCount.get();
	}

	public long getTotalReceiveCount() {
		return totalReceiveCount.get();
	}

	private class WebSocketHandler implements MessageHandler.Whole<String> {
		private final Session wsSession;
		private boolean closed;
		private UiSession uiSession;

		private final AtomicLong sendCount = new AtomicLong();
		private final AtomicLong receivedCount = new AtomicLong();

		public WebSocketHandler(Session session) {
			this.wsSession = session;
		}

		@Override
		public void onMessage(String payload) {
			receivedCount.addAndGet(payload.length());
			totalReceiveCount.addAndGet(payload.length());
			try {
				HttpSession httpSession = (HttpSession) wsSession.getUserProperties().get(WebSocketServerEndpointConfigurator.HTTP_SESSION_PROPERTY_NAME);
				DtoAbstractClientMessageWrapper clientMessage = new DtoAbstractClientMessageWrapper(mapper.readTree(payload));

				switch (clientMessage.getTypeId()) {
					case DtoINITWrapper.TYPE_ID -> {
						ServerSideClientInfo serverSideClientInfo = createServerSideClientInfo(wsSession);
						DtoINITWrapper init = clientMessage.as(DtoINITWrapper.class);

						DtoClientInfoWrapper uiClientInfo = init.getClientInfo();
						var clientInfo = new ClientInfo(
								serverSideClientInfo.getIp(),
								uiClientInfo.getScreenWidth(),
								uiClientInfo.getScreenHeight(),
								uiClientInfo.getViewPortWidth(),
								uiClientInfo.getViewPortHeight(),
								serverSideClientInfo.getPreferredLanguageIso(),
								uiClientInfo.getHighDensityScreen(),
								uiClientInfo.getTimezoneIana(),
								uiClientInfo.getTimezoneOffsetMinutes(),
								uiClientInfo.getClientTokens(),
								serverSideClientInfo.getUserAgentString(),
								Location.fromUiLocationWrapper(uiClientInfo.getLocation()),
								uiClientInfo.getClientParameters(),
								uiClientInfo.getTeamAppsVersion()
						);

						uiSession = sessionManager.initSession(
								init.getSessionId(),
								clientInfo,
								httpSession,
								init.getMaxRequestedCommandId(),
								new MessageSenderImpl()
						);
					}
					case DtoREINITWrapper.TYPE_ID -> {
						DtoREINITWrapper reinit = clientMessage.as(DtoREINITWrapper.class);
						String uiSessionId = reinit.getSessionId();
						uiSession = sessionManager.getUiSessionById(uiSessionId);
						if (uiSession != null) {
							uiSession.reinit(reinit.getLastReceivedCommandId(), reinit.getMaxRequestedCommandId(), new MessageSenderImpl());
						} else {
							LOGGER.warn("Could not find teamAppsUiSession for REINIT: " + uiSessionId);
							send(new DtoREINIT_NOK(DtoSessionClosingReason.SESSION_NOT_FOUND), null, null);
						}
					}
					case DtoTERMINATEWrapper.TYPE_ID -> {
						uiSession.close(DtoSessionClosingReason.TERMINATED_BY_CLIENT);
					}
					case DtoEVTWrapper.TYPE_ID -> {
						DtoEVTWrapper eventMessage = clientMessage.as(DtoEVTWrapper.class);
						uiSession.handleEvent(eventMessage.getId(), eventMessage.getUiEvent());
					}
					case DtoQRYWrapper.TYPE_ID -> {
						DtoQRYWrapper queryMessage = clientMessage.as(DtoQRYWrapper.class);
						uiSession.handleQuery(queryMessage.getId(), queryMessage.getUiQuery());
					}
					case DtoCMD_RESWrapper.TYPE_ID -> {
						DtoCMD_RESWrapper cmdResult = clientMessage.as(DtoCMD_RESWrapper.class);
						uiSession.handleCommandResult(cmdResult.getId(), cmdResult.getCmdId(), cmdResult.getResult());
					}
					case DtoCMD_REQWrapper.TYPE_ID -> {
						DtoCMD_REQWrapper cmdRequest = clientMessage.as(DtoCMD_REQWrapper.class);
						uiSession.handleCommandRequest(cmdRequest.getMaxRequestedCommandId(), cmdRequest.getLastReceivedCommandId());
					}
					case DtoKEEPALIVEWrapper.TYPE_ID -> {
						uiSession.handleKeepAlive();
					}
					default -> throw new TeamAppsCommunicationException("Unknown message type: " + clientMessage.getClass().getCanonicalName());
				}
			} catch (TeamAppsSessionNotFoundException e) {
				LOGGER.warn("TeamApps session not found: " + e.getSessionId());
				send(new DtoSESSION_CLOSED(DtoSessionClosingReason.SESSION_NOT_FOUND, e.getMessage()), this::close, (t) -> close());
			} catch (Exception e) {
				LOGGER.error("Exception while processing client message!", e);
				send(new DtoSESSION_CLOSED(DtoSessionClosingReason.SERVER_SIDE_ERROR, e.getMessage()), this::close, (t) -> close());
			}
		}

		private void send(DtoAbstractServerMessage message, Runnable sendingSuccessHandler, SendingErrorHandler sendingErrorHandler) {
			if (this.closed) {
				sendingErrorHandler.onErrorWhileSending(new TeamAppsCommunicationException("Connection closed!"));
				return;
			}
			try {
				String messageAsString;
				try {
					messageAsString = mapper.writeValueAsString(message);
				} catch (JsonProcessingException e) {
					throw new TeamAppsCommunicationException(e);
				}
				sendCount.addAndGet(messageAsString.length());
				totalSendCount.addAndGet(messageAsString.length());
				//noinspection Convert2Lambda
				wsSession.getAsyncRemote().sendText(messageAsString, new SendHandler() {
					@Override
					public void onResult(SendResult result) {
						if (result.isOK() && sendingSuccessHandler != null) {
							sendingSuccessHandler.run();
						}
						if (!result.isOK() && sendingErrorHandler != null) {
							sendingErrorHandler.onErrorWhileSending(result.getException());
						}
					}
				});
			} catch (Exception e) {
				if (sendingErrorHandler != null) {
					sendingErrorHandler.onErrorWhileSending(e);
				}
			}
		}

		private ServerSideClientInfo createServerSideClientInfo(Session session) {
			Map<String, Object> attributes = session.getUserProperties();
			return new ServerSideClientInfo(
					(String) attributes.get(WebSocketServerEndpointConfigurator.CLIENT_IP_PROPERTY_NAME),
					(String) attributes.get(WebSocketServerEndpointConfigurator.USER_AGENT_PROPERTY_NAME),
					(String) attributes.get(WebSocketServerEndpointConfigurator.LANGUAGE_PROPERTY_NAME)
			);
		}

		private void close() {
			this.closed = true;
			closeWebSocketSession(wsSession);
		}

		private class MessageSenderImpl implements MessageSender {
			@Override
			public void sendMessageAsynchronously(DtoAbstractServerMessage msg, SendingErrorHandler sendingErrorHandler) {
				send(msg, null, sendingErrorHandler);
			}

			@Override
			public void close(DtoSessionClosingReason closingReason, String message) {
				send(
						new DtoSESSION_CLOSED(closingReason, message),
						() -> jettyWorkaroundCloseExecutor.execute(WebSocketHandler.this::close),
						(t) -> jettyWorkaroundCloseExecutor.execute(() -> WebSocketHandler.this.close())
				);
			}

			@Override
			public long getDataReceived() {
				return receivedCount.get();
			}

			@Override
			public long getDataSent() {
				return sendCount.get();
			}
		}
	}

}
