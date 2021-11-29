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


export interface UiDisplayFieldConfig extends UiFieldConfig {
	_type?: string;
	showBorder?: boolean;
	showHtml?: boolean;
	removeStyleTags?: boolean
}

export interface UiDisplayFieldCommandHandler extends UiFieldCommandHandler {
	setShowBorder(showBorder: boolean): any;
	setShowHtml(showHtml: boolean): any;
	setRemoveStyleTags(removeStyleTags: boolean): any;
}

export interface UiDisplayFieldEventSource extends UiFieldEventSource {
}


