/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiTextFieldConfig} from "./UiTextFieldConfig";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiTextInputHandlingFieldConfig} from "./UiTextInputHandlingFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiTextFieldCommandHandler} from "./UiTextFieldConfig";
import {UiTextFieldEventSource} from "./UiTextFieldConfig";


export interface UiPasswordFieldConfig extends UiTextFieldConfig {
	_type?: string;
	sendValueAsMd5?: boolean;
	salt?: string;
	autofill?: boolean
}

export interface UiPasswordFieldCommandHandler extends UiTextFieldCommandHandler {
	setSendValueAsMd5(sendValueAsMd5: boolean): any;
	setSalt(salt: string): any;
}

export interface UiPasswordFieldEventSource extends UiTextFieldEventSource {
}


