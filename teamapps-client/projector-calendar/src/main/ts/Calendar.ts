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

import {
	DtoCalendar_DataNeededEvent,
	DtoCalendar_DayClickedEvent,
	DtoCalendar_DayHeaderClickedEvent,
	DtoCalendar_EventClickedEvent,
	DtoCalendar_EventMovedEvent,
	DtoCalendar_IntervalSelectedEvent,
	DtoCalendar_MonthHeaderClickedEvent,
	DtoCalendar_ViewChangedEvent,
	DtoCalendar_WeekHeaderClickedEvent,
	DtoCalendarCommandHandler,
	DtoCalendar,
	DtoCalendarEventSource, DtoCalendarViewMode, DtoCalendarEventClientRecord
} from "./generated";
import {Interval, intervalsOverlap} from "./intervals";

import {addDays, Calendar as FullCalendar} from '@fullcalendar/core';
import dayGridPlugin, {DayGridView} from '@fullcalendar/daygrid';
import timeGridPlugin, {TimeGridView} from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import momentTimeZone from './fullcalendar-moment-timezone';
import {EventInput, EventRenderingChoice} from "@fullcalendar/core/structs/event";
import {EventSourceError, ExtendedEventSourceInput} from "@fullcalendar/core/structs/event-source";
import {View} from "@fullcalendar/core/View";
import EventApi from "@fullcalendar/core/api/EventApi";
import {Duration} from "@fullcalendar/core/datelib/duration";
import {monthGridViewPlugin} from "./FullCalendarMonthGrid";
import {OptionsInputBase} from "@fullcalendar/core/types/input-types";
import {AbstractLegacyComponent, bind, parseHtml, prependChild, ServerObjectChannel, TeamAppsEvent} from "projector-client-object-api";

export class Calendar extends AbstractLegacyComponent<DtoCalendar> implements DtoCalendarCommandHandler, DtoCalendarEventSource {

	public readonly onEventClicked: TeamAppsEvent<DtoCalendar_EventClickedEvent> = new TeamAppsEvent();
	public readonly onEventMoved: TeamAppsEvent<DtoCalendar_EventMovedEvent> = new TeamAppsEvent();
	public readonly onDayClicked: TeamAppsEvent<DtoCalendar_DayClickedEvent> = new TeamAppsEvent();
	public readonly onIntervalSelected: TeamAppsEvent<DtoCalendar_IntervalSelectedEvent> = new TeamAppsEvent();
	public readonly onViewChanged: TeamAppsEvent<DtoCalendar_ViewChangedEvent> = new TeamAppsEvent();
	public readonly onDataNeeded: TeamAppsEvent<DtoCalendar_DataNeededEvent> = new TeamAppsEvent();
	public readonly onDayHeaderClicked: TeamAppsEvent<DtoCalendar_DayHeaderClickedEvent> = new TeamAppsEvent();
	public readonly onWeekHeaderClicked: TeamAppsEvent<DtoCalendar_WeekHeaderClickedEvent> = new TeamAppsEvent();
	public readonly onMonthHeaderClicked: TeamAppsEvent<DtoCalendar_MonthHeaderClickedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private eventSource: DtoCalendarFullCalendarEventSource;
	private calendar: FullCalendar;

	constructor(config: DtoCalendar, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);

		this.$main = parseHtml(`<div class="Calendar">
	<div class="calendar"></div>
</div>`);
		let $fullCalendarElement: HTMLElement = this.$main.querySelector(':scope > .calendar');

