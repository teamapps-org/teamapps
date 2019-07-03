package org.teamapps.ux.component.calendar;

import org.teamapps.common.format.Color;

import java.time.Instant;

public interface CalendarEvent {

	long getStart();

	default Instant getStartInstant() {
		return Instant.ofEpochMilli(getStart());
	}

	long getEnd();

	default Instant getEndInstant() {
		return Instant.ofEpochMilli(getEnd());
	}

	boolean isAllDay();

	boolean isAllowDragOperations();

	Color getBackgroundColor();

	Color getBorderColor();

	CalendarEventRenderingStyle getRendering();
}
