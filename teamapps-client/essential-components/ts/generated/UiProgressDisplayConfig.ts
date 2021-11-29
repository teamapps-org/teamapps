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
import {UiProgressStatus} from "./UiProgressStatus";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiProgressDisplayConfig extends UiComponentConfig {
	_type?: string;
	icon?: string;
	taskName?: string;
	statusMessage?: string;
	progress?: number;
	status?: UiProgressStatus;
	cancelable?: boolean
}

export interface UiProgressDisplayCommandHandler extends UiComponentCommandHandler {
	update(config: UiProgressDisplayConfig): any;
}

export interface UiProgressDisplayEventSource {
	onClicked: TeamAppsEvent<UiProgressDisplay_ClickedEvent>;
	onCancelButtonClicked: TeamAppsEvent<UiProgressDisplay_CancelButtonClickedEvent>;
}

export interface UiProgressDisplay_ClickedEvent extends UiEvent {
}

export interface UiProgressDisplay_CancelButtonClickedEvent extends UiEvent {
}

