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


export interface UiToolButtonConfig extends UiComponentConfig {
	_type?: string;
	icon: string;
	popoverText: string;
	grayOutIfNotHovered?: boolean;
	minDropDownWidth?: number;
	minDropDownHeight?: number;
	openDropDownIfNotSet?: boolean;
	dropDownComponent?: unknown
}

export function createUiToolButtonConfig(icon: string, popoverText: string, nonRequiredProperties?: {id?: string, debuggingId?: string, visible?: boolean, stylesBySelector?: {[name: string]: {[name: string]: string}}, classNamesBySelector?: {[name: string]: {[name: string]: boolean}}, attributesBySelector?: {[name: string]: {[name: string]: string}}, grayOutIfNotHovered?: boolean, minDropDownWidth?: number, minDropDownHeight?: number, openDropDownIfNotSet?: boolean, dropDownComponent?: unknown}): UiToolButtonConfig {
	return {
		_type: "UiToolButton",
		icon, popoverText,
		...(nonRequiredProperties||{})
	};
}

export interface UiToolButtonCommandHandler extends UiComponentCommandHandler {
	setIcon(icon: string): any;
	setPopoverText(popoverText: string): any;
	setGrayOutIfNotHovered(grayOutIfNotHovered: boolean): any;
	setDropDownSize(minDropDownWidth: number, minDropDownHeight: number): any;
	setOpenDropDownIfNotSet(openDropDownIfNotSet: boolean): any;
	setDropDownComponent(dropDownComponent: unknown): any;
}

export interface UiToolButtonEventSource {
	onClicked: TeamAppsEvent<UiToolButton_ClickedEvent>;
	onDropDownOpened: TeamAppsEvent<UiToolButton_DropDownOpenedEvent>;
}

export interface UiToolButton_ClickedEvent extends UiEvent {
}

export interface UiToolButton_DropDownOpenedEvent extends UiEvent {
}

