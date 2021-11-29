/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiCalendarEventClientRecordConfig} from "./UiCalendarEventClientRecordConfig";
import {UiCalendarViewMode} from "./UiCalendarViewMode";
import {UiWeekDay} from "./UiWeekDay";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiCalendarConfig extends UiComponentConfig {
	_type?: string;
	templates?: {[name: string]: UiTemplateConfig};
	initialData?: UiCalendarEventClientRecordConfig[];
	activeViewMode?: UiCalendarViewMode;
	displayedDate?: number;
	showHeader?: boolean;
	tableBorder?: boolean;
	showWeekNumbers?: boolean;
	businessHoursStart?: number;
	businessHoursEnd?: number;
	firstDayOfWeek?: UiWeekDay;
	workingDays?: UiWeekDay[];
	tableHeaderBackgroundColor?: string;
	defaultBackgroundColor?: string;
	defaultBorderColor?: string;
	navigateOnHeaderClicks?: boolean;
	minYearViewMonthTileWidth?: number;
	maxYearViewMonthTileWidth?: number;
	locale?: string;
	timeZoneId?: string
}

export interface UiCalendarCommandHandler extends UiComponentCommandHandler {
	setViewMode(viewMode: UiCalendarViewMode): any;
	setDisplayedDate(date: number): any;
	addEvent(theEvent: UiCalendarEventClientRecordConfig): any;
	removeEvent(eventId: number): any;
	setCalendarData(events: UiCalendarEventClientRecordConfig[]): any;
	clearCalendar(): any;
	registerTemplate(id: string, template: UiTemplateConfig): any;
	setTimeZoneId(timeZoneId: string): any;
}

export interface UiCalendarEventSource {
	onEventClicked: TeamAppsEvent<UiCalendar_EventClickedEvent>;
	onEventMoved: TeamAppsEvent<UiCalendar_EventMovedEvent>;
	onDayClicked: TeamAppsEvent<UiCalendar_DayClickedEvent>;
	onIntervalSelected: TeamAppsEvent<UiCalendar_IntervalSelectedEvent>;
	onDayHeaderClicked: TeamAppsEvent<UiCalendar_DayHeaderClickedEvent>;
	onWeekHeaderClicked: TeamAppsEvent<UiCalendar_WeekHeaderClickedEvent>;
	onMonthHeaderClicked: TeamAppsEvent<UiCalendar_MonthHeaderClickedEvent>;
	onViewChanged: TeamAppsEvent<UiCalendar_ViewChangedEvent>;
	onDataNeeded: TeamAppsEvent<UiCalendar_DataNeededEvent>;
}

export interface UiCalendar_EventClickedEvent extends UiEvent {
	eventId: number;
	isDoubleClick: boolean
}

export interface UiCalendar_EventMovedEvent extends UiEvent {
	eventId: number;
	newStart: number;
	newEnd: number
}

export interface UiCalendar_DayClickedEvent extends UiEvent {
	date: number;
	isDoubleClick: boolean
}

export interface UiCalendar_IntervalSelectedEvent extends UiEvent {
	start: number;
	end: number;
	allDay: boolean
}

export interface UiCalendar_DayHeaderClickedEvent extends UiEvent {
	date: number
}

export interface UiCalendar_WeekHeaderClickedEvent extends UiEvent {
	year: number;
	week: number;
	weekStartDate: number
}

export interface UiCalendar_MonthHeaderClickedEvent extends UiEvent {
	year: number;
	month: number;
	monthStartDate: number
}

export interface UiCalendar_ViewChangedEvent extends UiEvent {
	viewMode: UiCalendarViewMode;
	mainIntervalStart: number;
	mainIntervalEnd: number;
	displayedIntervalStart: number;
	displayedIntervalEnd: number
}

export interface UiCalendar_DataNeededEvent extends UiEvent {
	requestIntervalStart: number;
	requestIntervalEnd: number
}

