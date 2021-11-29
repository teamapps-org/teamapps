/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiIdentifiableClientRecordConfig} from "./UiIdentifiableClientRecordConfig";
import {UiClientRecordConfig} from "./UiClientRecordConfig";
import {UiCalendarEventRenderingStyle} from "./UiCalendarEventRenderingStyle";


export interface UiCalendarEventClientRecordConfig extends UiIdentifiableClientRecordConfig {
	_type?: string;
	timeGridTemplateId?: string;
	dayGridTemplateId?: string;
	monthGridTemplateId?: string;
	start?: number;
	end?: number;
	allDay?: boolean;
	allowDragOperations?: boolean;
	icon?: string;
	title?: string;
	backgroundColor?: string;
	borderColor?: string;
	rendering?: UiCalendarEventRenderingStyle
}


