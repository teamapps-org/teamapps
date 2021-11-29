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
import {UiSpacingConfig} from "./UiSpacingConfig";
import {UiHorizontalElementAlignment} from "./UiHorizontalElementAlignment";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiElegantPanelConfig extends UiComponentConfig {
	_type?: string;
	bodyBackgroundColor?: string;
	content?: unknown;
	padding?: UiSpacingConfig;
	horizontalContentAlignment?: UiHorizontalElementAlignment;
	maxContentWidth?: number
}

export interface UiElegantPanelCommandHandler extends UiComponentCommandHandler {
	setContent(content: unknown): any;
}


