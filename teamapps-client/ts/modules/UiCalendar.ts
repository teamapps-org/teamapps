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
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {
	UiCalendar_DataNeededEvent,
	UiCalendar_DayClickedEvent,
	UiCalendar_DayHeaderClickedEvent,
	UiCalendar_EventClickedEvent,
	UiCalendar_EventMovedEvent,
	UiCalendar_MonthHeaderClickedEvent,
	UiCalendar_ViewChangedEvent,
	UiCalendar_WeekHeaderClickedEvent,
	UiCalendarCommandHandler,
	UiCalendarConfig,
	UiCalendarEventSource
} from "../generated/UiCalendarConfig";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiCalendarViewMode} from "../generated/UiCalendarViewMode";
import {UiCalendarEventRenderingStyle} from "../generated/UiCalendarEventRenderingStyle";
import {EventFactory} from "../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {Interval, IntervalManager} from "./util/IntervalManager";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {parseHtml, prependChild, Renderer} from "./Common";
import {UiCalendarEventClientRecordConfig} from "../generated/UiCalendarEventClientRecordConfig";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";

import {addDays, Calendar} from '@fullcalendar/core';
import dayGridPlugin, {DayGridView} from '@fullcalendar/daygrid';
import timeGridPlugin, {TimeGridView} from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import momentTimeZone from '@fullcalendar/moment-timezone';
import {EventInput, EventRenderingChoice} from "@fullcalendar/core/structs/event";
import {EventSourceError, ExtendedEventSourceInput} from "@fullcalendar/core/structs/event-source";
import {bind} from "./util/Bind";
import {View} from "@fullcalendar/core/View";
import EventApi from "@fullcalendar/core/api/EventApi";
import {Duration} from "@fullcalendar/core/datelib/duration";
import {monthGridViewPlugin} from "./util/FullCalendarMonthGrid";
import {OptionsInputBase} from "@fullcalendar/core/types/input-types";

const VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING: { [index: number]: string } = {
	[UiCalendarViewMode.YEAR]: "year",
	[UiCalendarViewMode.MONTH]: "dayGridMonth",
	[UiCalendarViewMode.WEEK]: "timeGridWeek",
	[UiCalendarViewMode.DAY]: "timeGridDay"
};
const RENDERING_STYLE_2_FULL_CALENDAR_CONFIG_STRING: { [index: number]: EventRenderingChoice } = {
	[UiCalendarEventRenderingStyle.DEFAULT]: '',
	// [UiCalendarEventRenderingStyle.HIGHLIGHTED]: 'highlighted',
	[UiCalendarEventRenderingStyle.BACKGROUND]: 'background',
	[UiCalendarEventRenderingStyle.INVERSE_BACKGROUND]: 'inverse-background',
};

export class UiCalendar extends UiComponent<UiCalendarConfig> implements UiCalendarCommandHandler, UiCalendarEventSource {

	public readonly onEventClicked: TeamAppsEvent<UiCalendar_EventClickedEvent> = new TeamAppsEvent<UiCalendar_EventClickedEvent>(this);
	public readonly onEventMoved: TeamAppsEvent<UiCalendar_EventMovedEvent> = new TeamAppsEvent<UiCalendar_EventMovedEvent>(this);
	public readonly onDayClicked: TeamAppsEvent<UiCalendar_DayClickedEvent> = new TeamAppsEvent<UiCalendar_DayClickedEvent>(this);
	public readonly onViewChanged: TeamAppsEvent<UiCalendar_ViewChangedEvent> = new TeamAppsEvent<UiCalendar_ViewChangedEvent>(this);
	public readonly onDataNeeded: TeamAppsEvent<UiCalendar_DataNeededEvent> = new TeamAppsEvent<UiCalendar_DataNeededEvent>(this);
	public readonly onDayHeaderClicked: TeamAppsEvent<UiCalendar_DayHeaderClickedEvent> = new TeamAppsEvent<UiCalendar_DayHeaderClickedEvent>(this);
	public readonly onWeekHeaderClicked: TeamAppsEvent<UiCalendar_WeekHeaderClickedEvent> = new TeamAppsEvent<UiCalendar_WeekHeaderClickedEvent>(this);
	public readonly onMonthHeaderClicked: TeamAppsEvent<UiCalendar_MonthHeaderClickedEvent> = new TeamAppsEvent<UiCalendar_MonthHeaderClickedEvent>(this);

