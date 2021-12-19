/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import {NavigationDirection, TrivialComponent} from "./TrivialCore";
import {LocalDateTime} from "../datetime/LocalDateTime";
import {parseHtml} from "../Common";
import {TeamAppsEvent} from "../util/TeamAppsEvent";

export enum WeekDay {
    MONDAY = 1, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
export type TimeUnit = 'year'|'month'|'day';

export interface TrivialCalendarBoxConfig {
    locale?: string,
    selectedDate?: LocalDateTime,
    firstDayOfWeek?: WeekDay,
    highlightKeyboardNavigationState?: boolean
}

export class TrivialCalendarBox implements TrivialComponent {

    private config: TrivialCalendarBoxConfig;

    private keyboardNavigationState: TimeUnit;
    private keyboardNavCssClass: string;
    private selectedDate: LocalDateTime;
    private $calendarBox: HTMLElement;
    private $calendarDisplay: HTMLElement;
    private $yearDisplay: HTMLElement;
    private $monthDisplay: HTMLElement;
    private $monthTable: HTMLElement;
    private $year: HTMLElement;
    private $month: HTMLElement;

    public readonly onChange = new TeamAppsEvent<{ value: LocalDateTime, timeUnitEdited: TimeUnit}>(this);
    public readonly onOnEditingTimeUnitChange = new TeamAppsEvent<TimeUnit>(this);

    constructor(options: TrivialCalendarBoxConfig = {}) {
        let now = LocalDateTime.local();
        this.config = $.extend(<TrivialCalendarBoxConfig> {
            locale: "en-US",
            selectedDate: now,
            firstDayOfWeek: WeekDay.MONDAY,
            highlightKeyboardNavigationState: false
        }, options);

        this.keyboardNavigationState = 'day'; // 'year','month','day','hour','minute'
        this.keyboardNavCssClass = this.config.highlightKeyboardNavigationState ? "keyboard-nav" : "";

        this.selectedDate = this.config.selectedDate ?? now;
        this.$calendarBox = parseHtml('<div class="tr-calendarbox"></div>');
        this.$calendarDisplay = parseHtml('<div class="tr-calendar-display"></div>');
        this.$yearDisplay = parseHtml('<div class="year"><span class="back-button"></span><span class="name"></span><span class="forward-button"></span></div>');
        this.$calendarDisplay.append(this.$yearDisplay);
        this.$monthDisplay = parseHtml('<div class="month"><span class="back-button"></span><span class="name"></span><span class="forward-button"></span></div>');
        this.$calendarDisplay.append(this.$monthDisplay);
        this.$monthTable = parseHtml('<div class="month-table"></div>');
        this.$calendarDisplay.append(this.$monthDisplay);
        this.$year = this.$yearDisplay.querySelector(":scope .name");
        this.$month = this.$monthDisplay.querySelector(":scope .name");

        this.$yearDisplay.addEventListener("click", () => this.setKeyboardNavigationState("year"));
        let $yearBackButton = this.$yearDisplay.querySelector(":scope .back-button");
        $yearBackButton.addEventListener("click", () => this.navigateByUnit("year", "left", true));
        let $yearForwardButton = this.$yearDisplay.querySelector(":scope .forward-button");
        $yearForwardButton.addEventListener("click", () => this.navigateByUnit("year", "right", true));

        this.$monthDisplay.addEventListener("click", () => this.setKeyboardNavigationState("month"));
        let $monthBackButton = this.$monthDisplay.querySelector(":scope .back-button");
        $monthBackButton.addEventListener("click", () => this.navigateByUnit("month", "left", true));
        let $monthForwardButton = this.$monthDisplay.querySelector(":scope .forward-button");
        $monthForwardButton.addEventListener("click", () => this.navigateByUnit("month", "right", true));

        this.$calendarBox.append(this.$calendarDisplay);

        this.updateDisplay();
    }

    private getDaysForCalendarDisplay(dateInMonthDoBeDisplayed: LocalDateTime) {
        const firstDayOfMonth = dateInMonthDoBeDisplayed.startOf('month');
        const firstDayToBeDisplayed = this.firstDayOfWeek(firstDayOfMonth);

        const daysOfMonth: LocalDateTime[] = [];
        for (let day = firstDayToBeDisplayed; daysOfMonth.length < 42; day = day.plus({day: 1})) {
            daysOfMonth.push(day);
        }
        return daysOfMonth;
    }

    private firstDayOfWeek(firstDayOfMonth: LocalDateTime): LocalDateTime {
        return firstDayOfMonth.set({weekday: this.config.firstDayOfWeek <= firstDayOfMonth.weekday ? this.config.firstDayOfWeek : this.config.firstDayOfWeek - 7});
    }

