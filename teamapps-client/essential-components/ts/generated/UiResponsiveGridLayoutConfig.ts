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
import {UiResponsiveGridLayoutPolicyConfig} from "./UiResponsiveGridLayoutPolicyConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiResponsiveGridLayoutConfig extends UiComponentConfig {
	_type?: string;
	layoutPolicies: UiResponsiveGridLayoutPolicyConfig[];
	fillHeight?: boolean
}

export interface UiResponsiveGridLayoutCommandHandler extends UiComponentCommandHandler {
	updateLayoutPolicies(layoutPolicies: UiResponsiveGridLayoutPolicyConfig[]): any;
	setFillHeight(fillHeight: boolean): any;
}


