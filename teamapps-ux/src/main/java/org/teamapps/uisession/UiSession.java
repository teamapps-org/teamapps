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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.dto.*;
import org.teamapps.uisession.statistics.RunningUiSessionStats;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class UiSession {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final QualifiedUiSessionId sessionId;
	private String name;
	private final TeamAppsConfiguration config;
	private final ObjectMapper objectMapper;
	private CopyOnWriteArrayList<UiSessionListener> sessionListeners = new CopyOnWriteArrayList<>();

	private MessageSender messageSender;

	private final CommandBuffer commandBuffer;
	private final AtomicInteger commandIdCounter = new AtomicInteger();

	private final AtomicLong timestampOfLastMessageFromClient = new AtomicLong();
	private int lastReceivedClientMessageId;
	private boolean clientReadyToReceiveCommands = true;
	private UiSessionState state = UiSessionState.ACTIVE;

	private int maxRequestedCommandId = 0;
	private int lastSentCommandId;
	private long requestedCommandsZeroTimestamp = -1;

	private class ResultCallbackWithCommandClass {
		private final Consumer<Object> callback;
		private final Class<?> commandClass;

		public ResultCallbackWithCommandClass(Consumer<Object> callback, Class<?> commandClass) {
			this.callback = callback;
			this.commandClass = commandClass;
		}
	}

	private final Map<Integer, ResultCallbackWithCommandClass> resultCallbacksByCmdId = new ConcurrentHashMap<>();

	private final RunningUiSessionStats statistics;

	public UiSession(QualifiedUiSessionId sessionId, long creationTime, TeamAppsConfiguration config, ObjectMapper objectMapper, MessageSender messageSender) {
		this.sessionId = sessionId;
		this.name = sessionId.toString();
		this.config = config;
		this.objectMapper = objectMapper;
		this.timestampOfLastMessageFromClient.set(creationTime);
		this.messageSender = messageSender;

		statistics = new RunningUiSessionStats(System.currentTimeMillis(), sessionId, name);
		commandBuffer = new CommandBuffer(config.getCommandBufferSize());
	}

	public void updateStats() {
		statistics.update(messageSender.getDataSent(), messageSender.getDataReceived());
	}

	public QualifiedUiSessionId getSessionId() {
		return sessionId;
	}

	public void setName(String name) {
		this.name = name;
		statistics.nameChanged(name);
	}

	public String getName() {
		return name;
	}

	public long getTimestampOfLastMessageFromClient() {
		return timestampOfLastMessageFromClient.get();
	}

	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}

	public void addSessionListener(UiSessionListener sessionListener) {
		this.sessionListeners.add(sessionListener);
	}

	public int sendCommand(UiCommandWithResultCallback commandWithCallback) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Sending command ({}): {}", sessionId.getUiSessionId().substring(0, 8), commandWithCallback.getUiCommand().getClass().getSimpleName());
		}
		statistics.commandSent(commandWithCallback.getUiCommand());
		CMD cmd = createCMD(commandWithCallback);
		synchronized (this) {
			try {
				commandBuffer.addCommand(cmd);
			} catch (UnconsumedCommandsOverflowException e) {
				LOGGER.error("Too many unconsumed commands!", e);
				close(UiSessionClosingReason.COMMANDS_OVERFLOW);
				return -1;
			}
			sendAllQueuedCommandsIfPossible();
			return commandBuffer.getUnconsumedCommandsCount();
		}
	}

	public ClientBackPressureInfo getClientBackPressureInfo() {
		synchronized (this) {
			return new ClientBackPressureInfo(
					config.getCommandBufferSize(), commandBuffer.getUnconsumedCommandsCount(),
					config.getClientMinRequestedCommands(), config.getClientMaxRequestedCommands(), maxRequestedCommandId - lastSentCommandId,
					requestedCommandsZeroTimestamp
			);
		}
	}

	private CMD createCMD(UiCommandWithResultCallback commandWithCallback) {
		CMD cmd;
		try {
			int cmdId = commandIdCounter.incrementAndGet();
			cmd = new CMD(cmdId, objectMapper.writeValueAsString(commandWithCallback.getUiCommand()));

			if (commandWithCallback.getResultCallback() != null) {
				cmd.setAwaitsResponse(true);
				resultCallbacksByCmdId.put(cmdId, new ResultCallbackWithCommandClass(commandWithCallback.getResultCallback(), commandWithCallback.getUiCommand().getClass()));
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
		if (clientReadyToReceiveCommands) {
			List<CMD> cmdsToSend = new ArrayList<>();
			synchronized (this) {
				while (true) {
					if (!clientReadyToReceiveCommands) {
						break;
					}
					if (lastSentCommandId >= maxRequestedCommandId) {
						clientReadyToReceiveCommands = false;
						requestedCommandsZeroTimestamp = System.currentTimeMillis();
						break;
					} else {
						requestedCommandsZeroTimestamp = -1;
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
			this.clientReadyToReceiveCommands = true;
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
		sendAsyncWithErrorHandler(new INIT_OK(
				config.getClientMinRequestedCommands(),
				config.getClientMaxRequestedCommands(),
				config.getClientEventsBufferSize(),
				config.getKeepaliveMessageIntervalMillis()
		));
	}

	/**
	 * Make sure an exception in one sessionListener does not prevent the others from being invoked!
	 */
	private void failsafeInvokeSessionListeners(Consumer<UiSessionListener> runnable) {
		sessionListeners.forEach(sl -> {
			try {
				runnable.accept(sl);
			} catch (Exception e) {
				LOGGER.error("Exception while invoking sessionListener!", e);
			}
		});
	}

	public void handleEvent(int clientMessageId, UiEvent event) {
		statistics.eventReceived(event);
		this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Recieved event ({}): {}", sessionId.getUiSessionId().substring(0, 8), event.getUiEventType());
		}
		updateClientMessageId(clientMessageId);
		reviveConnection();
		failsafeInvokeSessionListeners(sl -> sl.onUiEvent(sessionId, event)
				.exceptionally(e -> {
					LOGGER.error("Exception while handling ui event", e);
					close(UiSessionClosingReason.SERVER_SIDE_ERROR);
					return null;
				}));
	}

	public void handleQuery(int clientMessageId, UiQuery query) {
		statistics.queryReceived(query);
		this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Recieved query ({}): {}", sessionId.getUiSessionId().substring(0, 8), query.getUiQueryType());
		}
		updateClientMessageId(clientMessageId);
		reviveConnection();
		failsafeInvokeSessionListeners(sl -> sl.onUiQuery(
				sessionId,
				query,
				result -> {
					sendAsyncWithErrorHandler(new QUERY_RESULT(clientMessageId, result));
					statistics.queryResultSentFor(query);
				},
				exception -> {
					LOGGER.error("Exception while handling ui event", exception);
					close(UiSessionClosingReason.SERVER_SIDE_ERROR);
				}
		));
	}

	public void handleCommandResult(int clientMessageId, int cmdId, Object result) {
		this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Recieved command result ({}): {}", sessionId.getUiSessionId().substring(0, 8), result);
		}
		updateClientMessageId(clientMessageId);
		reviveConnection();
		ResultCallbackWithCommandClass resultCallback = resultCallbacksByCmdId.remove(cmdId);
		if (resultCallback != null) {
			statistics.commandResultReceivedFor(resultCallback.commandClass);
			resultCallback.callback.accept(result);
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
			sendAsyncWithErrorHandler(new REINIT_NOK(UiSessionClosingReason.REINIT_COMMAND_ID_NOT_FOUND));
		}
	}

	public void sendAsyncWithErrorHandler(AbstractServerMessage message) {
		final long sendTime = System.currentTimeMillis();
		this.messageSender.sendMessageAsynchronously(message, (exception) -> {
			if (timestampOfLastMessageFromClient.get() <= sendTime) {
				clientReadyToReceiveCommands = false;
			}
		});
	}

	public void handleKeepAlive() {
		this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
		this.reviveConnection();
	}

	public void ping() {
		sendAsyncWithErrorHandler(new PING());
	}

	public void setActive() {
		setState(UiSessionState.ACTIVE);
	}

	public void setNearlyInactive() {
		setState(UiSessionState.NEARLY_INACTIVE);
	}

	public void setInactive() {
		setState(UiSessionState.INACTIVE);
	}

	public void close(UiSessionClosingReason reason) {
		setState(UiSessionState.CLOSED);
		this.messageSender.close(reason, null);
	}

	private void setState(UiSessionState sessionState) {
		boolean changed = sessionState != this.state;
		this.state = sessionState;
		if (changed) {
			statistics.stateChanged(sessionState);
			failsafeInvokeSessionListeners(sl -> sl.onStateChanged(sessionId, sessionState));
		}
	}

	public UiSessionState getState() {
		return this.state;
	}

	public RunningUiSessionStats getStatistics() {
		return statistics;
	}
}
