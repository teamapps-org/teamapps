/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiGraphConfig} from "./UiGraphConfig";
import {UiLongIntervalConfig} from "./UiLongIntervalConfig";
import {UiLineChartCurveType} from "./UiLineChartCurveType";


export interface UiLineGraphConfig extends UiGraphConfig {
	_type?: string;
	dataSeriesId?: string;
	graphType?: UiLineChartCurveType;
	dataDotRadius?: number;
	lineColorScaleMin?: string;
	lineColorScaleMax?: string;
	areaColorScaleMin?: string;
	areaColorScaleMax?: string
}


