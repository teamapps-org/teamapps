/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiChatNewFileConfig} from "./UiChatNewFileConfig";


export interface UiNewChatMessageConfig {
	_type?: string;
	text?: string;
	uploadedFiles?: UiChatNewFileConfig[]
}

export function createUiNewChatMessageConfig(nonRequiredProperties?: {text?: string, uploadedFiles?: UiChatNewFileConfig[]}): UiNewChatMessageConfig {
	return {
		_type: "UiNewChatMessage",
		...(nonRequiredProperties||{})
	};
}


