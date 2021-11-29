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


export interface UiPopupConfig extends UiComponentConfig {
	_type?: string;
	contentComponent?: unknown;
	width?: number;
	height?: number;
	x?: number;
	y?: number;
	backgroundColor?: string;
	modal?: boolean;
	dimmingColor?: string;
	closeOnEscape?: boolean;
	closeOnClickOutside?: boolean
}

export interface UiPopupCommandHandler extends UiComponentCommandHandler {
	setBackgroundColor(backgroundColor: string): any;
	setDimmingColor(backgroundColor: string): any;
	setPosition(x: number, y: number): any;
	setDimensions(width: number, height: number): any;
	close(): any;
}