		this.eventSource = new DtoCalendarFullCalendarEventSource();
		this.calendar = new FullCalendar($fullCalendarElement, {
			plugins: [momentTimeZone, interactionPlugin, dayGridPlugin, timeGridPlugin, monthGridViewPlugin],
			header: false,
			defaultView: config.activeViewMode,
			defaultDate: config.displayedDate,
			weekNumbers: config.showWeekNumbers,
			businessHours: {
				start: config.businessHoursStart + ':00',
				end: config.businessHoursEnd + ':00',
				daysOfWeek: config.workingDays
			},
			firstDay: config.firstDayOfWeek, // 1 = monday
			fixedWeekCount: true,
			eventBackgroundColor: config.defaultBackgroundColor,
			eventBorderColor: config.defaultBorderColor,
			eventTextColor: "#000",
			handleWindowResize: false, // we handle this ourselves!
			lazyFetching: false, // no intelligent fetching from fullcalendar. We handle all that!
			selectable: true,
			unselectAuto: false,
			timeZone: config.timeZoneId,
			height: () => {
				console.log("aösjfdsöaljfdsaf");
				return this.getHeight()
			},
			slotEventOverlap: false,
			locale: config.locale,
			views: {
				timeGrid: {
					slotLabelFormat: {
						hour: '2-digit',
						minute: '2-digit'
					}
				},
				day: {
					columnHeaderFormat: {weekday: 'long', month: 'numeric', day: 'numeric'}
				},
				year: {
					minMonthTileWidth: config.minYearViewMonthTileWidth,
					maxMonthTileWidth: config.maxYearViewMonthTileWidth
				}
			},
			eventSources: [this.eventSource],
			datesRender: (arg: {
				view: View;
				el: HTMLElement
			}) => {
				this.onViewChanged.fire({
					viewMode: arg.view.type as DtoCalendarViewMode,
					mainIntervalStart: +arg.view.currentStart,
					mainIntervalEnd: +arg.view.currentEnd,
					displayedIntervalStart: +arg.view.activeStart,
					displayedIntervalEnd: +arg.view.activeEnd
				});

				this.$main.classList.toggle("table-border", config.tableBorder);
				// this.$main.querySelectorAll(":scope .fc-bg td.fc-week-number.fc-widget-content").forEach($e => $e.classList.add('teamapps-blurredBackgroundImage'));
				// this.$main.querySelectorAll(":scope .fc-head td.fc-widget-header").forEach($e => $e.classList.add('teamapps-blurredBackgroundImage'));
				// if (view.type.toLowerCase().indexOf('agenda') !== -1 && moment().isAfter(view.intervalStart) && moment().isBefore(view.intervalEnd)) {
				// 	element.find(".fc-day-header.fc-" + ['sun', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat'][moment().day()]).addClass("fc-today");
				// }
			},
			eventRender: (arg: {
				isMirror: boolean;
				isStart: boolean;
				isEnd: boolean;
				event: EventApi;
				el: HTMLElement;
				view: View;
			}) => {
				let template = arg.view instanceof TimeGridView ? arg.event.extendedProps.timeGridTemplate
					: arg.view instanceof DayGridView ? arg.event.extendedProps.dayGridTemplate
						: arg.event.extendedProps.monthGridTemplate;

				let $fcContent = arg.el.querySelector(':scope .fc-content');
				if (template != null && !arg.event.rendering) {
					arg.el.classList.add('template-content');
					$fcContent.innerHTML = template.render(arg.event.extendedProps.data);
				} else {
					prependChild($fcContent, parseHtml(`<div class="fc-icon img img-16" style="background-image:url('${arg.event.extendedProps.icon}')">`));
				}
				if (arg.event.allDay) {
					arg.el.classList.add("all-day");
				}

				arg.el.addEventListener('click', (e) => {
					this.onEventClicked.fire({
						eventId: parseInt(arg.event.id),
						doubleClick: false
					});
				});
				arg.el.addEventListener('dblclick', (e) => {
					this.onEventClicked.fire({
						eventId: parseInt(arg.event.id),
						doubleClick: true
					});
				});
			},
			dateClick: (() => {
				let lastClickTimeStamp = 0;
				let lastClickClickedDate: Date;
				return (arg: {
					date: Date;
					dateStr: string;
					allDay: boolean;
					resource?: any;
					dayEl: HTMLElement;
					jsEvent: MouseEvent;
					view: View;
				}) => {
					let doubleClick = lastClickClickedDate != null && lastClickClickedDate.valueOf() == arg.date.valueOf() && arg.jsEvent.timeStamp - lastClickTimeStamp < 600;
					if (doubleClick) {
						lastClickTimeStamp = 0;
						lastClickClickedDate = null;
						this.onDayClicked.fire({
							date: arg.date.valueOf(),
							doubleClick: true
						});
					} else {
						lastClickTimeStamp = arg.jsEvent.timeStamp;
						lastClickClickedDate = arg.date;
						this.onDayClicked.fire({
							date: arg.date.valueOf(),
							doubleClick: false
						});
					}
				};
			})(),
			select: (selectionInfo) => {
				this.onIntervalSelected.fire({
					start: selectionInfo.start.valueOf(),
					end: selectionInfo.end.valueOf(),
					allDay: selectionInfo.allDay
				})
			},
			eventResize: (arg: {
				el: HTMLElement;
				startDelta: Duration;
				endDelta: Duration;
				prevEvent: EventApi;
				event: EventApi;
				revert: () => void;
				jsEvent: Event;
				view: View;
			}) => {
				const masterEvent = this.eventSource.getEvent(arg.event.id);
				masterEvent.start = arg.event.start;
				masterEvent.end = arg.event.end;
				this.onEventMoved.fire({
					eventId: parseInt(arg.event.id),
					newStart: arg.event.start.valueOf(),
					newEnd: arg.event.end.valueOf()
				});
			},
			eventDrop: (arg: {
				el: HTMLElement;
				event: EventApi;
				oldEvent: EventApi;
				delta: Duration;
				revert: () => void;
				jsEvent: Event;
				view: View;
			}) => {
				const masterEvent = this.eventSource.getEvent(arg.event.id);
				masterEvent.start = arg.event.start;
				masterEvent.end = arg.event.end;
				this.onEventMoved.fire({
					eventId: parseInt(arg.event.id),
					newStart: arg.event.start.valueOf(),
					newEnd: arg.event.end.valueOf()
				});
			},
			navLinks: true,
			navLinkDayClick: (date, jsEvent) => {
				this.onDayHeaderClicked.fire({
					date: date.valueOf()
				});
				if (this.config.navigateOnHeaderClicks) {
					this.calendar.changeView("timeGridDay", date);
				}
			},
			navLinkWeekClick: (weekStart, jsEvent) => {
				this.onWeekHeaderClicked.fire({
					year: addDays(weekStart, 6).getFullYear(),
					week: this.calendar.dateEnv.computeWeekNumber(weekStart),
					weekStartDate: weekStart.valueOf()
				});
				if (this.config.navigateOnHeaderClicks) {
					this.calendar.changeView("timeGridWeek", weekStart);
				}
			},
			navLinkMonthClick: (monthStart: Date, jsEvent: Event) => {
				this.onMonthHeaderClicked.fire({
					year: monthStart.getFullYear(),
					month: monthStart.getMonth(),
					monthStartDate: monthStart.valueOf()
				});
				if (this.config.navigateOnHeaderClicks) {
					this.calendar.changeView("dayGridMonth", monthStart);
				}
			}
		} as OptionsInputBase);

