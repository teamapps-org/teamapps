/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComboBoxConfig} from "./UiComboBoxConfig";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiTextInputHandlingFieldConfig} from "./UiTextInputHandlingFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiComboBoxTreeRecordConfig} from "./UiComboBoxTreeRecordConfig";
import {UiComboBoxCommandHandler} from "./UiComboBoxConfig";
import {UiComboBoxEventSource} from "./UiComboBoxConfig";

export enum UiTagComboBox_WrappingMode {
	SINGLE_LINE, MULTI_LINE, SINGLE_TAG_PER_LINE
}

export interface UiTagComboBoxConfig extends UiComboBoxConfig {
	_type?: string;
	maxEntries?: number;
	wrappingMode?: UiTagComboBox_WrappingMode;
	distinct?: boolean;
	twoStepDeletion?: boolean
}

export interface UiTagComboBoxCommandHandler extends UiComboBoxCommandHandler {
}

export interface UiTagComboBoxEventSource extends UiComboBoxEventSource {
}


