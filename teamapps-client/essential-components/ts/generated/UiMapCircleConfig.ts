/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {AbstractUiMapShapeConfig} from "./AbstractUiMapShapeConfig";
import {UiShapePropertiesConfig} from "./UiShapePropertiesConfig";
import {UiMapLocationConfig} from "./UiMapLocationConfig";


export interface UiMapCircleConfig extends AbstractUiMapShapeConfig {
	_type?: string;
	center?: UiMapLocationConfig;
	radius?: number
}

export function createUiMapCircleConfig(nonRequiredProperties?: {shapeProperties?: UiShapePropertiesConfig, center?: UiMapLocationConfig, radius?: number}): UiMapCircleConfig {
	return {
		_type: "UiMapCircle",
		...(nonRequiredProperties||{})
	};
}


