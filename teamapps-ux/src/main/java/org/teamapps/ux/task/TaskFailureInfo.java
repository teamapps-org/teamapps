package org.teamapps.ux.task;

public class TaskFailureInfo {
	private final String message;
	private final Throwable throwable;

	public TaskFailureInfo(String message) {
		this(message, null);
	}

	public TaskFailureInfo(String message, Throwable throwable) {
		this.message = message;
		this.throwable = throwable;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
