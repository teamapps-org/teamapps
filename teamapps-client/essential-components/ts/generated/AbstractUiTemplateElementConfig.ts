/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiSpacingConfig} from "./UiSpacingConfig";
import {UiHorizontalElementAlignment} from "./UiHorizontalElementAlignment";
import {UiVerticalElementAlignment} from "./UiVerticalElementAlignment";


export interface AbstractUiTemplateElementConfig {
	_type?: string;
	property: string;
	row: number;
	column: number;
	rowSpan?: number;
	colSpan?: number;
	horizontalAlignment?: UiHorizontalElementAlignment;
	verticalAlignment?: UiVerticalElementAlignment;
	margin?: UiSpacingConfig;
	backgroundColor?: string
}


