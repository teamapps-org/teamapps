/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.calendar;

import org.teamapps.common.format.Color;

import java.time.Instant;
import java.time.ZoneOffset;

public class CalendarEvent<RECORD> {

	private long start;
	private long end;
	private boolean allDay;
	private boolean allowDragOperations;
	private Color backgroundColor;
	private Color borderColor;
	private CalendarEventRenderingStyle rendering = CalendarEventRenderingStyle.DEFAULT;

	private RECORD record;

	public CalendarEvent(Instant start, Instant end, String title, boolean allDay, RECORD record) {
		this.start = start.toEpochMilli();
		this.end = end.toEpochMilli();
		this.allDay = allDay;
		this.record = record;
	}

	public CalendarEvent(Instant start, Instant end, String title, boolean allDay) {
		this(start, end, title, allDay, null);
	}

	public CalendarEvent(Instant start, Instant end, String title, RECORD record) {
		this(start, end, title, false, record);
	}

	public CalendarEvent(Instant start, Instant end, String title) {
		this(start, end, title, false, null);
	}

	public CalendarEvent(long start, long end, String title, boolean allDay, RECORD record) {
		this.start = start;
		this.end = end;
		this.allDay = allDay;
		this.record = record;
	}

	public CalendarEvent(long start, long end, String title, boolean allDay) {
		this(start, end, title, allDay, null);
	}

	public CalendarEvent(long start, long end, String title, RECORD record) {
		this(start, end, title, false, record);
	}

	public CalendarEvent(long start, long end, String title) {
		this(start, end, title, false, null);
	}

	@Override
	public String toString() {
		return "CalendarEvent{" +
				"start=" + start + " (" + Instant.ofEpochMilli(start).atZone(ZoneOffset.UTC).toString() + ")" +
				", end=" + Instant.ofEpochMilli(end).atZone(ZoneOffset.UTC).toString() +
				", allDay=" + allDay +
				", allowDragOperations=" + allowDragOperations +
				", backgroundColor=" + backgroundColor +
				", borderColor=" + borderColor +
				", rendering=" + rendering +
				", record=" + record +
				'}';
	}

	public Instant getStart() {
		return Instant.ofEpochMilli(start);
	}

	public long getStartAsLong() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void setStart(Instant start) {
		this.start = start.toEpochMilli();
	}

	public Instant getEnd() {
		return Instant.ofEpochMilli(end);
	}

	public long getEndAsLong() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public boolean isAllowDragOperations() {
		return allowDragOperations;
	}

	public void setAllowDragOperations(boolean allowDragOperations) {
		this.allowDragOperations = allowDragOperations;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public CalendarEventRenderingStyle getRendering() {
		return rendering;
	}

	public void setRendering(CalendarEventRenderingStyle rendering) {
		this.rendering = rendering;
	}

	public RECORD getRecord() {
		return record;
	}

	public void setRecord(RECORD record) {

		this.record = record;
	}
}
