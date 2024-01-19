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
import {
	DateObjectUnits,
	DateTime,
	DateTimeFormatOptions,
	DiffOptions,
	Duration,
	DurationObject,
	DurationUnit,
	Interval,
	LocaleOptions,
	ToISODateOptions,
	ToISOTimeOptions,
	ToSQLOptions,
	Zone,
	ZoneOptions
} from "luxon";

export type LocalDateObject = DateObjectUnits & LocaleOptions;

var LOCAL_ZONE = Intl.DateTimeFormat().resolvedOptions().timeZone;

export class LocalDateTime implements Omit<DateTime, "zone" | "zoneName" | "isInDST" | "isOffsetFixed" | "offsetNameShort" | "offsetNameLong" | "offset" | "setZone" | "diff" | "diffNow" | "endOf" | "equals" | "hasSame" | "minus" | "plus" | "reconfigure" | "set" | "setLocale" | "startOf" | "toJSDate" | "toMillis" | "toRelative" | "toRelativeCalendar" | "toRFC2822" | "toSeconds"> {

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

	set day(value: number) {
		this.dateTime.day = value;
	}

	get daysInMonth(): number {
		return this.dateTime.daysInMonth;
	}

	set daysInMonth(value: number) {
		this.dateTime.daysInMonth = value;
	}

	get daysInYear(): number {
		return this.dateTime.daysInYear;
	}

	set daysInYear(value: number) {
		this.dateTime.daysInYear = value;
	}

	get hour(): number {
		return this.dateTime.hour;
	}

	set hour(value: number) {
		this.dateTime.hour = value;
	}

	get invalidReason(): string | null {
		return this.dateTime.invalidReason;
	}

	set invalidReason(value: string | null) {
		this.dateTime.invalidReason = value;
	}

	get invalidExplanation(): string | null {
		return this.dateTime.invalidExplanation;
	}

	set invalidExplanation(value: string | null) {
		this.dateTime.invalidExplanation = value;
	}

	get isInLeapYear(): boolean {
		return this.dateTime.isInLeapYear;
	}

	set isInLeapYear(value: boolean) {
		this.dateTime.isInLeapYear = value;
	}

	get isValid(): boolean {
		return this.dateTime.isValid;
	}

	set isValid(value: boolean) {
		this.dateTime.isValid = value;
	}

	get locale(): string {
		return this.dateTime.locale;
	}

	set locale(value: string) {
		this.dateTime.locale = value;
	}

	get millisecond(): number {
		return this.dateTime.millisecond;
	}

	set millisecond(value: number) {
		this.dateTime.millisecond = value;
	}

	get minute(): number {
		return this.dateTime.minute;
	}

	set minute(value: number) {
		this.dateTime.minute = value;
	}

	get month(): number {
		return this.dateTime.month;
	}

	set month(value: number) {
		this.dateTime.month = value;
	}

	get monthLong(): string {
		return this.dateTime.monthLong;
	}

	set monthLong(value: string) {
		this.dateTime.monthLong = value;
	}

	get monthShort(): string {
		return this.dateTime.monthShort;
	}

	set monthShort(value: string) {
		this.dateTime.monthShort = value;
	}

	get numberingSystem(): string {
		return this.dateTime.numberingSystem;
	}

	set numberingSystem(value: string) {
		this.dateTime.numberingSystem = value;
	}

	get ordinal(): number {
		return this.dateTime.ordinal;
	}

	set ordinal(value: number) {
		this.dateTime.ordinal = value;
	}

	get outputCalendar(): string {
		return this.dateTime.outputCalendar;
	}

	set outputCalendar(value: string) {
		this.dateTime.outputCalendar = value;
	}

	get quarter(): number {
		return this.dateTime.quarter;
	}

	set quarter(value: number) {
		this.dateTime.quarter = value;
	}

	get second(): number {
		return this.dateTime.second;
	}

	set second(value: number) {
		this.dateTime.second = value;
	}

	get weekNumber(): number {
		return this.dateTime.weekNumber;
	}

	set weekNumber(value: number) {
		this.dateTime.weekNumber = value;
	}

