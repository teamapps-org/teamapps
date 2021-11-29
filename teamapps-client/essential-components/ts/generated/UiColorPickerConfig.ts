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
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiColorPickerConfig extends UiFieldConfig {
	_type?: string;
	defaultColor?: string;
	saveButtonCaption?: string;
	clearButtonCaption?: string
}

export interface UiColorPickerCommandHandler extends UiFieldCommandHandler {
}

export interface UiColorPickerEventSource extends UiFieldEventSource {
}


