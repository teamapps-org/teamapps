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
		let lineData = this.getDisplayedData()[this.config.middleLineDataSeriesId];
		this.line
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
		this.$line
			.attr("d", this.line(lineData))
			.attr("stroke", createUiColorCssString(this.config.lineColor));

		let areaDataMax = this.getDisplayedData()[this.config.upperBoundDataSeriesId];
		let areaDataMin = this.getDisplayedData()[this.config.lowerBoundDataSeriesId];
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

	protected getDataSeriesIds(): string[] {
		return [this.config.lowerBoundDataSeriesId, this.config.middleLineDataSeriesId, this.config.upperBoundDataSeriesId];
	}

	public destroy() {
		this.$main.remove();
		this.$yAxis.remove();
	}
}
