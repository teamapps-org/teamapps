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
import {UiToolbarConfig} from "./UiToolbarConfig";
import {UiNavigationBarConfig} from "./UiNavigationBarConfig";
import {UiPageTransition} from "./UiPageTransition";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiMobileLayoutConfig extends UiComponentConfig {
	_type?: string;
	toolbar?: unknown;
	initialView?: unknown;
	navigationBar?: unknown
}

export interface UiMobileLayoutCommandHandler extends UiComponentCommandHandler {
	setToolbar(toolbar: unknown): any;
	setNavigationBar(navBar: unknown): any;
	showView(component: unknown, animation: UiPageTransition, animationDuration: number): any;
}


