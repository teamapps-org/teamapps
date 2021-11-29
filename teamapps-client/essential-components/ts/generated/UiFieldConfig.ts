/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiFieldEditingMode} from "./UiFieldEditingMode";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiFieldConfig extends UiComponentConfig {
	_type?: string;
	editingMode?: UiFieldEditingMode;
	value?: any;
	fieldMessages?: UiFieldMessageConfig[]
}

export interface UiFieldCommandHandler extends UiComponentCommandHandler {
	setEditingMode(editingMode: UiFieldEditingMode): any;
	setValue(value: any): any;
	focus(): any;
	setFieldMessages(fieldMessages: UiFieldMessageConfig[]): any;
}

export interface UiFieldEventSource {
	onValueChanged: TeamAppsEvent<UiField_ValueChangedEvent>;
}

export interface UiField_ValueChangedEvent extends UiEvent {
	value: any
}

