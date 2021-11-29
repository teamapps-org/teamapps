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


export interface UiMapPolygonConfig extends AbstractUiMapShapeConfig {
	_type?: string;
	path?: UiMapLocationConfig[]
}

export function createUiMapPolygonConfig(nonRequiredProperties?: {shapeProperties?: UiShapePropertiesConfig, path?: UiMapLocationConfig[]}): UiMapPolygonConfig {
	return {
		_type: "UiMapPolygon",
		...(nonRequiredProperties||{})
	};
}


