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
import * as moment from "moment-timezone";
import {EventObjectInput} from "fullcalendar/src/types/input-types";
import * as log from "loglevel";
import {View} from "fullcalendar";
import {adjustIfColorTooBright, generateUUID, parseHtml, parseSvg} from "../Common";
import * as d3 from "d3";
import {Selection} from "d3-selection";
import Moment = moment.Moment;
import Logger = log.Logger;

class Segment {
	constructor(
		public start: Moment,
		public end: Moment,
		public isStart: boolean,
		public isEnd: boolean,
		public event: EventObjectInput
	) {
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

type Range = {
	start: Moment,
	end: Moment
}

const logger: Logger = log.getLogger("MyYearView");

interface Month {
	intervalStart: Moment,
	intervalEnd: Moment,
	start: Moment,
	end: Moment,
	intervalUnit: "month"
}
type DisplayedWeek = [DisplayedDay, DisplayedDay, DisplayedDay, DisplayedDay, DisplayedDay, DisplayedDay, DisplayedDay];
type DisplayedDay = {
	day: Moment,
	foreignMonth: boolean
};

// TODO
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
	private events: EventObjectInput[] = [];

	private weekDayShortNames = this.getWeekDayShortNames();

	// called once when the view is instantiated, when the user switches to the view.
	// initialize member variables or do other setup tasks.
	public initialize() {
	}

	// called when another view is selected (and thereby this one is removed from the DOM)
	public removeElement() {
		super.removeElement();
		this.$contentElement = null; // make sure everything gets rebuilt!
	}

	// responsible for displaying the skeleton of the view within the already-defined
	// this.el, a jQuery element.
	public render() {
		if (this.$contentElement == null) {
			this.uuid = "year-" + generateUUID();
			this.$contentElement = parseHtml(`<div class="fc-multi-month-content" id="${this.uuid}">`);
			this.$contentElement.append(parseSvg('<svg class="svg">'));
			this.el
				.classList.add('fc-multi-month-view')
				.append(this.$contentElement);
			this._svg = d3.select<SVGElement, undefined>("#" + this.uuid + " svg");
		}
	}

	public updateSize(totalHeight: number, isAuto: boolean, isResize: boolean) {
		super.updateSize(totalHeight, isAuto, isResize);
		const newWidth = this.el[0].offsetWidth;
		if (totalHeight !== this.height || newWidth !== this.width) {
			this.width = newWidth;
			this.height = totalHeight;
			this._svg
				.style("height", this.height + "px")
				.style("width", this.width + "px");
			this.updateMonthDisplays();
		}
	}

	// reponsible for rendering the given Event Objects
	public renderEvents(events: EventObjectInput[]) {
		this.events = events;
		this.updateMonthDisplays();
	}

	public destroyEvents() {
		// responsible for undoing everything in renderEvents
	}

	// Renders a visual indication of a selection
	public renderSelection(range: { start: Moment, end: Moment }) {
		logger.trace("renderSelection");
	}

	// responsible for undoing everything in renderSelection
	public destroySelection() {
		logger.trace("destroySelection");
	}

	private getMonthRangesToBeDisplayed(): Month[] {
		const numberOfMonths = this.intervalEnd.diff(this.intervalStart, "month");
		const firstMonth = this.intervalStart.clone().startOf("month").stripTime();
		const monthRanges: Month[] = [];
		for (let i = 0; i < numberOfMonths; i++) {
			const monthRange: any = {};
			monthRange.intervalStart = firstMonth.clone().add(i, "month");
			monthRange.intervalEnd = firstMonth.clone().add(i + 1, "month");
			monthRange.start = monthRange.intervalStart.clone().startOf("week");
			monthRange.end = monthRange.intervalEnd.clone().startOf("week").add(1, "week");
			monthRange.intervalUnit = "month";
			monthRanges.push(monthRange);
		}
		return monthRanges;
	}

	private getDisplayedWeeks(monthRange: Month): DisplayedWeek[] {
		let weeks: DisplayedWeek[] = [];
		let month = monthRange.intervalStart.month();
		for (let i = 0; i < 6; i++) {
			let week = [];
			for (let j = 0; j < 7; j++) {
				let day = monthRange.start.clone().add(i * 7 + j, "day");
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

		let bestNumberOfRows = Math.ceil(this.height / 270);
		if (bestNumberOfRows === 5) {
			bestNumberOfRows = 4;
		} else if (bestNumberOfRows > 6) {
			bestNumberOfRows = 6;
		}

		if (bestNumberOfColumns <= 2) {
			return bestNumberOfColumns; // scroll vertically and do not care for vertical space
		} else {
			return bestNumberOfColumns > bestNumberOfRows ? bestNumberOfColumns : 12 / bestNumberOfRows;
		}
	}

	// Renders the view into `this.el`, which should already be assigned
	public renderDates() {
		// this.updateMonthDisplays();
	}

	/**
	 * @deprecated use this.weekDayShortNames instead of calculating this all the time
	 * @returns {any[]}
	 */
	private getWeekDayShortNames(): string[] {
		let names = [];
		for (let i = 0; i < 7; i++) {
			names.push(moment.weekdaysMin()[(this.options.firstDay + i) % 7].substring(0, 1));
		}
		return names;
	}

	private calculateDayOccupationColorsFromEvents(events: EventObjectInput[]) {
		let segments = this.eventsToSegments(events);
		let segmentsByDay = this.groupSegmentsByDay(segments);

		let dayOccupationColors: { [dayString: string]: DayOccupationColorAmount[] } = {};
		Object.keys(segmentsByDay).forEach(dayString => {
			let segments = segmentsByDay[dayString];
			dayOccupationColors[dayString] = this.calculateDayOccupationColors(segments);
		});
		return dayOccupationColors;
	}

	private updateMonthDisplays() {
		if (!this.width || !this.height) {
			return;
		}

		const dayOccupationColors = this.calculateDayOccupationColorsFromEvents(this.events);

		const monthRanges = this.getMonthRangesToBeDisplayed();

		let numberOfCols = this.getMonthsPerRow();
		let numberOfRows = monthRanges.length / numberOfCols;
		let viewBoxWidth = 2 * this.padding + numberOfCols * this.monthWidth + (numberOfCols - 1) * this.spacing;
		let viewBoxHeight = Math.max(
			(this.height / this.width) * viewBoxWidth,
			2 * this.padding + numberOfRows * this.monthHeight + (numberOfRows - 1) * this.spacing
		);
		this._svg.attr("viewBox", `0 0 ${viewBoxWidth} ${viewBoxHeight}`);
		this._svg.style("height", Math.max(this.height, ((this.width / viewBoxWidth) * viewBoxHeight)) + "px");
		let calculateMonthPositionTransform = (monthRange: Month, monthIndex: number) => {
			let x = this.padding + (monthIndex % this.getMonthsPerRow()) * (this.monthWidth + this.spacing);
			let y = this.padding + Math.floor(monthIndex / this.getMonthsPerRow()) * (this.monthHeight + this.spacing);
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
			.text(monthRange => monthRange.intervalStart.format("MMMM"))
			.attr("x", 0)
			.attr("y", 10);
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
			.data((monthRange) => this.getDisplayedWeeks(monthRange));
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
			.attr("x", 6);
		_weekNumber.merge(_weekNumberEnter)
			.text(day => day.day.isoWeek());

		let _day = _week.merge(_weekEnter)
			.selectAll("g.UiCalendar-day")
			.data(week => week);
		let occupationRadius = (this.weekLineHeight - .5) / 2 - .7;
		let occupationCircleCenterOffset = -2.5;
		let _dayEnter = _day.enter()
			.append("g")
			.classed("UiCalendar-day", true)
			.attr("transform", (day, dayIndex) => `translate(${this.firstDayOffsetX + (dayIndex % 7) * this.dayColumnWidth}, 0)`)
			.call((g) => {
				g.append("text")
					.classed("day-number", true);
				g.append("circle")
					.classed("day-occupation-background-circle", true);
			});
		_day.merge(_dayEnter)
			.call((g) => {
				g.select("text.day-number")
					.text((d) => d.day.date());
				g.select(".day-occupation-background-circle")
					.attr("r", occupationRadius)
					.attr("cy", occupationCircleCenterOffset);
			})
			.classed("free", day => {
				let dayOccupationColorsForDay = dayOccupationColors[this.getDayString(day.day)];
				return !dayOccupationColorsForDay || dayOccupationColorsForDay.length === 0;
			})
			.classed("other-month", (day: DisplayedDay, dayIndex: number) => {
				return day.foreignMonth;
			})
			.classed("weekend", (day: DisplayedDay, dayIndex: number) => day.day.isoWeekday() > 5);
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
				return adjustIfColorTooBright(occupationColor.color, 210)
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
	private eventsToSegments(events: EventObjectInput[]): Segment[] {
		let segments: Segment[] = [];
		for (let i = 0; i < events.length; i++) {
			let event = events[i];
			segments.push(...this.eventToSegments(event));
		}
		return segments;
	}

	private eventToSegments(event: EventObjectInput): Segment[] {
		if ((event.start as Moment).day() === (event.end as Moment).day()
			&& (event.end as Moment).valueOf() - (event.start as Moment).valueOf() < 86400000 /*day*/) {
			// return [{
			// 	start: event.start as Moment,
			// 	end: event.end as Moment,
			// 	isStart: true,
			// 	isEnd: true,
			// 	event: event
			// }];
			return [new Segment(event.start as Moment, event.end as Moment, true, true, event)];
		} else {
			let segments: Segment[] = [];
			let segStart = event.start as Moment;
			let segEnd = event.start as Moment;
			while (segEnd.valueOf() < (event.end as Moment).valueOf()) {
				segStart = segEnd;
				segEnd = moment.min(event.end as Moment, segStart.clone().startOf("day").add(1, "day"));
				segments.push(new Segment(segStart, segEnd, segStart.isSame(event.start as Moment), segEnd.isSame(event.end as Moment), event));
			}
			return segments;
		}
	}

	private groupSegmentsByDay(segments: Segment[]): { [dayString: string]: Segment[] } {
		let segmentsByDay: { [dayString: string]: Segment[] } = {};
		for (let i = 0; i < segments.length; i++) {
			let segment = segments[i];
			let dayString = this.getDayString(segment.start);
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
			durationByBackgroundColor[segment.event.backgroundColor] += segment.end.valueOf() - (segment.start as Moment).valueOf();
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

	// this is way(!!) faster thang moment.format("YYYY-MM-DD")
	private getDayString(moment: Moment) {
		return `${moment.year()}-${moment.month()}-${moment.date()}`;
	}

}


