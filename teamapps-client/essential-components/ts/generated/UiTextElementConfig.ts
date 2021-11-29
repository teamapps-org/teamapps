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
import {UiFontStyleConfig} from "./UiFontStyleConfig";
import {UiTextAlignment} from "./UiTextAlignment";


export interface UiTextElementConfig extends AbstractUiTemplateElementConfig {
	_type?: string;
	fontStyle?: UiFontStyleConfig;
	lineHeight?: number;
	wrapLines?: boolean;
	padding?: UiSpacingConfig;
	textAlignment?: UiTextAlignment
}


