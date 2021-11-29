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
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiNavigationBarButtonConfig} from "./UiNavigationBarButtonConfig";
import {UiMultiProgressDisplayConfig} from "./UiMultiProgressDisplayConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiNavigationBarConfig extends UiComponentConfig {
	_type?: string;
	buttonTemplate: UiTemplateConfig;
	buttons?: UiNavigationBarButtonConfig[];
	backgroundColor?: string;
	borderColor?: string;
	fanOutComponents?: unknown[];
	multiProgressDisplay?: unknown
}

export interface UiNavigationBarCommandHandler extends UiComponentCommandHandler {
	setButtons(buttons: UiNavigationBarButtonConfig[]): any;
	setButtonVisible(buttonId: string, visible: boolean): any;
	setBackgroundColor(backgroundColor: string): any;
	setBorderColor(borderColor: string): any;
	addFanOutComponent(fanOutComponent: unknown): any;
	removeFanOutComponent(fanOutComponent: unknown): any;
	showFanOutComponent(fanOutComponent: unknown): any;
	hideFanOutComponent(): any;
	setMultiProgressDisplay(multiProgressDisplay: unknown): any;
}

export interface UiNavigationBarEventSource {
	onButtonClicked: TeamAppsEvent<UiNavigationBar_ButtonClickedEvent>;
	onFanoutClosedDueToClickOutsideFanout: TeamAppsEvent<UiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent>;
}

export interface UiNavigationBar_ButtonClickedEvent extends UiEvent {
	buttonId: string;
	visibleFanOutComponentId: string
}

export interface UiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent extends UiEvent {
}

