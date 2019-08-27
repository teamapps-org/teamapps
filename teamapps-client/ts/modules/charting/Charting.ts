import {UiScaleType} from "../../generated/UiScaleType";
import {UiTimeGraph} from "./UiTimeGraph";
import {UiLineChartCurveType} from "../../generated/UiLineChartCurveType";
import * as d3 from "d3";
import {Selection} from "d3";

export function fakeZeroIfLogScale(y: number, scaleType: UiScaleType) {
	if (scaleType === UiScaleType.LOG10 && y === 0) {
		return UiTimeGraph.LOGSCALE_MIN_Y;
	} else {
		return y;
	}
}

export const CurveTypeToCurveFactory = {
	[UiLineChartCurveType.LINEAR]: d3.curveLinear,
	[UiLineChartCurveType.STEP]: d3.curveStep,
	[UiLineChartCurveType.STEPBEFORE]: d3.curveStepBefore,
	[UiLineChartCurveType.STEPAFTER]: d3.curveStepAfter,
	[UiLineChartCurveType.BASIS]: d3.curveBasis,
	[UiLineChartCurveType.CARDINAL]: d3.curveCardinal,
	[UiLineChartCurveType.MONOTONE]: d3.curveMonotoneX,
	[UiLineChartCurveType.CATMULLROM]: d3.curveCatmullRom,
};

export interface DataPoint {
	x: number,
	y: number
}

export type SVGGSelection<DATUM = {}> = Selection<SVGGElement, DATUM, HTMLElement, undefined>;