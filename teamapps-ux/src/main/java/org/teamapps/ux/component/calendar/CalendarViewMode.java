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

import org.teamapps.dto.UiCalendarViewMode;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum CalendarViewMode {
	YEAR {
		@Override
		LocalDate getDisplayStart(LocalDate localDate, DayOfWeek firstDayOfWeek) {
			return floorToFirstDayOfWeek(LocalDate.of(localDate.getYear(), 1, 1), firstDayOfWeek);
		}

		@Override
		LocalDate getDisplayEnd(LocalDate localDate, DayOfWeek firstDayOfWeek) {
			return MONTH.getDisplayEnd(LocalDate.of(localDate.getYear(), 12, 1), firstDayOfWeek);
		}

		@Override
		public LocalDate decrement(LocalDate displayedDate) {
			return displayedDate.minusYears(1);
		}

		@Override
		public LocalDate increment(LocalDate displayedDate) {
			return displayedDate.plusYears(1);
		}
	}, MONTH {
		@Override
		LocalDate getDisplayStart(LocalDate localDate, DayOfWeek firstDayOfWeek) {
			return floorToFirstDayOfWeek(localDate.withDayOfMonth(1), firstDayOfWeek);
		}

		@Override
		LocalDate getDisplayEnd(LocalDate localDate, DayOfWeek firstDayOfWeek) {
			return getDisplayStart(localDate, firstDayOfWeek).plusDays(7 * 6); // always 6 weeks
		}

		@Override
		public LocalDate decrement(LocalDate displayedDate) {
			return displayedDate.minusMonths(1);
		}

		@Override
		public LocalDate increment(LocalDate displayedDate) {
			return displayedDate.plusMonths(1);
		}
	}, WEEK {
		@Override
		LocalDate getDisplayStart(LocalDate localDate, DayOfWeek firstDayOfWeek) {
			return floorToFirstDayOfWeek(localDate, firstDayOfWeek);
		}

		@Override
		LocalDate getDisplayEnd(LocalDate localDate, DayOfWeek firstDayOfWeek) {
			return getDisplayStart(localDate, firstDayOfWeek).plusDays(7);
		}

		@Override
		public LocalDate decrement(LocalDate displayedDate) {
			return displayedDate.minusDays(7);
		}

		@Override
		public LocalDate increment(LocalDate displayedDate) {
			return displayedDate.plusDays(7);
		}
	}, DAY {
		@Override
		LocalDate getDisplayStart(LocalDate localDate, DayOfWeek firstDayOfWeek) {
			return localDate;
		}

		@Override
		LocalDate getDisplayEnd(LocalDate localDate, DayOfWeek firstDayOfWeek) {
			return getDisplayStart(localDate, firstDayOfWeek).plusDays(1);
		}

		@Override
		public LocalDate decrement(LocalDate displayedDate) {
			return displayedDate.minusDays(1);
		}

		@Override
		public LocalDate increment(LocalDate displayedDate) {
			return displayedDate.plusDays(1);
		}
	};

	private static LocalDate floorToFirstDayOfWeek(LocalDate startOfYear, DayOfWeek firstDayOfWeek) {
		if (startOfYear.getDayOfWeek() == firstDayOfWeek) {
			return startOfYear;
		} else {
			return startOfYear.minusDays(((startOfYear.getDayOfWeek().getValue() - firstDayOfWeek.getValue()) + 7) % 7);
		}
	}

	abstract LocalDate getDisplayStart(LocalDate localDate, DayOfWeek firstDayOfWeek);

	/**
	 * exclusive!! (first day AFTER the displayed interval)
	 */
	abstract LocalDate getDisplayEnd(LocalDate localDate, DayOfWeek firstDayOfWeek);

	public UiCalendarViewMode toUiCalendarViewMode() {
		return UiCalendarViewMode.valueOf(this.name());
	}

	public abstract LocalDate decrement(LocalDate displayedDate);

	public abstract LocalDate increment(LocalDate displayedDate);
}
