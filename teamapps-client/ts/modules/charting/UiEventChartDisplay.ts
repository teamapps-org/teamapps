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
import {Line} from "d3-shape";
import {Selection} from "d3-selection";
import * as d3 from "d3";
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {CurveTypeToCurveFactory, DataPoint, fakeZeroIfLogScale, SVGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiLineChartBandConfig} from "../../generated/UiLineChartBandConfig";
import {D3Area2} from "./D3Area2";
import {isVisibleColor} from "../Common";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {UiLineChartDataDisplay, YAxis} from "./UiLineChartDataDisplay";
import {NamespaceLocalObject, ScaleLinear, ScaleTime} from "d3";
import {ScaleContinuousNumeric} from "d3-scale";

export class UiEventChartDisplay implements UiLineChartDataDisplay {

	private $main: Selection<SVGGElement, unknown, null, undefined>;
	private colorScale: ScaleLinear<string, string>;
	private scaleX: ScaleTime<number, number>;
	private scaleY: ScaleContinuousNumeric<number, number> = d3.scaleLinear().domain([0, 20]);

	constructor() {
		this.$main = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement)
			.attr("data-series-id", `testtesttodo`);
	}

	getMainSelection(): SVGSelection<any> {
		return this.$main;
	}

	getDataSeriesIds(): string[] {
		return [];
	}

	redraw() {
		this.colorScale = d3.scaleLinear<string, string>()
			.range(["#2222ff", "#cc0000"]);

		this.colorScale.domain();
		let data = [
			{start: 3600000, end: 2*7200000},
		];

		this.$main
			.selectAll("line.event")
			.data(data)
			.join("line")
			.classed("event", true)
			.attr("stroke", "red")
			.attr("stroke-width", 7)
			.attr("stroke-linecap", "round")
			.attr("x1", d => this.scaleX(d.start))
			.attr("x2", d => this.scaleX(d.end))
			.attr("y1", d => this.scaleY(10))
			.attr("y2", d => this.scaleY(10))
		;
		// <line x1="0" y1="80" x2="100" y2="20" stroke="black" />
	}

	setConfig(config: AbstractUiLineChartDataDisplayConfig): void {
	}

	setYRange(range: [number, number]): void {
		this.scaleY.range(range);
	}

	updateYAxisTickFormat(): void {
	}

	updateZoomX(zoomLevelIndex: number, scaleX: ScaleTime<number, number>): void {
		this.scaleX = scaleX;
	}

	destroy(): void {
	}

	getYAxis(): YAxis | null {
		return null;
	}

}



