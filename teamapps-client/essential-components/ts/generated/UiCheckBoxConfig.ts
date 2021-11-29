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


export interface UiCheckBoxConfig extends UiFieldConfig {
	_type?: string;
	caption?: string;
	backgroundColor?: string;
	checkColor?: string;
	borderColor?: string;
	htmlEnabled?: boolean
}

export interface UiCheckBoxCommandHandler extends UiFieldCommandHandler {
	setCaption(caption: string): any;
	setBackgroundColor(backgroundColor: string): any;
	setCheckColor(checkColor: string): any;
	setBorderColor(borderColor: string): any;
}

export interface UiCheckBoxEventSource extends UiFieldEventSource {
}


