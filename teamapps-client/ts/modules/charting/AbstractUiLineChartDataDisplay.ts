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
import {ScaleContinuousNumeric, ScaleTime} from "d3-scale";
import {SVGSelection} from "./Charting";
import {UiLineChartYScaleZoomMode} from "../../generated/UiLineChartYScaleZoomMode";
import * as d3 from "d3";
import {NamespaceLocalObject} from "d3";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiScaleType} from "../../generated/UiScaleType";
import {UiLineChartDataDisplay} from "./UiLineChartDataDisplay";
import {YAxis} from "./YAxis";

export abstract class AbstractUiLineChartDataDisplay<C extends AbstractUiLineChartDataDisplayConfig = AbstractUiLineChartDataDisplayConfig>
	implements UiLineChartDataDisplay<C> {

	protected config: C;

	protected scaleY: ScaleContinuousNumeric<number, number>;
	private yAxis: YAxis;

	protected zoomLevelIndex: number;
	protected scaleX: ScaleTime<number, number>;
	protected $main: SVGSelection<any>;

	constructor(
		config: C,
		protected timeGraphId: string,
		protected dataStore: TimeGraphDataStore
	) {
		this.yAxis = new YAxis(config.yAxisColor);
		this.setConfig(config)
		this.$main = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement)
			.attr("data-series-id", `${this.timeGraphId}-${this.config.id}`);
	}

	getMainSelection() {
		return this.$main;
	}

	public updateZoomX(zoomLevelIndex: number, scaleX: ScaleTime<number, number>) {
		this.zoomLevelIndex = zoomLevelIndex;
		this.scaleX = scaleX;
	}

	public redraw() {
		let yRange = this.getScaleYRangeOrNull();

		if (yRange != null && (yRange.minY !== this.scaleY.domain()[0] || yRange.maxY !== this.scaleY.domain()[1])) {
			d3.transition(`${this.timeGraphId}-${this.config.id}-zoomYToDisplayedDomain`)
				.ease(d3.easeLinear)
				.duration(300)
				.tween(`${this.timeGraphId}-${this.config.id}-zoomYToDisplayedDomain`, () => {
					// create interpolator and do not show nasty floating numbers
					let intervalInterpolator = d3.interpolateArray(this.scaleY.domain(), [yRange.minY, yRange.maxY]);
					return (t: number) => {
						this.scaleY.domain(intervalInterpolator(t));
						this.yAxis.draw();
						this.doRedraw();
					}
				});
		} else {
			this.yAxis.draw();
			this.doRedraw();
		}
	}

	private getScaleYRangeOrNull() {
		let minY: number, maxY: number;

		function crossesZero(bound: number, margin: number) {
			return Math.sign(bound + margin) !== Math.sign(bound);
		}

		if (this.config.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC) {
			let displayedDataYBounds = this.getDisplayedDataYBoundsOrNull();
			if (displayedDataYBounds == null) {
				return null;
			}
			let margin = (displayedDataYBounds[1] - displayedDataYBounds[0]) * .05;
			minY = crossesZero(displayedDataYBounds[0], -margin) ? displayedDataYBounds[0] : displayedDataYBounds[0] - margin;
			maxY = crossesZero(displayedDataYBounds[1], margin) ? displayedDataYBounds[1] : displayedDataYBounds[1] + margin;
		} else if (this.config.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO) {
			let displayedDataYBounds = this.getDisplayedDataYBoundsOrNull();
			if (displayedDataYBounds == null) {
				return null;
			}
			let margin = (displayedDataYBounds[1] - displayedDataYBounds[0]) * .05;
			minY = displayedDataYBounds[0] >= 0 ? 0 : displayedDataYBounds[0] - margin;
			maxY = displayedDataYBounds[1] <= 0 ? 0 : displayedDataYBounds[1] + margin;
		} else {
			minY = this.config.intervalY.min;
			maxY = this.config.intervalY.max;
		}
		minY = minY;
		return {minY, maxY};
	}

	private updateYScale(): void {
		const oldScaleY = this.scaleY;
		if (this.config.yScaleType === UiScaleType.SYMLOG) {
			this.scaleY = d3.scaleSymlog();
		} else if (this.config.yScaleType === UiScaleType.LOG10) {
			this.scaleY = d3.scaleLog();
		} else {
			this.scaleY = d3.scaleLinear();
		}
		if (oldScaleY != null) {
			this.scaleY.range(oldScaleY.range());
		}
		this.yAxis.setScale(this.scaleY);
	}

	setYRange(range: [number, number]) {
		this.scaleY.range(range);
		this.yAxis.setScale(this.scaleY);
	}

	protected abstract doRedraw(): void;

	public setConfig(config: C) {
		this.config = config;
		this.updateYScale();
	}

	protected getDisplayedData() {
		const zoomBoundsX = [+(this.scaleX.domain()[0]), +(this.scaleX.domain()[1])];
		return this.dataStore.getData(this.getDataSeriesIds(), this.zoomLevelIndex, zoomBoundsX[0], zoomBoundsX[1]);
	}

	public abstract getDataSeriesIds(): string[];

	public destroy(): void {
		this.yAxis.getSelection().remove();
	}

	private getDisplayedDataYBoundsOrNull(): [number, number] {
		let bounds = d3.extent(this.getDataSeriesIds()
			.flatMap(dataSeriesId => this.getDisplayedData()[dataSeriesId])
			.map(dataPoint => dataPoint.y));
		if (bounds[0] === undefined || bounds[1] === undefined) {
			bounds = null;
		} else {
			return bounds;
		}
	}

	getConfig() {
		return this.config;
	}

	getYAxis(): YAxis {
		return this.yAxis;
	}

}
