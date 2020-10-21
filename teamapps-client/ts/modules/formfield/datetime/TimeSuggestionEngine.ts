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

import {LocalDateTime} from "../../util/LocalDateTime";

export class TimeSuggestionEngine {

	constructor() {
	}

	public generateSuggestions(searchString: string): LocalDateTime[] {
		let suggestions: LocalDateTime[] = [];

		const match = searchString.match(/[^\d]/);
		const colonIndex = match != null ? match.index : null;
		if (colonIndex !== null) {
			const hourString = searchString.substring(0, colonIndex);
			const minuteString = searchString.substring(colonIndex + 1);
			suggestions = suggestions.concat(TimeSuggestionEngine.createTimeSuggestions(TimeSuggestionEngine.createHourSuggestions(hourString), TimeSuggestionEngine.createMinuteSuggestions(minuteString)));
		} else if (searchString.length > 0) { // is a number!
			if (searchString.length >= 2) {
				const hourString = searchString.substr(0, 2);
				const minuteString = searchString.substring(2, searchString.length);
				suggestions = suggestions.concat(TimeSuggestionEngine.createTimeSuggestions(TimeSuggestionEngine.createHourSuggestions(hourString), TimeSuggestionEngine.createMinuteSuggestions(minuteString)));
			}
			const hourString = searchString.substr(0, 1);
			const minuteString = searchString.substring(1, searchString.length);
			if (minuteString.length <= 2) {
				suggestions = suggestions.concat(TimeSuggestionEngine.createTimeSuggestions(TimeSuggestionEngine.createHourSuggestions(hourString), TimeSuggestionEngine.createMinuteSuggestions(minuteString)));
			}
		} else {
			suggestions = suggestions.concat(TimeSuggestionEngine.createTimeSuggestions(TimeSuggestionEngine.intRange(6, 24).concat(TimeSuggestionEngine.intRange(1, 5)), [0]));
		}
		return suggestions;
	}

	private static intRange(fromInclusive: number, toInclusive: number) {
		const ints = [];
		for (let i = fromInclusive; i <= toInclusive; i++) {
			ints.push(i)
		}
		return ints;
	}

	private static createTimeSuggestions(hourValues: number[], minuteValues: number[]): LocalDateTime[] {
		const entries: LocalDateTime[] = [];
		for (let i = 0; i < hourValues.length; i++) {
			const hour = hourValues[i];
			for (let j = 0; j < minuteValues.length; j++) {
				const minute = minuteValues[j];
				entries.push(LocalDateTime.fromObject({hour, minute})); // local!
			}
		}
		return entries;
	}

	private static createMinuteSuggestions(minuteString: string): number[] {
		const m = parseInt(minuteString);
		if (m < 0) {
			return [];
		} else if (isNaN(m)) {
			return [0, 30];
		} else if (minuteString.length === 1) {
			return []; // do not suggest something like "20" for input "2" - we simply cannot suggest anything satisfying at this point.
		} else if (minuteString.length === 2) {
			if (m > 59) {
				return [];
			} else {
				return [m];
			}
		} else { // minuteString.length > 2...
			return [];
		}
	}

	private static createHourSuggestions(hourString: string): number[] {
		const h = parseInt(hourString);
		if (isNaN(h)) {
			return TimeSuggestionEngine.intRange(1, 24);
			//} else if (h < 10) {
			//    return [(h + 12) % 24, h]; // afternoon first
			//} else if (h >= 10 && h < 12) {
			//    return [h, (h + 12) % 24]; // morning first
		} else if (h < 12) {
			return [h, (h + 12) % 24]; // morning first

		} else if (h <= 24) {
			return [h % 24];
		} else {
			return [];
		}
	}
}
