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

import * as moment from "moment";
import * as momentTimeZone from "moment-timezone";
import * as FullCalendar from 'fullcalendar';
import * as log from "loglevel";
import {EventObjectInput, EventSourceExtendedInput, EventSourceFunction, OptionsInput} from "fullcalendar/src/types/input-types";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {
	UiCalendar_DataNeededEvent,
	UiCalendar_DayClickedEvent,
	UiCalendar_EventClickedEvent,
	UiCalendar_EventMovedEvent,
	UiCalendar_ViewChangedEvent,
	UiCalendarCommandHandler,
	UiCalendarConfig,
	UiCalendarEventSource
} from "../generated/UiCalendarConfig";
import View from "fullcalendar/View";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiCalendarViewMode} from "../generated/UiCalendarViewMode";
import {UiCalendarEventRenderingStyle} from "../generated/UiCalendarEventRenderingStyle";
import {EventFactory} from "../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {Interval, IntervalManager} from "./util/IntervalManager";
import {createUiColorCssString} from "./util/CssFormatUtil";
import Default from "fullcalendar/Calendar";
import {MultiMonthView} from "./util/FullCalendarMultiMonthView";
import * as jstz from "jstz";
import {parseHtml, Renderer} from "./Common";
import {UiCalendarEventClientRecordConfig} from "../generated/UiCalendarEventClientRecordConfig";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";

(window as any).FullCalendar = FullCalendar; // needed for dynamically reloading locales
($.fullCalendar as any).views.multiMonth = MultiMonthView;
($.fullCalendar as any).views.year = MultiMonthView;
($.fullCalendar as any).views.year = {
	type: 'multiMonth',
	duration: {months: 12}
};

import Moment = moment.Moment;

const VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING: { [index: number]: string } = {
	[UiCalendarViewMode.YEAR]: "year",
	[UiCalendarViewMode.MONTH]: "month",
	[UiCalendarViewMode.WEEK]: "agendaWeek",
	[UiCalendarViewMode.DAY]: "agendaDay"
};
const RENDERING_STYLE_2_FULL_CALENDAR_CONFIG_STRING: { [index: number]: string } = {
	[UiCalendarEventRenderingStyle.DEFAULT]: undefined,
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

	private $main: HTMLElement;
	private $fullCalendar: JQuery;
	private eventSource: UiCalendarFullCalendarEventSource;
	private templateRenderers: { [name: string]: Renderer };

	constructor(config: UiCalendarConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml('<div class="UiCalendar" id="' + config.id + '">');
		let $fullCalendarElement = parseHtml('<div></div>');
		this.$main.appendChild($fullCalendarElement);
		this.$fullCalendar = $($fullCalendarElement);
		this.eventSource = new UiCalendarFullCalendarEventSource(context, config.id);
		this.eventSource.onViewChanged.addListener(eventObject => this.onViewChanged.fire(eventObject));
		this.eventSource.onDataNeeded.addListener(eventObject => this.onDataNeeded.fire(eventObject));
		config.initialData && this.eventSource.addEvents(0, Number.MAX_SAFE_INTEGER, config.initialData.map(e => this.convertToFullCalendarEvent(e)));
		this.templateRenderers = context.templateRegistry.createTemplateRenderers(config.templates);

		let viewModeNames = ["year", "month", "agendaWeek", "agendaDay"];
		let options: OptionsInput & { lang?: string } = {
			lang: context.config.isoLanguage,
			header: config.showHeader ? {
				left: 'prevYear,prev,next,nextYear today newEvent',
				center: 'title',
				right: viewModeNames.join(",")
			} : false,
			defaultView: VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING[config.activeViewMode],
			defaultDate: config.displayedDate,
			weekNumbers: config.showWeekNumbers,
			businessHours: {
				start: config.businessHoursStart + ':00',
				end: config.businessHoursEnd + ':00',
				dow: config.workingDays
			},
			firstDay: config.firstDayOfWeek != null ? config.firstDayOfWeek : context.config.firstDayOfWeek, // 1 = monday
			fixedWeekCount: true,
			eventBackgroundColor: createUiColorCssString(config.defaultBackgroundColor),
			eventBorderColor: createUiColorCssString(config.defaultBorderColor),
			eventTextColor: "#000",
			handleWindowResize: false, // we handle this ourselves!
			eventSources: [this.eventSource],
			lazyFetching: false, // no intelligent fetching from fullcalendar. We handle all that!
			selectable: true,
			unselectAuto: false,
			timezone: context.config.timeZoneId || jstz.determine().name(),
			eventRender: (event: EventObject, $event: JQuery) => {
				if (event.templateId != null && event.rendering == null || event.rendering == "default") {
					const $contentWrapper = $event.find('.fc-content');
					$contentWrapper[0].innerHTML = '';
					$contentWrapper.append(this.renderEventObject(event));
					$($event).on('click dblclick', (e) => {
						this.onEventClicked.fire(EventFactory.createUiCalendar_EventClickedEvent(config.id, event.id as number, e.type === 'dblclick'));
					});
				}
			},
			dayClick: (() => {
				let lastClickTimeStamp = 0;
				let lastClickClickedDate: Moment;
				return (date: moment.Moment, jsEvent: MouseEvent, view: View, resourceObj?: any) => {
					let isDoubleClick = lastClickClickedDate != null && lastClickClickedDate.valueOf() == date.valueOf() && jsEvent.timeStamp - lastClickTimeStamp < 600;
					if (isDoubleClick) {
						lastClickTimeStamp = 0;
						lastClickClickedDate = null;
						this.onDayClicked.fire(EventFactory.createUiCalendar_DayClickedEvent(config.id, date.valueOf(), true));
					} else {
						lastClickTimeStamp = jsEvent.timeStamp;
						lastClickClickedDate = date;
						this.onDayClicked.fire(EventFactory.createUiCalendar_DayClickedEvent(config.id, date.valueOf(), false));
					}
				};
			})(),
			eventResize: (event: EventObject, delta: moment.Duration, revertFunc: Function, jsEvent: Event, ui: any, view: View) => {
				const masterEvent = this.eventSource.getEvent(event.id);
				masterEvent.start = event.start;
				masterEvent.end = event.end;
				this.onEventMoved.fire(EventFactory.createUiCalendar_EventMovedEvent(config.id, event.id as number, moment(event.start).valueOf(), moment(event.end).valueOf()));
			},
			eventDrop: (event: EventObject, delta, revertFunc) => {
				const masterEvent = this.eventSource.getEvent(event.id);
				masterEvent.start = event.start;
				masterEvent.end = event.end;
				this.onEventMoved.fire(EventFactory.createUiCalendar_EventMovedEvent(config.id, event.id as number, moment(event.start).valueOf(), moment(event.end).valueOf()));
			},
			height: 600,
			slotEventOverlap: false,
			slotLabelFormat: "hh:mm",
			viewRender: (view, element) => {
				this.$main.classList.toggle("table-border", config.tableBorder);

				this.$fullCalendar.find(".fc-bg td.fc-week-number.fc-widget-content").addClass('teamapps-blurredBackgroundImage');
				this.$fullCalendar.find(".fc-head td.fc-widget-header").addClass('teamapps-blurredBackgroundImage');

				if (view.type.toLowerCase().indexOf('agenda') !== -1 && moment().isAfter(view.intervalStart) && moment().isBefore(view.intervalEnd)) {
					element.find(".fc-day-header.fc-" + ['sun', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat'][moment().day()]).addClass("fc-today");
				}
			}
		};
		this.$fullCalendar.fullCalendar(options);

		this.$main.append(parseHtml(`<style>
                #${config.id} .fc-head td.fc-widget-header>.fc-row.fc-widget-header {
                    background-color: ${createUiColorCssString(config.tableHeaderBackgroundColor)};
                }
                #${config.id} .fc-bgevent-skeleton td.fc-week-number {
                    background-color: ${createUiColorCssString(config.tableHeaderBackgroundColor)};
                }
            </style>`));
	}

	private renderEventObject(record: EventObject): string {
		const templateId = record.templateId;
		if (templateId != null && this.templateRenderers[templateId] != null) {
			const renderer = this.templateRenderers[templateId];
			return renderer.render(record.data);
		} else {
			return `<div class="no-template"></div>`;
		}
	}

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.templateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	protected onAttachedToDom() {
		this.reLayout();
	}

	private convertToFullCalendarEvent(event: UiCalendarEventClientRecordConfig): EventObject {

		let backgroundColor = event.backgroundColor;
		let backgroundColorCssString = typeof backgroundColor === 'string' ? backgroundColor
			: backgroundColor != null ? createUiColorCssString(backgroundColor) : null;

		let borderColor = event.borderColor;
		let borderColorCssString = typeof borderColor === 'string' ? borderColor
			: borderColor != null ? createUiColorCssString(borderColor) : null;

		return {
			id: event.id,
			start: moment(event.start),
			end: moment(event.end),
			title: event.asString,
			rendering: RENDERING_STYLE_2_FULL_CALENDAR_CONFIG_STRING[event.rendering],
			editable: event.allowDragOperations,
			templateId: event.templateId,
			allDay: event.allDay,
			backgroundColor: backgroundColorCssString,
			borderColor: borderColorCssString,
			data: event.values
		};
	}

	public setViewMode(viewMode: UiCalendarViewMode) {
		this.$fullCalendar.fullCalendar('changeView' as any /* not in declarations...*/, VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING[viewMode]);
	}

	public setDisplayedDate(date: number) {
		this.logger.debug("setDisplayedDate: " + moment(date).toString());
		this.$fullCalendar.fullCalendar('gotoDate', date);
	}

	public addEvent(theEvent: any) {
		this.eventSource.addEvent(this.convertToFullCalendarEvent(theEvent));
		this.$fullCalendar.fullCalendar('refetchEvents');
	}

	public removeEvent(eventId: any) {
		this.eventSource.removeEvent(eventId);
		this.$fullCalendar.fullCalendar('refetchEvents');
	}

	public setCalendarData(events: UiCalendarEventClientRecordConfig[]) {
		this.eventSource.removeAllEvents();
		this.eventSource.addEvents(0, Number.MAX_SAFE_INTEGER, events.map(e => this.convertToFullCalendarEvent(e)));
		this.$fullCalendar.fullCalendar('refetchEvents');
	}

	public clearCalendar() {
		this.logger.debug(`clearCalendar()`);
		this.eventSource.removeAllEvents();
		this.$fullCalendar.fullCalendar('refetchEvents');
	}

	onResize(): void {
		this.$fullCalendar.fullCalendar('render');
		this.$fullCalendar.fullCalendar('option', 'height', this.getHeight());
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}

	public destroy(): void {
		// nothing to do
	}
}

interface EventObject extends EventObjectInput {
	templateId: string;
	data: any;
}

export /* for testing ... */
class UiCalendarFullCalendarEventSource implements EventSourceExtendedInput {

	public readonly onViewChanged: TeamAppsEvent<UiCalendar_ViewChangedEvent> = new TeamAppsEvent<UiCalendar_ViewChangedEvent>(this);
	public readonly onDataNeeded: TeamAppsEvent<UiCalendar_DataNeededEvent> = new TeamAppsEvent<UiCalendar_DataNeededEvent>(this);

	private logger = log.getLogger((<any>UiCalendarFullCalendarEventSource.prototype).name || this.constructor.toString().match(/\w+/g)[1]);
	private intervalManager: IntervalManager = new IntervalManager();
	private cachedEvents: any[] = [];
	public events: EventSourceFunction;

	constructor(private teamappsUiContext: TeamAppsUiContext, private componentId: string) {
		this.events = this.createEventsFunction();
	}

	private createEventsFunction() {
		let me = this;
		// FullCalendar messes around with "this" because it uses the method as a plain function...
		return function (this: Default, start: Moment, end: Moment, timezone: boolean | string, callback: ((events: EventObject[]) => void)): void {

			start = me.removeTimeZoneOffsetFromAmbiguouslyTimedMoment(start, timezone);
			end = me.removeTimeZoneOffsetFromAmbiguouslyTimedMoment(end, timezone);

			let newInterval: Interval = new Interval(<number>start.valueOf(), <number>end.valueOf());

			if (!me.teamappsUiContext.executingCommand) {
				setTimeout(() => {
					me.onViewChanged.fire(EventFactory.createUiCalendar_ViewChangedEvent(
						me.componentId,
						parseInt(Object.keys(VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING).filter((enumValue: any) => VIEW_MODE_2_FULL_CALENDAR_CONFIG_STRING[enumValue] === view.type)[0]),
						view.intervalStart.valueOf(),
						view.intervalEnd.valueOf(),
						view.start.valueOf(),
						view.end.valueOf()
					));
				});
			}

			let uncoveredIntervals = me.intervalManager.getUncoveredIntervals(newInterval);

			let queryStart: number;
			let queryEnd: number;
			if (uncoveredIntervals.length > 0) {
				queryStart = Math.min.apply(Math, uncoveredIntervals.map(i => i.start));
				queryEnd = Math.max.apply(Math, uncoveredIntervals.map(i => i.end));
			} else {
				queryStart = 0;
				queryEnd = 0;
			}
			let view = this.getView();

			if (queryStart !== queryEnd) {
				me.logger.debug("DataNeededEvent: " + moment(queryStart).toString() + " - " + moment(queryEnd).toString());
				setTimeout(() => {
					me.onDataNeeded.fire(EventFactory.createUiCalendar_DataNeededEvent(me.componentId, queryStart, queryEnd));
				});
			}

			let displayedInterval = new Interval(+start, +end);
			me.logger.debug(`displayed: ${displayedInterval}`);
			let events = me.cachedEvents.filter(event => {
				let eventInterval = new Interval(+event.start, +event.end);
				let matches = IntervalManager.intervalsOverlap(displayedInterval, eventInterval);
				if (matches) {
					me.logger.debug(`matching: ${eventInterval}`);
				}
				return matches;
			});
			me.logger.debug(`Returning ${events.length} events`);
			callback(events);
		};
	}

	// see https://fullcalendar.io/docs/moment
	private removeTimeZoneOffsetFromAmbiguouslyTimedMoment(start: Moment, timezone: boolean | string): Moment {
		let x = momentTimeZone(start.valueOf()).tz(timezone.toString());
		return moment(x.add(-x.utcOffset(), "minutes").valueOf());
	}

	public addEvents(start: number, end: number, newEvents: EventObject[]) {
		newEvents.forEach(e => {
			if (e.end < start || e.start > end) {
				this.logger.error(`Event ${e.id} (${moment(e.start).toString()}-${moment(e.end).toString()}) is outside of specified start/end range (${moment(start).toString()}-${moment(end).toString()})! This will very probably lead to inconsistent client-side behaviour!`)
			}
			if (e.end.valueOf() <= e.start.valueOf()) {
				this.logger.warn(`Event ${e.id} has zero or negative duration! Changing to one millisecond!`);
				e.end = moment((e.start as Moment).valueOf() + 1);
			}
		});
		this.cachedEvents = this.cachedEvents.filter(e => {
			return (e.end < start || e.start >= end);
		}); // remove all obsolete events
		this.cachedEvents = this.cachedEvents.concat(newEvents);
		this.cachedEvents.sort((a, b) => a.start < b.start ? -1 : a.start > b.start ? 1 : 0);
		this.intervalManager.addInterval(new Interval(start, end));
	}

	public addEvent(newEvent: EventObject) {
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
}

TeamAppsUiComponentRegistry.registerComponentClass("UiCalendar", UiCalendar);
