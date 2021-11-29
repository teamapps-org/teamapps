/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiEvent} from "./UiEvent";
import {UiCommand} from "./UiCommand";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiSpecialKey} from "./UiSpecialKey";


export interface UiTextInputHandlingFieldConfig {
	_type?: string;
}

export interface UiTextInputHandlingFieldEventSource {
	onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>;
	onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>;
}

export interface UiTextInputHandlingField_TextInputEvent extends UiEvent {
	enteredString: string
}

export interface UiTextInputHandlingField_SpecialKeyPressedEvent extends UiEvent {
	key: UiSpecialKey
}

