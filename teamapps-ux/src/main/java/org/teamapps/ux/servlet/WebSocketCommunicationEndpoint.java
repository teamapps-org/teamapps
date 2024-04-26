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
import org.teamapps.dto.protocol.client.*;
import org.teamapps.dto.protocol.server.AbstractServerMessage;
import org.teamapps.dto.protocol.server.REINIT_NOK;
import org.teamapps.dto.protocol.server.SESSION_CLOSED;
import org.teamapps.dto.protocol.server.SessionClosingReason;
import org.teamapps.uisession.*;
import org.teamapps.ux.session.ClientInfo;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class WebSocketCommunicationEndpoint extends Endpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
				AbstractClientMessageWrapper clientMessage = new AbstractClientMessageWrapper(mapper.readTree(payload));

				switch (clientMessage.getTypeId()) {
					case INITWrapper.TYPE_ID -> {
						ServerSideClientInfo serverSideClientInfo = createServerSideClientInfo(wsSession);
						INITWrapper init = clientMessage.as(INITWrapper.class);

						ClientInfoWrapper dtoClientInfo = init.getClientInfo();
						var clientInfo = new ClientInfo(
								serverSideClientInfo.getIp(),
								dtoClientInfo.getScreenWidth(),
								dtoClientInfo.getScreenHeight(),
								dtoClientInfo.getViewPortWidth(),
								dtoClientInfo.getViewPortHeight(),
								serverSideClientInfo.getPreferredLanguageIso(),
								dtoClientInfo.getHighDensityScreen(),
								dtoClientInfo.getTimezoneIana(),
								dtoClientInfo.getTimezoneOffsetMinutes(),
								dtoClientInfo.getClientTokens(),
								serverSideClientInfo.getUserAgentString(),
								URI.create(dtoClientInfo.getLocation()).toURL(),
								dtoClientInfo.getClientParameters(),
								dtoClientInfo.getTeamAppsVersion()
						);

						uiSession = sessionManager.initSession(
								init.getSessionId(),
								clientInfo,
								httpSession,
								init.getMaxRequestedCommandId(),
								new MessageSenderImpl()
						);
					}
					case REINITWrapper.TYPE_ID -> {
						REINITWrapper reinit = clientMessage.as(REINITWrapper.class);
						String uiSessionId = reinit.getSessionId();
						uiSession = sessionManager.getUiSessionById(uiSessionId);
						if (uiSession != null) {
							uiSession.reinit(reinit.getLastReceivedCommandId(), reinit.getMaxRequestedCommandId(), new MessageSenderImpl());
						} else {
							LOGGER.warn("Could not find teamAppsUiSession for REINIT: " + uiSessionId);
							send(new REINIT_NOK(SessionClosingReason.SESSION_NOT_FOUND), null, null);
						}
					}
					case TERMINATEWrapper.TYPE_ID -> {
						uiSession.close(SessionClosingReason.TERMINATED_BY_CLIENT);
					}
					case EVTWrapper.TYPE_ID -> {
						EVTWrapper eventMessage = clientMessage.as(EVTWrapper.class);
						uiSession.handleEvent(eventMessage.getSequenceNumber(), eventMessage.getLibraryId(), eventMessage.getClientObjectId(), eventMessage.getName(), eventMessage.getParams());
					}
					case QUERYWrapper.TYPE_ID -> {
						QUERYWrapper queryMessage = clientMessage.as(QUERYWrapper.class);
						uiSession.handleQuery(queryMessage.getSequenceNumber(), queryMessage.getLibraryId(), queryMessage.getClientObjectId(), queryMessage.getName(), queryMessage.getParams());
					}
					case CMD_RESWrapper.TYPE_ID -> {
						CMD_RESWrapper cmdResult = clientMessage.as(CMD_RESWrapper.class);
						uiSession.handleCommandResult(cmdResult.getSequenceNumber(), cmdResult.getCmdSn(), cmdResult.getResult());
					}
					case REQNWrapper.TYPE_ID -> {
						REQNWrapper cmdRequest = clientMessage.as(REQNWrapper.class);
						uiSession.handleCommandRequest(cmdRequest.getMaxRequestedCommandId(), cmdRequest.getLastReceivedCommandId());
					}
					case KEEPALIVEWrapper.TYPE_ID -> {
						uiSession.handleKeepAlive();
					}
					default -> throw new TeamAppsCommunicationException("Unknown message type: " + clientMessage.getClass().getCanonicalName());
				}
			} catch (TeamAppsSessionNotFoundException e) {
				LOGGER.warn("TeamApps session not found: " + e.getSessionId());
				send(new SESSION_CLOSED(SessionClosingReason.SESSION_NOT_FOUND, e.getMessage()), this::close, (t) -> close());
			} catch (Exception e) {
				LOGGER.error("Exception while processing client message!", e);
				send(new SESSION_CLOSED(SessionClosingReason.SERVER_SIDE_ERROR, e.getMessage()), this::close, (t) -> close());
			}
		}

		private void send(AbstractServerMessage message, Runnable sendingSuccessHandler, SendingErrorHandler sendingErrorHandler) {
			String messageAsString;
			try {
				messageAsString = mapper.writeValueAsString(message);
			} catch (JsonProcessingException e) {
				throw new TeamAppsCommunicationException(e);
			}
			send(messageAsString, sendingSuccessHandler, sendingErrorHandler);
		}

		private void send(String messageString, Runnable sendingSuccessHandler, SendingErrorHandler sendingErrorHandler) {
			if (this.closed) {
				sendingErrorHandler.onErrorWhileSending(new TeamAppsCommunicationException("Connection closed!"));
				return;
			}
			try {

				sendCount.addAndGet(messageString.length());
				totalSendCount.addAndGet(messageString.length());
				//noinspection Convert2Lambda
				wsSession.getAsyncRemote().sendText(messageString, new SendHandler() {
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
			public void sendAsynchronously(List<String> messages, SendingErrorHandler sendingErrorHandler) {
				send(messages.stream().collect(Collectors.joining(",", "[", "]")), null, sendingErrorHandler);
			}

			@Override
			public void close(SessionClosingReason closingReason, String message) {
				send(
						new SESSION_CLOSED(closingReason, message),
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
