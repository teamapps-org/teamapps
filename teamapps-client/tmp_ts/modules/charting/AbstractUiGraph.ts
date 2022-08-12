/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
import {UiScaleType} from "../../generated/UiScaleType";
import {UiGraph} from "./UiGraph";
import {YAxis} from "./YAxis";
import {UiGraphConfig} from "../../generated/UiGraphConfig";
import {UiGraphDataConfig} from "../../generated/UiGraphDataConfig";
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {Interval} from "../util/IntervalManager";

export abstract class AbstractUiGraph<C extends UiGraphConfig = UiGraphConfig,
	D extends UiGraphDataConfig = UiGraphDataConfig>
	implements UiGraph<C, D> {

	protected config: C;

	protected scaleY: ScaleContinuousNumeric<number, number>;
	private yAxis: YAxis;

	protected zoomLevelIndex: number;
	protected scaleX: ScaleTime<number, number>;
	protected $main: SVGSelection<any>;

	constructor(
		config: C,
		protected timeGraphId: string
	) {
		this.yAxis = new YAxis(config.yAxisColor);
		this.setConfig(config)
		this.$main = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement)
			.attr("data-series-id", `${this.timeGraphId}-${this.config.id}`);
	}

	protected abstract doRedraw(): void;

	public abstract getUncoveredIntervals(zoomLevel: number, interval: [number, number]): [number, number][];

	public abstract markIntervalAsCovered(zoomLevel: number, interval: [number, number]): void;

	public abstract addData(zoomLevel: number, data: D): void;

	public abstract resetData(): void;

	public abstract getYDataBounds(xInterval: [number, number]): [number, number];

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

		let displayedDataYBounds = this.getYDataBounds(this.getDisplayedIntervalX());
		displayedDataYBounds = displayedDataYBounds[0] === undefined || displayedDataYBounds[1] === undefined ? [0, 1] : displayedDataYBounds;
		if (this.config.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC) {
			let margin = (displayedDataYBounds[1] - displayedDataYBounds[0]) * .05;
			minY = crossesZero(displayedDataYBounds[0], -margin) ? displayedDataYBounds[0] : displayedDataYBounds[0] - margin;
			maxY = crossesZero(displayedDataYBounds[1], margin) ? displayedDataYBounds[1] : displayedDataYBounds[1] + margin;
		} else if (this.config.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO) {
			let margin = (displayedDataYBounds[1] - displayedDataYBounds[0]) * .05;
			minY = displayedDataYBounds[0] >= 0 ? 0 : displayedDataYBounds[0] - margin;
			maxY = displayedDataYBounds[1] <= 0 ? 0 : displayedDataYBounds[1] + margin;
		} else {
			minY = this.config.intervalY.min;
			maxY = this.config.intervalY.max;
		}
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

	public setConfig(config: C) {
		this.config = config;
		this.updateYScale();
	}

	protected getDisplayedIntervalX(): [number, number] {
		return [+(this.scaleX.domain()[0]), +(this.scaleX.domain()[1])];
	}


	public destroy(): void {
		this.yAxis.getSelection().remove();
		this.$main.remove();
	}

	getConfig() {
		return this.config;
	}

	getYAxis(): YAxis {
		return this.yAxis;
	}

}
