import {Area, Line} from "d3-shape";
import {NamespaceLocalObject, Selection} from "d3-selection";
import * as d3 from "d3";
import {Axis, ScaleContinuousNumeric, ScaleLinear} from "d3";
import {yTickFormat} from "./UiTimeGraph";
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {UiScaleType} from "../../generated/UiScaleType";
import {UiLineChartLineConfig} from "../../generated/UiLineChartLineConfig";
import {createUiColorCssString} from "../util/CssFormatUtil";
import {CurveTypeToCurveFactory, DataPoint, fakeZeroIfLogScale, SVGGSelection} from "./Charting";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";

export class UiLineChartLine extends AbstractUiLineChartDataDisplay<UiLineChartLineConfig> {
	private zoomLevelData: UiTimeGraphDataPointConfig[][] = [];
	private line: Line<DataPoint>;
	private $line: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private area: Area<DataPoint>;
	private $area: Selection<SVGPathElement, {}, HTMLElement, undefined>;
	private $dots: d3.Selection<SVGGElement, {}, HTMLElement, undefined>;
	private $defs: Selection<SVGDefsElement, {}, HTMLElement, undefined>;
	private colorScale: ScaleLinear<string, string>;
	private $main: Selection<SVGGElement, {}, HTMLElement, undefined>;
	private numberOfZoomLevels: number;

	public $yAxis: SVGGSelection;
	private yAxis: Axis<number | { valueOf(): number }>;

	scaleY: ScaleContinuousNumeric<number, number>;
	private $yZeroLine: Selection<SVGLineElement, {}, HTMLElement, undefined>;

	constructor(
		timeGraphId: string,
		config: UiLineChartLineConfig,
		$container: SVGGSelection, // TODO do appending outside!! https://stackoverflow.com/a/19951169/524913
		numberOfZoomLevels: number,
		private dropShadowFilterId: string,
	) {
		super(config, timeGraphId);
		this.$yAxis = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement);
		this.yAxis = d3.axisLeft(null);
		this.updateYScaleType();
		this.yAxis = d3.axisLeft(this.scaleY); // TODO remove

		this.resetData(numberOfZoomLevels);
		this.$main = $container
			.append<SVGGElement>("g")
			.attr("data-series-id", `${this.timeGraphId}-${this.config.id}`);
		this.initLinesAndColorScale();
		this.initDomNodes();
	}

	updateYScaleType(): void {
		if (this.scaleY != null) return; // TODO remove!!!!

		if (this.config.yScaleType === UiScaleType.LOG10) {
			this.scaleY = d3.scaleLog();
		} else {
			this.scaleY = d3.scaleLinear();
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

	public resetData(numberOfZoomLevels: number): any {
		this.numberOfZoomLevels = numberOfZoomLevels;
		this.zoomLevelData = [];
		for (let i = 0; i < this.numberOfZoomLevels; i++) {
			this.zoomLevelData[i] = [];
		}
	}

	public getDisplayedData(zoomLevel: number, xStart: number, xEnd: number): DataPoint[] {
		let i = 0;
		for (; i < this.zoomLevelData[zoomLevel].length; i++) {
			if (this.zoomLevelData[zoomLevel][i].x > xStart) {
				break;
			}
		}
		let startIndex = i === 0 ? 0 : i - 1;
		for (; i < this.zoomLevelData[zoomLevel].length; i++) {
			if (this.zoomLevelData[zoomLevel][i].x >= xEnd) {
				break;
			}
		}
		let endIndex = i === this.zoomLevelData[zoomLevel].length ? i : i + 1;


		let data = this.zoomLevelData[zoomLevel].slice(startIndex, endIndex);
		// if (data.length > 200) {
		// 	data.forEach(d => (d as any).date = new Date(d.x).toString());
		// 	console.log(`zoomLevel: ${zoomLevel}; interval: ${new Date(xStart).toString()}-${new Date(xEnd).toString()}; layer: ${this.zoomLevelData[zoomLevel].length}; returned: ${data.length}`, data);
		// }
		return data;
	}

	public getDisplayedDataYBounds(): [number, number] {
		let minY = Number.MAX_VALUE;
		let maxY = Number.MIN_VALUE;
		const zoomBoundsX = [+(this.scaleX.domain()[0]), +(this.scaleX.domain()[1])];
		let displayedData = this.getDisplayedData(this.zoomLevelIndex, zoomBoundsX[0], zoomBoundsX[1]);
		displayedData.forEach(d => {
			if (d.y < minY) {
				minY = d.y;
			}
			if (d.y > maxY) {
				maxY = d.y;
			}
		});
		if (minY === Number.MAX_VALUE && maxY === Number.MIN_VALUE) {
			minY = 0;
			maxY = 1;
		}
		return [minY, maxY];
	}

	public addData(zoomLevel: number, intervalX: UiLongIntervalConfig, data: UiTimeGraphDataPointConfig[]) {
		let zoomLevelData = this.zoomLevelData[zoomLevel];
		let minOverlappingIndex: number = null;
		let maxOverlappingIndex: number = null;
		for (let i = 0; i < zoomLevelData.length; i++) {
			if (zoomLevelData[i].x >= intervalX.min && zoomLevelData[i].x <= intervalX.max) {
				if (minOverlappingIndex == null) {
					minOverlappingIndex = i;
				}
				maxOverlappingIndex = i;
			}
		}
		if (minOverlappingIndex != null && maxOverlappingIndex != null) {
			zoomLevelData.splice(minOverlappingIndex, maxOverlappingIndex - minOverlappingIndex + 1, ...data);
		} else {
			this.zoomLevelData[zoomLevel] = zoomLevelData.concat(data);
		}
		this.zoomLevelData[zoomLevel].sort((a, b) => a.x - b.x);
	}

	public doRedraw() {
		this.updateYAxisTickFormat();

		// console.log("scaleX", this.scaleX.domain(), this.scaleX.range());
		// console.log("scaleY", this.scaleY.domain(), this.scaleY.range());

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

		this.$defs.select(".area-gradient")
			.attr("y2", this.scaleY.range()[0]);

		let data = this.getDisplayedData(this.zoomLevelIndex, +(this.scaleX.domain()[0]), +(this.scaleX.domain()[1]));
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

	setConfig(lineFormat: UiLineChartLineConfig) {
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

	public destroy() {
		this.$main.remove();
		this.$yAxis.remove();
	}
}
