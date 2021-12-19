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
import * as d3 from "d3";
import {CurveTypeToCurveFactory, DataPoint, SVGSelection} from "./Charting";
import {AbstractUiGraph} from "./AbstractUiGraph";
import {LineGraphDataStore} from "./DataStore";
import {D3Area2} from "./D3Area2";
import {isVisibleColor} from "../Common";
import {UiHoseGraphConfig} from "../../generated/UiHoseGraphConfig";
import {UiHoseGraphDataConfig} from "../../generated/UiHoseGraphDataConfig";
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {IntervalManager} from "../util/IntervalManager";
import {UiLineGraphDataPointConfig} from "../../generated/UiLineGraphDataPointConfig";
import * as log from "loglevel";

export class UiHoseGraph extends AbstractUiGraph<UiHoseGraphConfig, UiHoseGraphDataConfig> {

	private static logger: log.Logger = log.getLogger("UiHoseGraph");

	private middleLine: Line<DataPoint>;
	private $middleLine: SVGSelection<any>;
	private lowerLine: Line<DataPoint>;
	private $lowerLine: SVGSelection<any>;
	private upperLine: Line<DataPoint>;
	private $upperLine: SVGSelection<any>;
	private area: D3Area2<DataPoint>;
	private $area: SVGSelection<any>;
	private $dots: SVGSelection<any>;

	private $yZeroLine: SVGSelection<any>;

	private upperLineDataStore = new LineGraphDataStore();
	private middleLineDataStore = new LineGraphDataStore();
	private lowerLineDataStore = new LineGraphDataStore();

	private get dataStores() {
		return [this.upperLineDataStore, this.middleLineDataStore, this.lowerLineDataStore];
	}

	constructor(
		timeGraphId: string,
		config: UiHoseGraphConfig,
		private dropShadowFilterId: string
	) {
		super(config, timeGraphId);
		this.$main.classed("hose-graph", true);
		this.initLinesAndColorScale();
		this.initDomNodes();
	}

	getUncoveredIntervals(zoomLevel: number, interval: [number, number]): [number, number][] {
		let uncoveredIntervalManager = new IntervalManager();
		this.dataStores.forEach(ds => uncoveredIntervalManager.addIntervals(ds.getUncoveredIntervals(zoomLevel, interval)));
		return uncoveredIntervalManager.getCoveredIntervals(); // !!
	}

	markIntervalAsCovered(zoomLevel: number, interval: [number, number]): void {
		this.dataStores.forEach(ds => ds.markIntervalAsCovered(zoomLevel, interval));
	}

	public addData(zoomLevel: number, data: UiHoseGraphDataConfig): void {
		if (data.upperLineData != null) {
			this.upperLineDataStore.addData(zoomLevel, data.upperLineData);
		}
		if (data.middleLineData != null) {
			this.middleLineDataStore.addData(zoomLevel, data.middleLineData);
		}
		if (data.lowerLineData != null) {
			this.lowerLineDataStore.addData(zoomLevel, data.lowerLineData);
		}
	}

	public resetData(): void {
		this.dataStores.forEach(ds => ds.reset());
	}

	public getYDataBounds(xInterval: [number, number]): [number, number] {
		return d3.extent(this.dataStores
			.map(ds => ds.getData(this.zoomLevelIndex, xInterval).dataPoints)
			.flat()
			.map(dataPoint => dataPoint.y));
	}

	private initLinesAndColorScale() {
		this.area = new D3Area2<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.config.graphType]);
		this.middleLine = d3.line<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.config.graphType]);
		this.lowerLine = d3.line<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.config.graphType]);
		this.upperLine = d3.line<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.config.graphType]);

	}

	private initDomNodes() {
		this.$area = this.$main.append<SVGPathElement>("path")
			.classed("area", true);
		this.$middleLine = this.$main.append<SVGPathElement>("path")
			.classed("line", true);
		this.$lowerLine = this.$main.append<SVGPathElement>("path")
			.classed("line", true);
		this.$upperLine = this.$main.append<SVGPathElement>("path")
			.classed("line", true);

		this.$yZeroLine = this.$main.append<SVGLineElement>("line")
			.classed("y-zero-line", true);

		// .style("filter", `url("#${this.dropShadowFilterId}")`);
		this.$dots = this.$main.append<SVGGElement>("g")
			.classed("dots", true);
	}

	public doRedraw() {
		let areaDataMax: UiLineGraphDataPointConfig[];
		if (isVisibleColor(this.config.upperLineColor)) {
			areaDataMax = this.upperLineDataStore.getData(this.zoomLevelIndex, this.getDisplayedIntervalX()).dataPoints;
		} else {
			UiHoseGraph.logger.info("NOT drawing upperLine, since line color is invisible!")
			areaDataMax = [];
		}
		let lineData: UiLineGraphDataPointConfig[];
		if (isVisibleColor(this.config.middleLineColor)) {
			lineData = this.middleLineDataStore.getData(this.zoomLevelIndex, this.getDisplayedIntervalX()).dataPoints;
		} else {
			UiHoseGraph.logger.info("NOT drawing middleLine, since line color is invisible!")
			lineData = [];
		}
		let areaDataMin: UiLineGraphDataPointConfig[];
		if (isVisibleColor(this.config.lowerLineColor)) {
			areaDataMin = this.lowerLineDataStore.getData(this.zoomLevelIndex, this.getDisplayedIntervalX()).dataPoints;
		} else {
			UiHoseGraph.logger.info("NOT drawing lowerLine, since line color is invisible!")
			areaDataMin = [];
		}

		this.middleLine
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(d.y));
		this.$middleLine
			.attr("d", this.middleLine(lineData))
			.attr("stroke", this.config.middleLineColor);

		this.lowerLine
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(d.y));
		this.$lowerLine
			.attr("d", this.lowerLine(areaDataMin))
			.attr("stroke", this.config.lowerLineColor);

		this.upperLine
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(d.y));
		this.$upperLine
			.attr("d", this.upperLine(areaDataMax))
			.attr("stroke", this.config.upperLineColor);


		this.area
			.x(d => {
				return this.scaleX(d.x)
			})
			.y((d, index) => {
				return this.scaleY(d.y)
			});
		this.$area
			.attr("d", this.area.writePath(areaDataMin, areaDataMax))
			.attr("fill", this.config.areaColor ?? "#00000000");

		let $dotsDataSelection = this.$dots.selectAll<SVGCircleElement, UiHoseGraphDataConfig>("circle.dot")
			.data(this.config.dataDotRadius > 0 ? lineData : [])
			.join("circle")
			.classed("dot", true)
			.attr("cx", d => this.scaleX(d.x))
			.attr("cy", d => this.scaleY(d.y))
			.attr("r", this.config.dataDotRadius);
		$dotsDataSelection.exit().remove();

		this.$yZeroLine
			.attr("x1", 0)
			.attr("y1", this.scaleY(0))
			.attr("x2", this.scaleX.range()[1])
			.attr("y2", this.scaleY(0))
			.attr("stroke", this.config.yAxisColor)
			.attr("visibility", (this.config.yZeroLineVisible && this.scaleY.domain()[0] !== 0) ? "visible" : "hidden");
	}

	setConfig(lineFormat: UiHoseGraphConfig) {
		super.setConfig(lineFormat);
		this.initLinesAndColorScale();
	}

}