		if (config.initialData) {
			this.eventSource.addEvents(0, Number.MAX_SAFE_INTEGER, config.initialData.map(e => this.convertToFullCalendarEvent(e)));
			this.calendar.refetchEvents();
		}
		this.eventSource.onDataNeeded.addListener((eventObject: DtoCalendar_DataNeededEvent) => {
			setTimeout(() => this.onDataNeeded.fire(eventObject)); // setTimeout needed because we might still be inside the constructor and no one will be registered to this...
		});

		this.calendar.render();

		this.setStyle(".fc-head td.fc-widget-header>.fc-row.fc-widget-header", {"background-color": config.tableHeaderBackgroundColor ?? ''});
		this.setStyle(".fc-bgevent-skeleton td.fc-week-number", {"background-color": config.tableHeaderBackgroundColor ?? ''});
	}

	public setViewMode(viewMode: DtoCalendarViewMode) {
		this.calendar.changeView(viewMode);
	}

	public setDisplayedDate(date: number) {
		this.calendar.gotoDate(new Date(date));
	}

	public addEvent(theEvent: any) {
		this.eventSource.addEvent(this.convertToFullCalendarEvent(theEvent));
		this.refreshEventsDisplay();
	}

	public removeEvent(eventId: any) {
		this.eventSource.removeEvent(eventId);
		this.refreshEventsDisplay();
	}

	public setCalendarData(events: DtoCalendarEventClientRecord[]) {
		this.eventSource.removeAllEvents();
		this.eventSource.addEvents(0, Number.MAX_SAFE_INTEGER, events.map(e => this.convertToFullCalendarEvent(e)));
		this.refreshEventsDisplay();
	}

	public clearCalendar() {
		console.debug(`clearCalendar()`);
		this.eventSource.removeAllEvents();
		this.refreshEventsDisplay();
	}

	private refreshEventsDisplay() {
		this.eventSource.setQueriesDisabled(true);
		try {
			this.calendar.refetchEvents();
		} finally {
			this.eventSource.setQueriesDisabled(false);
		}
	}

	onResize(): void {
		// this.$fullCalendar.fullCalendar('render');
		console.log("hiehgt: ", this.getHeight())
		this.calendar.setOption('height', () => {
			console.log("aösjfdsöaljfdsaf");
			return this.getHeight()
		});
	}

	public doGetMainElement(): HTMLElement {
		return this.$main;
	}

	public destroy(): void {
		super.destroy();
		this.calendar && this.calendar.destroy();
	}

	public convertToFullCalendarEvent(event: DtoCalendarEventClientRecord): EventInput {
		return {
			id: "" + event.id,
			start: new Date(event.start),
			end: new Date(event.end),
			title: event.title,
			rendering: event.rendering,
			editable: event.allowDragOperations,
			startEditable: event.allowDragOperations,
			durationEditable: event.allowDragOperations,
			allDay: event.allDay,
			backgroundColor: event.backgroundColor,
			borderColor: event.borderColor,
			textColor: "#000",
			extendedProps: {
				timeGridTemplate: event.timeGridTemplate,
				dayGridTemplate: event.dayGridTemplate,
				monthGridTemplate: event.monthGridTemplate,
				data: event.values,
				icon: event.icon
			}
		};
	}

	setTimeZoneId(timeZoneId: string): void {
		this.calendar.setOption("timeZone", timeZoneId);
	}

}

