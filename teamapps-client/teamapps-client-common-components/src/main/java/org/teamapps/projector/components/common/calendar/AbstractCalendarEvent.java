/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.projector.components.common.calendar;

import org.teamapps.common.format.Color;

import java.time.Instant;

public class AbstractCalendarEvent implements CalendarEvent {

	private long start;
	private long end;
	private boolean allDay;
	private boolean allowDragOperations;
	private Color backgroundColor;
	private Color borderColor;
	private CalendarEventRenderingStyle rendering = CalendarEventRenderingStyle.DEFAULT;

	public AbstractCalendarEvent(Instant start, Instant end) {
		this.start = start.toEpochMilli();
		this.end = end.toEpochMilli();
	}

	public AbstractCalendarEvent(long start, long end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		return "AbstractCalendarEvent{" +
				"start=" + start +
				", end=" + end +
				", allDay=" + allDay +
				", allowDragOperations=" + allowDragOperations +
				", backgroundColor=" + backgroundColor +
				", borderColor=" + borderColor +
				", rendering=" + rendering +
				'}';
	}

	@Override
	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	@Override
	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	@Override
	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	@Override
	public boolean isAllowDragOperations() {
		return allowDragOperations;
	}

	public void setAllowDragOperations(boolean allowDragOperations) {
		this.allowDragOperations = allowDragOperations;
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	@Override
	public CalendarEventRenderingStyle getRendering() {
		return rendering;
	}

	public void setRendering(CalendarEventRenderingStyle rendering) {
		this.rendering = rendering;
	}

}
