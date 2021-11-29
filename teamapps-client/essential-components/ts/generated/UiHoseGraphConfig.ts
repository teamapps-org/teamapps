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


export interface UiHoseGraphConfig extends UiGraphConfig {
	_type?: string;
	upperBoundDataSeriesId?: string;
	middleLineDataSeriesId?: string;
	lowerBoundDataSeriesId?: string;
	graphType?: UiLineChartCurveType;
	dataDotRadius?: number;
	areaColor?: string;
	middleLineColor?: string;
	lowerLineColor?: string;
	upperLineColor?: string
}


