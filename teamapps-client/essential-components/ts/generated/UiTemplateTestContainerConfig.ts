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
import {UiGridTemplateConfig} from "./UiGridTemplateConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiTemplateTestContainerConfig extends UiComponentConfig {
	_type?: string;
	template: UiGridTemplateConfig;
	data: any;
	minContainerWidth?: number;
	minContainerHeight?: number;
	maxContainerWidth?: number;
	maxContainerHeight?: number;
	description?: string
}

export interface UiTemplateTestContainerCommandHandler extends UiComponentCommandHandler {
}


