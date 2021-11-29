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


export interface UiButtonConfig extends UiFieldConfig {
	_type?: string;
	template: UiTemplateConfig;
	templateRecord: any;
	minDropDownWidth?: number;
	minDropDownHeight?: number;
	openDropDownIfNotSet?: boolean;
	dropDownComponent?: unknown;
	onClickJavaScript?: string
}

export interface UiButtonCommandHandler extends UiFieldCommandHandler {
	setTemplate(template: UiTemplateConfig, templateRecord: any): any;
	setTemplateRecord(templateRecord: any): any;
	setDropDownSize(minDropDownWidth: number, minDropDownHeight: number): any;
	setOpenDropDownIfNotSet(openDropDownIfNotSet: boolean): any;
	setDropDownComponent(dropDownComponent: unknown): any;
	setOnClickJavaScript(onClickJavaScript: string): any;
}

export interface UiButtonEventSource extends UiFieldEventSource {
	onClicked: TeamAppsEvent<UiButton_ClickedEvent>;
	onDropDownOpened: TeamAppsEvent<UiButton_DropDownOpenedEvent>;
}

export interface UiButton_ClickedEvent extends UiEvent {
}

export interface UiButton_DropDownOpenedEvent extends UiEvent {
}

