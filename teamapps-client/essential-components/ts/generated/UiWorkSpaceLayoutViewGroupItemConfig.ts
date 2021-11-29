/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiWorkSpaceLayoutItemConfig} from "./UiWorkSpaceLayoutItemConfig";
import {UiViewGroupPanelState} from "./UiViewGroupPanelState";


export interface UiWorkSpaceLayoutViewGroupItemConfig extends UiWorkSpaceLayoutItemConfig {
	_type?: string;
	viewNames: string[];
	selectedViewName?: string;
	persistent?: boolean;
	panelState?: UiViewGroupPanelState
}

export function createUiWorkSpaceLayoutViewGroupItemConfig(id: string, viewNames: string[], nonRequiredProperties?: {selectedViewName?: string, persistent?: boolean, panelState?: UiViewGroupPanelState}): UiWorkSpaceLayoutViewGroupItemConfig {
	return {
		_type: "UiWorkSpaceLayoutViewGroupItem",
		id, viewNames,
		...(nonRequiredProperties||{})
	};
}


