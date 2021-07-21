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
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {CurveTypeToCurveFactory, DataPoint, SVGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiLineChartBandConfig} from "../../generated/UiLineChartBandConfig";
import {D3Area2} from "./D3Area2";
import {isVisibleColor} from "../Common";

export class UiLineChartBand extends AbstractUiLineChartDataDisplay<UiLineChartBandConfig> {
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

	constructor(
		timeGraphId: string,
		config: UiLineChartBandConfig,
		private dropShadowFilterId: string,
		dataStore: TimeGraphDataStore
	) {
		super(config, timeGraphId, dataStore);
		this.initLinesAndColorScale();
		this.initDomNodes();
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
		let lineData = isVisibleColor(this.config.middleLineColor) ? this.getDisplayedData()[this.config.middleLineDataSeriesId] : [];
		let areaDataMin = isVisibleColor(this.config.lowerLineColor) ? this.getDisplayedData()[this.config.lowerBoundDataSeriesId] : [];
		let areaDataMax = isVisibleColor(this.config.upperLineColor) ? this.getDisplayedData()[this.config.upperBoundDataSeriesId] : [];

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
			.attr("fill", this.config.areaColor);

		let $dotsDataSelection = this.$dots.selectAll<SVGCircleElement, UiTimeGraphDataPointConfig>("circle.dot")
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

	setConfig(lineFormat: UiLineChartBandConfig) {
		super.setConfig(lineFormat);
		this.initLinesAndColorScale();
	}

	public getDataSeriesIds(): string[] {
		return [this.config.lowerBoundDataSeriesId, this.config.middleLineDataSeriesId, this.config.upperBoundDataSeriesId];
	}

	public destroy() {
		this.$main.remove();
	}
}



