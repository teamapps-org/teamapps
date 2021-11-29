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


export interface UiMapRectangleConfig extends AbstractUiMapShapeConfig {
	_type?: string;
	l1?: UiMapLocationConfig;
	l2?: UiMapLocationConfig
}

export function createUiMapRectangleConfig(nonRequiredProperties?: {shapeProperties?: UiShapePropertiesConfig, l1?: UiMapLocationConfig, l2?: UiMapLocationConfig}): UiMapRectangleConfig {
	return {
		_type: "UiMapRectangle",
		...(nonRequiredProperties||{})
	};
}


