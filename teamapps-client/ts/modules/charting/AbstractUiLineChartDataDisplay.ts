import {ScaleContinuousNumeric, ScaleTime} from "d3-scale";
import {fakeZeroIfLogScale, SVGGSelection} from "./Charting";
import {UiLineChartYScaleZoomMode} from "../../generated/UiLineChartYScaleZoomMode";
import * as d3 from "d3";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {createUiColorCssString} from "../util/CssFormatUtil";
import {NamespaceLocalObject} from "d3";
import {Axis} from "d3";
import {UiScaleType} from "../../generated/UiScaleType";
import {yTickFormat} from "./UiTimeGraph";

export abstract class AbstractUiLineChartDataDisplay<C extends AbstractUiLineChartDataDisplayConfig = AbstractUiLineChartDataDisplayConfig> {

	public scaleY: ScaleContinuousNumeric<number, number>;
	public $yAxis: SVGGSelection;
	private yAxis: Axis<number | { valueOf(): number }>;

	constructor(
		protected config: C,
		protected timeGraphId: string,
		protected dataStore: TimeGraphDataStore
	) {
		this.$yAxis = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement);
		this.yAxis = d3.axisLeft(null);
		this.updateYScale();
	}

	protected zoomLevelIndex: number;
	protected scaleX: ScaleTime<number, number>;

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
						this.redrawYAxis();
						this.doRedraw();
					}
				});
		} else {
			this.redrawYAxis();
			this.doRedraw();
		}
	}

	updateYScale(): void {
		const oldScaleY = this.scaleY;
		if (this.config.yScaleType === UiScaleType.LOG10) {
			this.scaleY = d3.scaleLog();
		} else {
			this.scaleY = d3.scaleLinear();
		}
		if (oldScaleY != null) {
			this.scaleY.range(oldScaleY.range());
		}
		let domainMin = fakeZeroIfLogScale(this.config.intervalY.min, this.config.yScaleType);
		this.scaleY.domain([domainMin, this.config.intervalY.max]);
		this.yAxis.scale(this.scaleY);
	}

	protected abstract doRedraw(): void;

	public updateYAxisTickFormat() {
		let availableHeight = Math.abs(this.scaleY.range()[1] - this.scaleY.range()[0]);
		let minY = this.scaleY.domain()[0];
		let maxY = this.scaleY.domain()[1];
		let delta = maxY - minY;
		let numberOfYTickGroups = Math.log10(delta) + 1;
		let heightPerYTickGroup = availableHeight / numberOfYTickGroups;

		if (this.config.yScaleType === UiScaleType.LOG10) {
			this.yAxis.tickFormat((value: number, i: number) => {
				if (value < 1) {
					return "";
				} else {
					if (heightPerYTickGroup >= 150) {
						return yTickFormat(value);
					} else if (heightPerYTickGroup >= 80) {
						let firstDigitOfValue = Number(("" + value)[0]);
						return firstDigitOfValue <= 5 ? yTickFormat(value) : "";
					} else if (heightPerYTickGroup >= 30) {
						let firstDigitOfValue = Number(("" + value)[0]);
						return firstDigitOfValue === 1 || firstDigitOfValue === 5 ? yTickFormat(value) : "";
					} else {
						let firstDigitOfValue = Number(("" + value)[0]);
						return firstDigitOfValue === 1 ? yTickFormat(value) : "";
					}
				}
			});
		} else {
			this.yAxis.tickFormat((domainValue: number) => {
				if (delta < 2) {
					return d3.format("-,.4r")(domainValue)
				} else {
					return yTickFormat(domainValue)
				}
			})
				.ticks(availableHeight / 20);
		}
	}

	private redrawYAxis() {
		this.updateYAxisTickFormat();
		this.$yAxis.call(this.yAxis);

		let $ticks = this.$yAxis.node().querySelectorAll('.tick');
		for (let i = 0; i < $ticks.length; i++) {
			let $text: SVGTextElement = $ticks[i].querySelector("text");
			let querySelector: any = $ticks[i].querySelector('line');
			if ($text.innerHTML === '') {
				querySelector.setAttribute("visibility", 'hidden');
			} else {
				querySelector.setAttribute("visibility", 'visible');
			}
		}

		this.$yAxis.style("color", createUiColorCssString(this.config.yAxisColor));
	}

	public setConfig(config: C) {
		this.config = config;
		this.updateYScale();
	}

	protected getDisplayedData() {
		const zoomBoundsX = [+(this.scaleX.domain()[0]), +(this.scaleX.domain()[1])];
		return this.dataStore.getData(this.getDataSeriesIds(), this.zoomLevelIndex, zoomBoundsX[0], zoomBoundsX[1]);
	}

	protected abstract getDataSeriesIds(): string[];

	public abstract destroy(): void;

	public getDisplayedDataYBounds(): [number, number] {
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
			bounds[0] = 0;
			bounds[1] = 1;
		}
		return bounds;
	}
}