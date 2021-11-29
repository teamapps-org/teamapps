/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiMapLocationConfig {
	_type?: string;
	latitude: number;
	longitude: number
}

export function createUiMapLocationConfig(latitude: number, longitude: number): UiMapLocationConfig {
	return {
		_type: "UiMapLocation",
		latitude, longitude
	};
}


