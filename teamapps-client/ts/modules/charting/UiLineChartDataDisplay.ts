/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import {ScaleTime} from "d3-scale";
import {SVGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {YAxis} from "./YAxis";

export interface UiLineChartDataDisplay<C extends AbstractUiLineChartDataDisplayConfig = AbstractUiLineChartDataDisplayConfig> {

	setConfig(config: C): void;
	getDataSeriesIds(): string[];
	getMainSelection(): SVGSelection<any>;

	updateZoomX(zoomLevelIndex: number, scaleX: ScaleTime<number, number>): void;

	getYAxis(): YAxis | null;

	setYRange(range: [number, number]): void;

	redraw():void;
	destroy(): void;

}
