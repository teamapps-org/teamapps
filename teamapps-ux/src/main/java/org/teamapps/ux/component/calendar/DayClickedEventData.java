/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DayClickedEventData {

	private final ZoneId zoneId;
	private final Instant dayStart;
	private final boolean isDoubleClick;

	public DayClickedEventData(ZoneId zoneId, Instant dayStart, boolean isDoubleClick) {
		this.zoneId = zoneId;
		this.dayStart = dayStart;
		this.isDoubleClick = isDoubleClick;
	}

	public Instant getDayStart() {
		return dayStart;
	}

	/**
	 * @return the LocalDate representation of the clicked day.
	 */
	public LocalDate getDayAsLocalDate() {
		return ZonedDateTime.ofInstant(dayStart, zoneId).toLocalDate();
	}

	public boolean isDoubleClick() {
		return isDoubleClick;
	}
}
