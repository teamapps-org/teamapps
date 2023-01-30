/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
import {UiScaleType} from "../../generated/UiScaleType";
import {UiTimeGraph} from "./UiTimeGraph";
import {UiLineChartCurveType} from "../../generated/UiLineChartCurveType";
import * as d3 from "d3";
import {BaseType, Selection} from "d3";

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

export type SVGSelection<DATUM = unknown> = Selection<SVGElement, DATUM, BaseType, undefined>;
export type SVGGSelection<DATUM = unknown> = Selection<SVGGElement, DATUM, BaseType, undefined>;
