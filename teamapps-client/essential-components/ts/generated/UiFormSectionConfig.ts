/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiGridColumnConfig} from "./UiGridColumnConfig";
import {UiGridRowConfig} from "./UiGridRowConfig";
import {UiFormSectionPlacementConfig} from "./UiFormSectionPlacementConfig";
import {UiSpacingConfig} from "./UiSpacingConfig";
import {UiBorderConfig} from "./UiBorderConfig";
import {UiShadowConfig} from "./UiShadowConfig";
import {UiTemplateConfig} from "./UiTemplateConfig";


export interface UiFormSectionConfig {
	_type?: string;
	id: string;
	columns: UiGridColumnConfig[];
	rows: UiGridRowConfig[];
	fieldPlacements: UiFormSectionPlacementConfig[];
	margin?: UiSpacingConfig;
	padding?: UiSpacingConfig;
	border?: UiBorderConfig;
	shadow?: UiShadowConfig;
	backgroundColor?: string;
	collapsible?: boolean;
	collapsed?: boolean;
	visible?: boolean;
	headerTemplate?: UiTemplateConfig;
	headerData?: any;
	drawHeaderLine?: boolean;
	gridGap?: number;
	fillRemainingHeight?: boolean;
	hideWhenNoVisibleFields?: boolean
}


