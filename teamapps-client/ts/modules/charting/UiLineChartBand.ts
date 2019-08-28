import {Area, Line} from "d3-shape";
import {NamespaceLocalObject, Selection} from "d3-selection";
import * as d3 from "d3";
import {Axis, ScaleContinuousNumeric, ScaleLinear} from "d3";
import {yTickFormat} from "./UiTimeGraph";
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {UiScaleType} from "../../generated/UiScaleType";
import {createUiColorCssString} from "../util/CssFormatUtil";
import {CurveTypeToCurveFactory, DataPoint, fakeZeroIfLogScale, SVGGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiLineChartBandConfig} from "../../generated/UiLineChartBandConfig";

export class UiLineChartBand extends AbstractUiLineChartDataDisplay<UiLineChartBandConfig> {
	private line: Line<DataPoint>;
	private $line: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private area: Area<DataPoint>;
	private $area: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private $dots: d3.Selection<SVGGElement, {}, HTMLElement, undefined>;
	private $defs: Selection<SVGDefsElement, {}, HTMLElement, undefined>;
	private $main: Selection<SVGGElement, {}, HTMLElement, undefined>;

	public $yAxis: SVGGSelection;
	private yAxis: Axis<number | { valueOf(): number }>;

	scaleY: ScaleContinuousNumeric<number, number>;
	private $yZeroLine: Selection<SVGLineElement, {}, HTMLElement, undefined>;

	constructor(
		timeGraphId: string,
		config: UiLineChartBandConfig,
		$container: SVGGSelection, // TODO append outside!! https://stackoverflow.com/a/19951169/524913
		private dropShadowFilterId: string,
		dataStore: TimeGraphDataStore
	) {
		super(config, timeGraphId, dataStore);
		this.$yAxis = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement);
		this.yAxis = d3.axisLeft(null);
		this.updateYScaleType();

		this.$main = $container
			.append<SVGGElement>("g")
			.attr("data-series-id", `${this.timeGraphId}-${this.config.id}`);
		this.initLinesAndColorScale();
		this.initDomNodes();
	}

	updateYScaleType(): void {
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

	private initLinesAndColorScale() {
		this.area = d3.area<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.config.graphType]);
		this.line = d3.line<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.config.graphType]);
	}

	private initDomNodes() {
		this.$area = this.$main.append<SVGPathElement>("path")
			.classed("area", true);
		this.$line = this.$main.append<SVGPathElement>("path")
			.classed("line", true)
			.attr("stroke", `url(#line-gradient-${this.timeGraphId}-${this.config.id})`);
		this.$yZeroLine = this.$main.append<SVGLineElement>("line")
			.classed("y-zero-line", true);

		// .style("filter", `url("#${this.dropShadowFilterId}")`);
		this.$dots = this.$main.append<SVGGElement>("g")
			.classed("dots", true);
	}

	public doRedraw() {
		this.updateYAxisTickFormat();

		// console.log("scaleX", this.scaleX.domain(), this.scaleX.range());
		// console.log("scaleY", this.scaleY.domain(), this.scaleY.range());

		let lineData = this.getDisplayedData()[this.config.middleLineDataSourceId];
		this.line
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
		this.$line
			.attr("d", this.line(lineData))
			.attr("stroke", createUiColorCssString(this.config.lineColor));

		let areaDataMax = this.getDisplayedData()[this.config.upperBoundDataSourceId];
		let areaDataMin = this.getDisplayedData()[this.config.lowerBoundDataSourceId];
		this.area
			.x(d => this.scaleX(d.x))
			.y0((d, index) => this.scaleY(fakeZeroIfLogScale(areaDataMin[index].y, this.config.yScaleType))) // TODO make sure these are in sync (no missing points) or fallback to line value??
			.y1((d, index) => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
		this.$area
			.attr("d", this.area(areaDataMax))
			.attr("fill", createUiColorCssString(this.config.areaColor));

		let $dotsDataSelection = this.$dots.selectAll<SVGCircleElement, UiTimeGraphDataPointConfig>("circle.dot")
			.data(this.config.dataDotRadius > 0 ? lineData : [])
			.join("circle")
			.classed("dot", true)
			.attr("cx", d => this.scaleX(d.x))
			.attr("cy", d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)))
			.attr("r", this.config.dataDotRadius);
		$dotsDataSelection.exit().remove();


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

		this.$yAxis.style("color", createUiColorCssString(this.config.axisColor));

		this.$yZeroLine
			.attr("x1", 0)
			.attr("y1", this.scaleY(0))
			.attr("x2", this.scaleX.range()[1])
			.attr("y2", this.scaleY(0))
			.attr("stroke", createUiColorCssString(this.config.axisColor))
			.attr("visibility", (this.config.yZeroLineVisible && this.scaleY.domain()[0] !== 0) ? "visible" : "hidden");
	}

	setConfig(lineFormat: UiLineChartBandConfig) {
		super.setConfig(lineFormat);
		this.updateYScaleType();
		this.initLinesAndColorScale();
		this.redraw();
		this.updateYAxisTickFormat();
	}

	public get yScaleWidth(): number {
		return (this.config.intervalY.max > 10000 || this.config.intervalY.min < 10) ? 37
			: (this.config.intervalY.max > 100) ? 30
				: 25;
	}

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

	protected getDataSourceIds(): string[] {
		return [this.config.lowerBoundDataSourceId, this.config.middleLineDataSourceId, this.config.upperBoundDataSourceId];
	}

	public destroy() {
		this.$main.remove();
		this.$yAxis.remove();
	}
}
