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
import {UiLinkTarget} from "./UiLinkTarget";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiLinkButtonConfig extends UiComponentConfig {
	_type?: string;
	text?: string;
	url?: string;
	target?: UiLinkTarget;
	onClickJavaScript?: string
}

export interface UiLinkButtonCommandHandler extends UiComponentCommandHandler {
	update(config: UiLinkButtonConfig): any;
}

export interface UiLinkButtonEventSource {
	onClicked: TeamAppsEvent<UiLinkButton_ClickedEvent>;
}

export interface UiLinkButton_ClickedEvent extends UiEvent {
}

