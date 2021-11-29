/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiFormSectionPlacementConfig} from "./UiFormSectionPlacementConfig";
import {UiGridPlacementConfig} from "./UiGridPlacementConfig";
import {UiFormSectionFloatingFieldConfig} from "./UiFormSectionFloatingFieldConfig";


export interface UiFormSectionFloatingFieldsPlacementConfig extends UiFormSectionPlacementConfig {
	_type?: string;
	floatingFields: UiFormSectionFloatingFieldConfig[];
	wrap?: boolean;
	horizontalSpacing?: number;
	verticalSpacing?: number
}


