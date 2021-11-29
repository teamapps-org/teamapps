/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiImageCropperSelectionConfig {
	_type?: string;
	left: number;
	top: number;
	width: number;
	height: number
}

export function createUiImageCropperSelectionConfig(left: number, top: number, width: number, height: number): UiImageCropperSelectionConfig {
	return {
		_type: "UiImageCropperSelection",
		left, top, width, height
	};
}


