/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiDateTimeFormatDescriptorConfig} from "./UiDateTimeFormatDescriptorConfig";
import {UiLocalDateConfig} from "./UiLocalDateConfig";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";

export enum UiLocalDateField_DropDownMode {
	CALENDAR, CALENDAR_SUGGESTION_LIST, SUGGESTION_LIST
}

export interface UiLocalDateFieldConfig extends UiFieldConfig {
	_type?: string;
	showDropDownButton?: boolean;
	showClearButton?: boolean;
	favorPastDates?: boolean;
	locale?: string;
	dateFormat?: UiDateTimeFormatDescriptorConfig;
	defaultSuggestionDate?: UiLocalDateConfig;
	shuffledFormatSuggestionsEnabled?: boolean;
	dropDownMode?: UiLocalDateField_DropDownMode;
	placeholderText?: string
}

export interface UiLocalDateFieldCommandHandler extends UiFieldCommandHandler {
	update(config: UiLocalDateFieldConfig): any;
}

export interface UiLocalDateFieldEventSource extends UiFieldEventSource {
}


