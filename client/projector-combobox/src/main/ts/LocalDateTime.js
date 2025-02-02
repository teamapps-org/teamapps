"use strict";
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
exports.LocalDateTime = void 0;
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
var luxon_1 = require("luxon");
var LOCAL_ZONE = Intl.DateTimeFormat().resolvedOptions().timeZone;
var LocalDateTime = /** @class */ (function () {
    function LocalDateTime(dateTime) {
        this.dateTime = dateTime.setZone("UTC", { keepLocalTime: true });
    }
    LocalDateTime.fromDateTime = function (dateTime) {
        return new LocalDateTime(dateTime);
    };
    LocalDateTime.fromObject = function (obj) {
        return new LocalDateTime(luxon_1.DateTime.fromObject(obj));
    };
    LocalDateTime.local = function () {
        return new LocalDateTime(luxon_1.DateTime.local());
    };
    LocalDateTime.fromISO = function (text) {
        return new LocalDateTime(luxon_1.DateTime.fromISO(text));
    };
    Object.defineProperty(LocalDateTime.prototype, "day", {
        get: function () {
            return this.dateTime.day;
        },
        set: function (value) {
            this.dateTime.day = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "daysInMonth", {
        get: function () {
            return this.dateTime.daysInMonth;
        },
        set: function (value) {
            this.dateTime.daysInMonth = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "daysInYear", {
        get: function () {
            return this.dateTime.daysInYear;
        },
        set: function (value) {
            this.dateTime.daysInYear = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "hour", {
        get: function () {
            return this.dateTime.hour;
        },
        set: function (value) {
            this.dateTime.hour = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "invalidReason", {
        get: function () {
            return this.dateTime.invalidReason;
        },
        set: function (value) {
            this.dateTime.invalidReason = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "invalidExplanation", {
        get: function () {
            return this.dateTime.invalidExplanation;
        },
        set: function (value) {
            this.dateTime.invalidExplanation = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "isInLeapYear", {
        get: function () {
            return this.dateTime.isInLeapYear;
        },
        set: function (value) {
            this.dateTime.isInLeapYear = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "isValid", {
        get: function () {
            return this.dateTime.isValid;
        },
        set: function (value) {
            this.dateTime.isValid = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "locale", {
        get: function () {
            return this.dateTime.locale;
        },
        set: function (value) {
            this.dateTime.locale = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "millisecond", {
        get: function () {
            return this.dateTime.millisecond;
        },
        set: function (value) {
            this.dateTime.millisecond = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "minute", {
        get: function () {
            return this.dateTime.minute;
        },
        set: function (value) {
            this.dateTime.minute = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "month", {
        get: function () {
            return this.dateTime.month;
        },
        set: function (value) {
            this.dateTime.month = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "monthLong", {
        get: function () {
            return this.dateTime.monthLong;
        },
        set: function (value) {
            this.dateTime.monthLong = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "monthShort", {
        get: function () {
            return this.dateTime.monthShort;
        },
        set: function (value) {
            this.dateTime.monthShort = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "numberingSystem", {
        get: function () {
            return this.dateTime.numberingSystem;
        },
        set: function (value) {
            this.dateTime.numberingSystem = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "ordinal", {
        get: function () {
            return this.dateTime.ordinal;
        },
        set: function (value) {
            this.dateTime.ordinal = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "outputCalendar", {
        get: function () {
            return this.dateTime.outputCalendar;
        },
        set: function (value) {
            this.dateTime.outputCalendar = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "quarter", {
        get: function () {
            return this.dateTime.quarter;
        },
        set: function (value) {
            this.dateTime.quarter = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "second", {
        get: function () {
            return this.dateTime.second;
        },
        set: function (value) {
            this.dateTime.second = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "weekNumber", {
        get: function () {
            return this.dateTime.weekNumber;
        },
        set: function (value) {
            this.dateTime.weekNumber = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "weekYear", {
        get: function () {
            return this.dateTime.weekYear;
        },
        set: function (value) {
            this.dateTime.weekYear = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "weekday", {
        get: function () {
            return this.dateTime.weekday;
        },
        set: function (value) {
            this.dateTime.weekday = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "weekdayLong", {
        get: function () {
            return this.dateTime.weekdayLong;
        },
        set: function (value) {
            this.dateTime.weekdayLong = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "weekdayShort", {
        get: function () {
            return this.dateTime.weekdayShort;
        },
        set: function (value) {
            this.dateTime.weekdayShort = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "weeksInWeekYear", {
        get: function () {
            return this.dateTime.weeksInWeekYear;
        },
        set: function (value) {
            this.dateTime.weeksInWeekYear = value;
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(LocalDateTime.prototype, "year", {
        get: function () {
            return this.dateTime.year;
        },
        set: function (value) {
            this.dateTime.year = value;
        },
        enumerable: false,
        configurable: true
    });
    LocalDateTime.prototype.diff = function (other, unit, options) {
        return this.dateTime.diff(other.dateTime, unit, options);
    };
    LocalDateTime.prototype.diffNow = function (unit, options) {
        return this.diff(LocalDateTime.local(), unit, options);
    };
    LocalDateTime.prototype.endOf = function (unit) {
        return new LocalDateTime(this.dateTime.endOf(unit));
    };
    LocalDateTime.prototype.equals = function (other) {
        return this.dateTime.equals(other.dateTime);
    };
    LocalDateTime.prototype.get = function (unit) {
        return this.dateTime.get(unit);
    };
    LocalDateTime.prototype.hasSame = function (other, unit) {
        return this.dateTime.hasSame(other.dateTime, unit);
    };
    LocalDateTime.prototype.minus = function (duration) {
        return new LocalDateTime(this.dateTime.minus(duration));
    };
    LocalDateTime.prototype.plus = function (duration) {
        return new LocalDateTime(this.dateTime.plus(duration));
    };
    LocalDateTime.prototype.reconfigure = function (properties) {
        return new LocalDateTime(this.dateTime.reconfigure(properties));
    };
    LocalDateTime.prototype.resolvedLocaleOpts = function (options) {
        return this.dateTime.resolvedLocaleOpts(options);
    };
    LocalDateTime.prototype.set = function (values) {
        return new LocalDateTime(this.dateTime.set(values));
    };
    LocalDateTime.prototype.setLocale = function (locale) {
        return new LocalDateTime(this.dateTime.setLocale(locale));
    };
    LocalDateTime.prototype.startOf = function (unit) {
        return new LocalDateTime(this.dateTime.startOf(unit));
    };
    LocalDateTime.prototype.toBSON = function () {
        return this.dateTime.toBSON();
    };
    LocalDateTime.prototype.toFormat = function (format, options) {
        return this.dateTime.toFormat(format, options);
    };
    LocalDateTime.prototype.toHTTP = function () {
        return this.dateTime.toHTTP();
    };
    LocalDateTime.prototype.toISO = function (options) {
        return this.dateTime.toISO(options);
    };
    LocalDateTime.prototype.toISODate = function (options) {
        return this.dateTime.toISODate(options);
    };
    LocalDateTime.prototype.toISOTime = function (options) {
        return this.dateTime.toISOTime(options);
    };
    LocalDateTime.prototype.toISOWeekDate = function () {
        return this.dateTime.toISOWeekDate();
    };
    LocalDateTime.prototype.toJSON = function () {
        return this.dateTime.toJSON();
    };
    LocalDateTime.prototype.toZoned = function (zone, options) {
        return this.dateTime.setZone(zone, __assign({ keepLocalTime: true }, options));
    };
    LocalDateTime.prototype.toUTC = function (offset, options) {
        return this.dateTime.toUTC(offset, __assign(__assign({}, options), { keepLocalTime: true }));
    };
    LocalDateTime.prototype.toLocal = function () {
        return this.toZoned(LOCAL_ZONE);
    };
    LocalDateTime.prototype.toLocaleParts = function (options) {
        return this.dateTime.toLocaleParts(options);
    };
    LocalDateTime.prototype.toLocaleString = function (options) {
        return this.dateTime.toLocaleString(options);
    };
    LocalDateTime.prototype.toObject = function (options) {
        var dateObject = this.dateTime.toObject(options);
        dateObject = __assign({}, dateObject);
        delete dateObject.zone;
        return dateObject;
    };
    LocalDateTime.prototype.toSQL = function (options) {
        return this.dateTime.toSQL(__assign(__assign({}, options), { includeOffset: false, includeZone: false }));
    };
    LocalDateTime.prototype.toSQLDate = function () {
        return this.dateTime.toSQLDate();
    };
    LocalDateTime.prototype.toSQLTime = function (options) {
        return this.dateTime.toSQLTime(__assign(__assign({}, options), { includeOffset: false, includeZone: false }));
    };
    LocalDateTime.prototype.toString = function () {
        return this.dateTime.year + "-" + this.dateTime.month + "-" + this.dateTime.day;
    };
    LocalDateTime.prototype.until = function (other) {
        return this.dateTime.until(other);
    };
    LocalDateTime.prototype.valueOf = function () {
        return this.dateTime.valueOf();
    };
    return LocalDateTime;
}());
exports.LocalDateTime = LocalDateTime;
