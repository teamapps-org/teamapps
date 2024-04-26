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
Object.defineProperty(exports, "__esModule", { value: true });
exports.TimeSuggestionEngine = void 0;
var LocalDateTime_1 = require("./LocalDateTime");
var TimeSuggestionEngine = /** @class */ (function () {
    function TimeSuggestionEngine() {
    }
    TimeSuggestionEngine.prototype.generateSuggestions = function (searchString) {
        var suggestions = [];
        var match = searchString.match(/[^\d]/);
        var colonIndex = match != null ? match.index : null;
        if (colonIndex !== null) {
            var hourString = searchString.substring(0, colonIndex);
            var minuteString = searchString.substring(colonIndex + 1);
            suggestions = suggestions.concat(TimeSuggestionEngine.createTimeSuggestions(TimeSuggestionEngine.createHourSuggestions(hourString), TimeSuggestionEngine.createMinuteSuggestions(minuteString)));
        }
        else if (searchString.length > 0) { // is a number!
            if (searchString.length >= 2) {
                var hourString_1 = searchString.substr(0, 2);
                var minuteString_1 = searchString.substring(2, searchString.length);
                suggestions = suggestions.concat(TimeSuggestionEngine.createTimeSuggestions(TimeSuggestionEngine.createHourSuggestions(hourString_1), TimeSuggestionEngine.createMinuteSuggestions(minuteString_1)));
            }
            var hourString = searchString.substr(0, 1);
            var minuteString = searchString.substring(1, searchString.length);
            if (minuteString.length <= 2) {
                suggestions = suggestions.concat(TimeSuggestionEngine.createTimeSuggestions(TimeSuggestionEngine.createHourSuggestions(hourString), TimeSuggestionEngine.createMinuteSuggestions(minuteString)));
            }
        }
        else {
            suggestions = suggestions.concat(TimeSuggestionEngine.createTimeSuggestions(TimeSuggestionEngine.intRange(6, 24).concat(TimeSuggestionEngine.intRange(1, 5)), [0]));
        }
        return suggestions;
    };
    TimeSuggestionEngine.intRange = function (fromInclusive, toInclusive) {
        var ints = [];
        for (var i = fromInclusive; i <= toInclusive; i++) {
            ints.push(i);
        }
        return ints;
    };
    TimeSuggestionEngine.createTimeSuggestions = function (hourValues, minuteValues) {
        var entries = [];
        for (var i = 0; i < hourValues.length; i++) {
            var hour = hourValues[i];
            for (var j = 0; j < minuteValues.length; j++) {
                var minute = minuteValues[j];
                entries.push(LocalDateTime_1.LocalDateTime.fromObject({ hour: hour, minute: minute })); // local!
            }
        }
        return entries;
    };
    TimeSuggestionEngine.createMinuteSuggestions = function (minuteString) {
        var m = parseInt(minuteString);
        if (m < 0) {
            return [];
        }
        else if (isNaN(m)) {
            return [0, 30];
        }
        else if (minuteString.length === 1) {
            return []; // do not suggest something like "20" for input "2" - we simply cannot suggest anything satisfying at this point.
        }
        else if (minuteString.length === 2) {
            if (m > 59) {
                return [];
            }
            else {
                return [m];
            }
        }
        else { // minuteString.length > 2...
            return [];
        }
    };
    TimeSuggestionEngine.createHourSuggestions = function (hourString) {
        var h = parseInt(hourString);
        if (isNaN(h)) {
            return TimeSuggestionEngine.intRange(1, 24);
            //} else if (h < 10) {
            //    return [(h + 12) % 24, h]; // afternoon first
            //} else if (h >= 10 && h < 12) {
            //    return [h, (h + 12) % 24]; // morning first
        }
        else if (h < 12) {
            return [h, (h + 12) % 24]; // morning first
        }
        else if (h <= 24) {
            return [h % 24];
        }
        else {
            return [];
        }
    };
    return TimeSuggestionEngine;
}());
exports.TimeSuggestionEngine = TimeSuggestionEngine;
