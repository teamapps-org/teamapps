/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiLocalDateConfig {
	_type?: string;
	year: number;
	month: number;
	day: number
}

export function createUiLocalDateConfig(year: number, month: number, day: number): UiLocalDateConfig {
	return {
		_type: "UiLocalDate",
		year, month, day
	};
}


