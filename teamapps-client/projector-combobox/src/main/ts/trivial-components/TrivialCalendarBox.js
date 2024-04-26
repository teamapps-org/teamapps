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
exports.TrivialCalendarBox = exports.WeekDay = void 0;
var LocalDateTime_1 = require("../LocalDateTime");
var teamapps_client_core_1 = require("teamapps-client-core");
var WeekDay;
(function (WeekDay) {
    WeekDay[WeekDay["MONDAY"] = 1] = "MONDAY";
    WeekDay[WeekDay["TUESDAY"] = 2] = "TUESDAY";
    WeekDay[WeekDay["WEDNESDAY"] = 3] = "WEDNESDAY";
    WeekDay[WeekDay["THURSDAY"] = 4] = "THURSDAY";
    WeekDay[WeekDay["FRIDAY"] = 5] = "FRIDAY";
    WeekDay[WeekDay["SATURDAY"] = 6] = "SATURDAY";
    WeekDay[WeekDay["SUNDAY"] = 7] = "SUNDAY";
})(WeekDay || (exports.WeekDay = WeekDay = {}));
var TrivialCalendarBox = /** @class */ (function () {
    function TrivialCalendarBox(options) {
        if (options === void 0) { options = {}; }
        var _this = this;
        var _a;
        this.onChange = new teamapps_client_core_1.TeamAppsEvent();
        this.onOnEditingTimeUnitChange = new teamapps_client_core_1.TeamAppsEvent();
        var now = LocalDateTime_1.LocalDateTime.local();
        this.config = __assign({ locale: "en-US", selectedDate: now, firstDayOfWeek: WeekDay.MONDAY, highlightKeyboardNavigationState: false }, options);
        this.keyboardNavigationState = 'day'; // 'year','month','day','hour','minute'
        this.keyboardNavCssClass = this.config.highlightKeyboardNavigationState ? "keyboard-nav" : "";
        this.selectedDate = (_a = this.config.selectedDate) !== null && _a !== void 0 ? _a : now;
        this.$calendarBox = (0, teamapps_client_core_1.parseHtml)('<div class="tr-calendarbox"></div>');
        this.$calendarDisplay = (0, teamapps_client_core_1.parseHtml)('<div class="tr-calendar-display"></div>');
        this.$yearDisplay = (0, teamapps_client_core_1.parseHtml)('<div class="year"><span class="back-button"></span><span class="name"></span><span class="forward-button"></span></div>');
        this.$calendarDisplay.append(this.$yearDisplay);
        this.$monthDisplay = (0, teamapps_client_core_1.parseHtml)('<div class="month"><span class="back-button"></span><span class="name"></span><span class="forward-button"></span></div>');
        this.$calendarDisplay.append(this.$monthDisplay);
        this.$monthTable = (0, teamapps_client_core_1.parseHtml)('<div class="month-table"></div>');
        this.$calendarDisplay.append(this.$monthDisplay);
        this.$year = this.$yearDisplay.querySelector(":scope .name");
        this.$month = this.$monthDisplay.querySelector(":scope .name");
        this.$yearDisplay.addEventListener("click", function () { return _this.setKeyboardNavigationState("year"); });
        var $yearBackButton = this.$yearDisplay.querySelector(":scope .back-button");
        $yearBackButton.addEventListener("click", function () { return _this.navigateByUnit("year", "left", true); });
        var $yearForwardButton = this.$yearDisplay.querySelector(":scope .forward-button");
        $yearForwardButton.addEventListener("click", function () { return _this.navigateByUnit("year", "right", true); });
        this.$monthDisplay.addEventListener("click", function () { return _this.setKeyboardNavigationState("month"); });
        var $monthBackButton = this.$monthDisplay.querySelector(":scope .back-button");
        $monthBackButton.addEventListener("click", function () { return _this.navigateByUnit("month", "left", true); });
        var $monthForwardButton = this.$monthDisplay.querySelector(":scope .forward-button");
        $monthForwardButton.addEventListener("click", function () { return _this.navigateByUnit("month", "right", true); });
        this.$calendarBox.append(this.$calendarDisplay);
        this.updateDisplay();
    }
    TrivialCalendarBox.prototype.getDaysForCalendarDisplay = function (dateInMonthDoBeDisplayed) {
        var firstDayOfMonth = dateInMonthDoBeDisplayed.startOf('month');
        var firstDayToBeDisplayed = this.firstDayOfWeek(firstDayOfMonth);
        var daysOfMonth = [];
        for (var day = firstDayToBeDisplayed; daysOfMonth.length < 42; day = day.plus({ day: 1 })) {
            daysOfMonth.push(day);
        }
        return daysOfMonth;
    };
    TrivialCalendarBox.prototype.firstDayOfWeek = function (firstDayOfMonth) {
        return firstDayOfMonth.set({ weekday: this.config.firstDayOfWeek <= firstDayOfMonth.weekday ? this.config.firstDayOfWeek : this.config.firstDayOfWeek - 7 });
    };
    TrivialCalendarBox.prototype.updateDisplay = function () {
        var _this = this;
        this.$year.textContent = "" + this.selectedDate.year;
        this.$month.textContent = (this.selectedDate.setLocale(this.config.locale).toLocaleString({ month: "long" }));
        this.$monthTable.remove();
        this.$monthTable = (0, teamapps_client_core_1.parseHtml)('<div class="month-table"></div>');
        this.$calendarDisplay.append(this.$monthTable);
        var daysToBeDisplayed = this.getDaysForCalendarDisplay(this.selectedDate);
        var $tr = document.createElement('tr');
        this.$monthTable.append($tr);
        var weekDay = this.firstDayOfWeek(LocalDateTime_1.LocalDateTime.local()).setLocale(this.config.locale);
        for (var i = 0; i < 7; i++, weekDay = weekDay.plus({ day: 1 })) {
            $tr.append('<th>' + weekDay.toLocaleString({ weekday: "narrow" }) + '</th>');
        }
        var today = LocalDateTime_1.LocalDateTime.local().startOf("day");
        for (var w = 0; w < daysToBeDisplayed.length / 7; w++) {
            var $tr_1 = document.createElement('tr');
            this.$monthTable.append($tr_1);
            var _loop_1 = function (d) {
                var day = daysToBeDisplayed[w * 7 + d];
                var $td = (0, teamapps_client_core_1.parseHtml)('<td>' + day.day + '</td>');
                if (day.month == this_1.selectedDate.month) {
                    $td.classList.add('current-month');
                }
                else {
                    $td.classList.add('other-month');
                }
                if (day.hasSame(today, 'day')) {
                    $td.classList.add('today');
                }
                if (day.hasSame(this_1.selectedDate, 'day')) {
                    $td.classList.add('selected');
                    if (this_1.keyboardNavigationState === 'day' && this_1.keyboardNavCssClass) {
                        $td.classList.add(this_1.keyboardNavCssClass);
                    }
                }
                $td.addEventListener("click", function (e) {
                    _this.setKeyboardNavigationState("day");
                    _this.setMonthAndDay(day.month, day.day, true);
                });
                $tr_1.append($td);
            };
            var this_1 = this;
            for (var d = 0; d < 7; d++) {
                _loop_1(d);
            }
        }
    };
    TrivialCalendarBox.prototype.setSelectedDate = function (dateTime) {
        this.selectedDate = dateTime;
        this.updateDisplay();
    };
    TrivialCalendarBox.prototype.setYear = function (year, fireEvent) {
        this.selectedDate = this.selectedDate.set({ year: year });
        this.updateDisplay();
        if (fireEvent) {
            this.onOnEditingTimeUnitChange.fire('year');
            this.fireChangeEvents('year');
        }
    };
    TrivialCalendarBox.prototype.setMonth = function (month, fireEvent) {
        this.selectedDate = this.selectedDate.set({ month: month });
        this.updateDisplay();
        if (fireEvent) {
            this.onOnEditingTimeUnitChange.fire('month');
            this.fireChangeEvents('month');
        }
    };
    TrivialCalendarBox.prototype.setDayOfMonth = function (day, fireEvent) {
        this.selectedDate = this.selectedDate.set({ day: day });
        this.updateDisplay();
        if (fireEvent) {
            this.onOnEditingTimeUnitChange.fire('day');
            this.fireChangeEvents('day');
        }
    };
    TrivialCalendarBox.prototype.setMonthAndDay = function (month, day, fireEvent) {
        this.selectedDate = this.selectedDate.set({ month: month, day: day });
        this.updateDisplay();
        if (fireEvent) {
            this.onOnEditingTimeUnitChange.fire('day');
            this.fireChangeEvents('month');
            this.fireChangeEvents('day');
        }
    };
    TrivialCalendarBox.prototype.fireChangeEvents = function (timeUnit) {
        this.onChange.fire({
            value: this.getSelectedDate(),
            timeUnitEdited: timeUnit
        });
    };
    TrivialCalendarBox.prototype.setKeyboardNavigationState = function (newKeyboardNavigationState) {
        var _this = this;
        var _a, _b;
        this.keyboardNavigationState = newKeyboardNavigationState;
        if (this.config.highlightKeyboardNavigationState) {
            if (this.keyboardNavCssClass) {
                [this.$yearDisplay, this.$monthDisplay, this.$monthTable.querySelector(':scope td.' + this.keyboardNavCssClass)]
                    .forEach(function (element) { return element.classList.remove(_this.keyboardNavCssClass); });
                if (this.keyboardNavigationState == 'year') {
                    this.$yearDisplay.classList.add(this.keyboardNavCssClass);
                }
                else if (this.keyboardNavigationState == 'month') {
                    this.$monthDisplay.classList.add(this.keyboardNavCssClass);
                }
                else if (this.keyboardNavigationState == 'day') {
                    (_b = (_a = this.$monthTable.querySelector(":scope .selected")) === null || _a === void 0 ? void 0 : _a.classList) === null || _b === void 0 ? void 0 : _b.add(this.keyboardNavCssClass);
                }
            }
        }
    };
    TrivialCalendarBox.prototype.getSelectedDate = function () {
        return this.selectedDate;
    };
    ;
    TrivialCalendarBox.prototype.navigateByUnit = function (unit, direction, fireEvent) {
        if (fireEvent === void 0) { fireEvent = false; }
        if (unit == 'year') {
            if (direction == 'down' || direction == 'left') {
                this.setYear(this.selectedDate.year - 1, fireEvent);
            }
            else if (direction == 'up' || direction == 'right') {
                this.setYear(this.selectedDate.year + 1, fireEvent);
            }
            return true;
        }
        else if (unit == 'month') {
            if (direction == 'down' || direction == 'left') {
                this.setMonth(this.selectedDate.month - 1, fireEvent);
            }
            else if (direction == 'up' || direction == 'right') {
                this.setMonth(this.selectedDate.month + 1, fireEvent);
            }
            return true;
        }
        else if (unit == 'day') {
            if (direction == 'down') {
                this.selectedDate = this.selectedDate.plus({ days: 7 });
            }
            else if (direction == 'left') {
                this.selectedDate = this.selectedDate.minus({ days: 1 });
            }
            else if (direction == 'up') {
                this.selectedDate = this.selectedDate.minus({ days: 7 });
            }
            else if (direction == 'right') {
                this.selectedDate = this.selectedDate.plus({ days: 1 });
            }
            this.updateDisplay();
            fireEvent && this.fireChangeEvents('day');
            return true;
        }
    };
    TrivialCalendarBox.prototype.navigate = function (direction) {
        this.navigateByUnit(this.keyboardNavigationState, direction);
    };
    ;
    TrivialCalendarBox.prototype.navigateTimeUnit = function (timeUnit, direction) {
        this.navigateByUnit(timeUnit, direction);
    };
    ;
    TrivialCalendarBox.prototype.getMainDomElement = function () {
        return this.$calendarBox;
    };
    TrivialCalendarBox.prototype.destroy = function () {
        this.$calendarBox.remove();
    };
    ;
    return TrivialCalendarBox;
}());
exports.TrivialCalendarBox = TrivialCalendarBox;
