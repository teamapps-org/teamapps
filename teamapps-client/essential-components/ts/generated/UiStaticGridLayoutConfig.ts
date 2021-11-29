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
import {UiGridLayoutConfig} from "./UiGridLayoutConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiStaticGridLayoutConfig extends UiComponentConfig {
	_type?: string;
	descriptor: UiGridLayoutConfig
}

export interface UiStaticGridLayoutCommandHandler extends UiComponentCommandHandler {
	updateLayout(descriptor: UiGridLayoutConfig): any;
}


