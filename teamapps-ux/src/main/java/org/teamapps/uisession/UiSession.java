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
import it.unimi.dsi.fastutil.ints.IntConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.dto.JsonWrapper;
import org.teamapps.dto.protocol.server.SessionClosingReason;
import org.teamapps.dto.protocol.server.*;
import org.teamapps.uisession.messagebuffer.ServerMessageBuffer;
import org.teamapps.uisession.messagebuffer.ServerMessageBufferException;
import org.teamapps.uisession.messagebuffer.ServerMessageBufferMessage;
import org.teamapps.uisession.statistics.RunningUiSessionStats;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class UiSession {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String sessionId;
	private String name;
	private final TeamAppsConfiguration config;
	private final CopyOnWriteArrayList<UiSessionListener> sessionListeners = new CopyOnWriteArrayList<>();

	private MessageSender messageSender;

	private final ServerMessageBuffer serverMessageBuffer;

	private final AtomicLong timestampOfLastMessageFromClient = new AtomicLong();
	private int lastReceivedClientMessageSequenceNumber;
	private boolean clientReadyToReceiveCommands = true;
	private UiSessionState state = UiSessionState.ACTIVE;

	private int maxRequestedSequenceNumber = 0;
	private int lastSentSequenceNumber;
	private long requestNZeroTimestamp = -1;

	private record ResultCallbackWithCommandName(Consumer<Object> callback, String commandName) {
	}

	private final Map<Integer, ResultCallbackWithCommandName> resultCallbacksByCmdId = new ConcurrentHashMap<>();

	private final RunningUiSessionStats statistics;

	public UiSession(String sessionId, long creationTime, TeamAppsConfiguration config, ObjectMapper objectMapper, MessageSender messageSender) {
		this.sessionId = sessionId;
		this.name = sessionId;
		this.config = config;
		this.timestampOfLastMessageFromClient.set(creationTime);
		this.messageSender = messageSender;

		statistics = new RunningUiSessionStats(System.currentTimeMillis(), sessionId, name);
		serverMessageBuffer = new ServerMessageBuffer(config.getCommandBufferLength(), config.getCommandBufferTotalSize(), objectMapper);
	}

	public void updateStats() {
		statistics.update(messageSender.getDataSent(), messageSender.getDataReceived());
	}

	public String getSessionId() {
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

	public void sendCommand(UiCommandWithResultCallback commandWithCallback) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Sending command ({}): {}", sessionId.substring(0, 8), commandWithCallback.getCommandName());
		}
		statistics.commandSent(commandWithCallback.getParams().getClass().getCanonicalName());
		CMD cmd = new CMD(commandWithCallback.getLibraryUuid(), commandWithCallback.getClientObjectId(),
				commandWithCallback.getCommandName(), commandWithCallback.getParams(), commandWithCallback.getResultCallback() != null);
		sendServerMessage(cmd, sequenceNumber -> {
			if (commandWithCallback.getResultCallback() != null) {
				resultCallbacksByCmdId.put(sequenceNumber, new ResultCallbackWithCommandName(commandWithCallback.getResultCallback(), commandWithCallback.getCommandName()));
			}
		});
	}

	public void sendServerMessage(AbstractReliableServerMessage serverMessage) {
		sendServerMessage(serverMessage, null);
	}

	private void sendServerMessage(AbstractReliableServerMessage serverMessage, IntConsumer sequenceNumberHandler) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Sending server message ({}): {}", sessionId.substring(0, 8), serverMessage.getClass().getSimpleName());
		}
		synchronized (this) {
			try {
				int sequenceNumber = serverMessageBuffer.addMessage(serverMessage);
				if (sequenceNumberHandler != null) {
					sequenceNumberHandler.accept(sequenceNumber);
				}
			} catch (ServerMessageBufferException e) {
				LOGGER.error("Exception while adding command to CommandBuffer!", e);
				close(SessionClosingReason.COMMANDS_OVERFLOW);
			} catch (Exception e) {
				LOGGER.error("Exception while creating CMD!", e);
				close(SessionClosingReason.SERVER_SIDE_ERROR);
			}
			sendQueuedCommands();
		}
	}

	public ClientBackPressureInfo getClientBackPressureInfo() {
		synchronized (this) {
			return new ClientBackPressureInfo(
					config.getCommandBufferLength(),
					serverMessageBuffer.getBufferedMessagesCount(),
					serverMessageBuffer.getUnconsumedMessagesCount(),
					config.getClientMinRequestedCommands(),
					config.getClientMaxRequestedCommands(),
					maxRequestedSequenceNumber - lastSentSequenceNumber,
					requestNZeroTimestamp
			);
		}
	}

	public boolean rewindToCommand(int commandId) {
		synchronized (this) {
			this.lastSentSequenceNumber = commandId - 1;
			return serverMessageBuffer.rewindToMessage(commandId);
		}
	}

	private void sendQueuedCommands() {
		if (clientReadyToReceiveCommands) {
			List<ServerMessageBufferMessage> cmdsToSend = new ArrayList<>();
			synchronized (this) {
				while (true) {
					if (!clientReadyToReceiveCommands) {
						break;
					}
					if (lastSentSequenceNumber >= maxRequestedSequenceNumber) {
						clientReadyToReceiveCommands = false;
						requestNZeroTimestamp = System.currentTimeMillis();
						break;
					} else {
						requestNZeroTimestamp = -1;
					}
					var cmd = serverMessageBuffer.consumeMessage();
					if (cmd != null) {
						lastSentSequenceNumber = cmd.sequenceNumber();
						cmdsToSend.add(cmd);
					} else {
						break;
					}
				}
			}
			if (!cmdsToSend.isEmpty()) {
				sendAsyncWithErrorHandler(cmdsToSend);
			}
		}
	}

	public void reviveConnection() {
		synchronized (this) {
			this.clientReadyToReceiveCommands = true;
			sendQueuedCommands();
		}
	}

	public void handleCommandRequest(int maxRequestedCommandId, Integer lastReceivedCommandIdOrNull) {
		LOGGER.trace("UiSession.requestCommands: maxRequestedCommandId = [" + maxRequestedCommandId + "]");
		this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
		synchronized (this) {
			if (lastReceivedCommandIdOrNull != null) {
				this.serverMessageBuffer.purgeTillMessage(lastReceivedCommandIdOrNull);
			}
			this.maxRequestedSequenceNumber = Math.max(maxRequestedCommandId, this.maxRequestedSequenceNumber);
			reviveConnection();
		}
	}

	public void sendInitOk() {
		LOGGER.debug("Sending INIT_OK for {}", sessionId);
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

	public void handleEvent(int sequenceNumber, String libraryId, String clientObjectId, String name, List<JsonWrapper> params) {
		statistics.eventReceived(libraryId + "." + name);
		this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Recieved event ({}): {}.{}", sessionId.substring(0, 8), sequenceNumber, name);
		}
		updateClientMessageSequenceNumber(sequenceNumber);
		reviveConnection();
		failsafeInvokeSessionListeners(sl -> sl.handleEvent(sessionId, libraryId, clientObjectId, name, params));
	}

	public void handleQuery(int sequenceNumber, String libraryId, String clientObjectId, String name, List<JsonWrapper> params) {
		statistics.queryReceived(libraryId + "." + name);
		this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Recieved query ({}): {}.{}", sessionId.substring(0, 8), sequenceNumber, name);
		}
		updateClientMessageSequenceNumber(sequenceNumber);
		reviveConnection();
		Consumer<Object> resultCallback = result -> {
			int resultSequenceNumber = sequenceNumberCounter.incrementAndGet();
			sendAsyncWithErrorHandler(new QUERY_RES(resultSequenceNumber, sequenceNumber, result));
			statistics.queryResultSentFor(libraryId + "." + name);
		};
		failsafeInvokeSessionListeners(sl -> sl.handleQuery(sessionId, libraryId, clientObjectId, name, params, resultCallback));
	}

	public void handleCommandResult(int sequenceNumber, int cmdId, Object result) {
		this.timestampOfLastMessageFromClient.set(System.currentTimeMillis());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Recieved command result ({}): {}", sessionId.substring(0, 8), result);
		}
		updateClientMessageSequenceNumber(sequenceNumber);
		reviveConnection();
		ResultCallbackWithCommandName resultCallback = resultCallbacksByCmdId.remove(cmdId);
		if (resultCallback != null) {
			statistics.commandResultReceivedFor(resultCallback.commandName());
			resultCallback.callback.accept(result);
		} else {
			LOGGER.error("Could not find result callback for CMD_RESULT! cmdId: " + cmdId);
		}
	}

	private void updateClientMessageSequenceNumber(int clientMessageSequenceNumber) {
		if (lastReceivedClientMessageSequenceNumber != -1 && clientMessageSequenceNumber != lastReceivedClientMessageSequenceNumber + 1) {
			LOGGER.warn("Missing event from client? Expected event id: " + lastReceivedClientMessageSequenceNumber + 1 + "; Got: " + clientMessageSequenceNumber);
		}
		lastReceivedClientMessageSequenceNumber = clientMessageSequenceNumber;
	}

	public void reinit(int lastReceivedCommandId, int maxRequestedCommandId, MessageSender messageSender) {
		setMessageSender(messageSender);

		if (rewindToCommand(lastReceivedCommandId)) {
			LOGGER.debug("REINIT successful: " + sessionId);
			synchronized (this) {
				this.maxRequestedSequenceNumber = Math.max(maxRequestedCommandId, this.maxRequestedSequenceNumber);
			}
			sendAsyncWithErrorHandler(new REINIT_OK(lastReceivedClientMessageSequenceNumber));
			reviveConnection();
		} else {
			LOGGER.warn("Could not reinit. Command with id " + lastReceivedCommandId + "not found in command buffer.");
			sendAsyncWithErrorHandler(new REINIT_NOK(SessionClosingReason.REINIT_COMMAND_ID_NOT_FOUND));
		}
	}

	public void sendAsyncWithErrorHandler(AbstractServerMessage message) {
		sendAsyncWithErrorHandler(List.of(message));
	}

	public void sendAsyncWithErrorHandler(List<AbstractServerMessage> messages) {
		final long sendTime = System.currentTimeMillis();
		this.messageSender.sendAsynchronously(messages, (exception) -> {
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

	public void close(SessionClosingReason reason) {
		if (this.state == UiSessionState.CLOSED) {
			return; // already closed. nothing to do
		}
		setState(UiSessionState.CLOSED);
		failsafeInvokeSessionListeners(sl -> sl.onClosed(sessionId, reason)); // note that this is executed AFTER the state change handlers!
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
