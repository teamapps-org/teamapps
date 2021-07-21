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
import {Selection} from "d3-selection";
import * as d3 from "d3";
import {NamespaceLocalObject, ScaleLinear, ScaleTime} from "d3";
import {SVGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {UiLineChartDataDisplay} from "./UiLineChartDataDisplay";
import {ScaleContinuousNumeric} from "d3-scale";
import {TimeGraphContext} from "./TimeGraphContext";
import {YAxis} from "./YAxis";

export class UiEventChartDisplay implements UiLineChartDataDisplay {

	private $main: Selection<SVGGElement, unknown, null, undefined>;
	private colorScale: ScaleLinear<string, string>;
	private scaleX: ScaleTime<number, number>;
	private scaleY: ScaleContinuousNumeric<number, number> = d3.scaleLinear().domain([0, 20]);

	constructor(private context: TimeGraphContext) {
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

		let data = [
			{start: 3600000, end: 2 * 7200000, y: 10},
			{start: 7200000, end: 7200000, y: 15},
			{start: 5000000, end: 5000000, y: 15},
			{start: 1000000, end: 2000000, y: 5},
		];

		// Three function that change the tooltip when user hover / move / leave a cell
		const mouseenter = (d: any, i: number, nodes: Element[]) => {
			d3.select(nodes[i])
				.style("stroke-width", "2");
			this.context.showPopover(nodes[i], "The exact value of<br>this cell is: " + d.y)
		};
		const mouseleave = (d: any, i: number, nodes: Element[]) => {
			d3.select(nodes[i])
				.style("stroke-width", "1");
			this.context.hidePopover();
		};

		const thickness = 10;

		this.$main
			.selectAll("rect.event-rect")
			.data(data)
			.join("rect")
			.classed("event-rect", true)
			.attr("stroke", "red")
			.attr("stroke-width", 1)
			.attr("fill", "#ffbbbb")
			// .attr("stroke-linecap", "round")
			.attr("x", d => this.scaleX(d.start) - (thickness / 2))
			.attr("width", d => this.scaleX(d.end) - this.scaleX(d.start) + thickness)
			.attr("y", d => this.scaleY(d.y) - (thickness / 2))
			.attr("rx", (thickness / 2))
			.attr("ry", (thickness / 2))
			.attr("height", d => thickness)
			.on("mouseenter", mouseenter)
			.on("mouseleave", mouseleave);

		this.$main
			.selectAll("line.event-line")
			.data(data)
			.join("line")
			.classed("event-line", true)
			.attr("stroke", "red")
			.attr("stroke-width", 1)
			.attr("stroke-linecap", "round")
			.attr("x1", d => this.scaleX(d.start))
			.attr("x2", d => this.scaleX(d.end))
			.attr("y1", d => this.scaleY(d.y))
			.attr("y2", d => this.scaleY(d.y));

		this.$main
			.selectAll("circle.event-dot")
			.data(data.flatMap(d => [{x: d.start, y: d.y}, {x: d.end, y: d.y}]))
			.join("circle")
			.classed("event-dot", true)
			.attr("fill", "red")
			.attr("cx", d => this.scaleX(d.x))
			.attr("cy", d => this.scaleY(d.y))
			.attr("r", "2");
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



