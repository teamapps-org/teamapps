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
package org.teamapps.server.uisession.messagebuffer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.protocol.server.AbstractReliableServerMessage;

import java.lang.invoke.MethodHandles;

/**
 * NOT THREAD-SAFE! Client code MUST provide synchronization.
 */
public class ServerMessageBuffer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final int maxFillableCapacity;
	/**
	 * The total number of characters that all messages in the buffer may hold.
	 * Keep in mind that every character in Java takes 1 or 2 bytes of heap space (prior Java 9 always 2 bytes).
	 */
	private final int maxTotalSize;
	private final ObjectMapper objectMapper;
	private final ServerMessageBufferMessage[] buffer;

	private int sequenceNumberCounter = 0;
	private int head = 0;
	private int nextConsumable = 0;
	private int tail = 0;
	private boolean bufferFlippedAtLeastOnce;
	private int totalSize = 0; // in characters

	public ServerMessageBuffer(int maxLength, int maxTotalSize, ObjectMapper objectMapper) {
		this.maxFillableCapacity = maxLength;
		this.maxTotalSize = maxTotalSize;
		this.objectMapper = objectMapper;
		buffer = new ServerMessageBufferMessage[maxFillableCapacity + 1];
	}

	public int getBufferedMessagesCount() {
		return head - tail + (head < tail ? buffer.length : 0);
	}

	public int getUnconsumedMessagesCount() {
		return head - nextConsumable + (head < nextConsumable ? buffer.length : 0);
	}

	/**
	 * @return sequence number
	 */
	public int addMessage(AbstractReliableServerMessage serverMessage) throws ServerMessageBufferException {
		int sequenceNumber = ++sequenceNumberCounter;
		serverMessage.setSequenceNumber(sequenceNumber);
		String messageString;
		try {
			messageString = objectMapper.writeValueAsString(serverMessage);
		} catch (Exception e) {
			throw new ServerMessageBufferException(e);
		}
		ServerMessageBufferMessage message = new ServerMessageBufferMessage(sequenceNumber, messageString);
		while (totalSize + message.message().length() > maxTotalSize) {
			if (!tryPurgingNextMessageFromTail()) {
				throw new ServerMessageBufferSizeOverflowException("Message buffer SIZE overflow. Max total size: " + maxTotalSize + " characters");
			}
		}
		if (getBufferedMessagesCount() == maxFillableCapacity) {
			if (!tryPurgingNextMessageFromTail()) {
				throw new ServerMessageBufferLengthOverflowException("Message buffer LENGTH overflow. Max capacity: " + maxFillableCapacity);
			}
		}
		buffer[head] = message;
		if (head + 1 == buffer.length) {
			bufferFlippedAtLeastOnce = true;
		}
		head = (head + 1) % buffer.length;
		totalSize += message.message().length();
		return sequenceNumber;
	}

	public ServerMessageBufferMessage consumeMessage() {
		if (getBufferedMessagesCount() > 0 && nextConsumable != head) {
			ServerMessageBufferMessage message = buffer[nextConsumable];
			nextConsumable = (nextConsumable + 1) % buffer.length;
			return message;
		} else {
			return null;
		}
	}

	public void purgeTillMessage(int sequenceNumberExclusive) {
		while (buffer[tail].sequenceNumber() != sequenceNumberExclusive) {
			if (!tryPurgingNextMessageFromTail()) {
				LOGGER.error("Will not purge next consumable message!");
				return;
			}
		}
	}

	private boolean tryPurgingNextMessageFromTail() {
		if (tail == nextConsumable) {
			return false;
		}
		ServerMessageBufferMessage cmd = buffer[tail];
		buffer[tail] = null;
		tail = (tail + 1) % buffer.length;
		totalSize -= cmd.message().length();
		return true;
	}

	public boolean rewindToMessage(long sequenceNumber) {
		if (sequenceNumber == -1) {
			if (bufferFlippedAtLeastOnce) {
				return false;
			} else {
				nextConsumable = 0;
				return true;
			}
		} else {
			for (int i = tail < nextConsumable ? nextConsumable - 1 : nextConsumable + buffer.length - 1; i >= tail; i--) {
				ServerMessageBufferMessage message = buffer[i % buffer.length];
				if (message == null) { // message already purged! - should never happen since only acknowledged messages get purged!
					return false;
				}
				if (message.sequenceNumber() == sequenceNumber) {
					nextConsumable = (i + 1) % buffer.length;
					return true;
				}
			}
			return false;
		}
	}

	public void clear() {
		head = 0;
		nextConsumable = 0;
		tail = 0;
	}
}
