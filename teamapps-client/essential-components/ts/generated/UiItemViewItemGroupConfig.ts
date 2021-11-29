/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiIdentifiableClientRecordConfig} from "./UiIdentifiableClientRecordConfig";
import {UiItemViewFloatStyle} from "./UiItemViewFloatStyle";
import {UiItemJustification} from "./UiItemJustification";


export interface UiItemViewItemGroupConfig {
	_type?: string;
	id?: string;
	itemTemplate: UiTemplateConfig;
	headerData?: UiIdentifiableClientRecordConfig;
	items?: UiIdentifiableClientRecordConfig[];
	headerVisible?: boolean;
	floatStyle?: UiItemViewFloatStyle;
	buttonWidth?: number;
	horizontalPadding?: number;
	verticalPadding?: number;
	horizontalItemMargin?: number;
	verticalItemMargin?: number;
	itemJustification?: UiItemJustification
}


