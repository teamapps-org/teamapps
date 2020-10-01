/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.AbstractClientMessage;
import org.teamapps.dto.AbstractServerMessage;
import org.teamapps.dto.CMD_REQUEST;
import org.teamapps.dto.CMD_RESULT;
import org.teamapps.dto.EVENT;
import org.teamapps.dto.INIT;
import org.teamapps.dto.KEEPALIVE;
import org.teamapps.dto.REINIT;
import org.teamapps.dto.SESSION_CLOSED;
import org.teamapps.dto.TERMINATE;
import org.teamapps.dto.UiSessionClosingReason;
import org.teamapps.json.TeamAppsObjectMapperFactory;
import org.teamapps.uisession.MessageSender;
import org.teamapps.uisession.QualifiedUiSessionId;
import org.teamapps.uisession.SendingErrorHandler;
import org.teamapps.uisession.TeamAppsSessionNotFoundException;
import org.teamapps.uisession.TeamAppsUiSessionManager;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

public class TeamAppsCommunicationEndpoint extends Endpoint {

	/**
	 * If the client wants to send a frame larger than this, an MESSAGE_TOO_LARGE exception will be thrown and the connection closes.
	 */
	private static final int MAX_BINARY_CLIENT_MESSAGE_SIZE = 1024 * 1024; // 1 MiB

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamAppsCommunicationEndpoint.class);

	private final ObjectMapper mapper = TeamAppsObjectMapperFactory.create();

	private final TeamAppsUiSessionManager sessionManager;

	public TeamAppsCommunicationEndpoint(TeamAppsUiSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.setMaxIdleTimeout(MAX_BINARY_CLIENT_MESSAGE_SIZE);
		session.setMaxBinaryMessageBufferSize(MAX_BINARY_CLIENT_MESSAGE_SIZE);
		session.addMessageHandler(new WebSocketHandler(session));
	}

	@Override
	public void onError(Session session, Throwable thr) {
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

	private class WebSocketHandler implements MessageHandler.Whole<String> {
		private final Session wsSession;
		private boolean closed;

		public WebSocketHandler(Session session) {
			this.wsSession = session;
		}

		@Override
		public void onMessage(String payload) {
			try {
				HttpSession httpSession = (HttpSession) wsSession.getUserProperties().get(WebSocketServerEndpointConfigurator.HTTP_SESSION_PROPERTY_NAME);

				// TODO #http-timeout implement heartbeat http requests (shame...)
				// ((AbstractSession) httpSession).setLastAccessedTime(System.currentTimeMillis()); // this is jetty-specific code. Change if we want to support another server.
				// tomcat: http://tomcat.apache.org/tomcat-5.5-doc/catalina/docs/api/org/apache/catalina/session/StandardSession.html#access()

				AbstractClientMessage clientMessage = mapper.readValue(payload, AbstractClientMessage.class);

				QualifiedUiSessionId qualifiedUiSessionId = new QualifiedUiSessionId(httpSession.getId(), clientMessage.getSessionId());
				ServerSideClientInfo serverSideClientInfo = createServerSideClientInfo(wsSession);
				if (clientMessage instanceof INIT) {
					INIT init = (INIT) clientMessage;
					init.getClientInfo().setIp(serverSideClientInfo.getIp());
					init.getClientInfo().setUserAgentString(serverSideClientInfo.getUserAgentString());
					init.getClientInfo().setPreferredLanguageIso(serverSideClientInfo.getPreferredLanguageIso());
					sessionManager.initSession(
							qualifiedUiSessionId,
							init.getClientInfo(),
							httpSession,
							init.getMaxRequestedCommandId(),
							new MessageSenderImpl()
					);
				} else if (clientMessage instanceof REINIT) {
					REINIT reinit = (REINIT) clientMessage;
					sessionManager.reinitSession(
							qualifiedUiSessionId,
							reinit.getLastReceivedCommandId(),
							reinit.getMaxRequestedCommandId(),
							new MessageSenderImpl()
					);
				} else if (clientMessage instanceof TERMINATE) {
					sessionManager.closeSession(qualifiedUiSessionId, UiSessionClosingReason.TERMINATED_BY_CLIENT);
				} else if (clientMessage instanceof EVENT) {
					EVENT eventMessage = (EVENT) clientMessage;
					sessionManager.handleEvent(qualifiedUiSessionId, eventMessage.getId(), eventMessage.getUiEvent());
				} else if (clientMessage instanceof CMD_RESULT) {
					CMD_RESULT cmdResult = (CMD_RESULT) clientMessage;
					sessionManager.handleCommandResult(qualifiedUiSessionId, cmdResult.getId(), cmdResult.getCmdId(), cmdResult.getResult());
				} else if (clientMessage instanceof CMD_REQUEST) {
					CMD_REQUEST cmdRequest = (CMD_REQUEST) clientMessage;
					sessionManager.handleCommandRequest(qualifiedUiSessionId, cmdRequest.getLastReceivedCommandId(), cmdRequest.getMaxRequestedCommandId());
				} else if (clientMessage instanceof KEEPALIVE) {
					sessionManager.handleKeepAlive(qualifiedUiSessionId);
				} else {
					throw new TeamAppsCommunicationException("Unknown message type: " + clientMessage.getClass().getCanonicalName());
				}
			} catch (TeamAppsSessionNotFoundException e) {
				LOGGER.warn("TeamApps session not found: " + e.getSessionId());
				send(wsSession, new SESSION_CLOSED(UiSessionClosingReason.SESSION_NOT_FOUND).setMessage(e.getMessage()), this::close, (t) -> close());
			} catch (Exception e) {
				LOGGER.error("Exception while processing client message!", e);
				send(wsSession, new SESSION_CLOSED(UiSessionClosingReason.SERVER_SIDE_ERROR).setMessage(e.getMessage()), this::close, (t) -> close());
			}
		}

		private void send(Session session, AbstractServerMessage message, Runnable sendingSuccessHandler, SendingErrorHandler sendingErrorHandler) {
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
				//noinspection Convert2Lambda
				session.getAsyncRemote().sendText(messageAsString, new SendHandler() {
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
				WebSocketHandler.this.send(wsSession, msg, null, sendingErrorHandler);
			}

			@Override
			public void close(UiSessionClosingReason closingReason, String message) {
				send(wsSession, new SESSION_CLOSED(closingReason).setMessage(message), WebSocketHandler.this::close, (t) -> WebSocketHandler.this.close());
			}
		}
	}

}
