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


export interface UiDummyComponentConfig extends UiComponentConfig {
	_type?: string;
	text?: string
}

export interface UiDummyComponentCommandHandler extends UiComponentCommandHandler {
	setText(text: string): any;
}

export interface UiDummyComponentEventSource {
	onClicked: TeamAppsEvent<UiDummyComponent_ClickedEvent>;
}

export interface UiDummyComponent_ClickedEvent extends UiEvent {
	clickCount: number
}

