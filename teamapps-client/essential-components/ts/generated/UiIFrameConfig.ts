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


export interface UiIFrameConfig extends UiComponentConfig {
	_type?: string;
	url?: string
}

export interface UiIFrameCommandHandler extends UiComponentCommandHandler {
	setUrl(url: string): any;
}


