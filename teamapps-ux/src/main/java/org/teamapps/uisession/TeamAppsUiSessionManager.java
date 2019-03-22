/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.AbstractServerMessage;
import org.teamapps.dto.INIT_NOK;
import org.teamapps.dto.INIT_OK;
import org.teamapps.dto.MULTI_CMD;
import org.teamapps.dto.REINIT_NOK;
import org.teamapps.dto.REINIT_OK;
import org.teamapps.dto.UiClientInfo;
import org.teamapps.dto.UiEvent;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implements a cache for {@link UiSession} instances.
 * <p>
 * It takes care of the removal of timed-out sessions. The "last used" information is updated every time a session is retrieved.
 */
public class TeamAppsUiSessionManager implements UiCommandExecutor, HttpSessionListener {

	private static final long UI_SESSION_TIMEOUT = 60_000; // TODO make configurable

	private ScheduledExecutorService scheduledExecutorService;
	private final ObjectMapper objectMapper;
	private UiSessionListener uiSessionListener;

	private final static Logger LOGGER = LoggerFactory.getLogger(TeamAppsUiSessionManager.class);

	private final Table<String, String, UiSession> sessionsById = Tables.synchronizedTable(HashBasedTable.create());

	public TeamAppsUiSessionManager(ObjectMapper objectMapper) {
		this(objectMapper, null);
	}