export /* for testing ... */
class DtoCalendarFullCalendarEventSource implements ExtendedEventSourceInput {

	public readonly onViewChanged: TeamAppsEvent<DtoCalendar_ViewChangedEvent> = new TeamAppsEvent<DtoCalendar_ViewChangedEvent>();
	public readonly onDataNeeded: TeamAppsEvent<DtoCalendar_DataNeededEvent> = new TeamAppsEvent<DtoCalendar_DataNeededEvent>();

	private cachedEvents: any[] = [];
	private queriesDisabled: boolean;

	@bind
	public events(query: { start: Date; end: Date; timeZone: string; }, successCallback: (events: EventInput[]) => void, failureCallback: (error: EventSourceError) => void) {
		// let uncoveredIntervals = this.intervalManager.getUncoveredIntervals(new Interval(<number>query.start.valueOf(), <number>query.end.valueOf()));
		//
		// let queryStart: number;
		// let queryEnd: number;
		// if (uncoveredIntervals.length > 0) {
		// 	queryStart = Math.min.apply(Math, uncoveredIntervals.map(i => i.start));
		// 	queryEnd = Math.max.apply(Math, uncoveredIntervals.map(i => i.end));
		// } else {
		// 	queryStart = 0;
		// 	queryEnd = 0;
		// }
		// if (queryStart !== queryEnd) {
		// console.debug("DataNeededEvent: " + query.start.toUTCString() + " - " + query.start.toUTCString());
		// this.onDataNeeded.fire(EventFactory.createDtoCalendar_DataNeededEvent(this.componentId, queryStart, queryEnd));
		// }

		if (!this.queriesDisabled) {
			this.onDataNeeded.fire({
				requestIntervalStart: +query.start,
				requestIntervalEnd: +query.end
			});
		}

		let displayedInterval: Interval = [+query.start, +query.end];
		console.debug(`displayed: ${displayedInterval}`);
		let events = this.cachedEvents.filter(event => {
			let eventInterval: Interval = [+event.start, +event.end];
			let matches = intervalsOverlap(displayedInterval, eventInterval);
			if (matches) {
				console.debug(`matching: ${eventInterval}`);
			}
			return matches;
		});
		console.debug(`Returning ${events.length} events`);
		successCallback(events);
	}

	public addEvents(start: number, end: number, newEvents: EventInput[]) {
		newEvents.forEach(e => {
			if ((e.end as number) < start || (e.start as number) > end) {
				console.error(`Event ${e.id} (${e.start}-${e.end}) is outside of specified start/end range (${new Date(start).toUTCString()}-${new Date(end).toUTCString()})! This will very probably lead to inconsistent client-side behaviour!`)
			}
			if (e.end.valueOf() <= e.start.valueOf()) {
				console.warn(`Event ${e.id} has zero or negative duration! Changing to one millisecond!`);
				e.end = new Date(+(e.start) + 1);
			}
		});
		this.cachedEvents = newEvents;
	}

	public addEvent(newEvent: EventInput) {
		console.debug("Adding 1 event");
		this.addEvents(null, null, [newEvent]);
	}

	public removeEvent(eventId: number | string) {
		this.cachedEvents = this.cachedEvents.filter(e => e.id !== eventId);
	}

	public getEvent(eventId: number | string) {
		for (let i = 0; i < this.cachedEvents.length; i++) {
			if (this.cachedEvents[i].id === eventId) {
				return this.cachedEvents[i];
			}
		}
	}

	public removeAllEvents(): void {
		this.cachedEvents = [];
	}

	setQueriesDisabled(queriesDisabled: boolean) {
		this.queriesDisabled = queriesDisabled;
	}
}



