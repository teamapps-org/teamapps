/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import * as log from "loglevel";
import {generateUUID, parseHtml, parseSvg} from "../Common";
import * as d3 from "d3";
import {Selection} from "d3-selection";
import {addDays, createFormatter, createPlugin, DateFormatter, EventStore, View} from "@fullcalendar/core";
import {toMoment} from "@fullcalendar/moment";
import {EventApi} from "@fullcalendar/core/api/EventApi";
import * as moment from "moment-timezone";
import {Moment} from "moment-timezone";

class Segment {
	constructor(
		public start: Moment,
		public end: Moment,
		public isStart: boolean,
		public isEnd: boolean,
		public event: EventApi
	) {
		console.log(`${start.toString()} - ${end.toString()}`)
	}

	public toString(): string {
		return this.start.toString() + ' - ' + this.end.toString();
	}
}

type DayOccupationColorAmount = {
	color: string,
	duration: number,
	startAngle: number,
	endAngle: number
};

interface DisplayedMonth {
	intervalStart: Moment,
	intervalEnd: Moment,
	displayedStart: Moment,
	displayedEnd: Moment,
	displayedWeeks: DisplayedWeek[]
}

type DisplayedWeek = [DisplayedDay, DisplayedDay, DisplayedDay, DisplayedDay, DisplayedDay, DisplayedDay, DisplayedDay];
type DisplayedDay = {
	day: Moment,
	foreignMonth: boolean
};

export class MultiMonthView extends View {

	private width: number;
	private height: number;

	private weekNumbersVisible: false; // display week numbers along the side?
	private $contentElement: HTMLElement;
	private uuid: string;
	private _svg: Selection<SVGElement, undefined, HTMLElement, any>;

	private padding = 15;
	private spacing = 15;
	private monthWidth = 93;
	private monthHeight = 90;
	private firstDayOffsetX = 17;
	private dayColumnWidth = 12;
	private firstWeekLineOffset = 37;
	private weekLineHeight = 11.5;

	private maxOccupationTime = 8; //hours
	private events: EventApi[] = null;

	private weekDayShortNames = this.initWeekDayShortNames();

	// called once when the view is instantiated, when the user switches to the view.
	// initialize member variables or do other setup tasks.
	public initialize() {
		this.uuid = "year-" + generateUUID();
		this.$contentElement = parseHtml(`<div class="fc-multi-month-content" id="${this.uuid}">`);
		let svgElement: SVGElement = parseSvg('<svg class="svg">');
		this.$contentElement.append(svgElement);
		this.el.classList.add('fc-multi-month-view');
		this.el.appendChild(this.$contentElement);
		this._svg = d3.select<SVGElement, undefined>(svgElement);
	}

	public updateSize(isResize: boolean, viewHeight: number, isAuto: boolean) {
		super.updateSize(isResize, viewHeight, isAuto);
		const newWidth = this.el.offsetWidth;
		if (viewHeight !== this.height || newWidth !== this.width) {
			this.width = newWidth;
			this.height = viewHeight;
			this.el.style.height = viewHeight + "px";
			this.updateMonthDisplays();
		}
	}

	// reponsible for rendering the given Event Objects
	public renderEvents(eventStore: EventStore) {
		this.events = this.context.calendar.getEvents();
		this.updateMonthDisplays();
	}

	private getMonthRangesToBeDisplayed(): DisplayedMonth[] {
		let startMoment = toMoment(this.currentStart, this.calendar);
		let endMoment = toMoment(this.currentEnd, this.calendar);
		const numberOfMonths = endMoment.diff(startMoment, 'month');
		console.log("number of months: " + numberOfMonths);
		const displayedMonths: DisplayedMonth[] = [];
		for (let i = 0; i < numberOfMonths; i++) {
			let intervalStart = startMoment.clone().add(i, 'month');
			let intervalEnd = startMoment.clone().add(i + 1, 'month');
			const monthRange: DisplayedMonth = {
				intervalStart,
				intervalEnd,
				displayedStart: this.startOfWeek(startMoment),
				displayedEnd: this.startOfWeek(intervalEnd).add(7, "day"),
				displayedWeeks: this.getDisplayedWeeks(intervalStart)
			};
			displayedMonths.push(monthRange);
		}
		return displayedMonths;
	}

	private startOfWeek(m: Moment) {
		let copy = m.clone();
		let firstDay = this.calendar.opt("firstDay") || 0;
		while(copy.day() !== firstDay) {
			copy.add(-1, 'day');
		}
		return copy;
	}

