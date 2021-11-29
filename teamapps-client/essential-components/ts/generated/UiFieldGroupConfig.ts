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
import {UiFieldConfig} from "./UiFieldConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiFieldGroupConfig extends UiComponentConfig {
	_type?: string;
	fields?: unknown[]
}

export interface UiFieldGroupCommandHandler extends UiComponentCommandHandler {
	setFields(fields: unknown[]): any;
}


