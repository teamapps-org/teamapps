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
import {fakeZeroIfLogScale, SVGSelection} from "./Charting";
import {UiLineChartYScaleZoomMode} from "../../generated/UiLineChartYScaleZoomMode";
import * as d3 from "d3";
import {Axis, NamespaceLocalObject} from "d3";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiScaleType} from "../../generated/UiScaleType";
import {yTickFormat} from "./UiTimeGraph";
import {UiLineChartDataDisplay, YAxis} from "./UiLineChartDataDisplay";

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
		this.yAxis = new YAxis(config.yScaleType, config.yAxisColor);
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
		let yRange = this.getYAxisRangeOrNull();

		if (yRange != null && (yRange.minY !== this.scaleY.domain()[0] || yRange.maxY !== this.scaleY.domain()[1])) {
			d3.transition(`${this.timeGraphId}-${this.config.id}-zoomYToDisplayedDomain`)
				.ease(d3.easeLinear)
				.duration(300)
				.tween(`${this.timeGraphId}-${this.config.id}-zoomYToDisplayedDomain`, () => {
					// create interpolator and do not show nasty floating numbers
					let intervalInterpolator = d3.interpolateArray(this.scaleY.domain(), [yRange.minY, yRange.maxY]);
					return (t: number) => {
						this.scaleY.domain(intervalInterpolator(t));
						this.yAxis.setScaleType(this.config.yScaleType);
						this.yAxis.draw();
						this.doRedraw();
					}
				});
		} else {
			this.yAxis.setScaleType(this.config.yScaleType);
			this.yAxis.draw();
			this.doRedraw();
		}
	}

	private getYAxisRangeOrNull() {
		let minY: number, maxY: number;
		if (this.config.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC) {
			let displayedDataYBounds = this.getDisplayedDataYBoundsOrNull();
			if (displayedDataYBounds == null) {
				return null;
			}
			let delta = displayedDataYBounds[1] - displayedDataYBounds[0];
			minY = displayedDataYBounds[0] - delta * .05;
			maxY = displayedDataYBounds[1] + delta * .05;
		} else if (this.config.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO) {
			let displayedDataYBounds = this.getDisplayedDataYBoundsOrNull();
			if (displayedDataYBounds == null) {
				return null;
			}
			minY = displayedDataYBounds[0] > 0 ? 0 : displayedDataYBounds[0];
			maxY = displayedDataYBounds[1] < 0 ? 0 : displayedDataYBounds[1];
		} else {
			minY = this.config.intervalY.min;
			maxY = this.config.intervalY.max;
		}
		minY = fakeZeroIfLogScale(minY, this.config.yScaleType);
		return {minY, maxY};
	}

	private updateYScale(): void {
		const oldScaleY = this.scaleY;
		if (this.config.yScaleType === UiScaleType.LOG10) {
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
		this.yAxis.setScaleType(config.yScaleType);
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
		let bounds = this.getDataSeriesIds().map(dataSeriesId => {
			let displayedData = this.getDisplayedData()[dataSeriesId];
			let minY = Number.POSITIVE_INFINITY;
			let maxY = Number.NEGATIVE_INFINITY;
			displayedData.forEach(d => {
				if (d.y < minY) {
					minY = d.y;
				}
				if (d.y > maxY) {
					maxY = d.y;
				}
			});
			return [minY, maxY];
		}).reduce((globalMinMax, currentMinMax) => {
			if (currentMinMax[0] < globalMinMax[0]) {
				globalMinMax[0] = currentMinMax[0];
			}
			if (currentMinMax[1] > globalMinMax[1]) {
				globalMinMax[1] = currentMinMax[1];
			}
			return globalMinMax;
		}, [Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY]) as [number, number];
		if (bounds[0] === Number.POSITIVE_INFINITY && bounds[1] === Number.NEGATIVE_INFINITY) {
			bounds = null;
		}
		return bounds;
	}

	getConfig() {
		return this.config;
	}

	getYAxis(): YAxis {
		return this.yAxis;
	}

}
