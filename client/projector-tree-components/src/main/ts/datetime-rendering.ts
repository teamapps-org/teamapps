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
import {DateTime} from "luxon";

export function createDateIconRenderer(locale: string): (time: DateTime) => string {
	const weekDayString = (dateTime: DateTime) => {
		let s = dateTime != null ? dateTime.setLocale(locale).toFormat("ccc") : "";
		return s.length > 2 ? s.substr(0, 2) : s;
	}
	return dateTime => {
		return `<svg viewBox="0 0 540 540" width="22" height="22" class="calendar-icon">
					<defs>
						<linearGradient id="Gradient1" x1="0" x2="0" y1="0" y2="1">
							<stop class="calendar-symbol-ring-gradient-stop1" offset="0%"></stop>
							<stop class="calendar-symbol-ring-gradient-stop2" offset="50%"></stop>
							<stop class="calendar-symbol-ring-gradient-stop3" offset="100%"></stop>
						</linearGradient>
					</defs>        
					<g id="layer1">
						<rect class="calendar-symbol-page-background" x="90" y="90" width="360" height="400" ry="3.8"></rect>
						<rect class="calendar-symbol-color" x="90" y="90" width="360" height="85" ry="3.5"></rect>
						<rect class="calendar-symbol-page" x="90" y="90" width="360" height="395" ry="3.8"></rect>
						<rect class="calendar-symbol-ring" fill="url('#Gradient2')" x="140" y="30" width="40" height="120" ry="30.8"></rect>
						<rect class="calendar-symbol-ring" fill="url('#Gradient2')" x="250" y="30" width="40" height="120" ry="30.8"></rect>
						<rect class="calendar-symbol-ring" fill="url('#Gradient2')" x="360" y="30" width="40" height="120" ry="30.8"></rect>
						<text class="calendar-symbol-date" x="270" y="415" text-anchor="middle">${weekDayString(dateTime)}</text>
					</g>
				</svg>`;
	}
}

export function createDateRenderer(locale: string, dateFormat: Intl.DateTimeFormatOptions, withIcon: boolean, additionalClass?: string): (time: DateTime) => string {
	let dateIconRenderer = createDateIconRenderer(locale);
	return (dateTime: DateTime) => {
		if (dateTime == null) {
			return "";
		}
		let dateTimeWithLocale = dateTime.setLocale(locale);
		return `<div class="tr-template-icon-single-line ${additionalClass ?? ''}">
					${withIcon ? dateIconRenderer(dateTime) : ''}
					<div class="content-wrapper tr-editor-area">${dateTimeWithLocale.toLocaleString(dateFormat)}</div>
				</div>`;
	};
}

export function createClockIconRenderer(): (time: DateTime) => string {
	const hourHandAngle = (time: DateTime) => time != null ? ((time.hour % 12) + time.minute / 60) * 30 : 0;
	const minuteHandAngle = (time: DateTime) => time != null ? time.minute * 6 : 0;
	const isNight = (time: DateTime) => time != null ? time.hour < 6 || time.hour >= 20 : false;
	return (dateTime: DateTime) => {
		return `<svg class="clock-icon night-${isNight(dateTime)}" viewBox="0 0 110 110" width="22" height="22"> 
					<circle class="clockcircle" cx="55" cy="55" r="45"></circle>
					<g class="hands">
						<line class="hourhand" x1="55" y1="55" x2="55" y2="35" transform="rotate(${hourHandAngle(dateTime)},55,55)"></line>
						<line class="minutehand" x1="55" y1="55" x2="55" y2="22" transform="rotate(${minuteHandAngle(dateTime)},55,55)"></line>
					</g> 
				</svg>`
	}
}

export function createTimeRenderer(locale: string, timeFormat: Intl.DateTimeFormatOptions, withIcon: boolean, additionalClass?: string): (time: DateTime) => string {
	let clockIconRenderer = createClockIconRenderer();
	return (dateTime: DateTime) => {
		if (dateTime == null) {
			return "";
		}
		return `<div class="tr-template-icon-single-line ${additionalClass ?? ''}">
					${withIcon ? clockIconRenderer(dateTime) : ''}
					<div class="content-wrapper tr-editor-area">${dateTime.setLocale(locale).toLocaleString(timeFormat)}</div>
				</div>`;
	};
}

// ==== LEGACY ====

export var dateTemplate = `<div class="tr-template-icon-single-line">
    <svg viewBox="0 0 540 540" width="22" height="22" class="calendar-icon">
        <defs>
            <linearGradient id="Gradient1" x1="0" x2="0" y1="0" y2="1">
                <stop class="calendar-symbol-ring-gradient-stop1" offset="0%"></stop>
                <stop class="calendar-symbol-ring-gradient-stop2" offset="50%"></stop>
                <stop class="calendar-symbol-ring-gradient-stop3" offset="100%"></stop>
            </linearGradient>
        </defs>        
        <g id="layer1">
            <rect class="calendar-symbol-page-background" x="90" y="90" width="360" height="400" ry="3.8"></rect>
            <rect class="calendar-symbol-color" x="90" y="90" width="360" height="85" ry="3.5"></rect>
            <rect class="calendar-symbol-page" x="90" y="90" width="360" height="395" ry="3.8"></rect>
            <rect class="calendar-symbol-ring" fill="url('#Gradient2')" x="140" y="30" width="40" height="120" ry="30.8"></rect>
            <rect class="calendar-symbol-ring" fill="url('#Gradient2')" x="250" y="30" width="40" height="120" ry="30.8"></rect>
            <rect class="calendar-symbol-ring" fill="url('#Gradient2')" x="360" y="30" width="40" height="120" ry="30.8"></rect>
            <text class="calendar-symbol-date" x="270" y="415" text-anchor="middle">{{weekDay}}</text>
        </g>
    </svg>
    <div class="content-wrapper tr-editor-area">{{displayString}}</div>
</div>`;

export var timeTemplate = '<div class="tr-template-icon-single-line">' +
	'<svg class="clock-icon night-{{isNight}}" viewBox="0 0 110 110" width="22" height="22"> ' +
	'<circle class="clockcircle" cx="55" cy="55" r="45"></circle>' +
	'<g class="hands">' +
	' <line class="hourhand" x1="55" y1="55" x2="55" y2="35" transform="rotate({{hourAngle}},55,55)"></line> ' +
	' <line class="minutehand" x1="55" y1="55" x2="55" y2="22" transform="rotate({{minuteAngle}},55,55)"></line>' +
	'</g> ' +
	'</svg>' +
	'  <div class="content-wrapper tr-editor-area">{{displayString}}</div>' +
	'</div>';


export function createTimseComboBoxEntry(h: number, m: number, locale: string, timeFormat: Intl.DateTimeFormatOptions) {
	return {
		hour: h,
		minute: m,
		hourString: pad(h, 2),
		minuteString: pad(m, 2),
		displayString: DateTime.fromObject({hour: h, minute: m}).setLocale(locale).toLocaleString(timeFormat),
		hourAngle: ((h % 12) + m / 60) * 30,
		minuteAngle: m * 6,
		isNight: h < 6 || h >= 20
	};
}

function pad(num: number, size: number) {
	let s = num + "";
	while (s.length < size) s = "0" + s;
	return s;
}


