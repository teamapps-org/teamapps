/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiCurrencyUnitConfig} from "./UiCurrencyUnitConfig";


export interface UiCurrencyValueConfig {
	_type?: string;
	currencyUnit: UiCurrencyUnitConfig;
	amount: string
}

export function createUiCurrencyValueConfig(currencyUnit: UiCurrencyUnitConfig, amount: string): UiCurrencyValueConfig {
	return {
		_type: "UiCurrencyValue",
		currencyUnit, amount
	};
}


