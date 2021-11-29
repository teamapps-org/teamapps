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
import {UiLineGraphDataConfig} from "./UiLineGraphDataConfig";


export interface UiHoseGraphDataConfig extends UiGraphDataConfig {
	_type?: string;
	lowerLineData: UiLineGraphDataConfig;
	middleLineData: UiLineGraphDataConfig;
	upperLineData: UiLineGraphDataConfig
}


