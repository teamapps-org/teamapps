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
package org.teamapps.ux.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.dto.*;
import org.teamapps.json.TeamAppsObjectMapperFactory;
import org.teamapps.uisession.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
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
	private final ObjectMapper mapper = TeamAppsObjectMapperFactory.create();

	private final AtomicLong totalSendCount = new AtomicLong();
	private final AtomicLong totalReceiveCount = new AtomicLong();

	private final TeamAppsSessionManager sessionManager;
	private final TeamAppsConfiguration teamAppsConfig;

	public WebSocketCommunicationEndpoint(TeamAppsSessionManager sessionManager, TeamAppsConfiguration teamAppsConfig) {
		this.sessionManager = sessionManager;
		this.teamAppsConfig = teamAppsConfig;
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

		private Optional<UiSession> getUiSession(String uiSessionId) {
			if (uiSession != null) {
				return Optional.of(uiSession);
			} else {
				UiSession session = sessionManager.getUiSessionById(uiSessionId);
				if (session != null) {
					this.uiSession = session;
				} else {
					LOGGER.warn("Could not find uiSession with id {}", uiSessionId);
				}
				return Optional.ofNullable(session);
			}
		}

		@Override
		public void onMessage(String payload) {
			receivedCount.addAndGet(payload.length());
			totalReceiveCount.addAndGet(payload.length());
			try {
				HttpSession httpSession = (HttpSession) wsSession.getUserProperties().get(WebSocketServerEndpointConfigurator.HTTP_SESSION_PROPERTY_NAME);
				AbstractClientMessage clientMessage = mapper.readValue(payload, AbstractClientMessage.class);

				String uiSessionId = clientMessage.getSessionId();
				if (clientMessage instanceof INIT) {
					ServerSideClientInfo serverSideClientInfo = createServerSideClientInfo(wsSession);
					INIT init = (INIT) clientMessage;
					init.getClientInfo().setIp(serverSideClientInfo.getIp());
					init.getClientInfo().setUserAgentString(serverSideClientInfo.getUserAgentString());
					init.getClientInfo().setPreferredLanguageIso(serverSideClientInfo.getPreferredLanguageIso());
					sessionManager.initSession(
							uiSessionId,
							init.getClientInfo(),
							httpSession,
							init.getMaxRequestedCommandId(),
							new MessageSenderImpl()
					);
				} else if (clientMessage instanceof REINIT) {
					REINIT reinit = (REINIT) clientMessage;
					getUiSession(uiSessionId).ifPresentOrElse(uiSession -> {
						uiSession.reinit(reinit.getLastReceivedCommandId(), reinit.getMaxRequestedCommandId(), new MessageSenderImpl());
					}, () -> {
						LOGGER.warn("Could not find teamAppsUiSession for REINIT: " + uiSessionId);
						send(new REINIT_NOK(UiSessionClosingReason.SESSION_NOT_FOUND), null, null);
					});
				} else if (clientMessage instanceof TERMINATE) {
					getUiSession(uiSessionId).ifPresent(uiSession -> uiSession.close(UiSessionClosingReason.TERMINATED_BY_CLIENT));
				} else if (clientMessage instanceof EVENT) {
					EVENT eventMessage = (EVENT) clientMessage;
					getUiSession(uiSessionId).ifPresent(uiSession -> uiSession.handleEvent(eventMessage.getId(), eventMessage.getUiEvent()));
				} else if (clientMessage instanceof QUERY) {
					QUERY queryMessage = (QUERY) clientMessage;
					getUiSession(uiSessionId).ifPresent(uiSession -> uiSession.handleQuery(queryMessage.getId(), queryMessage.getUiQuery()));
				} else if (clientMessage instanceof CMD_RESULT) {
					CMD_RESULT cmdResult = (CMD_RESULT) clientMessage;
					getUiSession(uiSessionId).ifPresent(uiSession -> uiSession.handleCommandResult(cmdResult.getId(), cmdResult.getCmdId(), cmdResult.getResult()));
				} else if (clientMessage instanceof CMD_REQUEST) {
					CMD_REQUEST cmdRequest = (CMD_REQUEST) clientMessage;
					getUiSession(uiSessionId).ifPresent(uiSession -> uiSession.handleCommandRequest(cmdRequest.getMaxRequestedCommandId(), cmdRequest.getLastReceivedCommandId()));
				} else if (clientMessage instanceof KEEPALIVE) {
					getUiSession(uiSessionId).ifPresent(UiSession::handleKeepAlive);
				} else {
					throw new TeamAppsCommunicationException("Unknown message type: " + clientMessage.getClass().getCanonicalName());
				}
			} catch (TeamAppsSessionNotFoundException e) {
				LOGGER.warn("TeamApps session not found: " + e.getSessionId());
				send(new SESSION_CLOSED(UiSessionClosingReason.SESSION_NOT_FOUND).setMessage(e.getMessage()), this::close, (t) -> close());
			} catch (Exception e) {
				LOGGER.error("Exception while processing client message!", e);
				send(new SESSION_CLOSED(UiSessionClosingReason.SERVER_SIDE_ERROR).setMessage(e.getMessage()), this::close, (t) -> close());
			}
		}

		private void send(AbstractServerMessage message, Runnable sendingSuccessHandler, SendingErrorHandler sendingErrorHandler) {
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
			public void sendMessageAsynchronously(AbstractServerMessage msg, SendingErrorHandler sendingErrorHandler) {
				send(msg, null, sendingErrorHandler);
			}

			@Override
			public void close(UiSessionClosingReason closingReason, String message) {
				send(
						new SESSION_CLOSED(closingReason).setMessage(message),
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
