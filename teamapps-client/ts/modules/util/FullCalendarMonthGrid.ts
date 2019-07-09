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
import {generateUUID, parseHtml, parseSvg} from "../Common";
import * as d3 from "d3";
import {Selection} from "d3-selection";
import {addDays, createFormatter, createPlugin, DateFormatter, EventApi, EventStore, View} from "@fullcalendar/core";
import {toMoment} from "@fullcalendar/moment";
import * as moment from "moment-timezone";
import {Moment} from "moment-timezone";
import {CalendarEventListPopper} from "../micro-components/CalendarEventListPopper";

class Segment {
	constructor(
		public start: Moment,
		public end: Moment,
		public isStart: boolean,
		public isEnd: boolean,
		public event: EventApi
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

export class MonthGridView extends View {

	private width: number;
	private height: number;

	private $contentElement: HTMLElement;
	private uuid: string;
	private _svg: Selection<SVGElement, undefined, HTMLElement, any>;


	private readonly padding = 12;
	private readonly dayColumnWidth = 12;
	private readonly monthNameHeight = 20;
	private readonly weekLineHeight = 11.5;
	private readonly spacingX = 15;
	private readonly spacingY = 6;
	private readonly textHangOffset = 4.8;

	private get monthWidth() {
		return (this.opt("weekNumbers") ? 8 : 7) * this.dayColumnWidth;
	}

	private get monthHeight() {
		return this.monthNameHeight + 7 * this.weekLineHeight;
	}


	private maxOccupationTime = 8; //hours
	private events: EventApi[] = null;

	private weekDayShortNames = this.initWeekDayShortNames();

	private eventsPopper: CalendarEventListPopper;
	private timeFormatter: DateFormatter = createFormatter({hour: 'numeric', minute: '2-digit'});

	// called once when the view is instantiated, when the user switches to the view.
	// initialize member variables or do other setup tasks.
	public initialize() {
		this.uuid = "year-" + generateUUID();
		this.$contentElement = parseHtml(`<div class="fc-month-grid-content" id="${this.uuid}">`);
		let svgElement: SVGElement = parseSvg('<svg class="svg">');
		this.$contentElement.append(svgElement);
		this.el.classList.add('fc-month-grid-view');
		this.el.appendChild(this.$contentElement);
		this._svg = d3.select<SVGElement, undefined>(svgElement);
		this.eventsPopper = new CalendarEventListPopper();
	}

	destroy(): void {
		super.destroy();
		this.eventsPopper.destroy();
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
		let firstDay = this.opt("firstDay") || 0;
		while (copy.day() !== firstDay) {
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

		let bestTileCoverageInfo = this.getBestTileCoverageInfo(monthRanges.length);

		let subTileZoom = Math.max(.8, 1 - (bestTileCoverageInfo.monthTileWidth - this.opt("minMonthTileWidth")) / (3 * this.opt("minMonthTileWidth")));

		let viewBoxWidth: number;
		let viewBoxHeight: number;
		let viewBoxOffsetX: number;
		let viewBoxOffsetY: number;
		var monthTileWidth = bestTileCoverageInfo.monthTileWidth;
		if (!bestTileCoverageInfo.needsScroll && this.opt("maxMonthTileWidth") && monthTileWidth > this.opt("maxMonthTileWidth")) {
			viewBoxWidth = bestTileCoverageInfo.preferredDrawAreaWidth * (monthTileWidth / this.opt("maxMonthTileWidth"));
			viewBoxHeight = bestTileCoverageInfo.preferredDrawAreaHeight * (monthTileWidth / this.opt("maxMonthTileWidth"));
			viewBoxOffsetX = (bestTileCoverageInfo.preferredDrawAreaWidth - viewBoxWidth) / 2;
			viewBoxOffsetY = (bestTileCoverageInfo.preferredDrawAreaHeight - viewBoxHeight) / 2;
		} else {
			viewBoxWidth = bestTileCoverageInfo.preferredDrawAreaWidth;
			viewBoxHeight = bestTileCoverageInfo.preferredDrawAreaHeight;
			viewBoxOffsetX = 0;
			viewBoxOffsetY = 0;
		}
		this._svg.attr("viewBox", `${viewBoxOffsetX} ${viewBoxOffsetY} ${viewBoxWidth} ${viewBoxHeight}`);
		var svgHeight = Math.max(this.height, bestTileCoverageInfo.needsScroll ? bestTileCoverageInfo.height : bestTileCoverageInfo.height);
		this._svg.style("height", svgHeight + "px");

		let calculateMonthPositionTransform = (monthRange: DisplayedMonth, monthIndex: number) => {
			var colIndex = monthIndex % bestTileCoverageInfo.numberOfCols;
			var rowIndex = Math.floor(monthIndex / bestTileCoverageInfo.numberOfCols);
			let x = this.padding + colIndex * (this.monthWidth + this.spacingX);
			let y = this.padding + rowIndex * (this.monthHeight + this.spacingY);
			return `translate(${x},${y})`;
		};

		let _month = this._svg
			.selectAll("g.fc-month-grid-month")
			.data(monthRanges);
		let _monthEnter = _month
			.enter()
			.append("g")
			.classed("fc-month-grid-month", true)
			.attr("transform", calculateMonthPositionTransform);
		// _monthEnter.append("rect")
		// 	.attr("fill", "none")
		// 	.attr("stroke", "black")
		// 	.attr("width", this.monthWidth)
		// 	.attr("height", this.monthHeight);
		_monthEnter
			.append("text")
			.classed("fc-month-grid-month-name", true)
			.text(monthRange => this.dateEnv.format(strippedUtcLikeDate(monthRange.intervalStart), this.monthNameFormatter))
			.attr("x", 0)
			.attr("y", 10)
			.on("click", (monthRange) => this.context.options.navLinkMonthClick && this.context.options.navLinkMonthClick(monthRange.intervalStart.toDate(), d3.event));
		_month.merge(_monthEnter)
			.transition()
			.attr("transform", calculateMonthPositionTransform);
		_month.exit()
			.remove();

		let _dayName = _month.merge(_monthEnter)
			.selectAll("text.fc-month-grid-day-name")
			.data([
				this.weekDayShortNames[this.opt("firstDay")],
				this.weekDayShortNames[(this.opt("firstDay") + 1) % 7],
				this.weekDayShortNames[(this.opt("firstDay") + 2) % 7],
				this.weekDayShortNames[(this.opt("firstDay") + 3) % 7],
				this.weekDayShortNames[(this.opt("firstDay") + 4) % 7],
				this.weekDayShortNames[(this.opt("firstDay") + 5) % 7],
				this.weekDayShortNames[(this.opt("firstDay") + 6) % 7]
			]);
		var _dayNameEnter = _dayName
			.enter()
			.append("text")
			.classed("fc-month-grid-day-name", true)
			.text(name => name);
		_dayName.merge(_dayNameEnter)
			.attr("transform", (name, i) => `translate(${(i + (this.opt("weekNumbers") ? 1 : 0) + 0.5 /*center!*/) * this.dayColumnWidth}, ${this.monthNameHeight + this.textHangOffset}) scale(${subTileZoom})`);

		let _week = _month.merge(_monthEnter)
			.selectAll("g.fc-month-grid-week")
			.data((monthRange) => monthRange.displayedWeeks);
		let _weekEnter = _week.enter()
			.append("g")
			.classed("fc-month-grid-week", true)
			.attr("transform", (week, i) => `translate(0, ${this.monthNameHeight + (i + 1) * this.weekLineHeight})`);
		_week.exit()
			.remove();

		let _weekNumber = _week.merge(_weekEnter)
			.selectAll("text.fc-month-grid-week-number")
			.data(week => this.opt("weekNumbers") ? [week[Math.floor(week.length / 2)]] : []);
		let _weekNumberEnter = _weekNumber.enter()
			.append("text")
			.classed("fc-month-grid-week-number", true)
			.attr("x", 6)
			.attr("y", this.textHangOffset)
			.on("click", (d) => this.context.options.navLinkWeekClick && this.context.options.navLinkWeekClick(d.day.toDate(), d3.event));
		_weekNumber.merge(_weekNumberEnter)
			.text(day => this.dateEnv.computeWeekNumber(day.day.toDate()))
			.attr("transform", (week, i) => `scale(${subTileZoom})`);

		let _day = _week.merge(_weekEnter)
			.selectAll("g.fc-month-grid-day")
			.data(week => week);
		let occupationRadius = (this.weekLineHeight - .5) / 2 - .7;
		let occupationCircleCenterOffset = 2.4;
		let _dayEnter = _day.enter()
			.append("g")
			.classed("fc-month-grid-day", true)
			.call((g) => {
				g.append("circle")
					.classed("day-occupation-background-circle", true);
				g.append("text")
					.classed("day-number", true);
			});
		_dayEnter.on("click", (d) => this.context.options.navLinkDayClick && this.context.options.navLinkDayClick(d.day.toDate(), d3.event));
		_day.merge(_dayEnter)
			.attr("transform", (day, dayIndex) => `translate(${((dayIndex % 7) + (this.opt("weekNumbers") ? 1 : 0) + 0.5) * this.dayColumnWidth}, 0) scale(${subTileZoom})`)
			.call((g) => {
				g.select(".day-occupation-background-circle")
					.attr("r", occupationRadius)
					.attr("cy", occupationCircleCenterOffset);
				g.select("text.day-number")
					.text((d) => d.day.date())
					.attr("y", () => this.textHangOffset);
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

		_dayEnter.on("pointerenter", (d, i, nodes) => {
			if (!d.foreignMonth) {
				var bestTileCoverageInfo = this.getBestTileCoverageInfo(monthRanges.length);
				let subTileZoom = Math.max(.8, 1 - (bestTileCoverageInfo.monthTileWidth - this.opt("minMonthTileWidth")) / (3 * this.opt("minMonthTileWidth")));
				var strokeWidth = 1.7 / (subTileZoom * subTileZoom);
				d3.select(nodes[i])
					.selectAll("path.day-occupation")
					.transition()
					.ease(d3.easeQuad)
					.duration(200)
					.style("stroke-width", `${strokeWidth}px`)
					.attr("d", (occupation: DayOccupationColorAmount, i, groups) => {
						var radius = occupationRadius + (strokeWidth - 1) / 2;
						return this.describeArc(0, occupationCircleCenterOffset, radius, occupation.startAngle, occupation.endAngle);
					});
				let events = this.getEventsForDay(d);
				this.updatePopper(d.day, events, nodes[i].querySelector(".day-occupation-background-circle"));
			}
		}).on("pointerleave", (d, i, nodes) => {
			d3.select(nodes[i])
				.selectAll("path.day-occupation")
				.transition()
				.ease(d3.easeQuad)
				.duration(200)
				.style("stroke-width", "1px")
				.attr("d", (occupation: DayOccupationColorAmount, i, groups) => {
					return this.describeArc(0, occupationCircleCenterOffset, occupationRadius, occupation.startAngle, occupation.endAngle);
				});
			this.eventsPopper.setVisible(false);
		});

		let _dayOccupation = _day.merge(_dayEnter).selectAll("path.day-occupation")
			.data(day => {
				return day.foreignMonth ? [] : dayOccupationColors[this.getDayString(day.day)] || [];
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
				if (hslColor.l > 0.55) {
					hslColor.l = 0.55;
				}
				if (hslColor.s < 0.4) {
					hslColor.s = 0.4;
				}
				return hslColor.toString();
			});

		_dayOccupation.exit().remove();
	}

	private getBestTileCoverageInfo(numberOfMonths: number) {
		const possibleNumberOfColumns = [1, 2, 3, 4, 6, 12];
		let areaCoverageInfos: { preferredDrawAreaHeight: number; fittingWidth: number; fittingHeight: number; numberOfCols: number; widthCorrectionFactor: number; heightCorrectionFactor: number; numberOfRows: number; fittingCorrectionFactor: number; preferredDrawAreaWidth: number; fittingMonthTileWidth: number, scrollingWidth: number, scrollingHeight: number, scrollingMonthTileWidth: number }[] = [];
		for (let i = 0; i < possibleNumberOfColumns.length; i++) {
			const numberOfCols = possibleNumberOfColumns[i];
			const numberOfRows = Math.ceil(numberOfMonths / numberOfCols);
			const preferredDrawAreaWidth = 2 * this.padding + numberOfCols * this.monthWidth + (numberOfCols - 1) * this.spacingX;
			const preferredDrawAreaHeight = 2 * this.padding + numberOfRows * this.monthHeight + (numberOfRows - 1) * this.spacingY;

			const widthCorrectionFactor = this.width / preferredDrawAreaWidth;
			const heightCorrectionFactor = this.height / preferredDrawAreaHeight;
			const fittingCorrectionFactor = Math.min(widthCorrectionFactor, heightCorrectionFactor);

			const fittingWidth = fittingCorrectionFactor * preferredDrawAreaWidth;
			const fittingHeight = fittingCorrectionFactor * preferredDrawAreaHeight;
			const fittingMonthTileWidth = fittingCorrectionFactor * this.monthWidth;

			const scrollingWidth = widthCorrectionFactor * preferredDrawAreaWidth;
			const scrollingHeight = widthCorrectionFactor * preferredDrawAreaHeight;
			const scrollingMonthTileWidth = widthCorrectionFactor * this.monthWidth;

			areaCoverageInfos.push({
				numberOfCols,
				numberOfRows,
				preferredDrawAreaWidth,
				preferredDrawAreaHeight,
				widthCorrectionFactor,
				heightCorrectionFactor,
				fittingCorrectionFactor,
				fittingWidth,
				fittingHeight,
				fittingMonthTileWidth,
				scrollingWidth,
				scrollingHeight,
				scrollingMonthTileWidth
			});
		}

		let bestCoverageInfo = areaCoverageInfos.slice()
			.sort((a, b) => b.fittingMonthTileWidth - a.fittingMonthTileWidth)[0];
		const needsScroll = bestCoverageInfo.fittingMonthTileWidth < this.opt("minMonthTileWidth");
		if (needsScroll) {
			bestCoverageInfo = areaCoverageInfos.slice()
				.filter(info => info.scrollingMonthTileWidth > this.opt("minMonthTileWidth"))
				.sort((a, b) => a.scrollingHeight - b.scrollingHeight)[0];
		}
		if (bestCoverageInfo == null) {
			bestCoverageInfo = areaCoverageInfos[0]; // fall back to one column
		}

		return {
			preferredDrawAreaWidth: bestCoverageInfo.preferredDrawAreaWidth,
			preferredDrawAreaHeight: bestCoverageInfo.preferredDrawAreaHeight,
			monthTileWidth: needsScroll ? bestCoverageInfo.scrollingMonthTileWidth : bestCoverageInfo.fittingMonthTileWidth,
			height: needsScroll ? bestCoverageInfo.scrollingHeight : bestCoverageInfo.fittingHeight,
			numberOfCols: bestCoverageInfo.numberOfCols,
			needsScroll: needsScroll
		};
	}

	private getEventsForDay(d: DisplayedDay) {
		let dayStart = d.day;
		let dayEnd = dayStart.clone().add(1, 'day');
		return this.events.filter(e => {
			let eventStart = toMoment(e.start, this.calendar);
			let eventEnd = e.end != null ? toMoment(e.end, this.calendar) : eventStart.clone().add(1, 'second');
			return +eventEnd > +dayStart && +eventStart < +(dayEnd);
		});
	}

	private updatePopper(day: Moment, events: EventApi[], dayElement: Element) {
		let allDayEvents = events.filter(e => e.allDay);
		let normalEvents = events.filter(e => !e.allDay);

		if (events.length > 0) {
			this.renderPopperEvents(day, allDayEvents, true);
			this.renderPopperEvents(day, normalEvents, false);
			this.eventsPopper.setReferenceElement(dayElement);
			this.eventsPopper.setVisible(true);
		} else {
			this.eventsPopper.setVisible(false);
		}
	}

	private renderPopperEvents(day: Moment, events: EventApi[], allDay: boolean) {
		const cssClass = allDay ? 'all-day' : '';
		let allDayEventsHtml = '';
		events.forEach(event => {
			allDayEventsHtml += `<div class="fc-event ${cssClass}" style="background-color:${event.backgroundColor};border-color:${event.borderColor};color:${event.textColor}">
									<div class="fc-content">
										<span class="fc-time ${allDay ? 'hidden' : ''}">${event.formatRange({hour: 'numeric', minute: 'numeric'})}</span>
										<span class="fc-title">${event.title}</span>            	
									</div>
								</div>`;
		});
		let $container = allDay ? this.eventsPopper.$allDayEventsContainer : this.eventsPopper.$normalEventsContainer;
		$container.innerHTML = allDayEventsHtml;
		$container.classList.toggle("hidden", events.length === 0);

		let eventRender: (arg: { isMirror: boolean, isStart: boolean, isEnd: boolean, event: EventApi, el: HTMLElement, view: View }) => void = this.opt("eventRender");
		if (eventRender != null) {
			$container.querySelectorAll(':scope .fc-event').forEach((el, i) => {
				let event = events[i];
				eventRender({
					event: event,
					isMirror: false,
					isStart: day.isSame(event.start, 'day'),
					isEnd: day.isSame(event.end, 'day'),
					el: el as HTMLElement,
					view: this
				})
			});
		}
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

export var monthGridViewPlugin = createPlugin({
	views: {
		monthGrid: {
			class: MonthGridView,
			duration: {
				months: 12
			},
			minMonthTileWidth: 175,
			maxMonthTileWidth: 0
		},
		year: {
			type: 'monthGrid',
			duration: {
				years: 1
			}
		}
	}
});


