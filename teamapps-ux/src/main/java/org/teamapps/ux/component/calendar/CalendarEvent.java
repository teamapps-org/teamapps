package org.teamapps.ux.component.calendar;

import org.teamapps.common.format.Color;
import org.teamapps.icons.api.Icon;

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

	/**
	 * In case the event is rendered without a template, use this icon.
 	 */
	default Icon getIcon() {
		return null;
	}

	/**
	 * In case the event is rendered without a template, use this title.
	 */
	default String getTitle() {
		return null;
	}

	Color getBackgroundColor();

	Color getBorderColor();

	CalendarEventRenderingStyle getRendering();

	boolean isAllowDragOperations();
}
