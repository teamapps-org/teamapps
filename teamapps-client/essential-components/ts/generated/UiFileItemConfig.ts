/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiFileItemConfig {
	_type?: string;
	uuid?: string;
	icon?: string;
	thumbnail?: string;
	fileName?: string;
	description?: string;
	size?: number;
	linkUrl?: string
}

export function createUiFileItemConfig(nonRequiredProperties?: {uuid?: string, icon?: string, thumbnail?: string, fileName?: string, description?: string, size?: number, linkUrl?: string}): UiFileItemConfig {
	return {
		_type: "UiFileItem",
		...(nonRequiredProperties||{})
	};
}


