/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
/*
 *
 *  Copyright 2016 Yann Massard (https://github.com/yamass) and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import Moment = moment.Moment;

import moment from "moment-timezone";

export interface DateSuggestion {
	moment: Moment;
	ymdOrder: string;
}
export type YearMonthDayOrder = "YMD" | "YDM" | "MDY" | "MYD" | "DMY" | "DYM";

export interface Options {
	preferredDateFormat?: string,

	/**
	 * By default, the engine favors future dates. This flag makes it favor past dates.
	 */
	favorPastDates?: boolean
}

interface LocalDate {
	year:number,
	/** zero-indexed! */
	month: number,
	day: number
}

export class TrivialDateSuggestionEngine {
	
	private options: Options;

	constructor(options: Options) {
		this.options = {
			preferredDateFormat: "YYYY-MM-DD",
			favorPastDates: false,
			...options
		}
	}

	public generateSuggestions(searchString: string, now: Moment | Date): DateSuggestion[] {
		now = moment(now);
		let suggestions: DateSuggestion[];
		if (searchString.match(/[^\d]/)) {
			let fragments = searchString.split(/[^\d]/).filter(f => !!f);
			suggestions = this.createSuggestionsForFragments(fragments, now);
		} else {
			suggestions = this.generateSuggestionsForDigitsOnlyInput(searchString, now);
		}

		// sort by relevance
		let preferredYmdOrder: YearMonthDayOrder = TrivialDateSuggestionEngine.dateFormatToYmdOrder(this.options.preferredDateFormat);
		suggestions.sort(function (a, b) {
			if (preferredYmdOrder.indexOf(a.ymdOrder) === -1 && preferredYmdOrder.indexOf(b.ymdOrder) !== -1) {
				return 1;
			} else if (preferredYmdOrder.indexOf(a.ymdOrder) !== -1 && preferredYmdOrder.indexOf(b.ymdOrder) === -1) {
				return -1;
			} else if (a.ymdOrder.length != b.ymdOrder.length) { // D < DM < DMY
				return a.ymdOrder.length - b.ymdOrder.length;
			} else {
				return a.moment.diff(now, 'days') - b.moment.diff(now, 'days'); // nearer is better
			}
		});

		suggestions = this.removeDuplicates(suggestions);

		return suggestions;
	}

	private removeDuplicates(suggestions: DateSuggestion[]) {
		let seenDates: Moment[] = [];
		return suggestions.filter(s => {
			let dateAlreadyContained = seenDates.filter(seenDate => s.moment.isSame(seenDate, 'day')).length > 0;
			if (dateAlreadyContained) {
				return false;
			} else {
				seenDates.push(s.moment);
				return true;
			}
		});
	}

	public static dateFormatToYmdOrder(dateFormat: string): YearMonthDayOrder {
		let ymdIndexes: { [key: string]: number } = {
			D: dateFormat.indexOf("D"),
			M: dateFormat.indexOf("M"),
			Y: dateFormat.indexOf("Y")
		};
		return <YearMonthDayOrder> (["D", "M", "Y"].sort((a, b) => ymdIndexes[a] - ymdIndexes[b]).join(""));
	}

	private static createSuggestion(moment: Moment, ymdOrder: string): DateSuggestion {
		return {moment, ymdOrder};
	}

	public generateSuggestionsForDigitsOnlyInput(input: string, today: Moment): DateSuggestion[] {
		input = input || "";

		if (input.length === 0) {
			return this.createSuggestionsForFragments([], today);
		} else if (input.length > 8) {
			return [];
		}

		let suggestions: DateSuggestion[] = [];
		for (let i = 1; i <= input.length; i++) {
			for (let j = Math.min(input.length, i + 1); j <= input.length && j - i <= 4; j - i === 2 ? j += 2 : j++) {
				suggestions = suggestions.concat(this.createSuggestionsForFragments([input.substring(0, i), input.substring(i, j), input.substring(j, input.length)], today));
			}
		}
		return suggestions;
	}

	todayOrFavoriteDirection (m: Moment, today: Moment): boolean {
		return this.options.favorPastDates ? today.isSameOrAfter(m, 'day') : today.isSameOrBefore(m, 'day');
	}

