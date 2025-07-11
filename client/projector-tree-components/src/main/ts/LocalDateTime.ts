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
import {
	type DateObjectUnits,
	DateTime,
	type DateTimeFormatOptions,
	type DateTimeUnit,
	type DiffOptions,
	Duration,
	type DurationLike,
	type DurationUnit,
	type EndOfOptions,
	type HasSameOptions,
	Interval,
	type LocaleOptions,
	type PossibleDaysInMonth,
	type ToISODateOptions,
	type ToISOTimeOptions,
	Zone,
	type ZoneOptions
} from "luxon";

export type LocalDateObject = DateObjectUnits & LocaleOptions;

var LOCAL_ZONE = Intl.DateTimeFormat().resolvedOptions().timeZone;

export class LocalDateTime {

	private dateTime: DateTime; // guaranteed to be "UTC"

	constructor(dateTime: DateTime) {
		this.dateTime = dateTime.setZone("UTC", {keepLocalTime: true});
	}

	static fromDateTime(dateTime: DateTime) {
		return new LocalDateTime(dateTime)
	}

	static fromObject(obj: LocalDateObject) {
		return new LocalDateTime(DateTime.fromObject(obj))
	}

	static local() {
		return new LocalDateTime(DateTime.local())
	}

	static fromISO(text: string) {
		return new LocalDateTime(DateTime.fromISO(text));
	}

	get day(): number {
		return this.dateTime.day;
	}

	get daysInMonth(): PossibleDaysInMonth {
		return this.dateTime.daysInMonth;
	}

	get daysInYear(): number {
		return this.dateTime.daysInYear;
	}

	get hour(): number {
		return this.dateTime.hour;
	}

	get invalidReason(): string | null {
		return this.dateTime.invalidReason;
	}

	get invalidExplanation(): string | null {
		return this.dateTime.invalidExplanation;
	}

	get isInLeapYear(): boolean {
		return this.dateTime.isInLeapYear;
	}

	get isValid(): boolean {
		return this.dateTime.isValid;
	}

	get locale(): string {
		return this.dateTime.locale;
	}

	get millisecond(): number {
		return this.dateTime.millisecond;
	}

	get minute(): number {
		return this.dateTime.minute;
	}

	get month(): number {
		return this.dateTime.month;
	}

	get monthLong(): string {
		return this.dateTime.monthLong;
	}

	get monthShort(): string {
		return this.dateTime.monthShort;
	}

	get numberingSystem(): string {
		return this.dateTime.numberingSystem;
	}

	get ordinal(): number {
		return this.dateTime.ordinal;
	}

	get outputCalendar(): string {
		return this.dateTime.outputCalendar;
	}

	get quarter(): number {
		return this.dateTime.quarter;
	}

	get second(): number {
		return this.dateTime.second;
	}

	get weekNumber(): number {
		return this.dateTime.weekNumber;
	}

	get weekYear(): number {
		return this.dateTime.weekYear;
	}

	get weekday(): number {
		return this.dateTime.weekday;
	}

	get weekdayLong(): string {
		return this.dateTime.weekdayLong;
	}

	get weekdayShort(): string {
		return this.dateTime.weekdayShort;
	}

	get weeksInWeekYear(): number {
		return this.dateTime.weeksInWeekYear;
	}

	get year(): number {
		return this.dateTime.year;
	}

	diff(other: LocalDateTime, unit?: DurationUnit | DurationUnit[], options?: DiffOptions): Duration {
		return this.dateTime.diff(other.dateTime, unit, options);
	}

	diffNow(unit?: DurationUnit | DurationUnit[], options?: DiffOptions): Duration {
		return this.diff(LocalDateTime.local(), unit, options);
	}

	endOf(unit: DateTimeUnit, opts?: EndOfOptions): LocalDateTime {
		return new LocalDateTime(this.dateTime.endOf(unit, opts));
	}

	equals(other: LocalDateTime): boolean {
		return this.dateTime.equals(other.dateTime);
	}

	get(unit: keyof DateTime): number {
		return this.dateTime.get(unit);
	}

	hasSame(other: LocalDateTime, unit: DateTimeUnit, opts?: HasSameOptions): boolean {
		return this.dateTime.hasSame(other.dateTime, unit, opts);
	}

	minus(duration: DurationLike): LocalDateTime {
		return new LocalDateTime(this.dateTime.minus(duration));
	}

	plus(duration: DurationLike): LocalDateTime {
		return new LocalDateTime(this.dateTime.plus(duration));
	}

	reconfigure(properties: LocaleOptions): LocalDateTime {
		return new LocalDateTime(this.dateTime.reconfigure(properties));
	}

	set(values: DateObjectUnits): LocalDateTime {
		return new LocalDateTime(this.dateTime.set(values));
	}

	setLocale(locale: string): LocalDateTime {
		return new LocalDateTime(this.dateTime.setLocale(locale));
	}

	startOf(unit: DateTimeUnit, opts?: { useLocaleWeeks?: boolean }): LocalDateTime {
		return new LocalDateTime(this.dateTime.startOf(unit, opts));
	}

	toBSON(): Date {
		return this.dateTime.toBSON();
	}

	toFormat(format: string, options?: DateTimeFormatOptions): string {
		options = this.normalizeDateTimeFormatOptionsForChrome(options);
		return this.dateTime.toFormat(format, options);
	}

	toHTTP(): string {
		return this.dateTime.toHTTP();
	}

	toISO(options?: ToISOTimeOptions): string {
		return this.dateTime.toISO(options);
	}

	toISODate(options?: ToISODateOptions): string {
		return this.dateTime.toISODate(options);
	}

	toISOTime(options?: ToISOTimeOptions): string {
		return this.dateTime.toISOTime(options);
	}

	toISOWeekDate(): string {
		return this.dateTime.toISOWeekDate();
	}

	toJSON(): string {
		return this.dateTime.toJSON();
	}

	toZoned(zone: string | Zone, options?: ZoneOptions): DateTime {
		return this.dateTime.setZone(zone, {keepLocalTime: true, ...options});
	}

	toUTC(offset?: number, options?: ZoneOptions): DateTime {
		return this.dateTime.toUTC(offset, {...options, keepLocalTime: true});
	}

	toLocal(): DateTime {
		return this.toZoned(LOCAL_ZONE);
	}

	toLocaleParts(options?: LocaleOptions & DateTimeFormatOptions): (any & { type: string })[] {
		options = this.normalizeDateTimeFormatOptionsForChrome(options);
		return this.dateTime.toLocaleParts(options);
	}

	toLocaleString(options?: LocaleOptions & DateTimeFormatOptions): string {
		options = this.normalizeDateTimeFormatOptionsForChrome(options);
		return this.dateTime.toLocaleString(options);
	}

	toString(): string {
		return this.dateTime.year + "-" + this.dateTime.month + "-" + this.dateTime.day;
	}

	until(other: DateTime): Interval {
		return this.dateTime.until(other) as Interval;
	}

	valueOf() {
		return this.dateTime.valueOf();
	}

	private normalizeDateTimeFormatOptionsForChrome(options?: DateTimeFormatOptions): DateTimeFormatOptions {
		if (options?.fractionalSecondDigits == null) {
			return options;
		}
		return {...options, fractionalSecondDigits: options.fractionalSecondDigits > 0 ? options.fractionalSecondDigits : undefined};
	}
}
