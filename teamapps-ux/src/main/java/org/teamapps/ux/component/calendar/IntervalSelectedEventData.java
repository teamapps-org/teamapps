/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
