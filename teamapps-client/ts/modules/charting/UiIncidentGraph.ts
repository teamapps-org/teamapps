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
import * as d3 from "d3";
import {GraphContext, PopperHandle} from "./GraphContext";
import {AbstractUiGraph} from "./AbstractUiGraph";
import {UiIncidentGraphConfig} from "../../generated/UiIncidentGraphConfig";
import {UiIncidentGraphDataConfig} from "../../generated/UiIncidentGraphDataConfig";
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {IncidentGraphDataStore} from "./DataStore";

export class UiIncidentGraph extends AbstractUiGraph<UiIncidentGraphConfig, UiIncidentGraphDataConfig> {

	private dataStore = new IncidentGraphDataStore();
	private popperHandle: PopperHandle;

	constructor(
		timeGraphId: string,
		config: UiIncidentGraphConfig,
		private graphContext: GraphContext
	) {
		super(config, timeGraphId);
		this.popperHandle = graphContext.getPopperHandle();
	}

	public addData(zoomLevel: number, intervalX: UiLongIntervalConfig, data: UiIncidentGraphDataConfig): void {
		this.dataStore.addData(zoomLevel, [intervalX.min, intervalX.max], data)
	}

	public resetData(): void {
		this.dataStore.reset();
	}

	public getYDataBounds(intervalX: [number, number]): [number, number] {
		return d3.extent(this.dataStore.getData(this.zoomLevelIndex, intervalX).dataPoints.map(dp => dp.y));
	}

	doRedraw() {
		// Three function that change the tooltip when user hover / move / leave a cell
		const mouseenter = (d: any, i: number, nodes: Element[]) => {
			d3.select(nodes[i])
				.style("stroke-width", "2");
			this.popperHandle.update(nodes[i], d.tooltipHtml);
		};
		const mouseleave = (d: any, i: number, nodes: Element[]) => {
			d3.select(nodes[i])
				.style("stroke-width", "1");
			this.popperHandle.hide();
		};

		const thickness = 10;

		let data = this.dataStore.getData(this.zoomLevelIndex, this.getDisplayedIntervalX());

		this.$main
			.selectAll("rect.incident-rect")
			.data(data.dataPoints)
			.join("rect")
			.classed("incident-rect", true)
			.attr("stroke", d => d.color)
			.attr("stroke-width", 1)
			.attr("fill", d => {
				let color = d3.color(d.color);
				color.opacity = color.opacity / 6;
				return color.toString();
			})
			// .attr("stroke-linecap", "round")
			.attr("x", d => Math.max(-thickness, this.scaleX(d.x1) - (thickness / 2)))
			.attr("width", d => {
				// this whole min max mess is necessary to make the rectangles not get overly large and thereby control where the tooltip
				// will appear (should be at the middle of the _visible_ portion of the rectangle)
				const x1 = Math.max(-thickness, this.scaleX(d.x1) - (thickness / 2));
				const x2 = Math.min(this.scaleX.range()[1] + thickness, this.scaleX(d.x2) - (thickness / 2));
				return Math.max(0, x2 - x1 + thickness);
			})
			.attr("y", d => this.scaleY(d.y) - (thickness / 2))
			.attr("rx", (thickness / 2))
			.attr("ry", (thickness / 2))
			.attr("height", d => thickness)
			.on("mouseenter", mouseenter)
			.on("mouseleave", mouseleave);

		this.$main
			.selectAll("line.incident-line")
			.data(data.dataPoints)
			.join("line")
			.classed("incident-line", true)
			.attr("stroke", d => d.color)
			.attr("stroke-width", 1)
			.attr("stroke-linecap", "round")
			.attr("x1", d => this.scaleX(d.x1))
			.attr("x2", d => this.scaleX(d.x2))
			.attr("y1", d => this.scaleY(d.y))
			.attr("y2", d => this.scaleY(d.y));

		this.$main
			.selectAll("circle.incident-dot")
			.data(data.dataPoints.flatMap(d => [{x: d.x1, y: d.y, color: d.color}, {x: d.x2, y: d.y, color: d.color}]))
			.join("circle")
			.classed("incident-dot", true)
			.attr("fill", d => d.color)
			.attr("cx", d => this.scaleX(d.x))
			.attr("cy", d => this.scaleY(d.y))
			.attr("r", "2");
	}


	destroy() {
		super.destroy();
		this.popperHandle.destroy();
	}
}



