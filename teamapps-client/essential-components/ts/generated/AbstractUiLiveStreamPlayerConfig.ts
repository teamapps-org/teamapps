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


export interface AbstractUiLiveStreamPlayerConfig extends UiComponentConfig {
	_type?: string;
}

export interface AbstractUiLiveStreamPlayerCommandHandler extends UiComponentCommandHandler {
	play(url: string): any;
	stop(): any;
	setVolume(volume: number): any;
}


