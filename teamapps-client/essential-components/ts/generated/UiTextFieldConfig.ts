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
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";
import {UiTextInputHandlingFieldEventSource} from "./UiTextInputHandlingFieldConfig";


export interface UiTextFieldConfig extends UiFieldConfig, UiTextInputHandlingFieldConfig {
	_type?: string;
	maxCharacters?: number;
	showClearButton?: boolean;
	placeholderText?: string;
	autofill?: boolean
}

export interface UiTextFieldCommandHandler extends UiFieldCommandHandler {
	setMaxCharacters(maxCharacters: number): any;
	setShowClearButton(showClearButton: boolean): any;
	setPlaceholderText(placeholderText: string): any;
}

export interface UiTextFieldEventSource extends UiFieldEventSource, UiTextInputHandlingFieldEventSource {
}


