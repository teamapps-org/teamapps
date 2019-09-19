package org.teamapps.ux.task;

public class ProgressChangeEventData {

	private final String statusMessage;
	private final double progress;
	private final ProgressStatus status;
	private final boolean cancelable;

	public ProgressChangeEventData(ProgressStatus status, String statusMessage, double progress, boolean cancelable) {
		this.statusMessage = statusMessage;
		this.progress = progress;
		this.status = status;
		this.cancelable = cancelable;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public double getProgress() {
		return progress;
	}

	public ProgressStatus getStatus() {
		return status;
	}

	public boolean isCancelable() {
		return cancelable;
	}
}
