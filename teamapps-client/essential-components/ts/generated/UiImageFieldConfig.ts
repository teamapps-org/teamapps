/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiBorderConfig} from "./UiBorderConfig";
import {UiShadowConfig} from "./UiShadowConfig";
import {UiImageSizing} from "./UiImageSizing";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiImageFieldConfig extends UiFieldConfig {
	_type?: string;
	width?: string;
	height?: string;
	border?: UiBorderConfig;
	shadow?: UiShadowConfig;
	imageSizing?: UiImageSizing;
	backgroundColor?: string
}

export interface UiImageFieldCommandHandler extends UiFieldCommandHandler {
	update(config: UiImageFieldConfig): any;
}

export interface UiImageFieldEventSource extends UiFieldEventSource {
}


