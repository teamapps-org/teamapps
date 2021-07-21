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
import {SVGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiLineChartDataDisplayGroupConfig} from "../../generated/UiLineChartDataDisplayGroupConfig";
import {UiTimeGraph} from "./UiTimeGraph";
import {ScaleTime} from 'd3';
import {UiLineChartYScaleZoomMode} from "../../generated/UiLineChartYScaleZoomMode";

export class UiLineChartDataDisplayGroup extends AbstractUiLineChartDataDisplay<UiLineChartDataDisplayGroupConfig> {

	private $yZeroLine: SVGSelection;
	private dataDisplays: AbstractUiLineChartDataDisplay[];

	constructor(
		timeGraphId: string,
		config: UiLineChartDataDisplayGroupConfig,
		private dropShadowFilterId: string,
		dataStore: TimeGraphDataStore
	) {
		super(config, timeGraphId, dataStore);

		this.$main.classed("data-display-group", true)
			.attr("data-series-ids", `${this.timeGraphId}-${this.config.id}`);
		this.initDomNodes();

		this.config.dataDisplays.forEach(ddConfig => ddConfig.yScaleZoomMode = UiLineChartYScaleZoomMode.FIXED)
		this.dataDisplays = this.config.dataDisplays
			.map(ddConfig => UiTimeGraph.createDataDisplay(timeGraphId, ddConfig, dropShadowFilterId, dataStore) as AbstractUiLineChartDataDisplay);

		this.dataDisplays.forEach(dd => this.$main.node().append(dd.getMainSelection().node()));
	}

	updateZoomX(zoomLevelIndex: number, scaleX: ScaleTime<number, number>) {
		this.dataDisplays.forEach(dd => dd.updateZoomX(zoomLevelIndex, scaleX));
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

		this.dataDisplays.forEach(dd => {
			dd.getConfig().intervalY = {min: this.scaleY.domain()[0], max: this.scaleY.domain()[1]};
			dd.redraw()
		});
	}

	setYRange(range: [number, number]) {
		super.setYRange(range);
		this.dataDisplays.forEach(dd => {
			dd.setYRange(range);
		});
	}

	setConfig(lineFormat: UiLineChartDataDisplayGroupConfig) {
		super.setConfig(lineFormat);
	}

	public getDataSeriesIds(): string[] {
		return this.dataDisplays
			.map(dd => dd.getDataSeriesIds())
			.reduce((x, y) => x.concat(y), []);
	}

	public destroy() {
		this.$main.remove();
	}
}