	private $main: HTMLElement;
	private eventSource: UiCalendarFullCalendarEventSource;
	private templateRenderers: { [name: string]: Renderer };
	private calendar: Calendar;

	constructor(config: UiCalendarConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiCalendar" id="${config.id}">
	<div class="calendar"></div>
</div>`);
		let $fullCalendarElement: HTMLElement = this.$main.querySelector(':scope > .calendar');

		this.templateRenderers = context.templateRegistry.createTemplateRenderers(config.templates);

		this.eventSource = new UiCalendarFullCalendarEventSource(context, config.id, () => this.calendar);
		this.calendar = new Calendar($fullCalendarElement, {
			plugins: [momentTimeZone, interactionPlugin, dayGridPlugin, timeGridPlugin, monthGridViewPlugin],
			header: false,
			defaultView: VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING[config.activeViewMode],
			defaultDate: config.displayedDate,
			weekNumbers: config.showWeekNumbers,
			businessHours: {
				start: config.businessHoursStart + ':00',
				end: config.businessHoursEnd + ':00',
				daysOfWeek: config.workingDays
			},
			firstDay: config.firstDayOfWeek != null ? config.firstDayOfWeek : context.config.firstDayOfWeek, // 1 = monday
			fixedWeekCount: true,
			eventBackgroundColor: createUiColorCssString(config.defaultBackgroundColor),
			eventBorderColor: createUiColorCssString(config.defaultBorderColor),
			eventTextColor: "#000",
			handleWindowResize: false, // we handle this ourselves!
			lazyFetching: false, // no intelligent fetching from fullcalendar. We handle all that!
			selectable: true,
			unselectAuto: false,
			timeZone: config.timeZoneId || context.config.timeZoneId || 'local',
			height: 600,
			slotEventOverlap: false,
			locale: context.config.isoLanguage,
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
				this.onViewChanged.fire(EventFactory.createUiCalendar_ViewChangedEvent(
					this.getId(),
					parseInt(Object.keys(VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING).filter((enumValue: any) => VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING[enumValue] === arg.view.type)[0]),
					+arg.view.currentStart,
					+arg.view.currentEnd,
					+arg.view.activeStart,
					+arg.view.activeEnd
				));

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
				let templateId = arg.view instanceof TimeGridView ? arg.event.extendedProps.timeGridTemplateId
					: arg.view instanceof DayGridView ? arg.event.extendedProps.dayGridTemplateId
						: arg.event.extendedProps.monthGridTemplateId;

				if (templateId != null && !arg.event.rendering) {
					// const $contentWrapper = arg.el.querySelector(':scope .fc-content');
					if (this.templateRenderers[templateId] != null) {
						const renderer = this.templateRenderers[templateId];
						// arg.el.appendChild(parseHtml());
						arg.el.innerHTML = renderer.render(arg.event.extendedProps.data);
					}
				} else {
					let $fcContent = arg.el.querySelector(':scope .fc-content');
					prependChild($fcContent, parseHtml(`<div class="fc-icon img img-16" style="background-image:url(${this._context.getIconPath(arg.event.extendedProps.icon, 16)})">`));
				}
				if (arg.event.allDay) {
					arg.el.classList.add("all-day");
				}

				arg.el.addEventListener('click', (e) => {
					this.onEventClicked.fire(EventFactory.createUiCalendar_EventClickedEvent(config.id, parseInt(arg.event.id), false));
				});
				arg.el.addEventListener('dblclick', (e) => {
					this.onEventClicked.fire(EventFactory.createUiCalendar_EventClickedEvent(config.id, parseInt(arg.event.id), true));
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
					console.log(arg.jsEvent.type);
					let isDoubleClick = lastClickClickedDate != null && lastClickClickedDate.valueOf() == arg.date.valueOf() && arg.jsEvent.timeStamp - lastClickTimeStamp < 600;
					if (isDoubleClick) {
						lastClickTimeStamp = 0;
						lastClickClickedDate = null;
						this.onDayClicked.fire(EventFactory.createUiCalendar_DayClickedEvent(config.id, arg.date.valueOf(), true));
					} else {
						lastClickTimeStamp = arg.jsEvent.timeStamp;
						lastClickClickedDate = arg.date;
						this.onDayClicked.fire(EventFactory.createUiCalendar_DayClickedEvent(config.id, arg.date.valueOf(), false));
					}
				};
			})(),
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
				this.onEventMoved.fire(EventFactory.createUiCalendar_EventMovedEvent(config.id, parseInt(arg.event.id), arg.event.start.valueOf(), arg.event.end.valueOf()));
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
				this.onEventMoved.fire(EventFactory.createUiCalendar_EventMovedEvent(config.id, parseInt(arg.event.id), arg.event.start.valueOf(), arg.event.end.valueOf()));
			},
			navLinks: true,
			navLinkDayClick: (date, jsEvent) => {
				this.onDayHeaderClicked.fire(EventFactory.createUiCalendar_DayHeaderClickedEvent(this.getId(), date.valueOf()));
				if (this._config.navigateOnHeaderClicks) {
					this.calendar.changeView("timeGridDay", date);
				}
			},
			navLinkWeekClick: (weekStart, jsEvent) => {
				this.onWeekHeaderClicked.fire(EventFactory.createUiCalendar_WeekHeaderClickedEvent(this.getId(), addDays(weekStart, 6).getFullYear(), this.calendar.dateEnv.computeWeekNumber(weekStart), weekStart.valueOf()));
				if (this._config.navigateOnHeaderClicks) {
					this.calendar.changeView("timeGridWeek", weekStart);
				}
			},
			navLinkMonthClick: (monthStart: Date, jsEvent: Event) => {
				this.onMonthHeaderClicked.fire(EventFactory.createUiCalendar_MonthHeaderClickedEvent(this.getId(), monthStart.getFullYear(), monthStart.getMonth(), monthStart.valueOf()));
				if (this._config.navigateOnHeaderClicks) {
					this.calendar.changeView("dayGridMonth", monthStart);
				}
			}
		} as OptionsInputBase);

		if (config.initialData) {
			this.eventSource.addEvents(0, Number.MAX_SAFE_INTEGER, config.initialData.map(e => this.convertToFullCalendarEvent(e)));
			this.calendar.refetchEvents();
		}
		this.eventSource.onDataNeeded.addListener((eventObject: UiCalendar_DataNeededEvent) => {
			setTimeout(() => this.onDataNeeded.fire(eventObject)); // setTimeout needed because we might still be inside the constructor and no one will be registered to this...
		});

		this.calendar.render();

		this.$main.append(parseHtml(`<style>
                #${config.id} .fc-head td.fc-widget-header>.fc-row.fc-widget-header {
                    background-color: ${createUiColorCssString(config.tableHeaderBackgroundColor)};
                }
                #${config.id} .fc-bgevent-skeleton td.fc-week-number {
                    background-color: ${createUiColorCssString(config.tableHeaderBackgroundColor)};
                }
            </style>`));
	}

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.templateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	protected onAttachedToDom() {
		this.reLayout();
	}

	public setViewMode(viewMode: UiCalendarViewMode) {
		this.calendar.changeView(VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING[viewMode]);
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

	public setCalendarData(events: UiCalendarEventClientRecordConfig[]) {
		this.eventSource.removeAllEvents();
		this.eventSource.addEvents(0, Number.MAX_SAFE_INTEGER, events.map(e => this.convertToFullCalendarEvent(e)));
		this.refreshEventsDisplay();
	}

	public clearCalendar() {
		this.logger.debug(`clearCalendar()`);
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
		this.calendar.setOption('height', this.getHeight());
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}

	public destroy(): void {
		this.calendar && this.calendar.destroy();
	}

	public convertToFullCalendarEvent(event: UiCalendarEventClientRecordConfig): EventInput {
		let backgroundColor = event.backgroundColor;
		let backgroundColorCssString = typeof backgroundColor === 'string' ? backgroundColor
			: backgroundColor != null ? createUiColorCssString(backgroundColor) : null;

		let borderColor = event.borderColor;
		let borderColorCssString = typeof borderColor === 'string' ? borderColor
			: borderColor != null ? createUiColorCssString(borderColor) : null;

		return {
			id: "" + event.id,
			start: new Date(event.start),
			end: new Date(event.end),
			title: event.title,
			rendering: RENDERING_STYLE_2_FULL_CALENDAR_CONFIG_STRING[event.rendering],
			editable: event.allowDragOperations,
			startEditable: event.allowDragOperations,
			durationEditable: event.allowDragOperations,
			allDay: event.allDay,
			backgroundColor: backgroundColorCssString,
			borderColor: borderColorCssString,
			textColor: "#000",
			extendedProps: {
				timeGridTemplateId: event.timeGridTemplateId,
				dayGridTemplateId: event.dayGridTemplateId,
				monthGridTemplateId: event.monthGridTemplateId,
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
class UiCalendarFullCalendarEventSource implements ExtendedEventSourceInput {

	public readonly onViewChanged: TeamAppsEvent<UiCalendar_ViewChangedEvent> = new TeamAppsEvent<UiCalendar_ViewChangedEvent>(this);
	public readonly onDataNeeded: TeamAppsEvent<UiCalendar_DataNeededEvent> = new TeamAppsEvent<UiCalendar_DataNeededEvent>(this);

	private logger = log.getLogger((<any>UiCalendarFullCalendarEventSource.prototype).name || this.constructor.toString().match(/\w+/g)[1]);
	private intervalManager: IntervalManager = new IntervalManager();
	private cachedEvents: any[] = [];
	private queriesDisabled: boolean;

	constructor(private teamappsUiContext: TeamAppsUiContext, private componentId: string, private fullCalendarAccessor: () => Calendar) {
	}

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
		// this.logger.debug("DataNeededEvent: " + query.start.toUTCString() + " - " + query.start.toUTCString());
		// this.onDataNeeded.fire(EventFactory.createUiCalendar_DataNeededEvent(this.componentId, queryStart, queryEnd));
		// }

		if (!this.queriesDisabled) {
			this.onDataNeeded.fire(EventFactory.createUiCalendar_DataNeededEvent(this.componentId, +query.start, +query.end));
		}

		let displayedInterval = new Interval(+query.start, +query.end);
		this.logger.debug(`displayed: ${displayedInterval}`);
		let events = this.cachedEvents.filter(event => {
			let eventInterval = new Interval(+event.start, +event.end);
			let matches = IntervalManager.intervalsOverlap(displayedInterval, eventInterval);
			if (matches) {
				this.logger.debug(`matching: ${eventInterval}`);
			}
			return matches;
		});
		this.logger.debug(`Returning ${events.length} events`);
		successCallback(events);
	}

	public addEvents(start: number, end: number, newEvents: EventInput[]) {
		newEvents.forEach(e => {
			if (e.end < start || e.start > end) {
				this.logger.error(`Event ${e.id} (${e.start}-${e.end}) is outside of specified start/end range (${new Date(start).toUTCString()}-${new Date(end).toUTCString()})! This will very probably lead to inconsistent client-side behaviour!`)
			}
			if (e.end.valueOf() <= e.start.valueOf()) {
				this.logger.warn(`Event ${e.id} has zero or negative duration! Changing to one millisecond!`);
				e.end = new Date(+(e.start) + 1);
			}
		});
		this.cachedEvents = this.cachedEvents.filter(e => {
			return (e.end < start || e.start >= end);
		}); // remove all obsolete events
		this.cachedEvents = this.cachedEvents.concat(newEvents);
		this.cachedEvents.sort((a, b) => a.start < b.start ? -1 : a.start > b.start ? 1 : 0);
		this.intervalManager.addInterval(new Interval(start, end));
	}

	public addEvent(newEvent: EventInput) {
		this.logger.debug("Adding 1 event");
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
		this.intervalManager = new IntervalManager();
	}

	setQueriesDisabled(queriesDisabled: boolean) {
		this.queriesDisabled = queriesDisabled;
	}
}


TeamAppsUiComponentRegistry.registerComponentClass("UiCalendar", UiCalendar);