	private createSuggestionsForFragments(fragments: string[], today: Moment): DateSuggestion[] {
		function mod(n:number, m:number) {
			return ((n % m) + m) % m;
		}

		function numberToYear(n: number): number {
			let shortYear = today.year() % 100;
			let yearSuggestionBoundary = (shortYear + 20) % 100; // suggest 20 years into the future and 80 year backwards
			let currentCentury = Math.floor(today.year() / 100) * 100;
			if (n < yearSuggestionBoundary) {
				return currentCentury + n;
			} else if (n < 100) {
				return currentCentury - 100 + n;
			} else if (n > today.year() - 100 && n < today.year() + 100) {
				return n;
			} else {
				return null;
			}
		}

		let [s1, s2, s3] = fragments;
		let [n1, n2, n3] = [parseInt(s1), parseInt(s2), parseInt(s3)];
		let suggestions = [];

		if (!s1 && !s2 && !s3) {
			let result = [];
			for (let i = 0; i < 7; i++) {
				result.push(TrivialDateSuggestionEngine.createSuggestion(moment(today).add((this.options.favorPastDates ? -1 : 1) * i, "day"), ""));
			}
			return result;
		} else if (s1 && !s2 && !s3) {
			if (n1 > 0 && n1 <= 31) {
				let nextValidDate = this.findNextValidDate({year: today.year(), month: today.month(), day: n1}, (currentDate) => {
					  return {
						  year : currentDate.year + (this.options.favorPastDates ? (currentDate.month == 0 ? -1 : 0) : (currentDate.month == 11 ? 1 : 0)),
						  month : mod(currentDate.month + (this.options.favorPastDates ? -1 : 1), 12),
						  day: currentDate.day
					  }
				}, today);
				if (nextValidDate) {
					suggestions.push(TrivialDateSuggestionEngine.createSuggestion(nextValidDate, "D"));
				}
			}
		} else if (s1 && s2 && !s3) {
			if (n1 <= 12 && n2 > 0 && n2 <= 31) {
				let nextValidDate = this.findNextValidDate({year: today.year(), month: n1 - 1, day: n2}, (currentDate) => {
					return {
						year : currentDate.year + (this.options.favorPastDates ? -1 : 1),
						month : currentDate.month,
						day: currentDate.day
					}
				}, today);
				if (nextValidDate) {
					suggestions.push(TrivialDateSuggestionEngine.createSuggestion(nextValidDate, "MD"));
				}
			}
			if (n2 <= 12 && n1 > 0 && n1 <= 31) {
				let nextValidDate = this.findNextValidDate({year: today.year(), month: n2 - 1, day: n1}, (currentDate) => {
					return {
						year : currentDate.year + (this.options.favorPastDates ? -1 : 1),
						month : currentDate.month,
						day: currentDate.day
					}
				}, today);
				if (nextValidDate) {
					suggestions.push(TrivialDateSuggestionEngine.createSuggestion(nextValidDate, "DM"));
				}
			}
		} else { // s1 && s2 && s3
			let mom;
			mom = moment([numberToYear(n1), n2 - 1, s3]);
			if (mom.isValid()) {
				suggestions.push(TrivialDateSuggestionEngine.createSuggestion(mom, "YMD"));
			}
			mom = moment([numberToYear(n1), n3 - 1, s2]);
			if (mom.isValid()) {
				suggestions.push(TrivialDateSuggestionEngine.createSuggestion(mom, "YDM"));
			}
			mom = moment([numberToYear(n2), n1 - 1, s3]);
			if (mom.isValid()) {
				suggestions.push(TrivialDateSuggestionEngine.createSuggestion(mom, "MYD"));
			}
			mom = moment([numberToYear(n2), n3 - 1, s1]);
			if (mom.isValid()) {
				suggestions.push(TrivialDateSuggestionEngine.createSuggestion(mom, "DYM"));
			}
			mom = moment([numberToYear(n3), n1 - 1, s2]);
			if (mom.isValid()) {
				suggestions.push(TrivialDateSuggestionEngine.createSuggestion(mom, "MDY"));
			}
			mom = moment([numberToYear(n3), n2 - 1, s1]);
			if (mom.isValid()) {
				suggestions.push(TrivialDateSuggestionEngine.createSuggestion(mom, "DMY"));
			}
		}

		return suggestions;
	};

	private findNextValidDate(startDate: LocalDate, incementor: (currentDate: LocalDate) => LocalDate, today: Moment): Moment {
		let currentDate = startDate;
		let momentInNextMonth: Moment = moment(startDate);
		let numberOfIterations = 0;
		while (!(momentInNextMonth.isValid() && this.todayOrFavoriteDirection(momentInNextMonth, today)) && numberOfIterations < 4) {
			currentDate = incementor(currentDate);
			momentInNextMonth = moment(currentDate);
			numberOfIterations++;
		}
		return momentInNextMonth.isValid() ? momentInNextMonth : null;
	}
}
