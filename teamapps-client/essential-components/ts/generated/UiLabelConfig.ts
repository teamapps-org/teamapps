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
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiLabelConfig extends UiFieldConfig {
	_type?: string;
	caption: string;
	icon?: string;
	targetComponent?: unknown
}

export interface UiLabelCommandHandler extends UiFieldCommandHandler {
	setCaption(caption: string): any;
	setIcon(icon: string): any;
	setTargetComponent(targetField: unknown): any;
}

export interface UiLabelEventSource extends UiFieldEventSource {
	onClicked: TeamAppsEvent<UiLabel_ClickedEvent>;
}

export interface UiLabel_ClickedEvent extends UiEvent {
}

