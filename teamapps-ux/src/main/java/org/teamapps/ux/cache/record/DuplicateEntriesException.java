package org.teamapps.ux.cache.record;

public class DuplicateEntriesException extends RuntimeException {

	public DuplicateEntriesException() {
	}

	public DuplicateEntriesException(String message) {
		super(message);
	}

	public DuplicateEntriesException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateEntriesException(Throwable cause) {
		super(cause);
	}

	public DuplicateEntriesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
