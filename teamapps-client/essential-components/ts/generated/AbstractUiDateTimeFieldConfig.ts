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
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface AbstractUiDateTimeFieldConfig extends UiFieldConfig {
	_type?: string;
	showDropDownButton?: boolean;
	favorPastDates?: boolean;
	locale?: string;
	dateFormat?: UiDateTimeFormatDescriptorConfig;
	timeFormat?: UiDateTimeFormatDescriptorConfig
}

export interface AbstractUiDateTimeFieldCommandHandler extends UiFieldCommandHandler {
	setShowDropDownButton(showDropDownButton: boolean): any;
	setFavorPastDates(favorPastDates: boolean): any;
	setLocaleAndFormats(locale: string, dateFormat: UiDateTimeFormatDescriptorConfig, timeFormat: UiDateTimeFormatDescriptorConfig): any;
}

export interface AbstractUiDateTimeFieldEventSource extends UiFieldEventSource {
}


