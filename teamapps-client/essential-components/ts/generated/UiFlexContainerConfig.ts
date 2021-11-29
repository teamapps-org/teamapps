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
import {UiCssFlexDirection} from "./UiCssFlexDirection";
import {UiCssAlignItems} from "./UiCssAlignItems";
import {UiCssJustifyContent} from "./UiCssJustifyContent";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiFlexContainerConfig extends UiComponentConfig {
	_type?: string;
	components?: unknown[];
	flexDirection?: UiCssFlexDirection;
	alignItems?: UiCssAlignItems;
	justifyContent?: UiCssJustifyContent
}

export interface UiFlexContainerCommandHandler extends UiComponentCommandHandler {
	addComponent(component: unknown): any;
	removeComponent(component: unknown): any;
}


