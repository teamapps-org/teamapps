/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiColumnDefinitionConfig} from "./UiColumnDefinitionConfig";
import {UiCompositeSubFieldConfig} from "./UiCompositeSubFieldConfig";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiCompositeFieldConfig extends UiFieldConfig {
	_type?: string;
	columnDefinitions: UiColumnDefinitionConfig[];
	rowHeights: number[];
	subFields: UiCompositeSubFieldConfig[];
	horizontalCellSpacing?: number;
	verticalCellSpacing?: number;
	padding?: number;
	drawFieldBorders?: boolean
}

export interface UiCompositeFieldCommandHandler extends UiFieldCommandHandler {
}

export interface UiCompositeFieldEventSource extends UiFieldEventSource {
}


