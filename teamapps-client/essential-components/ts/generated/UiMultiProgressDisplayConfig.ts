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
import {UiDefaultMultiProgressDisplayConfig} from "./UiDefaultMultiProgressDisplayConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiMultiProgressDisplayConfig extends UiComponentConfig {
	_type?: string;
	runningCount?: number;
	statusMessages?: string[]
}

export interface UiMultiProgressDisplayCommandHandler extends UiComponentCommandHandler {
	update(config: UiDefaultMultiProgressDisplayConfig): any;
}

export interface UiMultiProgressDisplayEventSource {
	onClicked: TeamAppsEvent<UiMultiProgressDisplay_ClickedEvent>;
}

export interface UiMultiProgressDisplay_ClickedEvent extends UiEvent {
}

