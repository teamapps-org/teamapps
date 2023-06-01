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
package org.teamapps.uisession.commandbuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.protocol.CMD;

import java.lang.invoke.MethodHandles;

/**
 * NOT THREAD-SAFE! Synchronization must be provided by client code.
 */
public class CommandBuffer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final int maxFillableCapacity;
	/**
	 * The total number of characters that all commands in the command buffer of a session may hold.
	 * Keep in mind that every character in Java takes 1 or 2 bytes of heap space (prior Java 9 always 2 bytes).
	 */
	private final int maxTotalSize;
	private final CMD[] buffer;
	private int head = 0;
	private int nextConsumable = 0;
	private int tail = 0;
	private boolean bufferFlippedAtLeastOnce;
	private int totalSize = 0; // in characters

	public CommandBuffer(int maxLength, int maxTotalSize) {
		this.maxFillableCapacity = maxLength;
		this.maxTotalSize = maxTotalSize;
		buffer = new CMD[maxFillableCapacity + 1];
	}

	public int getBufferedCommandsCount() {
		return head - tail + (head < tail ? buffer.length : 0);
	}

	public int getUnconsumedCommandsCount() {
		return head - nextConsumable + (head < nextConsumable ? buffer.length : 0);
	}

	public int getCommandsSize() {
		int size = 0;
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] != null) {
				size += buffer[i].getUiCommand().length();
			}
		}
		return size;
	}

	public void addCommand(CMD command) throws CommandBufferException {
		while (totalSize + command.getUiCommand().length() > maxTotalSize) {
			if (!tryPurgingNextCommandFromTail()) {
				throw new CommandBufferSizeOverflowException("Command buffer SIZE overflow. Max total size: " + maxTotalSize + " characters");
			}
		}
		if (getBufferedCommandsCount() == maxFillableCapacity) {
			if (!tryPurgingNextCommandFromTail()) {
				throw new CommandBufferLengthOverflowException("Command buffer LENGTH overflow. Max capacity: " + maxFillableCapacity);
			}
		}
		buffer[head] = command;
		if (head + 1 == buffer.length) {
			bufferFlippedAtLeastOnce = true;
		}
		head = (head + 1) % buffer.length;
		totalSize += command.getUiCommand().length();
	}

	public CMD consumeCommand() {
		if (getBufferedCommandsCount() > 0 && nextConsumable != head) {
			CMD command = buffer[nextConsumable];
			nextConsumable = (nextConsumable + 1) % buffer.length;
			return command;
		} else {
			return null;
		}
	}

	public void purgeTillCommand(int commandIdExclusive) {
		while (buffer[tail].getId() != commandIdExclusive) {
			if (!tryPurgingNextCommandFromTail()) {
				LOGGER.error("Will not purge next consumable command!");
				return;
			}
		}
	}

	private boolean tryPurgingNextCommandFromTail() {
		if (tail == nextConsumable) {
			return false;
		}
		CMD cmd = buffer[tail];
		buffer[tail] = null;
		tail = (tail + 1) % buffer.length;
		totalSize -= cmd.getUiCommand().length();
		return true;
	}

	public boolean rewindToCommand(long commandId) {
		if (commandId == -1) {
			if (bufferFlippedAtLeastOnce) {
				return false;
			} else {
				nextConsumable = 0;
				return true;
			}
		} else {
			for (int i = tail < nextConsumable ? nextConsumable - 1 : nextConsumable + buffer.length - 1; i >= tail; i--) {
				CMD command = buffer[i % buffer.length];
				if (command == null) { // command already purged! - should never happen since only acknowledged commands get purged!
					return false;
				}
				if (command.getId() == commandId) {
					nextConsumable = (i + 1) % buffer.length;
					return true;
				}
			}
			return false;
		}
	}

	public long getNextCommandId() {
		return buffer[nextConsumable].getId();
	}

	public void clear() {
		head = 0;
		nextConsumable = 0;
		tail = 0;
	}
}