	get weekYear(): number {
		return this.dateTime.weekYear;
	}

	set weekYear(value: number) {
		this.dateTime.weekYear = value;
	}

	get weekday(): number {
		return this.dateTime.weekday;
	}

	set weekday(value: number) {
		this.dateTime.weekday = value;
	}

	get weekdayLong(): string {
		return this.dateTime.weekdayLong;
	}

	set weekdayLong(value: string) {
		this.dateTime.weekdayLong = value;
	}

	get weekdayShort(): string {
		return this.dateTime.weekdayShort;
	}

	set weekdayShort(value: string) {
		this.dateTime.weekdayShort = value;
	}

	get weeksInWeekYear(): number {
		return this.dateTime.weeksInWeekYear;
	}

	set weeksInWeekYear(value: number) {
		this.dateTime.weeksInWeekYear = value;
	}

	get year(): number {
		return this.dateTime.year;
	}

	set year(value: number) {
		this.dateTime.year = value;
	}

	diff(other: LocalDateTime, unit?: DurationUnit | DurationUnit[], options?: DiffOptions): Duration {
		return this.dateTime.diff(other.dateTime, unit, options);
	}

	diffNow(unit?: DurationUnit | DurationUnit[], options?: DiffOptions): Duration {
		return this.diff(LocalDateTime.local(), unit, options);
	}

	endOf(unit: DurationUnit): LocalDateTime {
		return new LocalDateTime(this.dateTime.endOf(unit));
	}

	equals(other: LocalDateTime): boolean {
		return this.dateTime.equals(other.dateTime);
	}

	get(unit: keyof DateTime): number {
		return this.dateTime.get(unit);
	}

	hasSame(other: LocalDateTime, unit: DurationUnit): boolean {
		return this.dateTime.hasSame(other.dateTime, unit);
	}

	minus(duration: Duration | number | DurationObject): LocalDateTime {
		return new LocalDateTime(this.dateTime.minus(duration));
	}

	plus(duration: Duration | number | DurationObject): LocalDateTime {
		return new LocalDateTime(this.dateTime.plus(duration));
	}

	reconfigure(properties: LocaleOptions): LocalDateTime {
		return new LocalDateTime(this.dateTime.reconfigure(properties));
	}

	resolvedLocaleOpts(options?: DateTimeFormatOptions): Intl.ResolvedDateTimeFormatOptions {
		return this.dateTime.resolvedLocaleOpts(options);
	}

	set(values: DateObjectUnits): LocalDateTime {
		return new LocalDateTime(this.dateTime.set(values));
	}

	setLocale(locale: string): LocalDateTime {
		return new LocalDateTime(this.dateTime.setLocale(locale));
	}

	startOf(unit: DurationUnit): LocalDateTime {
		return new LocalDateTime(this.dateTime.startOf(unit));
	}

	toBSON(): Date {
		return this.dateTime.toBSON();
	}

	toFormat(format: string, options?: DateTimeFormatOptions): string {
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
		return this.dateTime.toLocaleParts(options);
	}

	toLocaleString(options?: LocaleOptions & DateTimeFormatOptions): string {
		return this.dateTime.toLocaleString(options);
	}

	toObject(options?: { includeConfig?: boolean }): LocalDateObject {
		let dateObject = this.dateTime.toObject(options);
		dateObject = {...dateObject};
		delete dateObject.zone;
		return dateObject;
	}

	toSQL(options?: ToSQLOptions): string {
		return this.dateTime.toSQL({...options, includeOffset: false, includeZone: false});
	}

	toSQLDate(): string {
		return this.dateTime.toSQLDate();
	}

	toSQLTime(options?: ToSQLOptions): string {
		return this.dateTime.toSQLTime({...options, includeOffset: false, includeZone: false});
	}

	toString(): string {
		return this.dateTime.year + "-" + this.dateTime.month + "-" + this.dateTime.day;
	}

	until(other: DateTime): Interval {
		return this.dateTime.until(other);
	}

	valueOf() {
		return this.dateTime.valueOf();
	}
}
