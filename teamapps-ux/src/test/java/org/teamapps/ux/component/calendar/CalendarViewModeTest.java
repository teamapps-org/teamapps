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

import org.junit.Assert;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class CalendarViewModeTest {

	@Test
	public void testGetDisplayStartInstantForYear() throws Exception {
		int displayedYear = 2000;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(1999, 12, 27), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(1999, 12, 28), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(1999, 12, 29), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(1999, 12, 30), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(1999, 12, 31), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2000, 1, 1), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(1999, 12, 26), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}

		displayedYear = 2001;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(2001, 1, 1), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(2000, 12, 26), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(2000, 12, 27), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(2000, 12, 28), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(2000, 12, 29), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2000, 12, 30), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(2000, 12, 31), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}

		displayedYear = 2002;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(2001, 12, 31), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(2002, 1, 1), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(2001, 12, 26), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(2001, 12, 27), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(2001, 12, 28), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2001, 12, 29), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(2001, 12, 30), CalendarViewMode.YEAR.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}
	}

	@Test
	public void getDisplayEndInstantForYear() throws Exception {
		int displayedYear = 2000;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(2001, 1, 8), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 9), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 10), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 11), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 12), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 6), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 7), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}

		displayedYear = 2001;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(2002, 1, 7), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(2002, 1, 8), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(2002, 1, 9), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(2002, 1, 10), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(2002, 1, 11), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2002, 1, 12), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(2002, 1, 6), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}

		displayedYear = 2002;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(2003, 1, 6), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(2003, 1, 7), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(2003, 1, 8), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(2003, 1, 9), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(2003, 1, 10), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2003, 1, 11), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(2003, 1, 12), CalendarViewMode.YEAR.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}
	}

	@Test
	public void testGetDisplayStartInstantForMonth() throws Exception {
		int displayedYear = 2000;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(2000, 1, 31), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(2000, 2, 1), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(2000, 1, 26), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(2000, 1, 27), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(2000, 1, 28), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2000, 1, 29), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(2000, 1, 30), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}

		displayedYear = 2001;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(2001, 1, 29), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 30), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 31), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(2001, 2, 1), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 26), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 27), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(2001, 1, 28), CalendarViewMode.MONTH.getDisplayStart(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}
	}

	@Test
	public void getDisplayEndInstantForMonth() throws Exception {
		int displayedYear = 2000;
		for (int februaryDay = 1; februaryDay <= 14; februaryDay++) {
			Assert.assertEquals(LocalDate.of(2000, 3, 13), CalendarViewMode.MONTH.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.MONDAY));
			Assert.assertEquals(LocalDate.of(2000, 3, 14), CalendarViewMode.MONTH.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.TUESDAY));
			Assert.assertEquals(LocalDate.of(2000, 3, 8), CalendarViewMode.MONTH.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.WEDNESDAY));
			Assert.assertEquals(LocalDate.of(2000, 3, 9), CalendarViewMode.MONTH.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.THURSDAY));
			Assert.assertEquals(LocalDate.of(2000, 3, 10), CalendarViewMode.MONTH.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.FRIDAY));
			Assert.assertEquals(LocalDate.of(2000, 3, 11), CalendarViewMode.MONTH.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SATURDAY));
			Assert.assertEquals(LocalDate.of(2000, 3, 12), CalendarViewMode.MONTH.getDisplayEnd(LocalDate.of(displayedYear, 2, februaryDay), DayOfWeek.SUNDAY));
		}
	}

}
