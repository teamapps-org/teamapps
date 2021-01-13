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
package org.teamapps.ux.session;

import org.teamapps.dto.*;

public class DateTimeFormatDescriptor {

	// either dateStyle and/or timeStyle can be set, or the other fields. Both does not work. Exception: hourCycle
	private final FullLongMediumShortType dateStyle;
	private final FullLongMediumShortType timeStyle;

	private final Integer fractionalSecondDigits;
	private final Boolean dayPeriod;
	private final HourCycleType hourCycle;

	private final LongShortNarrowType weekday;
	private final LongShortNarrowType era;
	private final NumericType year;
	private final NumericOrLongShortNarrowType month;
	private final NumericType day;
	private final NumericType hour;
	private final NumericType minute;
	private final NumericType second;

	public static Builder builer() {
		return new Builder();
	}

	public static DateTimeFormatDescriptor forDate(FullLongMediumShortType dateStyle) {
		return new DateTimeFormatDescriptor(dateStyle, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	public static DateTimeFormatDescriptor forTime(FullLongMediumShortType timeStyle) {
		return new DateTimeFormatDescriptor(null, timeStyle, null, null, null, null, null, null, null, null, null, null, null);
	}

	public static DateTimeFormatDescriptor forTime(FullLongMediumShortType timeStyle, HourCycleType hourCycle) {
		return new DateTimeFormatDescriptor(null, timeStyle, null, null, hourCycle, null, null, null, null, null, null, null, null);
	}

	private DateTimeFormatDescriptor(FullLongMediumShortType dateStyle, FullLongMediumShortType timeStyle,
									 Integer fractionalSecondDigits, Boolean dayPeriod, HourCycleType hourCycle,
									 LongShortNarrowType weekday, LongShortNarrowType era, NumericType year,
									 NumericOrLongShortNarrowType month, NumericType day, NumericType hour,
									 NumericType minute, NumericType second) {
		this.dateStyle = dateStyle;
		this.timeStyle = timeStyle;
		this.fractionalSecondDigits = fractionalSecondDigits;
		this.dayPeriod = dayPeriod;
		this.hourCycle = hourCycle;
		this.weekday = weekday;
		this.era = era;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	public UiDateTimeFormatDescriptor toDateTimeFormatDescriptor() {
		UiDateTimeFormatDescriptor ui = new UiDateTimeFormatDescriptor();
		ui.setDateStyle(dateStyle != null ? dateStyle.toUiValue() : null);
		ui.setTimeStyle(timeStyle != null ? timeStyle.toUiValue() : null);
		ui.setFractionalSecondDigits(fractionalSecondDigits == null || fractionalSecondDigits == 0 ? null : fractionalSecondDigits); // 0 not accepted by Chrome!
		ui.setDayPeriod(dayPeriod);
		ui.setHourCycle(hourCycle != null ? hourCycle.toUiValue() : null);
		ui.setWeekday(weekday != null ? weekday.toUiValue() : null);
		ui.setEra(era != null ? era.toUiValue() : null);
		ui.setYear(year != null ? year.toUiValue() : null);
		ui.setMonth(month != null ? month.toUiValue() : null);
		ui.setDay(day != null ? day.toUiValue() : null);
		ui.setHour(hour != null ? hour.toUiValue() : null);
		ui.setMinute(minute != null ? minute.toUiValue() : null);
		ui.setSecond(second != null ? second.toUiValue() : null);
		return ui;
	}

	public FullLongMediumShortType getDateStyle() {
		return dateStyle;
	}

	public FullLongMediumShortType getTimeStyle() {
		return timeStyle;
	}

	public Boolean getDayPeriod() {
		return dayPeriod;
	}

	public Integer getFractionalSecondDigits() {
		return fractionalSecondDigits;
	}

	public Boolean isDayPeriod() {
		return dayPeriod;
	}

	public HourCycleType getHourCycle() {
		return hourCycle;
	}

	public LongShortNarrowType getWeekday() {
		return weekday;
	}

	public LongShortNarrowType getEra() {
		return era;
	}

	public NumericType getYear() {
		return year;
	}

	public NumericOrLongShortNarrowType getMonth() {
		return month;
	}

	public NumericType getDay() {
		return day;
	}

	public NumericType getHour() {
		return hour;
	}

	public NumericType getMinute() {
		return minute;
	}

	public NumericType getSecond() {
		return second;
	}

	// === BUILDER ===

	public static class Builder {
		private Integer fractionalSecondDigits;
		private Boolean dayPeriod;
		private HourCycleType hourCycle;

		private LongShortNarrowType weekday;
		private LongShortNarrowType era;
		private NumericType year;
		private NumericOrLongShortNarrowType month;
		private NumericType day;
		private NumericType hour;
		private NumericType minute;
		private NumericType second;

		public Builder setFractionalSecondDigits(Integer fractionalSecondDigits) {
			this.fractionalSecondDigits = fractionalSecondDigits;
			return this;
		}

		public Builder setDayPeriod(Boolean dayPeriod) {
			this.dayPeriod = dayPeriod;
			return this;
		}

		public Builder setHourCycle(HourCycleType hourCycle) {
			this.hourCycle = hourCycle;
			return this;
		}

		public Builder setWeekday(LongShortNarrowType weekday) {
			this.weekday = weekday;
			return this;
		}

		public Builder setEra(LongShortNarrowType era) {
			this.era = era;
			return this;
		}

		public Builder setYear(NumericType year) {
			this.year = year;
			return this;
		}

		public Builder setMonth(NumericOrLongShortNarrowType month) {
			this.month = month;
			return this;
		}

		public Builder setDay(NumericType day) {
			this.day = day;
			return this;
		}

		public Builder setHour(NumericType hour) {
			this.hour = hour;
			return this;
		}

		public Builder setMinute(NumericType minute) {
			this.minute = minute;
			return this;
		}

		public Builder setSecond(NumericType second) {
			this.second = second;
			return this;
		}

		public DateTimeFormatDescriptor build() {
			return new DateTimeFormatDescriptor(
					null, null,
					fractionalSecondDigits,
					dayPeriod,
					hourCycle,
					weekday,
					era,
					year,
					month,
					day,
					hour,
					minute,
					second
			);
		}
	}

	// === ENUMS ===

	public enum HourCycleType {
		/**
		 * 0-11
		 */
		H11("h11"),

		/**
		 * 1-12
		 */
		H12("h12"),

		/**
		 * 0-23
		 */
		H23("h23"),

		/**
		 * 1-24
		 */
		H24("h24");

		private final String jsLiteral;

		HourCycleType(String jsLiteral) {
			this.jsLiteral = jsLiteral;
		}

		public String getJsLiteral() {
			return jsLiteral;
		}

		public UiHourCycleType toUiValue() {
			return UiHourCycleType.valueOf(name());
		}
	}

	public enum FullLongMediumShortType {
		FULL("full"),
		LONG("long"),
		MEDIUM("medium"),
		SHORT("short");

		private final String jsLiteral;

		FullLongMediumShortType(String jsLiteral) {
			this.jsLiteral = jsLiteral;
		}

		public String getJsLiteral() {
			return jsLiteral;
		}

		public UiFullLongMediumShortType toUiValue() {
			return UiFullLongMediumShortType.valueOf(name());
		}
	}

	public enum LongShortNarrowType {
		LONG("long"),
		SHORT("short"),
		NARROW("narrow");

		private final String jsLiteral;

		LongShortNarrowType(String jsLiteral) {
			this.jsLiteral = jsLiteral;
		}

		public String getJsLiteral() {
			return jsLiteral;
		}

		public UiLongShortNarrowType toUiValue() {
			return UiLongShortNarrowType.valueOf(name());
		}
	}

	public enum NumericType {
		NUMERIC("numeric"),
		TWO_DIGIT("2-digit");

		private final String jsLiteral;

		NumericType(String jsLiteral) {
			this.jsLiteral = jsLiteral;
		}

		public String getJsLiteral() {
			return jsLiteral;
		}

		public UiNumericType toUiValue() {
			return UiNumericType.valueOf(name());
		}
	}

	public enum NumericOrLongShortNarrowType {
		NUMERIC("numeric"),
		TWO_DIGIT("2-digit"),
		LONG("long"),
		SHORT("short"),
		NARROW("narrow");

		private final String jsLiteral;

		NumericOrLongShortNarrowType(String jsLiteral) {
			this.jsLiteral = jsLiteral;
		}

		public String getJsLiteral() {
			return jsLiteral;
		}

		public UiNumericOrLongShortNarrowType toUiValue() {
			return UiNumericOrLongShortNarrowType.valueOf(name());
		}
	}

}
