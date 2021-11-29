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
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiVerticalLayoutConfig extends UiComponentConfig {
	_type?: string;
	components?: unknown[];
	fixedChildHeight?: number
}

export interface UiVerticalLayoutCommandHandler extends UiComponentCommandHandler {
	addComponent(component: unknown): any;
	removeComponent(component: unknown): any;
}


