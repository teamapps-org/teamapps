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
import {UiTextInputHandlingFieldConfig} from "./UiTextInputHandlingFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiComboBoxTreeRecordConfig} from "./UiComboBoxTreeRecordConfig";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";
import {UiTextInputHandlingFieldEventSource} from "./UiTextInputHandlingFieldConfig";


export interface UiComboBoxConfig extends UiFieldConfig, UiTextInputHandlingFieldConfig {
	_type?: string;
	templates?: {[name: string]: UiTemplateConfig};
	showDropDownButton?: boolean;
	showDropDownAfterResultsArrive?: boolean;
	highlightFirstResultEntry?: boolean;
	showHighlighting?: boolean;
	autoComplete?: boolean;
	textHighlightingEntryLimit?: number;
	allowAnyText?: boolean;
	showClearButton?: boolean;
	animate?: boolean;
	showExpanders?: boolean;
	placeholderText?: string
	lazyChildren: (query: UiComboBox_LazyChildrenQuery) => Promise<UiComboBoxTreeRecordConfig[]>;
	retrieveDropdownEntries: (query: UiComboBox_RetrieveDropdownEntriesQuery) => Promise<UiComboBoxTreeRecordConfig[]>
}

export interface UiComboBoxCommandHandler extends UiFieldCommandHandler {
	registerTemplate(id: string, template: UiTemplateConfig): any;
	replaceFreeTextEntry(freeText: string, newEntry: UiComboBoxTreeRecordConfig): any;
}

export interface UiComboBoxEventSource extends UiFieldEventSource, UiTextInputHandlingFieldEventSource {
}


export interface UiComboBox_LazyChildrenQuery extends UiQuery {
	parentId: number
}

export interface UiComboBox_RetrieveDropdownEntriesQuery extends UiQuery {
	queryString: string
}