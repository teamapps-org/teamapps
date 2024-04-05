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

import {LocalDateTime} from "./LocalDateTime";

export interface DateSuggestion {
	date: LocalDateTime;
	ymdOrder: string;
}

export type YearMonthDayOrder = "YMD" | "YDM" | "MDY" | "MYD" | "DMY" | "DYM";

export interface Options {
	locale?: string,
	preferredYearMonthDayOrder?: YearMonthDayOrder,

	/**
	 * By default, the engine favors future dates. This flag makes it favor past dates.
	 */
	favorPastDates?: boolean
}

interface FictiveLocalDate {
	year: number,
	month: number,
	day: number
}

export class DateSuggestionEngine {

	private preferredYmdOrder: YearMonthDayOrder;
	private favorPastDates: boolean;

	constructor(options: Options) {
		this.preferredYmdOrder = options.preferredYearMonthDayOrder
			?? (options.locale != null ? getYearMonthDayOrderForLocale(options.locale) : undefined)
			?? "YMD";
		this.favorPastDates = options.favorPastDates ?? false;
	}

	public generateSuggestions(searchString: string, now: LocalDateTime, options?: {
		suggestAdjacentWeekForEmptyInput?: boolean,
		shuffledFormatSuggestionsEnabled?: boolean
	}): LocalDateTime[] {

		options = {
			suggestAdjacentWeekForEmptyInput: true,
			shuffledFormatSuggestionsEnabled: false,
			...(options ?? {})
		}

		if (searchString.replace(/[^\d]/g, '').length == 0) {
			if (options.suggestAdjacentWeekForEmptyInput) {
				return this.createAdjacentWeekDaySuggestions(now).map(s => s.date);
			} else {
				return [];
			}
		}

		let suggestions: DateSuggestion[];
		if (searchString.match(/[^\d]/)) {
			let fragments = searchString.split(/[^\d]/).filter(f => !!f);
			suggestions = this.createSuggestionsForFragments(fragments, now);
		} else {
			suggestions = this.generateSuggestionsForDigitsOnlyInput(searchString, now);
		}

		// sort by relevance
		let preferredYmdOrder: YearMonthDayOrder = this.preferredYmdOrder;
		suggestions.sort(function (a, b) {
			if (preferredYmdOrder.indexOf(a.ymdOrder) === -1 && preferredYmdOrder.indexOf(b.ymdOrder) !== -1) {
				return 1;
			} else if (preferredYmdOrder.indexOf(a.ymdOrder) !== -1 && preferredYmdOrder.indexOf(b.ymdOrder) === -1) {
				return -1;
			} else if (a.ymdOrder.length != b.ymdOrder.length) { // D < DM < DMY
				return a.ymdOrder.length - b.ymdOrder.length;
			} else {
				return a.date.diff(now, 'days').days - b.date.diff(now, 'days').days; // nearer is better
			}
		});

		return suggestions
			.filter(value => {
				return options.shuffledFormatSuggestionsEnabled || preferredYmdOrder.indexOf(value.ymdOrder) !== -1
			})
			.map(s => s.date)
			.filter(this.removeDuplicateDates());
	}

	private removeDuplicateDates() {
		let seenDates: LocalDateTime[] = [];
		return (d: LocalDateTime) => {
			let dateAlreadyContained = seenDates.filter(seenDate => d.equals(seenDate)).length > 0;
			if (dateAlreadyContained) {
				return false;
			} else {
				seenDates.push(d);
				return true;
			}
		};
	}

