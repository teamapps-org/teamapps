/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiGridPlacementConfig} from "./UiGridPlacementConfig";
import {UiFloatingComponentGridPlacementItemConfig} from "./UiFloatingComponentGridPlacementItemConfig";


export interface UiFloatingComponentGridPlacementConfig extends UiGridPlacementConfig {
	_type?: string;
	components: UiFloatingComponentGridPlacementItemConfig[];
	wrap?: boolean;
	horizontalSpacing?: number;
	verticalSpacing?: number
}