	public TeamAppsUiSessionManager(ObjectMapper objectMapper, UiSessionListener uiSessionListener) {
		this.uiSessionListener = uiSessionListener;
		this.objectMapper = objectMapper;
		this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		});
		this.scheduledExecutorService.scheduleAtFixedRate(() -> removeTimedOutSessions(UI_SESSION_TIMEOUT), UI_SESSION_TIMEOUT, UI_SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	public void setUiSessionListener(UiSessionListener uiSessionListener) {
		this.uiSessionListener = uiSessionListener;
	}

	private UiSession getSessionById(QualifiedUiSessionId sessionId) {
		return sessionsById.get(sessionId.getHttpSessionId(), sessionId.getUiSessionId());
	}

	public void initSession(
			QualifiedUiSessionId sessionId,
			UiClientInfo clientInfo,
			int maxRequestedCommandId,
			MessageSender messageSender
	) {
		LOGGER.trace("initSession: sessionId = [" + sessionId + "], clientInfo = [" + clientInfo + "], "
				+ "maxRequestedCommandId = [" + maxRequestedCommandId + "], messageSender = [" + messageSender + "]");
		UiSession session;
		boolean isRefresh = false;

		synchronized (sessionsById) {
			if (sessionsById.contains(sessionId.getHttpSessionId(), sessionId.getUiSessionId())) {
				isRefresh = true;
				session = getSessionById(sessionId);
			} else {
				session = new UiSession(sessionId, clientInfo, System.currentTimeMillis(), uiSessionListener, messageSender);
				sessionsById.put(sessionId.getHttpSessionId(), sessionId.getUiSessionId(), session);
			}
		}

		if (isRefresh) {
			if (session != null) {
				session.handleClientRefresh(maxRequestedCommandId, messageSender);
			} else {
				messageSender.sendMessageAsynchronously(new INIT_NOK(INIT_NOK.Reason.SESSION_NOT_FOUND), null);
			}
		} else {
			session.init(maxRequestedCommandId);
		}

	}

	public void handleEvent(QualifiedUiSessionId sessionId, int clientMessageId, UiEvent event) {
		UiSession session = getSessionById(sessionId);
		if (session == null) {
			throw new TeamAppsSessionNotFoundException(sessionId);
		} else {
			session.handleEvent(clientMessageId, event);
		}
	}

	public void handleCommandResult(QualifiedUiSessionId sessionId, int clientMessageId, int cmdId, Object result) {
		UiSession session = getSessionById(sessionId);
		if (session == null) {
			throw new TeamAppsSessionNotFoundException(sessionId);
		} else {
			session.handleCommandResult(clientMessageId, cmdId, result);
		}
	}

	public void handleKeepAlive(QualifiedUiSessionId sessionId) {
		UiSession session = getSessionById(sessionId);
		if (session == null) {
			throw new TeamAppsSessionNotFoundException(sessionId);
		} else {
			session.handleKeepAlive();
		}
	}

	public void reinitSession(QualifiedUiSessionId sessionId, int lastReceivedCommandId,
	                          int maxRequestedCommandId, MessageSender messageSender) {
		UiSession session = getSessionById(sessionId);
		if (session != null) {
			session.reinit(lastReceivedCommandId, maxRequestedCommandId, messageSender);
		} else {
			LOGGER.warn("Could not find teamAppsUiSession for REINIT: " + sessionId);
			messageSender.sendMessageAsynchronously(new REINIT_NOK(REINIT_NOK.Reason.SESSION_NOT_FOUND), null);
		}
	}

	public void handleCommandRequest(QualifiedUiSessionId qualifiedUiSessionId, int lastReceivedCommandId, int maxRequestedCommandId) {
		UiSession session = getSessionById(qualifiedUiSessionId);
		if (session == null) {
			throw new TeamAppsSessionNotFoundException(qualifiedUiSessionId);
		} else {
			session.handleCommandRequest(lastReceivedCommandId, maxRequestedCommandId);
		}
	}

	public void sendCommand(QualifiedUiSessionId sessionId, UiCommandWithResultCallback commandWithCallback) {
		UiSession session = getSessionById(sessionId);
		if (session != null) {
			try {
				session.sendCommand(commandWithCallback);
			} catch (UnconsumedCommandsOverflowException e) {
				LOGGER.error("Too many unconsumed commands!", e);
				closeSession(sessionId, SessionClosingReason.COMMANDS_OVERFLOW);
			}
		} else {
			LOGGER.warn("Cannot send command to non-existing session: " + sessionId);
		}
	}

	public void sendCommands(QualifiedUiSessionId sessionId, List<UiCommandWithResultCallback> commandsWithCallback) {
		UiSession session = getSessionById(sessionId);
		if (session != null) {
			try {
				ArrayList<UiCommandWithResultCallback> uiCommandsCopy = new ArrayList<>(commandsWithCallback);
				session.sendCommands(uiCommandsCopy);
			} catch (UnconsumedCommandsOverflowException e) {
				LOGGER.error("Too many unconsumed commands!", e);
				closeSession(sessionId, SessionClosingReason.COMMANDS_OVERFLOW);
			}
		} else {
			LOGGER.warn("Cannot send commands to non-existing session: " + sessionId);
		}
	}

	public void closeSession(QualifiedUiSessionId sessionId, SessionClosingReason reason) {
		if (sessionsById.remove(sessionId.getHttpSessionId(), sessionId.getUiSessionId()) != null) {
			LOGGER.info("Actively closing session: " + sessionId);
			uiSessionListener.onUiSessionClosed(sessionId, reason);
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		// nothing to do...
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		removeAllSessionsForHttpSession(se.getSession().getId());
	}

	public void removeAllSessionsForHttpSession(String httpSessionId) {
		LOGGER.trace("TeamAppsUiSessionManager.removeAllSessionsForHttpSession");
		ArrayList<UiSession> removedSessions;
		synchronized (sessionsById) {
			Map<String, UiSession> sessionsToBeClosed = sessionsById.row(httpSessionId);
			removedSessions = new ArrayList<>(sessionsToBeClosed.values());
			sessionsToBeClosed.clear();
		}
		removedSessions.forEach(session -> {
			LOGGER.info("Removed session since HTTP session was closed: " + session.sessionId);
			uiSessionListener.onUiSessionClosed(session.sessionId, SessionClosingReason.HTTP_SESSION_CLOSED);
		});
	}

	public void removeTimedOutSessions(long timeoutMilliSeconds) {
		long now = System.currentTimeMillis();
		List<UiSession> removedSessions;
		synchronized (sessionsById) {
			removedSessions = new ArrayList<>();
			sessionsById.values().removeIf(session -> {
				boolean isTimedOut = now - session.getTimestampOfLastMessageFromClient() > timeoutMilliSeconds;
				if (isTimedOut) {
					LOGGER.debug("UI session timed out after " + timeoutMilliSeconds + " ms: " + session.sessionId);
					removedSessions.add(session);
				}
				return isTimedOut;
			});
		}
		removedSessions.forEach(session -> {
			LOGGER.info("Session timeout: " + session.sessionId);
			uiSessionListener.onUiSessionClosed(session.sessionId, SessionClosingReason.TIMED_OUT);
		});
	}

	public void destroy() {
		scheduledExecutorService.shutdown();
	}

	private class UiSession {

		private static final int COMMAND_BUFFER_SIZE = 10_000;

		private final QualifiedUiSessionId sessionId;
		private final UiClientInfo clientInfo;
		private final UiSessionListener sessionListener;

		private MessageSender messageSender;

		private final CommandBuffer commandBuffer = new CommandBuffer(COMMAND_BUFFER_SIZE);
		private AtomicInteger commandIdCounter = new AtomicInteger();

		private AtomicLong timestampOfLastMessageFromClient = new AtomicLong();
		private int lastReceivedClientMessageId;
		private boolean connectionActive = true;

		private int maxRequestedCommandId = 0;
		private int lastSentCommandId;

		private Map<Integer, Consumer> resultCallbacksByCmdId = new ConcurrentHashMap<>();

		public UiSession(QualifiedUiSessionId sessionId, UiClientInfo clientInfo, long creationTime, UiSessionListener sessionListener, MessageSender messageSender) {
			this.sessionId = sessionId;
			this.clientInfo = clientInfo;
			this.timestampOfLastMessageFromClient.set(creationTime);
			this.sessionListener = sessionListener;
			this.messageSender = messageSender;
		}

		public long getTimestampOfLastMessageFromClient() {
			return timestampOfLastMessageFromClient.get();
		}

		public void setMessageSender(MessageSender messageSender) {
			this.messageSender = messageSender;
		}

		public void sendCommand(UiCommandWithResultCallback commandWithCallback) throws UnconsumedCommandsOverflowException {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Sending command ({}): {}" , sessionId.getUiSessionId().substring(0, 8), commandWithCallback.getUiCommand().getClass().getSimpleName());
			}
			CMD cmd = createCMD(commandWithCallback);
			synchronized (this) {
				commandBuffer.addCommand(cmd);
				sendAllQueuedCommandsIfPossible();
			}
		}

		public void sendCommands(List<UiCommandWithResultCallback> commandsWithCallback) throws UnconsumedCommandsOverflowException {
			if (LOGGER.isDebugEnabled()) {
				commandsWithCallback.stream()
						.map(UiCommandWithResultCallback::getUiCommand)
						.forEach(command -> LOGGER.debug("Sending command ({}): {}" , sessionId.getUiSessionId().substring(0, 8), command.getClass().getSimpleName()));
			}
			List<CMD> cmds = commandsWithCallback.stream()
					.map(commandWithCallback -> createCMD(commandWithCallback))
					.collect(Collectors.toList());
			synchronized (this) {
				for (CMD cmd : cmds) {
					commandBuffer.addCommand(cmd);
				}
				sendAllQueuedCommandsIfPossible();
			}
		}

		private CMD createCMD(UiCommandWithResultCallback commandWithCallback) {
			CMD cmd;
			try {
				int cmdId = commandIdCounter.incrementAndGet();
				cmd = new CMD(cmdId, objectMapper.writeValueAsString(commandWithCallback.getUiCommand()));

				if (commandWithCallback.getResultCallback() != null) {
					cmd.setAwaitsResponse(true);
					resultCallbacksByCmdId.put(cmdId, commandWithCallback.getResultCallback());
				}
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			return cmd;
		}

		public boolean rewindToCommand(int commandId) {
			synchronized (this) {
				this.lastSentCommandId = commandId - 1;
				return commandBuffer.rewindToCommand(commandId);
			}
		}

		private void sendAllQueuedCommandsIfPossible() {
			if (connectionActive) {
				List<CMD> cmdsToSend = new ArrayList<>();
				synchronized (this) {
					while (true) {
						if (lastSentCommandId >= maxRequestedCommandId) {
							connectionActive = false;
							break;
						}
						CMD cmd = commandBuffer.consumeCommand();
						if (cmd != null) {
							lastSentCommandId = cmd.getId();
							cmdsToSend.add(cmd);
						} else {
							break;
						}
					}
				}
				if (!cmdsToSend.isEmpty()) {
					sendAsyncWithErrorHandler(new MULTI_CMD(cmdsToSend));
				}
			}
		}

		public void reviveConnection() {
			synchronized (this) {
				this.connectionActive = true;
				sendAllQueuedCommandsIfPossible();
			}
		}

		public void handleCommandRequest(int lastReceivedCommandId, int maxRequestedCommandId) {
			LOGGER.trace("UiSession.requestCommands: maxRequestedCommandId = [" + maxRequestedCommandId + "]");
			this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
			synchronized (this) {
				this.commandBuffer.purgeTillCommand(lastReceivedCommandId);
				this.maxRequestedCommandId = Math.max(maxRequestedCommandId, this.maxRequestedCommandId);
				reviveConnection();
			}
		}

		public void init(int maxRequestedCommandId) {
			this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
			synchronized (this) {
				this.maxRequestedCommandId = maxRequestedCommandId;
			}
			LOGGER.debug("INIT successful: " + sessionId);
			sessionListener.onUiSessionStarted(sessionId, clientInfo);
			sendAsyncWithErrorHandler(new INIT_OK());
		}

		public void handleClientRefresh(int maxRequestedCommandId, MessageSender messageSender) {
			this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
			synchronized (this) {
				this.messageSender = messageSender;
				this.commandBuffer.clear();
				commandIdCounter.set(0);
				lastReceivedClientMessageId = -1;
				connectionActive = true;
				this.maxRequestedCommandId = maxRequestedCommandId;
				lastSentCommandId = 0;
			}
			LOGGER.debug("INIT (client refresh) successful: " + sessionId);
			sessionListener.onUiSessionClientRefresh(sessionId, clientInfo);
			sendAsyncWithErrorHandler(new INIT_OK());
		}

		public void handleEvent(int clientMessageId, UiEvent event) {
			this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Recieved event ({}): {}", sessionId.getUiSessionId().substring(0, 8), event.getUiEventType());
			}
			updateClientMessageId(clientMessageId);
			reviveConnection();
			sessionListener.onUiEvent(sessionId, event);
		}

		public void handleCommandResult(int clientMessageId, int cmdId, Object result) {
			this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Recieved command result ({}): {}", sessionId.getUiSessionId().substring(0, 8));
			}
			updateClientMessageId(clientMessageId);
			reviveConnection();
			Consumer resultCallback = resultCallbacksByCmdId.remove(cmdId);
			if (resultCallback != null) {
				resultCallback.accept(result);
			} else {
				LOGGER.error("Could not find result callback for CMD_RESULT! cmdId: " + cmdId);
			}
		}

		private void updateClientMessageId(int clientMessageId) {
			if (lastReceivedClientMessageId != -1 && clientMessageId != lastReceivedClientMessageId + 1) {
				LOGGER.warn("Missing event from client? Expected event id: " + lastReceivedClientMessageId + 1 + "; Got: " + clientMessageId);
			}
			lastReceivedClientMessageId = clientMessageId;
		}

		public void reinit(int lastReceivedCommandId, int maxRequestedCommandId, MessageSender messageSender) {
			setMessageSender(messageSender);

			if (rewindToCommand(lastReceivedCommandId)) {
				LOGGER.debug("REINIT successful: " + sessionId);
				synchronized (this) {
					this.maxRequestedCommandId = Math.max(maxRequestedCommandId, this.maxRequestedCommandId);
				}
				sendAsyncWithErrorHandler(new REINIT_OK(lastReceivedClientMessageId));
				reviveConnection();
			} else {
				LOGGER.warn("Could not reinit. Command with id " + lastReceivedCommandId + "not found in command buffer.");
				sendAsyncWithErrorHandler(new REINIT_NOK(REINIT_NOK.Reason.COMMAND_ID_NOT_FOUND));
			}
		}

		private void sendAsyncWithErrorHandler(AbstractServerMessage message) {
			final long sendTime = System.currentTimeMillis();
			this.messageSender.sendMessageAsynchronously(message, (exception) -> {
				if (timestampOfLastMessageFromClient.get() <= sendTime) {
					connectionActive = false;
				}
			});
		}

		public void handleKeepAlive() {
			this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
			this.reviveConnection();
		}
	}

}