	public generateSuggestionsForDigitsOnlyInput(input: string, today: LocalDateTime): DateSuggestion[] {
		input = input || "";

		if (input.length === 0) {
			return this.createSuggestionsForFragments([], today);
		} else if (input.length > 8) {
			return [];
		}

		let fragmentSets: [string, string, string][] = [];
		for (let i = 1; i <= input.length; i++) {
			for (let j = Math.min(input.length, i + 1); j <= input.length && j - i <= 4; j - i === 2 ? j += 2 : j++) {
				let fragments: [string, string, string] = [input.substring(0, i), input.substring(i, j), input.substring(j, input.length)];
				if (this.validateFragments(fragments)){
					fragmentSets.push(fragments);
				}
			}
		}

		let allSuggestions: DateSuggestion[] = [];
		for (const fragments of fragmentSets) {
			let suggestions = this.createSuggestionsForFragments(fragments, today);
			allSuggestions = allSuggestions.concat(suggestions);
		}

		return allSuggestions;
	}

	private validateFragments(fragments: [string, string, string]) {
		if (fragments.some(f => f.length === 3)) { // do not allow fragments of length 3
			return false;
		}
		if (fragments.some(f => f.length > 4)) { // do not allow fragments of length > 4
			return false;
		}
		if (fragments.filter(f => f.length > 2).length > 1) { // do not allow more than one fragment with length > 2 (so 4)
			return false;
		}
		if (fragments.some(f => f.length == 4 && f.charAt(0) == '0')) { // do not allow 4 digit fragments padded with zeros
			return false;
		}
		return true;
	}

	todayOrFavoriteDirection(date: LocalDateTime, today: LocalDateTime): boolean {
		return this.favorPastDates ? today.startOf("day") >= date.startOf("day") : today.startOf("day") <= date.startOf("day");
	}

	private createSuggestionsForFragments(fragments: string[], today: LocalDateTime): DateSuggestion[] {
		function mod(n: number, m: number) {
			return ((n % m) + m) % m;
		}

		function numberToYear(n: number): number {
			let shortYear = today.year % 100;
			let yearSuggestionBoundary = (shortYear + 20) % 100; // suggest 20 years into the future and 80 year backwards
			let currentCentury = Math.floor(today.year / 100) * 100;
			if (n >= 1000 && n <= 9999) { // four digit year
				if (n >= 1900 && n <= 2100) {
					return n;
				} else {
					return null; // we're not getting more historic or futuristic here
				}
			} else {
				if (n < yearSuggestionBoundary) {
					return currentCentury + n;
				} else if (n < 100) {
					return currentCentury - 100 + n;
				} else if (n > today.year - 120 && n < today.year + 120) {
					return n;
				} else {
					return null;
				}
			}
		}

		let [s1, s2, s3] = fragments;
		let [n1, n2, n3] = [parseInt(s1), parseInt(s2), parseInt(s3)];
		let suggestions = [];

		if (!s1 && !s2 && !s3) {
			return this.createAdjacentWeekDaySuggestions(today);
		} else if (s1 && !s2 && !s3) {
			if (n1 > 0 && n1 <= 31) {
				let nextValidDate = this.findNextValidDate({
					year: today.year,
					month: today.month,
					day: n1
				}, (currentDate) => {
					// increase month
					return {
						year: currentDate.year + (this.favorPastDates ? (currentDate.month == 1 ? -1 : 0) : (currentDate.month == 12 ? 1 : 0)),
						month: mod((currentDate.month - 1) + (this.favorPastDates ? -1 : 1), 12) + 1,
						day: currentDate.day
					}
				}, today);
				if (nextValidDate) {
					suggestions.push(createSuggestion(nextValidDate, "D"));
				}
			}
		} else if (s1 && s2 && !s3) {
			if (n1 <= 12 && n2 > 0 && n2 <= 31) {
				let nextValidDate = this.findNextValidDate({
					year: today.year,
					month: n1,
					day: n2
				}, (currentDate) => {
					return {
						year: currentDate.year + (this.favorPastDates ? -1 : 1),
						month: currentDate.month,
						day: currentDate.day
					}
				}, today);
				if (nextValidDate) {
					suggestions.push(createSuggestion(nextValidDate, "MD"));
				}
			}
			if (n2 <= 12 && n1 > 0 && n1 <= 31) {
				let nextValidDate = this.findNextValidDate({
					year: today.year,
					month: n2,
					day: n1
				}, (currentDate) => {
					return {
						year: currentDate.year + (this.favorPastDates ? -1 : 1),
						month: currentDate.month,
						day: currentDate.day
					}
				}, today);
				if (nextValidDate) {
					suggestions.push(createSuggestion(nextValidDate, "DM"));
				}
			}
		} else { // s1 && s2 && s3
			let dateTime;
			if (numberToYear(n1) != null) {
				dateTime = LocalDateTime.fromObject({year: numberToYear(n1), month: n2, day: n3});
				if (dateTime.isValid) {
					suggestions.push(createSuggestion(dateTime, "YMD"));
				}
				dateTime = LocalDateTime.fromObject({year: numberToYear(n1), month: n3, day: n2});
				if (dateTime.isValid) {
					suggestions.push(createSuggestion(dateTime, "YDM"));
				}
			}
			if (numberToYear(n2) != null) {
				dateTime = LocalDateTime.fromObject({year: numberToYear(n2), month: n1, day: n3});
				if (dateTime.isValid) {
					suggestions.push(createSuggestion(dateTime, "MYD"));
				}
				dateTime = LocalDateTime.fromObject({year: numberToYear(n2), month: n3, day: n1});
				if (dateTime.isValid) {
					suggestions.push(createSuggestion(dateTime, "DYM"));
				}
			}
			if (numberToYear(n3) != null) {
				dateTime = LocalDateTime.fromObject({year: numberToYear(n3), month: n1, day: n2});
				if (dateTime.isValid) {
					suggestions.push(createSuggestion(dateTime, "MDY"));
				}
				dateTime = LocalDateTime.fromObject({year: numberToYear(n3), month: n2, day: n1});
				if (dateTime.isValid) {
					suggestions.push(createSuggestion(dateTime, "DMY"));
				}
			}
		}

		return suggestions;
	};

