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
import {Area, Line} from "d3-shape";
import {Selection} from "d3-selection";
import * as d3 from "d3";
import {NamespaceLocalObject, ScaleLinear} from "d3";
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {UiLineChartLineConfig} from "../../generated/UiLineChartLineConfig";
import {CurveTypeToCurveFactory, DataPoint, fakeZeroIfLogScale, SVGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {isVisibleColor} from "../Common";

export class UiLineChartLine extends AbstractUiLineChartDataDisplay<UiLineChartLineConfig> {
	private line: Line<DataPoint>;
	private $line: SVGSelection<any>;
	private area: Area<DataPoint>;
	private $area: SVGSelection<any>;
	private $dots: SVGSelection<any>;
	private $defs: SVGSelection<any>;
	private colorScale: ScaleLinear<string, string>;

	private $yZeroLine: SVGSelection<any>;

	constructor(
		timeGraphId: string,
		config: UiLineChartLineConfig,
		private dropShadowFilterId: string,
		dataStore: TimeGraphDataStore
	) {
		super(config, timeGraphId, dataStore);

		this.initLinesAndColorScale();
		this.initDomNodes();
	}

	private initLinesAndColorScale() {
		this.area = d3.area<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.config.graphType]);
		this.line = d3.line<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.config.graphType]);
		this.colorScale = d3.scaleLinear<string, string>()
			.range([this.config.lineColorScaleMin, this.config.lineColorScaleMax]);
	}

	private initDomNodes() {
		this.$area = this.$main.append<SVGPathElement>("path")
			.classed("area", true)
			.attr("fill", `url('#area-gradient-${this.timeGraphId}-${this.config.id}')`);
		this.$line = this.$main.append<SVGPathElement>("path")
			.classed("line", true)
			.attr("stroke", `url('#line-gradient-${this.timeGraphId}-${this.config.id}')`);
		this.$yZeroLine = this.$main.append<SVGLineElement>("line")
			.classed("y-zero-line", true);

		// .style("filter", `url("#${this.dropShadowFilterId}")`);
		this.$dots = this.$main.append<SVGGElement>("g")
			.classed("dots", true);

		this.$defs = this.$main.append<SVGDefsElement>("defs")
			.html(`<linearGradient class="line-gradient" id="line-gradient-${this.timeGraphId}-${this.config.id}" x1="0" x2="0" y1="0" y2="100" gradientUnits="userSpaceOnUse">
	    </linearGradient>
	    <linearGradient class="area-gradient" id="area-gradient-${this.timeGraphId}-${this.config.id}" x1="0" x2="0" y1="0" y2="100" gradientUnits="userSpaceOnUse">
	    </linearGradient>`);
	}

	public doRedraw() {
		this.colorScale.domain(this.scaleY.domain());
		this.$defs.select(".line-gradient")
			.attr("y2", this.scaleY.range()[0])
			.selectAll("stop")
			.data([this.config.lineColorScaleMax, this.config.lineColorScaleMin])
			.join("stop")
			.attr("stop-color", d => d)
			.attr("offset", (datum, index) => index);
		this.$defs.select(".area-gradient")
			.attr("y2", this.scaleY.range()[0])
			.selectAll("stop")
			.data([this.config.areaColorScaleMax, this.config.areaColorScaleMin])
			.join("stop")
			.attr("stop-color", d => d)
			.attr("offset", (datum, index) => index);

		let data = this.getDisplayedData()[this.config.dataSeriesId];
		this.line
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
		this.$line.attr("d", this.line(data));

		if (isVisibleColor(this.config.areaColorScaleMin) || isVisibleColor(this.config.areaColorScaleMax)) {
			this.area
				.x(d => this.scaleX(d.x))
				.y0(this.scaleY.range()[0])
				.y1(d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
			this.$area.attr("d", this.area(data));
		}

		let $dotsDataSelection = this.$dots.selectAll<SVGCircleElement, UiTimeGraphDataPointConfig>("circle.dot")
			.data(this.config.dataDotRadius > 0 ? data : [])
			.join("circle")
			.classed("dot", true)
			.attr("cx", d => this.scaleX(d.x))
			.attr("cy", d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)))
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

	setConfig(lineFormat: UiLineChartLineConfig) {
		super.setConfig(lineFormat);
		this.initLinesAndColorScale();
	}

	public getDataSeriesIds(): string[] {
		return [this.config.dataSeriesId];
	}

	public destroy() {
		this.$main.remove();
	}
}
