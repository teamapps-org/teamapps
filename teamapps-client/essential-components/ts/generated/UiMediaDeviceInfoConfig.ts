/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiMediaDeviceKind} from "./UiMediaDeviceKind";


export interface UiMediaDeviceInfoConfig {
	_type?: string;
	deviceId?: string;
	groupId?: string;
	kind?: UiMediaDeviceKind;
	label?: string
}

export function createUiMediaDeviceInfoConfig(nonRequiredProperties?: {deviceId?: string, groupId?: string, kind?: UiMediaDeviceKind, label?: string}): UiMediaDeviceInfoConfig {
	return {
		_type: "UiMediaDeviceInfo",
		...(nonRequiredProperties||{})
	};
}


