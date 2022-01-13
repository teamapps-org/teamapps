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

import {
	DateSuggestionEngine,
	getYearMonthDayOrderFromDateFormat
} from "../modules/formfield/datetime/DateSuggestionEngine";
import { LocalDateTime } from "../modules/datetime/LocalDateTime";


describe('TeamApps.extractAllPossibleDateFragmentCombinations', function () {
	it('returns suggestions for the next week if the input is empty', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"})
			.generateSuggestions("", LocalDateTime.fromISO("2015-01-27"))).toEqual([
			LocalDateTime.fromISO("2015-01-27"),
			LocalDateTime.fromISO("2015-01-28"),
			LocalDateTime.fromISO("2015-01-29"),
			LocalDateTime.fromISO("2015-01-30"),
			LocalDateTime.fromISO("2015-01-31"),
			LocalDateTime.fromISO("2015-02-01"),
			LocalDateTime.fromISO("2015-02-02")
		]);
	});
	it('returns future day suggestions for a single digit input', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("2", LocalDateTime.fromISO("2015-01-01"))).toEqual([
			LocalDateTime.fromISO("2015-01-02")
		]);
	});
	it('returns future day suggestions for a single digit input, jumping into the next month if necessary', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("2", LocalDateTime.fromISO("2015-01-05"))).toEqual([
			LocalDateTime.fromISO("2015-02-02")
		]);
	});
	it('returns past day suggestions for a single digit input, if favorPastDates == true', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD", favorPastDates: true}).generateSuggestions("3", LocalDateTime.fromISO("2015-01-02"))).toEqual([
			LocalDateTime.fromISO("2014-12-03")
		]);
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD", favorPastDates: true}).generateSuggestions("4", LocalDateTime.fromISO("2015-01-05"))).toEqual([
			LocalDateTime.fromISO("2015-01-04")
		]);
	});
	it('returns day (!) and day-month suggestions if the input has 2 digits and is interpretable as day', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("14", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2015-01-14"),
			LocalDateTime.fromISO("2016-01-04"),
			LocalDateTime.fromISO("2015-04-01")
		]);
	});
	it('skips months until it finds a valid date in the future for day-only input', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("30", LocalDateTime.fromISO("2015-01-31"))).toEqual([
			LocalDateTime.fromISO("2015-03-30")
		]);
	});
	it('skips months until it finds a valid date in the past for day-only input, if favorPastDates == true', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD", favorPastDates: true}).generateSuggestions("30", LocalDateTime.fromISO("2015-03-29"))).toEqual([
			LocalDateTime.fromISO("2015-01-30")
		]);
	});
	it('returns only day-month suggestions if the input has 2 digits and is NOT interpretable as day', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("94", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2015-09-04"),
			LocalDateTime.fromISO("2015-04-09"),
		]);
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("94", LocalDateTime.fromISO("2015-10-01"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2016-09-04"),
			LocalDateTime.fromISO("2016-04-09"),
		]);
	});
	it('returns only day-month suggestions if the input has 2 digits and is NOT interpretable as day - in the past, if favorPastDates == true', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD", favorPastDates: true}).generateSuggestions("94", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2014-09-04"),
			LocalDateTime.fromISO("2014-04-09"),
		]);
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD", favorPastDates: true}).generateSuggestions("94", LocalDateTime.fromISO("2015-10-01"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2015-09-04"),
			LocalDateTime.fromISO("2015-04-09"),
		]);
	});
	it('returns day-month and day-month-year suggestions for 3-digit inputs', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("123", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2015-01-23"),
			LocalDateTime.fromISO("2015-12-03"),
			LocalDateTime.fromISO("2001-02-03"),
			LocalDateTime.fromISO("2015-03-12"),
			LocalDateTime.fromISO("2001-03-02"),
			LocalDateTime.fromISO("2002-01-03"),
			LocalDateTime.fromISO("2002-03-01"),
			LocalDateTime.fromISO("2003-01-02"),
			LocalDateTime.fromISO("2003-02-01"),
		]);
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("193", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2001-09-03"),
			LocalDateTime.fromISO("2015-03-19"),
			LocalDateTime.fromISO("2001-03-09"),
			LocalDateTime.fromISO("2003-01-09"),
			LocalDateTime.fromISO("2003-09-01"),
			LocalDateTime.fromISO("2009-01-03"),
			LocalDateTime.fromISO("2009-03-01"),
		]);
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("789", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2007-08-09"),
			LocalDateTime.fromISO("2007-09-08"),
			LocalDateTime.fromISO("2008-07-09"),
			LocalDateTime.fromISO("2008-09-07"),
			LocalDateTime.fromISO("2009-07-08"),
			LocalDateTime.fromISO("2009-08-07"),
		]);
	});
	it('returns day-month and day-month-year suggestions for 4-digit inputs', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("1212", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		}))
			.toEqual([
				LocalDateTime.fromISO("2015-12-12"),
				LocalDateTime.fromISO("2001-02-12"),
				LocalDateTime.fromISO("2012-01-02"),
				LocalDateTime.fromISO("2001-02-21"),
				LocalDateTime.fromISO("2001-12-02"),
				LocalDateTime.fromISO("2002-01-12"),
				LocalDateTime.fromISO("2002-01-21"),
				LocalDateTime.fromISO("2002-12-01"),
				LocalDateTime.fromISO("2012-02-01"),
				LocalDateTime.fromISO("2021-01-02"),
				LocalDateTime.fromISO("2021-02-01")
			]);
	});
	it('returns and day-month-year suggestions for 5-digit inputs', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "MDY"}).generateSuggestions("12123", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		}))
			.toEqual([
				LocalDateTime.fromISO("2003-12-12"),
				LocalDateTime.fromISO("2023-01-21"),
				LocalDateTime.fromISO("2023-12-01"),
				LocalDateTime.fromISO("2001-12-23"),
				LocalDateTime.fromISO("2012-01-23"),
				LocalDateTime.fromISO("2012-03-12"),
				LocalDateTime.fromISO("2012-12-03"),
				LocalDateTime.fromISO("2021-01-23"),
				LocalDateTime.fromISO("2023-01-12")
			]);
	});
	it('returns and day-month-year suggestions for 6-digit inputs', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("121423", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2014-12-23"),
			LocalDateTime.fromISO("2023-12-14")
		]);
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("129923", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("1999-12-23")
		]);
	});
	it('returns and day-month-year suggestions for 8-digit inputs', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("10210023", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			LocalDateTime.fromISO("2100-10-23")
		]);
	});
	it('returns an empty array for 9-digit inputs', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("102111232", LocalDateTime.fromISO("2015-01-05"))).toEqual([]);
	});
	it('suggests the right thing for 4417 ;-)', () => {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "DMY"}).generateSuggestions("4417", LocalDateTime.fromISO("2017-05-12"), {
			shuffledFormatSuggestionsEnabled: true
		}))
			.toEqual([
				LocalDateTime.fromISO("2017-04-04"),
				LocalDateTime.fromISO("1941-04-07"),
				LocalDateTime.fromISO("1941-07-04"),
				LocalDateTime.fromISO("1944-01-07"),
				LocalDateTime.fromISO("1944-07-01"),
				LocalDateTime.fromISO("2004-04-17")
			]);
	});
	it('suggests the right thing for 231221 ;-)', () => {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "DMY"}).generateSuggestions("231221", LocalDateTime.fromISO("2017-05-12"), {
			shuffledFormatSuggestionsEnabled: true
		}))
			.toEqual([
				LocalDateTime.fromISO("2021-12-23"),
				LocalDateTime.fromISO("2023-12-21"),
			]);
	});
	it('Does not make suggestions for 3 digit days (leading zero)', () => {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "DMY"}).generateSuggestions("0210202", LocalDateTime.fromISO("2017-05-12")))
			.toEqual([]);
	});
	it('does not allow zero-padded 4-digit fragments', function () {
		expect(new DateSuggestionEngine({preferredYearMonthDayOrder: "YMD"}).generateSuggestions("00230130", LocalDateTime.fromISO("2015-01-05"), {
			shuffledFormatSuggestionsEnabled: true
		})).toEqual([
			// NOT LocalDateTime.fromISO("2023-01-30")
		]);
	});
});

describe('DateSuggestionEngine.dateFormatToYmdOrder', function () {
	it('gets the YMD order right', function () {
		expect(getYearMonthDayOrderFromDateFormat("YYYY-MM-DD")).toEqual("YMD");
		expect(getYearMonthDayOrderFromDateFormat("MM/DD/YYYY")).toEqual("MDY");
		expect(getYearMonthDayOrderFromDateFormat("M/D/Y")).toEqual("MDY");
		expect(getYearMonthDayOrderFromDateFormat("DD.MM.YYYY")).toEqual("DMY");
		expect(getYearMonthDayOrderFromDateFormat("DD.MM.DD.YYYY")).toEqual("DMY");
	});
});
