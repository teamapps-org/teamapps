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
package org.teamapps.uisession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOT THREAD-SAFE! Synchronization must be provided by using code.
 */
public class CommandBuffer {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandBuffer.class);

	private final int maxFillableCapacity;
	private final CMD[] buffer;
	private int head = 0;
	private int nextConsumable = 0;
	private int tail = 0;
	private boolean bufferFlippedAtLeastOnce;

	public CommandBuffer(int capacity) {
		this.maxFillableCapacity = capacity;
		buffer = new CMD[maxFillableCapacity + 1];
	}

	public int getBufferedCommandsCount() {
		return head - tail + (head < tail ? buffer.length : 0);
	}

	public int getUnconsumedCommandsCount() {
		return head - nextConsumable + (head < nextConsumable ? buffer.length : 0);
	}

	public void addCommand(CMD command) throws UnconsumedCommandsOverflowException {
		if (getBufferedCommandsCount() == maxFillableCapacity) {
			if (tail == nextConsumable) {
				throw new UnconsumedCommandsOverflowException("Command buffer overflow. Max capacity: " + maxFillableCapacity);
			}
			tail = (tail + 1) % buffer.length;
		}
		buffer[head] = command;
		if (head + 1 == buffer.length) {
			bufferFlippedAtLeastOnce = true;
		}
		head = (head + 1) % buffer.length;
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
			if (tail == nextConsumable) {
				LOGGER.error("Will not purge next consumable command!");
				return;
			}
			buffer[tail] = null;
			tail = (tail + 1) % buffer.length;
		}
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
