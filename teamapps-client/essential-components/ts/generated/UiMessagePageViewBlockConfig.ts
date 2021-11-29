/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiPageViewBlockConfig} from "./UiPageViewBlockConfig";
import {UiToolButtonConfig} from "./UiToolButtonConfig";
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiClientRecordConfig} from "./UiClientRecordConfig";
import {UiHorizontalElementAlignment} from "./UiHorizontalElementAlignment";


export interface UiMessagePageViewBlockConfig extends UiPageViewBlockConfig {
	_type?: string;
	topTemplate?: UiTemplateConfig;
	topRecord?: UiClientRecordConfig;
	topRecordAlignment?: UiHorizontalElementAlignment;
	html?: string;
	imageUrls?: string[]
}


