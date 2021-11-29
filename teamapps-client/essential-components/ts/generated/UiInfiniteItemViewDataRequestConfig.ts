/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiInfiniteItemViewDataRequestConfig {
	_type?: string;
	startIndex: number;
	length: number
}

export function createUiInfiniteItemViewDataRequestConfig(startIndex: number, length: number): UiInfiniteItemViewDataRequestConfig {
	return {
		_type: "UiInfiniteItemViewDataRequest",
		startIndex, length
	};
}


