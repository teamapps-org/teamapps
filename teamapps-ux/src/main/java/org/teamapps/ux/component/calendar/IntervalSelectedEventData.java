package org.teamapps.ux.component.calendar;

import java.time.Instant;
import java.time.ZoneId;

public class IntervalSelectedEventData {

	private final ZoneId timeZone;
	private final Instant start;
	private final Instant end;
	private final boolean allDay;

	public IntervalSelectedEventData(ZoneId timeZone, Instant start, Instant end, boolean allDay) {
		this.timeZone = timeZone;
		this.start = start;
		this.end = end;
		this.allDay = allDay;
	}

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public Instant getStart() {
		return start;
	}

	public Instant getEnd() {
		return end;
	}

	public boolean isAllDay() {
		return allDay;
	}
}
