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
import {ScaleTime} from "d3-scale";
import {SVGSelection} from "./Charting";
import {YAxis} from "./YAxis";
import {UiGraphDataConfig} from "../../generated/UiGraphDataConfig";
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {UiGraphConfig} from "../../generated/UiGraphConfig";

export interface UiGraph<C extends UiGraphConfig = UiGraphConfig, D extends UiGraphDataConfig = UiGraphDataConfig> {

	setConfig(config: C): void;

	getMainSelection(): SVGSelection<any>;

	updateZoomX(zoomLevelIndex: number, scaleX: ScaleTime<number, number>): void;

	getYAxis(): YAxis | null;

	setYRange(range: [number, number]): void;

	redraw(): void;

	getUncoveredIntervals(zoomLevel: number, interval: [number, number]): [number, number][];

	markIntervalAsCovered(zoomLevel: number, interval: [number, number]): void;

	addData(zoomLevel: number, data: D): void;

	resetData(): void;

	destroy(): void;

}
