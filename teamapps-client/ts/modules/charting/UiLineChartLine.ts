import {Area, Line} from "d3-shape";
import {NamespaceLocalObject, Selection} from "d3-selection";
import * as d3 from "d3";
import {Axis, ScaleContinuousNumeric, ScaleLinear} from "d3";
import {yTickFormat} from "./UiTimeGraph";
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {UiScaleType} from "../../generated/UiScaleType";
import {UiLineChartLineConfig} from "../../generated/UiLineChartLineConfig";
import {createUiColorCssString} from "../util/CssFormatUtil";
import {CurveTypeToCurveFactory, DataPoint, fakeZeroIfLogScale, SVGGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";
import {TimeGraphDataStore} from "./TimeGraphDataStore";

export class UiLineChartLine extends AbstractUiLineChartDataDisplay<UiLineChartLineConfig> {
	private line: Line<DataPoint>;
	private $line: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private area: Area<DataPoint>;
	private $area: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private $dots: d3.Selection<SVGGElement, {}, HTMLElement, undefined>;
	private $defs: Selection<SVGDefsElement, {}, HTMLElement, undefined>;
	private colorScale: ScaleLinear<string, string>;
	private $main: Selection<SVGGElement, {}, HTMLElement, undefined>;

	private $yZeroLine: Selection<SVGLineElement, {}, HTMLElement, undefined>;

	constructor(
		timeGraphId: string,
		config: UiLineChartLineConfig,
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
		this.colorScale = d3.scaleLinear<string, string>()
			.range([createUiColorCssString(this.config.lineColorScaleMin), createUiColorCssString(this.config.lineColorScaleMax)]);
	}

	private initDomNodes() {
		this.$area = this.$main.append<SVGPathElement>("path")
			.classed("area", true)
			.attr("fill", `url(#area-gradient-${this.timeGraphId}-${this.config.id})`);
		this.$line = this.$main.append<SVGPathElement>("path")
			.classed("line", true)
			.attr("stroke", `url(#line-gradient-${this.timeGraphId}-${this.config.id})`);
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
			.data([createUiColorCssString(this.config.lineColorScaleMax), createUiColorCssString(this.config.lineColorScaleMin)])
			.join("stop")
			.attr("stop-color", d => d)
			.attr("offset", (datum, index) => index);
		this.$defs.select(".area-gradient")
			.attr("y2", this.scaleY.range()[0])
			.selectAll("stop")
			.data([createUiColorCssString(this.config.areaColorScaleMax), createUiColorCssString(this.config.areaColorScaleMin)])
			.join("stop")
			.attr("stop-color", d => d)
			.attr("offset", (datum, index) => index);

		let data = this.getDisplayedData()[this.config.dataSeriesId];
		this.line
			.x(d => this.scaleX(d.x))
			.y(d => this.scaleY(fakeZeroIfLogScale(d.y, this.config.yScaleType)));
		this.$line.attr("d", this.line(data));

		if (this.config.areaColorScaleMin != null && this.config.areaColorScaleMin.alpha > 0
			|| this.config.areaColorScaleMax != null && this.config.areaColorScaleMax.alpha > 0) { // do not render transparent area!
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
			.attr("stroke", createUiColorCssString(this.config.yAxisColor))
			.attr("visibility", (this.config.yZeroLineVisible && this.scaleY.domain()[0] !== 0) ? "visible" : "hidden");
	}

	setConfig(lineFormat: UiLineChartLineConfig) {
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
		return [this.config.dataSeriesId];
	}

	public destroy() {
		this.$main.remove();
		this.$yAxis.remove();
	}
}
