import {ScaleContinuousNumeric, ScaleTime} from "d3-scale";
import {fakeZeroIfLogScale, SVGGSelection} from "./Charting";
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {UiLineChartYScaleZoomMode} from "../../generated/UiLineChartYScaleZoomMode";
import * as d3 from "d3";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";

export abstract class AbstractUiLineChartDataDisplay<C extends AbstractUiLineChartDataDisplayConfig = AbstractUiLineChartDataDisplayConfig> {
	constructor(
		protected config: C,
		protected timeGraphId: string
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

	public abstract addData(zoomLevel: number, intervalX: UiLongIntervalConfig, data: UiTimeGraphDataPointConfig[]): void;


	public abstract getDisplayedDataYBounds(): [number, number];

	public abstract resetData(numberOfZoomLevels: number): any;

	public setConfig(config: C) {
		this.config = config;
	}


	public abstract destroy(): void;
}