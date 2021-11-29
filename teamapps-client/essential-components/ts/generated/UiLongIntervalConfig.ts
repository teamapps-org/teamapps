/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiLongIntervalConfig {
	_type?: string;
	min: number;
	max: number
}

export function createUiLongIntervalConfig(min: number, max: number): UiLongIntervalConfig {
	return {
		_type: "UiLongInterval",
		min, max
	};
}


