package org.teamapps.uisession.statistics;

public class TimestampedLong {

	private final long timestamp;
	private final long value;

	public TimestampedLong(long timestamp, long value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getValue() {
		return value;
	}
}
