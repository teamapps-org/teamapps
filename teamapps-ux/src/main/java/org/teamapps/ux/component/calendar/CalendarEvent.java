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

	public CalendarEvent(Instant start, Instant end, RECORD record) {
		this.start = start.toEpochMilli();
		this.end = end.toEpochMilli();
		this.record = record;
	}

	public CalendarEvent(long start, long end, RECORD record) {
		this.start = start;
		this.end = end;
		this.record = record;
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

	public CalendarEvent<RECORD> setStart(long start) {
		this.start = start;
		return this;
	}

	public CalendarEvent<RECORD> setStart(Instant start) {
		this.start = start.toEpochMilli();
		return this;
	}

	public Instant getEnd() {
		return Instant.ofEpochMilli(end);
	}

	public long getEndAsLong() {
		return end;
	}

	public CalendarEvent<RECORD> setEnd(long end) {
		this.end = end;
		return this;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public CalendarEvent<RECORD> setAllDay(boolean allDay) {
		this.allDay = allDay;
		return this;
	}

	public boolean isAllowDragOperations() {
		return allowDragOperations;
	}

	public CalendarEvent<RECORD> setAllowDragOperations(boolean allowDragOperations) {
		this.allowDragOperations = allowDragOperations;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public CalendarEvent<RECORD> setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public CalendarEvent<RECORD> setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	public CalendarEventRenderingStyle getRendering() {
		return rendering;
	}

	public CalendarEvent<RECORD> setRendering(CalendarEventRenderingStyle rendering) {
		this.rendering = rendering;
		return this;
	}

	public RECORD getRecord() {
		return record;
	}

	public CalendarEvent<RECORD> setRecord(RECORD record) {
		this.record = record;
		return this;
	}
}
