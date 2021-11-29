/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiClientObjectConfig} from "./UiClientObjectConfig";


export interface UiComponentConfig extends UiClientObjectConfig {
	_type?: string;
	debuggingId?: string;
	visible?: boolean;
	stylesBySelector?: {[name: string]: {[name: string]: string}};
	classNamesBySelector?: {[name: string]: {[name: string]: boolean}};
	attributesBySelector?: {[name: string]: {[name: string]: string}}
}

export interface UiComponentCommandHandler {
	setVisible(visible: boolean): any;
	setStyle(selector: string, styles: {[name: string]: string}): any;
	setClassNames(selector: string, classNames: {[name: string]: boolean}): any;
	setAttributes(selector: string, attributes: {[name: string]: string}): any;
}