	private getDisplayedWeeks(intervalStart: Moment): DisplayedWeek[] {
		let weeks: DisplayedWeek[] = [];
		let month = intervalStart.month();
		let displayedStart = this.startOfWeek(intervalStart);
		for (let i = 0; i < 6; i++) {
			let week = [];
			for (let j = 0; j < 7; j++) {
				let day = displayedStart.clone().add(i * 7 + j, 'day');
				week.push({
					day: day,
					foreignMonth: day.month() !== month
				});
			}
			weeks.push(week as DisplayedWeek);
		}
		return weeks;
	}

	private getMonthsPerRow() {
		let bestNumberOfColumns = Math.ceil(this.width / 270);
		if (bestNumberOfColumns === 5) {
			bestNumberOfColumns = 4;
		} else if (bestNumberOfColumns > 6) {
			bestNumberOfColumns = 6;
		}
		return bestNumberOfColumns;
	}

	private initWeekDayShortNames(): string[] {
		let formatter = createFormatter({weekday: 'narrow'});
		let names = [];
		for (let i = 0; i < 7; i++) {
			let date = addDays(new Date(), i);
			names[date.getDay()] = this.dateEnv.format(date, formatter);
		}
		return names;
	}

	private calculateDayOccupationColorsFromEvents(events: EventApi[]) {
		let segments = this.eventsToSegments(events);
		let segmentsByDay = this.groupSegmentsByDay(segments);

		let dayOccupationColors: { [dayString: string]: DayOccupationColorAmount[] } = {};
		Object.keys(segmentsByDay).forEach(dayString => {
			let segments = segmentsByDay[dayString];
			dayOccupationColors[dayString] = this.calculateDayOccupationColors(segments);
		});
		return dayOccupationColors;
	}

	private monthNameFormatter: DateFormatter = createFormatter({month: 'long'});