    private updateDisplay() {
        this.$year.textContent = "" + this.selectedDate.year;
        this.$month.textContent = (this.selectedDate.setLocale(this.config.locale).toLocaleString({month: "long"}));
        this.$monthTable.remove();
        this.$monthTable = parseHtml('<div class="month-table"></div>');
        this.$calendarDisplay.append(this.$monthTable);

        const daysToBeDisplayed = this.getDaysForCalendarDisplay(this.selectedDate);

        let $tr = $('<tr>').appendTo(this.$monthTable);
        let weekDay = this.firstDayOfWeek(LocalDateTime.local()).setLocale(this.config.locale);
        for (let i = 0; i < 7; i++, weekDay = weekDay.plus({day: 1})) {
            $tr.append('<th>' + weekDay.toLocaleString({weekday: "narrow"}) + '</th>');
        }
        let today = LocalDateTime.local().startOf("day");
        for (let w = 0; w < daysToBeDisplayed.length / 7; w++) {
            $tr = $('<tr>').appendTo(this.$monthTable);
            for (let d = 0; d < 7; d++) {
                const day = daysToBeDisplayed[w * 7 + d];
                const $td = $('<td>' + day.day + '</td>');
                if (day.month == this.selectedDate.month) {
                    $td.addClass('current-month');
                } else {
                    $td.addClass('other-month');
                }
                if (day.hasSame(today, 'day')) {
                    $td.addClass('today');
                }
                if (day.hasSame(this.selectedDate, 'day')) {
                    $td.addClass('selected');
                    if (this.keyboardNavigationState === 'day') {
                        $td.addClass(this.keyboardNavCssClass);
                    }
                }
                $td.click(((day: LocalDateTime) => {
                    this.setKeyboardNavigationState("day");
                    this.setMonthAndDay(day.month, day.day, true);
                }).bind(this, day));
                $tr.append($td);
            }
        }
    }

    public setSelectedDate(dateTime: LocalDateTime) {
        this.selectedDate = dateTime;
        this.updateDisplay();
    }

    public setYear(year: number, fireEvent?: boolean) {
        this.selectedDate = this.selectedDate.set({year});
        this.updateDisplay();
        if (fireEvent) {
            this.onOnEditingTimeUnitChange.fire('year');
            this.fireChangeEvents('year');
        }
    }

    public setMonth(month: number, fireEvent?: boolean) {
        this.selectedDate = this.selectedDate.set({month});
        this.updateDisplay();
        if (fireEvent) {
            this.onOnEditingTimeUnitChange.fire('month');
            this.fireChangeEvents('month');
        }
    }

    public setDayOfMonth(day: number, fireEvent?: boolean) {
        this.selectedDate = this.selectedDate.set({day});
        this.updateDisplay();
        if (fireEvent) {
            this.onOnEditingTimeUnitChange.fire('day');
            this.fireChangeEvents('day');
        }
    }

    public setMonthAndDay(month: number, day: number, fireEvent?: boolean) {
        this.selectedDate = this.selectedDate.set({month, day});
        this.updateDisplay();
        if (fireEvent) {
            this.onOnEditingTimeUnitChange.fire('day');
            this.fireChangeEvents('month');
            this.fireChangeEvents('day');
        }
    }

    private fireChangeEvents(timeUnit: TimeUnit) {
        this.onChange.fire({
            value: this.getSelectedDate(),
            timeUnitEdited: timeUnit
        });
    }

    public setKeyboardNavigationState(newKeyboardNavigationState: TimeUnit) {
        this.keyboardNavigationState = newKeyboardNavigationState;
        if (this.config.highlightKeyboardNavigationState) {
            let me = this;
            [this.$yearDisplay, this.$monthDisplay, this.$monthTable.querySelector(':scope td.' + this.keyboardNavCssClass)]
                .forEach(element => element.classList.remove(me.keyboardNavCssClass));
            if (this.keyboardNavigationState == 'year') {
                this.$yearDisplay.classList.add(this.keyboardNavCssClass);
            } else if (this.keyboardNavigationState == 'month') {
                this.$monthDisplay.classList.add(this.keyboardNavCssClass);
            } else if (this.keyboardNavigationState == 'day') {
                this.$monthTable.querySelector(":scope .selected")?.classList?.add(this.keyboardNavCssClass);
            } 
        }
    }

    public getSelectedDate() {
        return this.selectedDate;
    };

    private navigateByUnit(unit: TimeUnit, direction: NavigationDirection, fireEvent: boolean = false): boolean { // returns true if effectively navigated, false if nothing has changed
        if (unit == 'year') {
            if (direction == 'down' || direction == 'left') {
                this.setYear(this.selectedDate.year - 1, fireEvent);
            } else if (direction == 'up' || direction == 'right') {
                this.setYear(this.selectedDate.year + 1, fireEvent);
            }
            return true;
        } else if (unit == 'month') {
            if (direction == 'down' || direction == 'left') {
                this.setMonth(this.selectedDate.month - 1, fireEvent);
            } else if (direction == 'up' || direction == 'right') {
                this.setMonth(this.selectedDate.month + 1, fireEvent);
            }
            return true;
        } else if (unit == 'day') {
            if (direction == 'down') {
                this.selectedDate = this.selectedDate.plus({days: 7});
            } else if (direction == 'left') {
                this.selectedDate = this.selectedDate.minus({days: 1});
            } else if (direction == 'up') {
                this.selectedDate = this.selectedDate.minus({days: 7});
            } else if (direction == 'right') {
                this.selectedDate = this.selectedDate.plus({days: 1});
            }
            this.updateDisplay();
            fireEvent && this.fireChangeEvents('day');
            return true;
        }
    }

    public navigate(direction: NavigationDirection) { // returns true if effectively navigated, false if nothing has changed
        this.navigateByUnit(this.keyboardNavigationState, direction);
    };

    public navigateTimeUnit(timeUnit: TimeUnit, direction: NavigationDirection) { // returns true if effectively navigated, false if nothing has changed
        this.navigateByUnit(timeUnit, direction);
    };

    getMainDomElement(): HTMLElement {
        return this.$calendarBox;
    }

    public destroy() {
        this.$calendarBox.remove();
    };

}