	private createAdjacentWeekDaySuggestions(today: LocalDateTime) {
		let result = [];
		for (let i = 0; i < 7; i++) {
			result.push(createSuggestion(today.plus({days: this.favorPastDates ? -i : i}), ""));
		}
		return result;
	}

	private findNextValidDate(startDate: FictiveLocalDate, incementor: (currentDate: FictiveLocalDate) => FictiveLocalDate, today: LocalDateTime): LocalDateTime {
		let currentFictiveDate = startDate;
		let currentDateTime: LocalDateTime = LocalDateTime.fromObject(currentFictiveDate);
		let numberOfIterations = 0;
		while (!(currentDateTime.isValid && this.todayOrFavoriteDirection(currentDateTime, today)) && numberOfIterations < 4) {
			currentFictiveDate = incementor(currentFictiveDate);
			currentDateTime = LocalDateTime.fromObject(currentFictiveDate);
			numberOfIterations++;
		}
		return currentDateTime.isValid ? currentDateTime : null;
	}
}

function createSuggestion(date: LocalDateTime, ymdOrder: string): DateSuggestion {
	return {date, ymdOrder};
}

export function getYearMonthDayOrderFromDateFormat(dateFormat: string): YearMonthDayOrder {
	let ymdIndexes: { [key: string]: number } = {
		Y: dateFormat.toUpperCase().indexOf("Y"),
		M: dateFormat.toUpperCase().indexOf("M"),
		D: dateFormat.toUpperCase().indexOf("D")
	};
	return <YearMonthDayOrder>(["D", "M", "Y"].sort((a, b) => ymdIndexes[a] - ymdIndexes[b]).join(""));
}

export function getYearMonthDayOrderForLocale(locale: string): YearMonthDayOrder {
	let parts = LocalDateTime.fromObject({
		year: 2020,
		month: 11,
		day: 30
	}).setLocale(locale).toLocaleParts({
		year: "numeric",
		month: "2-digit",
		day: "2-digit"
	});
	return parts
		.filter(p => p.type === "year" || p.type === "month" || p.type === "day")
		.map(p => (p.type as string).charAt(0).toUpperCase())
		.join("") as YearMonthDayOrder;
}
