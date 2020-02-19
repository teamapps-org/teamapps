/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import {createUiColorCssString} from "../util/CssFormatUtil";
import {CurveTypeToCurveFactory, DataPoint, fakeZeroIfLogScale, SVGGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiLineChartBandConfig} from "../../generated/UiLineChartBandConfig";
import {D3Area2} from "./D3Area2";
import {UiColorConfig} from "../../generated/UiColorConfig";

export class UiLineChartBand extends AbstractUiLineChartDataDisplay<UiLineChartBandConfig> {
	private middleLine: Line<DataPoint>;
	private $middleLine: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private lowerLine: Line<DataPoint>;
	private $lowerLine: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private upperLine: Line<DataPoint>;
	private $upperLine: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private area: D3Area2<DataPoint>;
	private $area: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private $dots: d3.Selection<SVGGElement, {}, HTMLElement, undefined>;
	private $main: Selection<SVGGElement, {}, HTMLElement, undefined>;

	private $yZeroLine: Selection<SVGLineElement, {}, HTMLElement, undefined>;

	constructor(
		timeGraphId: string,
		config: UiLineChartBandConfig,
		$container: SVGGSelection, // TODO append outside!! https://stackoverflow.com/a/19951169/524913
		private dropShadowFilterId: string,
		dataStore: TimeGraphDataStore
	) {
		super(config, timeGraphId, dataStore);

		this.$main = $container
			.append<SVGGElement>("g")
			.attr("data-series-id", `${this.timeGraphId}-${this.config.id}`);
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
	
	private isVisibleColor(c: UiColorConfig) {
		return c != null && c.alpha > 0; 
	}

	public doRedraw() {
		let lineData = this.isVisibleColor(this.config.middleLineColor) ? this.getDisplayedData()[this.config.middleLineDataSeriesId] : [];
		let areaDataMin = this.isVisibleColor(this.config.lowerLineColor) ? this.getDisplayedData()[this.config.lowerBoundDataSeriesId] : [];
		let areaDataMax = this.isVisibleColor(this.config.upperLineColor) ? this.getDisplayedData()[this.config.upperBoundDataSeriesId] : [];

		this.middleLine
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
		this.$middleLine
			.attr("d", this.middleLine(lineData))
			.attr("stroke", createUiColorCssString(this.config.middleLineColor));
		
		this.lowerLine
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
		this.$lowerLine
			.attr("d", this.lowerLine(areaDataMin))
			.attr("stroke", createUiColorCssString(this.config.lowerLineColor));
		
		this.upperLine
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
		this.$upperLine
			.attr("d", this.upperLine(areaDataMax))
			.attr("stroke", createUiColorCssString(this.config.upperLineColor));


		this.area
			.x(d => {
				return this.scaleX(d.x)
			})
			.y((d, index) => {
				return this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType))
			});
		this.$area
			.attr("d", this.area.writePath(areaDataMin, areaDataMax))
			.attr("fill", createUiColorCssString(this.config.areaColor));

		let $dotsDataSelection = this.$dots.selectAll<SVGCircleElement, UiTimeGraphDataPointConfig>("circle.dot")
			.data(this.config.dataDotRadius > 0 ? lineData : [])
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
			.attr("stroke", createUiColorCssString(this.config.yAxisColor))
			.attr("visibility", (this.config.yZeroLineVisible && this.scaleY.domain()[0] !== 0) ? "visible" : "hidden");
	}

	setConfig(lineFormat: UiLineChartBandConfig) {
		super.setConfig(lineFormat);
		this.initLinesAndColorScale();
		this.redraw();
	}

	public get yScaleWidth(): number {
		return (this.config.intervalY.max > 10000 || this.config.intervalY.min < 10) ? 37
			: (this.config.intervalY.max > 100) ? 30
				: 25;
	}

	public getDataSeriesIds(): string[] {
		return [this.config.lowerBoundDataSeriesId, this.config.middleLineDataSeriesId, this.config.upperBoundDataSeriesId];
	}

	public destroy() {
		this.$main.remove();
		this.$yAxis.remove();
	}
}



