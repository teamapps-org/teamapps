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
import {SVGSelection} from "./Charting";
import {AbstractUiGraph} from "./AbstractUiGraph";
import {UiTimeGraph} from "./UiTimeGraph";
import {ScaleTime} from 'd3';
import {UiLineChartYScaleZoomMode} from "../../generated/UiLineChartYScaleZoomMode";
import {UiGraphGroupConfig} from "../../generated/UiGraphGroupConfig";
import {UiGraphGroupDataConfig} from "../../generated/UiGraphGroupDataConfig";
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import * as d3 from "d3";
import {GraphContext} from "./GraphContext";

export class UiGraphGroup extends AbstractUiGraph<UiGraphGroupConfig, UiGraphGroupDataConfig> {

	private $yZeroLine: SVGSelection;
	private graphs = new Map<String, AbstractUiGraph>();

	constructor(
		timeGraphId: string,
		config: UiGraphGroupConfig,
		private dropShadowFilterId: string,
		graphContext: GraphContext
	) {
		super(config, timeGraphId);

		this.$main.classed("data-display-group", true)
			.attr("data-series-ids", `${this.timeGraphId}-${this.config.id}`);
		this.initDomNodes();

		this.config.graphs.forEach(graphConfig => {
			graphConfig.yScaleZoomMode = UiLineChartYScaleZoomMode.FIXED;
			this.graphs.set(graphConfig.id, UiTimeGraph.createDataDisplay(timeGraphId, graphConfig, dropShadowFilterId, graphContext));
		})

		this.graphs.forEach(dd => this.$main.node().append(dd.getMainSelection().node()));
	}

	addData(zoomLevel: number, intervalX: UiLongIntervalConfig, data: UiGraphGroupDataConfig): void {
		for(let [graphId, graphData] of Object.entries(data.graphDataByGraphId)) {
			this.graphs.get(graphId).addData(zoomLevel, intervalX, graphData);
		}
	}

	public getYDataBounds(xInterval: [number, number]): [number, number] {
		return d3.extent(Array.of(...this.graphs.values())
			.flatMap(graph => graph.getYDataBounds(xInterval)));
	}

	resetData(): void {
	}

	updateZoomX(zoomLevelIndex: number, scaleX: ScaleTime<number, number>) {
		this.graphs.forEach(dd => dd.updateZoomX(zoomLevelIndex, scaleX));
		super.updateZoomX(zoomLevelIndex, scaleX);
	}

	private initDomNodes() {
		this.$yZeroLine = this.$main.append<SVGLineElement>("line")
			.classed("y-zero-line", true);
	}

	protected doRedraw() {
		this.$yZeroLine
			.attr("x1", 0)
			.attr("y1", this.scaleY(0))
			.attr("x2", this.scaleX.range()[1])
			.attr("y2", this.scaleY(0))
			.attr("stroke", this.config.yAxisColor)
			.attr("visibility", (this.config.yZeroLineVisible && this.scaleY.domain()[0] !== 0) ? "visible" : "hidden");

		this.graphs.forEach(dd => {
			dd.getConfig().intervalY = {min: this.scaleY.domain()[0], max: this.scaleY.domain()[1]};
			dd.redraw()
		});
	}

	setYRange(range: [number, number]) {
		super.setYRange(range);
		this.graphs.forEach(dd => {
			dd.setYRange(range);
		});
	}

}