	private updateMonthDisplays() {
		if (!this.width || !this.height || this.events == null) {
			return;
		}

		const dayOccupationColors = this.calculateDayOccupationColorsFromEvents(this.events);

		const monthRanges = this.getMonthRangesToBeDisplayed();

		let numberOfCols = this.getMonthsPerRow();
		let numberOfRows = monthRanges.length / numberOfCols;

		let neededDrawAreaWidth = 2 * this.padding + numberOfCols * this.monthWidth + (numberOfCols - 1) * this.spacing;
		let availableDrawAreaWidth = this.width / 2;
		let viewBoxWidth = availableDrawAreaWidth > neededDrawAreaWidth ? availableDrawAreaWidth : neededDrawAreaWidth;
		let availableDrawAreaHeight = (this.height / this.width) * viewBoxWidth;
		let neededDrawAreaHeight = 2 * this.padding + numberOfRows * this.monthHeight + (numberOfRows - 1) * this.spacing + 10 /*some additional space at the bottom...*/;
		let viewBoxHeight = Math.max(
			availableDrawAreaHeight,
			neededDrawAreaHeight
		);
		console.log(`Math.min(0, -(${availableDrawAreaHeight} - ${viewBoxHeight}) / 2)`);
		this._svg.attr("viewBox", `${-(viewBoxWidth - neededDrawAreaWidth) / 2} ${Math.min(0, -(availableDrawAreaHeight - neededDrawAreaHeight) / 2)} ${viewBoxWidth} ${viewBoxHeight}`);
		this._svg.style("height", Math.max(((this.width / viewBoxWidth) * viewBoxHeight)) + "px");
		let calculateMonthPositionTransform = (monthRange: DisplayedMonth, monthIndex: number) => {
			let x = this.padding + (monthIndex % numberOfCols) * (this.monthWidth + this.spacing);
			let y = this.padding + Math.floor(monthIndex / numberOfCols) * (this.monthHeight + this.spacing);
			return `translate(${x},${y})`;
		};

		let _month = this._svg
			.selectAll("g.UiCalendar-month")
			.data(monthRanges);
		let _monthEnter = _month
			.enter()
			.append("g")
			.classed("UiCalendar-month", true)
			.attr("transform", calculateMonthPositionTransform);
		_monthEnter
			.append("text")
			.classed("UiCalendar-month-name", true)
			.text(monthRange => this.dateEnv.format(strippedUtcLikeDate(monthRange.intervalStart), this.monthNameFormatter))
			.attr("x", 0)
			.attr("y", 10)
			.on("click", (monthRange) => this.context.options.navLinkMonthClick && this.context.options.navLinkMonthClick(monthRange.intervalStart.toDate(), d3.event));
		_month.merge(_monthEnter)
			.transition()
			.attr("transform", calculateMonthPositionTransform);
		_month.merge(_monthEnter)
			.selectAll("text.UiCalendar-day-name")
			.data(this.weekDayShortNames)
			.enter()
			.append("text")
			.classed("UiCalendar-day-name", true)
			.text(name => name)
			.attr("x", (name, i) => this.firstDayOffsetX + i * this.dayColumnWidth)
			.attr("y", 25);
		_month.exit()
			.remove();

		let _week = _month.merge(_monthEnter)
			.selectAll("g.UiCalendar-week")
			.data((monthRange) => monthRange.displayedWeeks);
		let _weekEnter = _week.enter()
			.append("g")
			.classed("UiCalendar-week", true)
			.attr("transform", (week, i) => `translate(0, ${this.firstWeekLineOffset + i * this.weekLineHeight})`);
		_week.exit()
			.remove();

		let _weekNumber = _week.merge(_weekEnter)
			.selectAll("text.UiCalendar-week-number")
			.data(week => [week[Math.floor(week.length / 2)]]);
		let _weekNumberEnter = _weekNumber.enter()
			.append("text")
			.classed("UiCalendar-week-number", true)
			.attr("x", 6)
			.on("click", (d) => this.context.options.navLinkWeekClick && this.context.options.navLinkWeekClick(d.day.toDate(), d3.event));
		_weekNumber.merge(_weekNumberEnter)
			.text(day => this.dateEnv.computeWeekNumber(day.day.toDate()));

		let _day = _week.merge(_weekEnter)
			.selectAll("g.UiCalendar-day")
			.data(week => week);
		let occupationRadius = (this.weekLineHeight - .5) / 2 - .7;
		let occupationCircleCenterOffset = -2.5;
		let _dayEnter = _day.enter()
			.append("g")
			.classed("UiCalendar-day", true)
			.attr("transform", (day, dayIndex) => `translate(${this.firstDayOffsetX + (dayIndex % 7) * this.dayColumnWidth}, 0)`)
			.on("click", (d) => this.context.options.navLinkDayClick && this.context.options.navLinkDayClick(d.day.toDate(), d3.event))
			.call((g) => {
				g.append("circle")
					.classed("day-occupation-background-circle", true);
				g.append("text")
					.classed("day-number", true);
			});
		_day.merge(_dayEnter)
			.call((g) => {
				g.select(".day-occupation-background-circle")
					.attr("r", occupationRadius)
					.attr("cy", occupationCircleCenterOffset);
				g.select("text.day-number")
					.text((d) => d.day.date());
			})
			.classed("free", day => {
				let dayOccupationColorsForDay = dayOccupationColors[this.getDayString(day.day)];
				return !dayOccupationColorsForDay || dayOccupationColorsForDay.length === 0;
			})
			.classed("other-month", (day: DisplayedDay, dayIndex: number) => {
				return day.foreignMonth;
			})
			.classed("today", d => {
				return !d.foreignMonth && moment().isSame(d.day, 'day')
			})
			.classed("weekend", (day: DisplayedDay, dayIndex: number) => this.context.options.businessHours.daysOfWeek.indexOf(day.day.day()) == -1);
		_day.exit()
			.remove();

		let _dayOccupation = _day.merge(_dayEnter).selectAll("path.day-occupation")
			.data(day => {
				return dayOccupationColors[this.getDayString(day.day)] || [];
			});
		let _dayOccupationEnter = _dayOccupation.enter()
			.append("path")
			.classed("day-occupation", true);
		_dayOccupation.merge(_dayOccupationEnter)
			.attr("d", (occupation, i, j) => {
				return this.describeArc(0, occupationCircleCenterOffset, occupationRadius, occupation.startAngle, occupation.endAngle);
			})
			.attr("data-duration-in-hours", (occupation, i, j) => (occupation.duration / 3600000))
			.style("stroke", occupationColor => {
				let hslColor = d3.hsl(occupationColor.color);
				if (hslColor.l > 0.45) {
					hslColor.l = 0.45;
				}
				if (hslColor.s < 0.5) {
					hslColor.s = 0.5;
				}
				return hslColor.toString();
			});
		_dayOccupation.exit().remove();
	}

