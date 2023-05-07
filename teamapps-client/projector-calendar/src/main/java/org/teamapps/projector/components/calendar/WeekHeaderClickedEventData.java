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
package org.teamapps.projector.components.calendar;

import java.time.LocalDate;
import java.time.ZoneId;

public class WeekHeaderClickedEventData {

	private final ZoneId zoneId;
	private final int year;
	private final int week;
	private final LocalDate startOfWeek;

	public WeekHeaderClickedEventData(ZoneId zoneId, int year, int week, LocalDate startOfWeek) {
		this.zoneId = zoneId;
		this.year = year;
		this.week = week;
		this.startOfWeek = startOfWeek;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}

	public int getYear() {
		return year;
	}

	public int getWeek() {
		return week;
	}

	public LocalDate getStartOfWeek() {
		return startOfWeek;
	}
}
