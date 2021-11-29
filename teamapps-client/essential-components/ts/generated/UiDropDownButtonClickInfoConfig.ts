/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiDropDownButtonClickInfoConfig {
	_type?: string;
	isOpening: boolean;
	isContentSet: boolean
}

export function createUiDropDownButtonClickInfoConfig(isOpening: boolean, isContentSet: boolean): UiDropDownButtonClickInfoConfig {
	return {
		_type: "UiDropDownButtonClickInfo",
		isOpening, isContentSet
	};
}


