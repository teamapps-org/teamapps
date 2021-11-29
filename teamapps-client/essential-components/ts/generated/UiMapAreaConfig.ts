/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiMapAreaConfig {
	_type?: string;
	minLatitude: number;
	maxLatitude: number;
	minLongitude: number;
	maxLongitude: number
}

export function createUiMapAreaConfig(minLatitude: number, maxLatitude: number, minLongitude: number, maxLongitude: number): UiMapAreaConfig {
	return {
		_type: "UiMapArea",
		minLatitude, maxLatitude, minLongitude, maxLongitude
	};
}


