/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiGraphDataConfig} from "./UiGraphDataConfig";
import {UiLongIntervalConfig} from "./UiLongIntervalConfig";
import {UiLineGraphDataPointConfig} from "./UiLineGraphDataPointConfig";


export interface UiLineGraphDataConfig extends UiGraphDataConfig {
	_type?: string;
	dataPoints: UiLineGraphDataPointConfig[]
}


