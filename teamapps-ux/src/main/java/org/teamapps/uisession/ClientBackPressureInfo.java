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
