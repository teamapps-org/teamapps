/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "@teamapps/teamapps-client-core";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiToolbarButtonGroupConfig} from "./UiToolbarButtonGroupConfig";
import {UiToolbarButtonConfig} from "./UiToolbarButtonConfig";
import {UiDropDownButtonClickInfoConfig} from "./UiDropDownButtonClickInfoConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface AbstractUiToolContainerConfig extends UiComponentConfig {
	_type?: string;
	leftButtonGroups: UiToolbarButtonGroupConfig[];
	rightButtonGroups: UiToolbarButtonGroupConfig[];
	backgroundColor?: string
}

export interface AbstractUiToolContainerCommandHandler extends UiComponentCommandHandler {
	setButtonVisible(groupId: string, buttonId: string, visible: boolean): any;
	setButtonColors(groupId: string, buttonId: string, backgroundColor: string, hoverBackgroundColor: string): any;
	setButtonGroupVisible(groupId: string, visible: boolean): any;
	addButton(groupId: string, button: UiToolbarButtonConfig, neighborButtonId: string, beforeNeighbor: boolean): any;
	removeButton(groupId: string, buttonId: string): any;
	addButtonGroup(group: UiToolbarButtonGroupConfig, rightSide: boolean): any;
	removeButtonGroup(groupId: string): any;
	setButtonHasDropDown(groupId: string, buttonId: string, hasDropDown: boolean): any;
	setDropDownComponent(groupId: string, buttonId: string, component: unknown): any;
}

export interface AbstractUiToolContainerEventSource {
	onToolbarButtonClick: TeamAppsEvent<AbstractUiToolContainer_ToolbarButtonClickEvent>;
}

export interface AbstractUiToolContainer_ToolbarButtonClickEvent extends UiEvent {
	groupId: string;
	buttonId: string;
	dropDownClickInfo: UiDropDownButtonClickInfoConfig
}

