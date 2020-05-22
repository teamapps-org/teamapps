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

import org.teamapps.data.extract.BeanPropertyExtractor;

import java.util.List;

public class SimpleCalendar<PAYLOAD> extends Calendar<SimpleCalendarEvent<PAYLOAD>> {

	public SimpleCalendar() {
		super(new SimpleCalendarModel<PAYLOAD>());
		setPropertyExtractor(new BeanPropertyExtractor<SimpleCalendarEvent<PAYLOAD>>().addProperty("description",
				event -> event.getStartInstant().atZone(getTimeZone()).format(getSessionContext().getConfiguration().getTimeFormatter())
				+ "\u2009-\u2009" + event.getEndInstant().atZone(getTimeZone()).format(getSessionContext().getConfiguration().getTimeFormatter())));
	}

	public SimpleCalendar(List<SimpleCalendarEvent<PAYLOAD>> events) {
		super(new SimpleCalendarModel<>(events));
	}

	public void setEvents(List<SimpleCalendarEvent<PAYLOAD>> events) {
		((SimpleCalendarModel<PAYLOAD>) getModel()).setEvents(events);
	}

	public void addEvent(SimpleCalendarEvent<PAYLOAD> event) {
		((SimpleCalendarModel<PAYLOAD>) getModel()).addEvent(event);
	}

	public void removeEvent(SimpleCalendarEvent<PAYLOAD> event) {
		((SimpleCalendarModel<PAYLOAD>) getModel()).removeEvent(event);
	}
}
