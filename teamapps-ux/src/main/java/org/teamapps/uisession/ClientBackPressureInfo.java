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

public class ClientBackPressureInfo {

	private final int maxCommandBufferSize;
	private final int unconsumedCommandsCount;

	private final int minRequestedCommands;
	private final int maxRequestedCommands;
	private final int remainingRequestedCommands;

	private final long requestedCommandsBelowMinTimestamp;

	public ClientBackPressureInfo(int maxCommandBufferSize, int unconsumedCommandsCount, int minRequestedCommands, int maxRequestedCommands, int remainingRequestedCommands,
	                              long requestedCommandsBelowMinTimestamp) {
		this.maxCommandBufferSize = maxCommandBufferSize;
		this.unconsumedCommandsCount = unconsumedCommandsCount;
		this.minRequestedCommands = minRequestedCommands;
		this.maxRequestedCommands = maxRequestedCommands;
		this.remainingRequestedCommands = remainingRequestedCommands;
		this.requestedCommandsBelowMinTimestamp = requestedCommandsBelowMinTimestamp;
	}

	public boolean isBusy() {
		boolean clientNotRequestingMoreCommands = requestedCommandsBelowMinTimestamp > 0 && requestedCommandsBelowMinTimestamp < System.currentTimeMillis() - 500;
		boolean commandBufferCriticallyFull = unconsumedCommandsCount > 500;
		return clientNotRequestingMoreCommands || commandBufferCriticallyFull;
	}

	public int getMaxCommandBufferSize() {
		return maxCommandBufferSize;
	}

	public int getUnconsumedCommandsCount() {
		return unconsumedCommandsCount;
	}

	public int getMinRequestedCommands() {
		return minRequestedCommands;
	}

	public int getMaxRequestedCommands() {
		return maxRequestedCommands;
	}

	public int getRemainingRequestedCommands() {
		return remainingRequestedCommands;
	}

	public long getRequestedCommandsBelowMinTimestamp() {
		return requestedCommandsBelowMinTimestamp;
	}

	@Override
	public String toString() {
		return "ClientBackPressureInfo{" +
				"maxCommandBufferSize=" + maxCommandBufferSize +
				", commandBufferFillSize=" + unconsumedCommandsCount +
				", minRequestedCommands=" + minRequestedCommands +
				", maxRequestedCommands=" + maxRequestedCommands +
				", remainingRequestedCommands=" + remainingRequestedCommands +
				", requestedCommandsBelowMinTimestamp=" + requestedCommandsBelowMinTimestamp +
				", timeSinceLastCommandRequestAfterZero=" + (System.currentTimeMillis() - requestedCommandsBelowMinTimestamp) +
				'}';
	}
}
