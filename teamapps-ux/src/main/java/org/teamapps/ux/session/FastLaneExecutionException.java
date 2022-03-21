package org.teamapps.ux.session;

public class FastLaneExecutionException extends RuntimeException {

	public FastLaneExecutionException() {
	}

	public FastLaneExecutionException(String message) {
		super(message);
	}

	public FastLaneExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public FastLaneExecutionException(Throwable cause) {
		super(cause);
	}

	public FastLaneExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
