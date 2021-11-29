/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {AbstractUiDateTimeFieldConfig} from "./AbstractUiDateTimeFieldConfig";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiDateTimeFormatDescriptorConfig} from "./UiDateTimeFormatDescriptorConfig";
import {AbstractUiDateTimeFieldCommandHandler} from "./AbstractUiDateTimeFieldConfig";
import {AbstractUiDateTimeFieldEventSource} from "./AbstractUiDateTimeFieldConfig";


export interface UiInstantDateTimeFieldConfig extends AbstractUiDateTimeFieldConfig {
	_type?: string;
	timeZoneId?: string
}

export interface UiInstantDateTimeFieldCommandHandler extends AbstractUiDateTimeFieldCommandHandler {
	setTimeZoneId(timeZoneId: string): any;
}

export interface UiInstantDateTimeFieldEventSource extends AbstractUiDateTimeFieldEventSource {
}

