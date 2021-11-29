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


export interface UiComponentFieldConfig extends UiFieldConfig {
	_type?: string;
	component?: unknown;
	height?: number;
	bordered?: boolean
}

export interface UiComponentFieldCommandHandler extends UiFieldCommandHandler {
	setComponent(component: unknown): any;
	setHeight(height: number): any;
	setBordered(bordered: boolean): any;
}

export interface UiComponentFieldEventSource extends UiFieldEventSource {
}


