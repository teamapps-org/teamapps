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
import {UiSplitDirection} from "./UiSplitDirection";
import {UiSplitSizePolicy} from "./UiSplitSizePolicy";


export interface UiWorkSpaceLayoutSplitItemConfig extends UiWorkSpaceLayoutItemConfig {
	_type?: string;
	splitDirection: UiSplitDirection;
	sizePolicy?: UiSplitSizePolicy;
	referenceChildSize?: number;
	firstChild: UiWorkSpaceLayoutItemConfig;
	lastChild: UiWorkSpaceLayoutItemConfig
}

export function createUiWorkSpaceLayoutSplitItemConfig(id: string, splitDirection: UiSplitDirection, firstChild: UiWorkSpaceLayoutItemConfig, lastChild: UiWorkSpaceLayoutItemConfig, nonRequiredProperties?: {sizePolicy?: UiSplitSizePolicy, referenceChildSize?: number}): UiWorkSpaceLayoutSplitItemConfig {
	return {
		_type: "UiWorkSpaceLayoutSplitItem",
		id, splitDirection, firstChild, lastChild,
		...(nonRequiredProperties||{})
	};
}


