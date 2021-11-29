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
import {UiGaugeOptionsConfig} from "./UiGaugeOptionsConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiGaugeConfig extends UiComponentConfig {
	_type?: string;
	options: UiGaugeOptionsConfig
}

export interface UiGaugeCommandHandler extends UiComponentCommandHandler {
	setOptions(options: UiGaugeOptionsConfig): any;
	setValue(value: number): any;
}


