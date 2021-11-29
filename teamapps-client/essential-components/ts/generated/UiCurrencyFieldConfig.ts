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
import {UiCurrencyUnitConfig} from "./UiCurrencyUnitConfig";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";
import {UiTextInputHandlingFieldEventSource} from "./UiTextInputHandlingFieldConfig";


export interface UiCurrencyFieldConfig extends UiFieldConfig, UiTextInputHandlingFieldConfig {
	_type?: string;
	locale?: string;
	currencyUnits?: UiCurrencyUnitConfig[];
	fixedPrecision?: number;
	showCurrencyBeforeAmount?: boolean;
	showCurrencySymbol?: boolean;
	alphaKeysQueryForCurrency?: boolean
}

export interface UiCurrencyFieldCommandHandler extends UiFieldCommandHandler {
	setLocale(locale: string): any;
	setCurrencyUnits(currencyUnits: UiCurrencyUnitConfig[]): any;
	setFixedPrecision(fixedPrecision: number): any;
	setShowCurrencyBeforeAmount(showCurrencyBeforeAmount: boolean): any;
	setShowCurrencySymbol(showCurrencySymbol: boolean): any;
}

export interface UiCurrencyFieldEventSource extends UiFieldEventSource, UiTextInputHandlingFieldEventSource {
}


