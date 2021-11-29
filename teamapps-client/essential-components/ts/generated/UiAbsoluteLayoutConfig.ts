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
import {UiAbsolutePositionedComponentConfig} from "./UiAbsolutePositionedComponentConfig";
import {UiAnimationEasing} from "./UiAnimationEasing";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiAbsoluteLayoutConfig extends UiComponentConfig {
	_type?: string;
	components?: UiAbsolutePositionedComponentConfig[]
}

export interface UiAbsoluteLayoutCommandHandler extends UiComponentCommandHandler {
	update(components: UiAbsolutePositionedComponentConfig[], animationDuration: number, easing: UiAnimationEasing): any;
}


