/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.calendar;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ViewChangedEventData {

	private final ZoneId zoneId;
	private final CalendarViewMode viewMode;
	private final Instant mainIntervalStart;
	private final Instant mainIntervalEnd;
	private final Instant displayedIntervalStart;
	private final Instant displayedIntervalEnd;

	public ViewChangedEventData(ZoneId zoneId, CalendarViewMode viewMode, Instant mainIntervalStart, Instant mainIntervalEnd,
	                            Instant displayedIntervalStart, Instant displayedIntervalEnd) {
		this.zoneId = zoneId;
		this.viewMode = viewMode;
		this.mainIntervalStart = mainIntervalStart;
		this.mainIntervalEnd = mainIntervalEnd;
		this.displayedIntervalStart = displayedIntervalStart;
		this.displayedIntervalEnd = displayedIntervalEnd;
	}

	/**
	 * @return the LocalDate representation of the main interval start (e.g. first day of displayed month in month view).
	 */
	public LocalDate getMainIntervalStartAsLocalDate() {
		return ZonedDateTime.ofInstant(mainIntervalStart, zoneId).toLocalDate();
	}

	/**
	 * @return the LocalDate representation of the main interval end (e.g. first day of month after displayed month in month view).
	 * This date is exclusive, i.e. NOT part of the main interval anymore.
	 */
	public LocalDate getMainIntervalEndAsLocalDate() {
		return ZonedDateTime.ofInstant(mainIntervalEnd, zoneId).toLocalDate();
	}

	/**
	 * @return the LocalDate representation of the displayed interval start (e.g. first day of week (monday or sunday) before first day of month in month view).
	 */
	public LocalDate getDisplayedIntervalStartAsLocalDate() {
		return ZonedDateTime.ofInstant(displayedIntervalStart, zoneId).toLocalDate();
	}

	/**
	 * @return the LocalDate representation of the displayed interval end.
	 * This date is exclusive, i.e. NOT part of the displayed interval anymore.
	 */
	public LocalDate getDisplayedIntervalEndAsLocalDate() {
		return ZonedDateTime.ofInstant(displayedIntervalEnd, zoneId).toLocalDate();
	}

	public CalendarViewMode getViewMode() {
		return viewMode;
	}

	public Instant getMainIntervalStart() {
		return mainIntervalStart;
	}

	public Instant getMainIntervalEnd() {
		return mainIntervalEnd;
	}

	public Instant getDisplayedIntervalStart() {
		return displayedIntervalStart;
	}

	public Instant getDisplayedIntervalEnd() {
		return displayedIntervalEnd;
	}

}
