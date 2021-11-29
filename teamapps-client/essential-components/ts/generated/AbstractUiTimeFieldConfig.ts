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
import {UiTextInputHandlingFieldConfig} from "./UiTextInputHandlingFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiDateTimeFormatDescriptorConfig} from "./UiDateTimeFormatDescriptorConfig";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";
import {UiTextInputHandlingFieldEventSource} from "./UiTextInputHandlingFieldConfig";


export interface AbstractUiTimeFieldConfig extends UiFieldConfig, UiTextInputHandlingFieldConfig {
	_type?: string;
	showDropDownButton?: boolean;
	showClearButton?: boolean;
	locale?: string;
	timeFormat?: UiDateTimeFormatDescriptorConfig
}

export interface AbstractUiTimeFieldCommandHandler extends UiFieldCommandHandler {
	setShowDropDownButton(showDropDownButton: boolean): any;
	setShowClearButton(showClearButton: boolean): any;
	setLocaleAndTimeFormat(locale: string, timeFormat: UiDateTimeFormatDescriptorConfig): any;
}

export interface AbstractUiTimeFieldEventSource extends UiFieldEventSource, UiTextInputHandlingFieldEventSource {
}


