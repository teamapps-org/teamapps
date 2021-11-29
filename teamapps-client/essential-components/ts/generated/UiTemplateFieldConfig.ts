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
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiTemplateFieldConfig extends UiFieldConfig {
	_type?: string;
	template?: UiTemplateConfig
}

export interface UiTemplateFieldCommandHandler extends UiFieldCommandHandler {
	update(config: UiTemplateFieldConfig): any;
}

export interface UiTemplateFieldEventSource extends UiFieldEventSource {
	onClicked: TeamAppsEvent<UiTemplateField_ClickedEvent>;
}

export interface UiTemplateField_ClickedEvent extends UiEvent {
}

