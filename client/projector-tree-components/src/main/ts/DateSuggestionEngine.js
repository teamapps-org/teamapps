"use strict";
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
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.getYearMonthDayOrderForLocale = exports.getYearMonthDayOrderFromDateFormat = exports.DateSuggestionEngine = void 0;
var LocalDateTime_1 = require("./LocalDateTime");
var DateSuggestionEngine = /** @class */ (function () {
    function DateSuggestionEngine(options) {
        var _a, _b, _c;
        this.preferredYmdOrder = (_b = (_a = options.preferredYearMonthDayOrder) !== null && _a !== void 0 ? _a : (options.locale != null ? getYearMonthDayOrderForLocale(options.locale) : undefined)) !== null && _b !== void 0 ? _b : "YMD";
        this.favorPastDates = (_c = options.favorPastDates) !== null && _c !== void 0 ? _c : false;
    }
    DateSuggestionEngine.prototype.generateSuggestions = function (searchString, now, options) {
        options = __assign({ suggestAdjacentWeekForEmptyInput: true, shuffledFormatSuggestionsEnabled: false }, (options !== null && options !== void 0 ? options : {}));
        if (searchString.replace(/[^\d]/g, '').length == 0) {
            if (options.suggestAdjacentWeekForEmptyInput) {
                return this.createAdjacentWeekDaySuggestions(now).map(function (s) { return s.date; });
            }
            else {
                return [];
            }
        }
        var suggestions;
        if (searchString.match(/[^\d]/)) {
            var fragments = searchString.split(/[^\d]/).filter(function (f) { return !!f; });
            suggestions = this.createSuggestionsForFragments(fragments, now);
        }
        else {
            suggestions = this.generateSuggestionsForDigitsOnlyInput(searchString, now);
        }
        // sort by relevance
        var preferredYmdOrder = this.preferredYmdOrder;
        suggestions.sort(function (a, b) {
            if (preferredYmdOrder.indexOf(a.ymdOrder) === -1 && preferredYmdOrder.indexOf(b.ymdOrder) !== -1) {
                return 1;
            }
            else if (preferredYmdOrder.indexOf(a.ymdOrder) !== -1 && preferredYmdOrder.indexOf(b.ymdOrder) === -1) {
                return -1;
            }
            else if (a.ymdOrder.length != b.ymdOrder.length) { // D < DM < DMY
                return a.ymdOrder.length - b.ymdOrder.length;
            }
            else {
                return a.date.diff(now, 'days').days - b.date.diff(now, 'days').days; // nearer is better
            }
        });
        return suggestions
            .filter(function (value) {
            return options.shuffledFormatSuggestionsEnabled || preferredYmdOrder.indexOf(value.ymdOrder) !== -1;
        })
            .map(function (s) { return s.date; })
            .filter(this.removeDuplicateDates());
    };
    DateSuggestionEngine.prototype.removeDuplicateDates = function () {
        var seenDates = [];
        return function (d) {
            var dateAlreadyContained = seenDates.filter(function (seenDate) { return d.equals(seenDate); }).length > 0;
            if (dateAlreadyContained) {
                return false;
            }
            else {
                seenDates.push(d);
                return true;
            }
        };
    };
    DateSuggestionEngine.prototype.generateSuggestionsForDigitsOnlyInput = function (input, today) {
        input = input || "";
        if (input.length === 0) {
            return this.createSuggestionsForFragments([], today);
        }
        else if (input.length > 8) {
            return [];
        }
        var fragmentSets = [];
        for (var i = 1; i <= input.length; i++) {
            for (var j = Math.min(input.length, i + 1); j <= input.length && j - i <= 4; j - i === 2 ? j += 2 : j++) {
                var fragments = [input.substring(0, i), input.substring(i, j), input.substring(j, input.length)];
                if (this.validateFragments(fragments)) {
                    fragmentSets.push(fragments);
                }
            }
        }
        var allSuggestions = [];
        for (var _i = 0, fragmentSets_1 = fragmentSets; _i < fragmentSets_1.length; _i++) {
            var fragments = fragmentSets_1[_i];
            var suggestions = this.createSuggestionsForFragments(fragments, today);
            allSuggestions = allSuggestions.concat(suggestions);
        }
        return allSuggestions;
    };
    DateSuggestionEngine.prototype.validateFragments = function (fragments) {
        if (fragments.some(function (f) { return f.length === 3; })) { // do not allow fragments of length 3
            return false;
        }
        if (fragments.some(function (f) { return f.length > 4; })) { // do not allow fragments of length > 4
            return false;
        }
        if (fragments.filter(function (f) { return f.length > 2; }).length > 1) { // do not allow more than one fragment with length > 2 (so 4)
            return false;
        }
        if (fragments.some(function (f) { return f.length == 4 && f.charAt(0) == '0'; })) { // do not allow 4 digit fragments padded with zeros
            return false;
        }
        return true;
    };
    DateSuggestionEngine.prototype.todayOrFavoriteDirection = function (date, today) {
        return this.favorPastDates ? today.startOf("day") >= date.startOf("day") : today.startOf("day") <= date.startOf("day");
    };
    DateSuggestionEngine.prototype.createSuggestionsForFragments = function (fragments, today) {
        var _this = this;
        function mod(n, m) {
            return ((n % m) + m) % m;
        }
        function numberToYear(n) {
            var shortYear = today.year % 100;
            var yearSuggestionBoundary = (shortYear + 20) % 100; // suggest 20 years into the future and 80 year backwards
            var currentCentury = Math.floor(today.year / 100) * 100;
            if (n >= 1000 && n <= 9999) { // four digit year
                if (n >= 1900 && n <= 2100) {
                    return n;
                }
                else {
                    return null; // we're not getting more historic or futuristic here
                }
            }
            else {
                if (n < yearSuggestionBoundary) {
                    return currentCentury + n;
                }
                else if (n < 100) {
                    return currentCentury - 100 + n;
                }
                else if (n > today.year - 120 && n < today.year + 120) {
                    return n;
                }
                else {
                    return null;
                }
            }
        }
        var s1 = fragments[0], s2 = fragments[1], s3 = fragments[2];
        var _a = [parseInt(s1), parseInt(s2), parseInt(s3)], n1 = _a[0], n2 = _a[1], n3 = _a[2];
        var suggestions = [];
        if (!s1 && !s2 && !s3) {
            return this.createAdjacentWeekDaySuggestions(today);
        }
        else if (s1 && !s2 && !s3) {
            if (n1 > 0 && n1 <= 31) {
                var nextValidDate = this.findNextValidDate({
                    year: today.year,
                    month: today.month,
                    day: n1
                }, function (currentDate) {
                    // increase month
                    return {
                        year: currentDate.year + (_this.favorPastDates ? (currentDate.month == 1 ? -1 : 0) : (currentDate.month == 12 ? 1 : 0)),
                        month: mod((currentDate.month - 1) + (_this.favorPastDates ? -1 : 1), 12) + 1,
                        day: currentDate.day
                    };
                }, today);
                if (nextValidDate) {
                    suggestions.push(createSuggestion(nextValidDate, "D"));
                }
            }
        }
        else if (s1 && s2 && !s3) {
            if (n1 <= 12 && n2 > 0 && n2 <= 31) {
                var nextValidDate = this.findNextValidDate({
                    year: today.year,
                    month: n1,
                    day: n2
                }, function (currentDate) {
                    return {
                        year: currentDate.year + (_this.favorPastDates ? -1 : 1),
                        month: currentDate.month,
                        day: currentDate.day
                    };
                }, today);
                if (nextValidDate) {
                    suggestions.push(createSuggestion(nextValidDate, "MD"));
                }
            }
            if (n2 <= 12 && n1 > 0 && n1 <= 31) {
                var nextValidDate = this.findNextValidDate({
                    year: today.year,
                    month: n2,
                    day: n1
                }, function (currentDate) {
                    return {
                        year: currentDate.year + (_this.favorPastDates ? -1 : 1),
                        month: currentDate.month,
                        day: currentDate.day
                    };
                }, today);
                if (nextValidDate) {
                    suggestions.push(createSuggestion(nextValidDate, "DM"));
                }
            }
        }
        else { // s1 && s2 && s3
            var dateTime = void 0;
            if (numberToYear(n1) != null) {
                dateTime = LocalDateTime_1.LocalDateTime.fromObject({ year: numberToYear(n1), month: n2, day: n3 });
                if (dateTime.isValid) {
                    suggestions.push(createSuggestion(dateTime, "YMD"));
                }
                dateTime = LocalDateTime_1.LocalDateTime.fromObject({ year: numberToYear(n1), month: n3, day: n2 });
                if (dateTime.isValid) {
                    suggestions.push(createSuggestion(dateTime, "YDM"));
                }
            }
            if (numberToYear(n2) != null) {
                dateTime = LocalDateTime_1.LocalDateTime.fromObject({ year: numberToYear(n2), month: n1, day: n3 });
                if (dateTime.isValid) {
                    suggestions.push(createSuggestion(dateTime, "MYD"));
                }
                dateTime = LocalDateTime_1.LocalDateTime.fromObject({ year: numberToYear(n2), month: n3, day: n1 });
                if (dateTime.isValid) {
                    suggestions.push(createSuggestion(dateTime, "DYM"));
                }
            }
            if (numberToYear(n3) != null) {
                dateTime = LocalDateTime_1.LocalDateTime.fromObject({ year: numberToYear(n3), month: n1, day: n2 });
                if (dateTime.isValid) {
                    suggestions.push(createSuggestion(dateTime, "MDY"));
                }
                dateTime = LocalDateTime_1.LocalDateTime.fromObject({ year: numberToYear(n3), month: n2, day: n1 });
                if (dateTime.isValid) {
                    suggestions.push(createSuggestion(dateTime, "DMY"));
                }
            }
        }
        return suggestions;
    };
    ;
    DateSuggestionEngine.prototype.createAdjacentWeekDaySuggestions = function (today) {
        var result = [];
        for (var i = 0; i < 7; i++) {
            result.push(createSuggestion(today.plus({ days: this.favorPastDates ? -i : i }), ""));
        }
        return result;
    };
    DateSuggestionEngine.prototype.findNextValidDate = function (startDate, incementor, today) {
        var currentFictiveDate = startDate;
        var currentDateTime = LocalDateTime_1.LocalDateTime.fromObject(currentFictiveDate);
        var numberOfIterations = 0;
        while (!(currentDateTime.isValid && this.todayOrFavoriteDirection(currentDateTime, today)) && numberOfIterations < 4) {
            currentFictiveDate = incementor(currentFictiveDate);
            currentDateTime = LocalDateTime_1.LocalDateTime.fromObject(currentFictiveDate);
            numberOfIterations++;
        }
        return currentDateTime.isValid ? currentDateTime : null;
    };
    return DateSuggestionEngine;
}());
exports.DateSuggestionEngine = DateSuggestionEngine;
function createSuggestion(date, ymdOrder) {
    return { date: date, ymdOrder: ymdOrder };
}
function getYearMonthDayOrderFromDateFormat(dateFormat) {
    var ymdIndexes = {
        Y: dateFormat.toUpperCase().indexOf("Y"),
        M: dateFormat.toUpperCase().indexOf("M"),
        D: dateFormat.toUpperCase().indexOf("D")
    };
    return (["D", "M", "Y"].sort(function (a, b) { return ymdIndexes[a] - ymdIndexes[b]; }).join(""));
}
exports.getYearMonthDayOrderFromDateFormat = getYearMonthDayOrderFromDateFormat;
function getYearMonthDayOrderForLocale(locale) {
    var parts = LocalDateTime_1.LocalDateTime.fromObject({
        year: 2020,
        month: 11,
        day: 30
    }).setLocale(locale).toLocaleParts({
        year: "numeric",
        month: "2-digit",
        day: "2-digit"
    });
    return parts
        .filter(function (p) { return p.type === "year" || p.type === "month" || p.type === "day"; })
        .map(function (p) { return p.type.charAt(0).toUpperCase(); })
        .join("");
}
exports.getYearMonthDayOrderForLocale = getYearMonthDayOrderForLocale;
