/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiSortDirection} from "./UiSortDirection";


export interface UiTableDataRequestConfig {
	_type?: string;
	startIndex: number;
	length: number;
	sortField: string;
	sortDirection: UiSortDirection
}

export function createUiTableDataRequestConfig(startIndex: number, length: number, sortField: string, sortDirection: UiSortDirection): UiTableDataRequestConfig {
	return {
		_type: "UiTableDataRequest",
		startIndex, length, sortField, sortDirection
	};
}


