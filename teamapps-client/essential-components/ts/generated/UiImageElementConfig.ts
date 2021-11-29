/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {AbstractUiTemplateElementConfig} from "./AbstractUiTemplateElementConfig";
import {UiSpacingConfig} from "./UiSpacingConfig";
import {UiBorderConfig} from "./UiBorderConfig";
import {UiShadowConfig} from "./UiShadowConfig";
import {UiImageSizing} from "./UiImageSizing";


export interface UiImageElementConfig extends AbstractUiTemplateElementConfig {
	_type?: string;
	width: number;
	height: number;
	border?: UiBorderConfig;
	padding?: UiSpacingConfig;
	shadow?: UiShadowConfig;
	imageSizing?: UiImageSizing
}


