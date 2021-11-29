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
import {UiGridColumnConfig} from "./UiGridColumnConfig";
import {UiGridRowConfig} from "./UiGridRowConfig";
import {AbstractUiTemplateElementConfig} from "./AbstractUiTemplateElementConfig";
import {UiSpacingConfig} from "./UiSpacingConfig";
import {UiBorderConfig} from "./UiBorderConfig";


export interface UiGridTemplateConfig extends UiTemplateConfig {
	_type?: string;
	columns: UiGridColumnConfig[];
	rows: UiGridRowConfig[];
	elements: AbstractUiTemplateElementConfig[];
	minWidth?: number;
	maxWidth?: number;
	minHeight?: number;
	maxHeight?: number;
	padding?: UiSpacingConfig;
	gridGap?: number;
	backgroundColor?: string;
	border?: UiBorderConfig;
	ariaLabelProperty?: string;
	titleProperty?: string
}


