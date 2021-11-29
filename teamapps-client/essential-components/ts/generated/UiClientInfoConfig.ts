/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";


export interface UiClientInfoConfig {
	_type?: string;
	ip?: string;
	userAgentString?: string;
	preferredLanguageIso?: string;
	screenWidth?: number;
	screenHeight?: number;
	viewPortWidth?: number;
	viewPortHeight?: number;
	highDensityScreen?: boolean;
	timezoneIana?: string;
	timezoneOffsetMinutes?: number;
	clientTokens?: string[];
	clientUrl?: string;
	clientParameters?: {[name: string]: any};
	teamAppsVersion?: string
}

export function createUiClientInfoConfig(nonRequiredProperties?: {ip?: string, userAgentString?: string, preferredLanguageIso?: string, screenWidth?: number, screenHeight?: number, viewPortWidth?: number, viewPortHeight?: number, highDensityScreen?: boolean, timezoneIana?: string, timezoneOffsetMinutes?: number, clientTokens?: string[], clientUrl?: string, clientParameters?: {[name: string]: any}, teamAppsVersion?: string}): UiClientInfoConfig {
	return {
		_type: "UiClientInfo",
		...(nonRequiredProperties||{})
	};
}


