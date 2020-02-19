/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCalendarModel<PAYLOAD> extends AbstractCalendarModel<SimpleCalendarEvent<PAYLOAD>> {

	private List<SimpleCalendarEvent<PAYLOAD>> events;

	public SimpleCalendarModel(List<SimpleCalendarEvent<PAYLOAD>> events) {
		this.events = new ArrayList<>(events);
	}

	public SimpleCalendarModel() {
		this(new ArrayList<>());
	}

	@Override
	public List<SimpleCalendarEvent<PAYLOAD>> getEventsForInterval(Instant start, Instant end) {
		return events.stream()
				.filter(event -> event.getEnd() >= start.toEpochMilli() && event.getStart() < end.toEpochMilli())
				.collect(Collectors.toList());
	}

	public List<SimpleCalendarEvent<PAYLOAD>> getEvents() {
		return events;
	}

	public void setEvents(List<SimpleCalendarEvent<PAYLOAD>> events) {
		this.events = events;
		this.onCalendarDataChanged.fire(null);
	}

	public void addEvent(SimpleCalendarEvent<PAYLOAD> event) {
		this.events.add(event);
		this.onCalendarDataChanged.fire(null);
	}

	public void removeEvent(SimpleCalendarEvent<PAYLOAD> event) {
		this.events.remove(event);
		this.onCalendarDataChanged.fire(null);
	}
	
}