	private describeArc(centerX: number, centerY: number, radius: number, startAngle: number, endAngle: number) {
		let startX = centerX + (radius * Math.sin(startAngle));
		let startY = centerY - radius * Math.cos(startAngle);
		let endX = centerX + (radius * Math.sin(endAngle));
		let endY = centerY - radius * Math.cos(endAngle);
		let largeArc = endAngle - startAngle <= Math.PI ? "0" : "1";
		let arcSweep = endAngle - startAngle <= Math.PI ? "1" : "0";
		return `M ${startX} ${startY} A ${radius} ${radius} 0 ${largeArc} 1 ${endX} ${endY}`;
	}

	/* Events
	 ------------------------------------------------------------------------------------------------------------------*/
	private eventsToSegments(events: EventApi[]): Segment[] {
		let segments: Segment[] = [];
		for (let i = 0; i < events.length; i++) {
			let event = events[i];
			segments.push(...this.eventToSegments(event));
		}
		return segments;
	}

	private eventToSegments(event: EventApi): Segment[] {
		let startMoment = toMoment(event.start, this.calendar);
		let endMoment = event.end != null ? toMoment(event.end, this.calendar) : null;
		if (event.end == null) {
			let start = startMoment.clone().startOf("day").hour(8);
			let end = startMoment.clone().startOf("day").hour(16);
			return [new Segment(moment(start), moment(end), true, true, event)];
		} else if (startMoment.day() === endMoment.day() && endMoment.valueOf() - startMoment.valueOf() < 86400000 /*day*/) {
			return [new Segment(startMoment, endMoment, true, true, event)];
		} else {
			let segments: Segment[] = [];
			let segStart = startMoment;
			let segEnd = startMoment;
			while (segEnd.valueOf() < endMoment.valueOf()) {
				segStart = segEnd;
				segEnd = moment.min(endMoment, segStart.clone().startOf("day").add(1, "day"));
				segments.push(new Segment(segStart.clone(), segEnd.clone(), +segStart === +event.start, +segEnd === +event.end, event));
			}
			return segments;
		}
	}

	private groupSegmentsByDay(segments: Segment[]): { [dayString: string]: Segment[] } {
		let segmentsByDay: { [dayString: string]: Segment[] } = {};
		for (let i = 0; i < segments.length; i++) {
			let segment = segments[i];
			let dayString = this.getDayString(moment(segment.start));
			if (!segmentsByDay[dayString]) {
				segmentsByDay[dayString] = [];
			}
			segmentsByDay[dayString].push(segment);
		}
		return segmentsByDay;
	}

	private calculateDayOccupationColors(mergableSegments: Segment[]): DayOccupationColorAmount[] {
		let durationByBackgroundColor: { [color: string]: number } = {};
		for (let i = 0; i < mergableSegments.length; i++) {
			let segment: Segment = mergableSegments[i];
			if (!durationByBackgroundColor[segment.event.backgroundColor]) {
				durationByBackgroundColor[segment.event.backgroundColor] = 0;
			}
			durationByBackgroundColor[segment.event.backgroundColor] += segment.end.valueOf() - segment.start.valueOf();
		}

		let sortedColorDurations: DayOccupationColorAmount[] = Object.keys(durationByBackgroundColor).map(color => {
			return {
				color,
				duration: durationByBackgroundColor[color],
				startAngle: null,
				endAngle: null
			}
		}).sort((e1, e2) => e2.duration - e1.duration);

		let totalTime = sortedColorDurations.reduce((sum, colorDuration) => sum + colorDuration.duration, 0);
		let fullOccupation = Math.max(totalTime, this.maxOccupationTime * 3600000);
		let timeOffset = 0;

		for (let i = 0; i < sortedColorDurations.length; i++) {
			let colorDuration = sortedColorDurations[i];
			colorDuration.startAngle = timeOffset / fullOccupation * 2 * Math.PI;
			let endAngle = (timeOffset + colorDuration.duration) / fullOccupation * 2 * Math.PI;
			if (colorDuration.startAngle === 0 && endAngle === 2 * Math.PI) {
				endAngle -= .0001; // else the arc won't be drawn...
			}
			colorDuration.endAngle = endAngle;
			timeOffset += colorDuration.duration;
		}

		return sortedColorDurations;
	}

	// this is faster formatting!
	private getDayString(d: Moment) {
		return `${d.year()}-${d.month()}-${d.date()}`;
	}

}

function strippedUtcLikeDate(m: Moment) {
	return moment(m).utc().add(m.utcOffset(), 'm').toDate();
}

export var multiMonthViewPlugin = createPlugin({
	views: {
		multiMonth: {
			class: MultiMonthView,
			duration: {
				months: 12
			}
		},
		year: {
			type: 'multiMonth',
			duration: {
				years: 1
			}
		}
	}
});


