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
import {UiGridPlacementConfig} from "./UiGridPlacementConfig";
import {UiSpacingConfig} from "./UiSpacingConfig";
import {UiBorderConfig} from "./UiBorderConfig";
import {UiShadowConfig} from "./UiShadowConfig";
import {UiVerticalElementAlignment} from "./UiVerticalElementAlignment";
import {UiHorizontalElementAlignment} from "./UiHorizontalElementAlignment";


export interface UiGridLayoutConfig {
	_type?: string;
	columns: UiGridColumnConfig[];
	rows: UiGridRowConfig[];
	componentPlacements: UiGridPlacementConfig[];
	margin?: UiSpacingConfig;
	padding?: UiSpacingConfig;
	border?: UiBorderConfig;
	shadow?: UiShadowConfig;
	backgroundColor?: string;
	gridGap?: number;
	verticalAlignment?: UiVerticalElementAlignment;
	horizontalAlignment?: UiHorizontalElementAlignment
}


