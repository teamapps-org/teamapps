/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiChatNewFileConfig {
	_type?: string;
	uploadedFileUuid?: string;
	fileName?: string
}

export function createUiChatNewFileConfig(nonRequiredProperties?: {uploadedFileUuid?: string, fileName?: string}): UiChatNewFileConfig {
	return {
		_type: "UiChatNewFile",
		...(nonRequiredProperties||{})
	};
}


