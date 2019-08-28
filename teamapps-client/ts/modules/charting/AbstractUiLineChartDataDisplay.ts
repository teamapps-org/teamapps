import {ScaleContinuousNumeric, ScaleTime} from "d3-scale";
import {fakeZeroIfLogScale, SVGGSelection} from "./Charting";
import {UiLineChartYScaleZoomMode} from "../../generated/UiLineChartYScaleZoomMode";
import * as d3 from "d3";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {TimeGraphDataStore} from "./TimeGraphDataStore";

export abstract class AbstractUiLineChartDataDisplay<C extends AbstractUiLineChartDataDisplayConfig = AbstractUiLineChartDataDisplayConfig> {
	constructor(
		protected config: C,
		protected timeGraphId: string,
		protected dataStore: TimeGraphDataStore
	) {

	}

	protected zoomLevelIndex: number;
	protected scaleX: ScaleTime<number, number>;

	abstract get scaleY(): ScaleContinuousNumeric<number, number>

	abstract get $yAxis(): SVGGSelection;

	abstract get yScaleWidth(): number;

	public updateZoomX(zoomLevelIndex: number, scaleX: ScaleTime<number, number>) {
		this.zoomLevelIndex = zoomLevelIndex;
		this.scaleX = scaleX;

		this.redraw();
	}

	public redraw() {
		let minY: number, maxY: number;
		if (this.config.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC) {
			let displayedDataYBounds = this.getDisplayedDataYBounds();
			let delta = displayedDataYBounds[1] - displayedDataYBounds[0];
			minY = displayedDataYBounds[0] - delta * .05;
			maxY = displayedDataYBounds[1] + delta * .05;
		} else if (this.config.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO) {
			[minY, maxY] = this.getDisplayedDataYBounds();
			if (minY > 0) {
				minY = 0;
			}
			if (maxY < 0) {
				maxY = 0;
			}
		} else {
			minY = this.config.intervalY.min;
			maxY = this.config.intervalY.max;
		}
		minY = fakeZeroIfLogScale(minY, this.config.yScaleType);

		if (minY !== this.scaleY.domain()[0] || maxY !== this.scaleY.domain()[1]) {
			if (Math.abs(this.scaleY.domain()[0]) > 1e15 || Math.abs(this.scaleY.domain()[1]) > 1e15) { // see https://github.com/d3/d3-interpolate/pull/63
				this.scaleY.domain([0, 1])
			}

			d3.transition(`${this.timeGraphId}-${this.config.id}-zoomYToDisplayedDomain`)
				.ease(d3.easeLinear)
				.duration(300)
				.tween(`${this.timeGraphId}-${this.config.id}-zoomYToDisplayedDomain`, () => {
					// create interpolator and do not show nasty floating numbers
					let intervalInterpolator = d3.interpolateArray(this.scaleY.domain().map(date => +date), [minY, maxY]);
					return (t: number) => {
						this.scaleY.domain(intervalInterpolator(t));
						this.doRedraw();
					}
				});
		} else {
			this.doRedraw()
		}
	}

	protected abstract doRedraw(): void;

	public setConfig(config: C) {
		this.config = config;
	}

	protected getDisplayedData() {
		const zoomBoundsX = [+(this.scaleX.domain()[0]), +(this.scaleX.domain()[1])];
		return this.dataStore.getData(this.getDataSourceIds(), this.zoomLevelIndex, zoomBoundsX[0], zoomBoundsX[1]);
	}

	protected abstract getDataSourceIds(): string[];

	public abstract destroy(): void;

	public getDisplayedDataYBounds(): [number, number] {
		return this.getDataSourceIds().map(dataSourceId => {
			let displayedData = this.getDisplayedData()[dataSourceId];
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
			if (minY === Number.POSITIVE_INFINITY && maxY === Number.NEGATIVE_INFINITY) {
				minY = 0;
				maxY = 1;
			}
			return [minY, maxY];
		}).reduce((previousValue, currentValue) => {
			if (currentValue[0] < previousValue[0]) {
				previousValue[0] = currentValue[0];
			}
			if (currentValue[1] > previousValue[1]) {
				previousValue[1] = currentValue[1];
			}
			return currentValue;
		}, [Number.POSITIVE_INFINITY, Number.NEGATIVE_INFINITY]) as [number, number];
	}
}