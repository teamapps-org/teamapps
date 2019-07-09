/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import * as d3 from "d3";
import {BaseType, NamespaceLocalObject} from "d3";
import {Selection} from "d3-selection";
import {ScaleContinuousNumeric, ScaleLinear, ScaleTime} from "d3-scale";
import {UiScaleType} from "../generated/UiScaleType";
import {bind} from "./util/Bind";
import {ZoomBehavior, ZoomedElementBaseType} from "d3-zoom";
import {Area, Line} from "d3-shape";
import {Axis} from "d3-axis";
import {Interval, IntervalManager} from "./util/IntervalManager";
import {UiTimeGraphDataPointConfig} from "../generated/UiTimeGraphDataPointConfig";
import {generateUUID, parseHtml} from "./Common";
import {
	UiTimeGraph_DataNeededEvent,
	UiTimeGraph_IntervalSelectedEvent,
	UiTimeGraph_ZoomedEvent,
	UiTimeGraphCommandHandler,
	UiTimeGraphConfig,
	UiTimeGraphEventSource
} from "../generated/UiTimeGraphConfig";
import {createUiLongIntervalConfig, UiLongIntervalConfig} from "../generated/UiLongIntervalConfig";
import {BrushBehavior} from "d3-brush";
import {UiLineChartCurveType} from "../generated/UiLineChartCurveType";
import {UiLineChartLineFormatConfig} from "../generated/UiLineChartLineFormatConfig";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {debouncedMethod, DebounceMode} from "./util/debounce";
import {UiLineChartYScaleZoomMode} from "../generated/UiLineChartYScaleZoomMode";
import {UiLineChartMouseScrollZoomPanMode} from "../generated/UiLineChartMouseScrollZoomPanMode";
import {UiTimeChartZoomLevelConfig} from "../generated/UiTimeChartZoomLevelConfig";

type SVGGSelection<DATUM = {}> = Selection<SVGGElement, DATUM, HTMLElement, undefined>;

interface DataPoint {
	x: number,
	y: number
}

function fakeZeroIfLogScale(y: number, scaleType: UiScaleType) {
	if (scaleType === UiScaleType.LOG10 && y === 0) {
		return UiTimeGraph.LOGSCALE_MIN_Y;
	} else {
		return y;
	}
}

const yTickFormat = d3.format("-,.2s");

export class UiTimeGraph extends AbstractUiComponent<UiTimeGraphConfig> implements UiTimeGraphCommandHandler, UiTimeGraphEventSource {

	public readonly onDataNeeded: TeamAppsEvent<UiTimeGraph_DataNeededEvent> = new TeamAppsEvent<UiTimeGraph_DataNeededEvent>(this);
	public readonly onIntervalSelected: TeamAppsEvent<UiTimeGraph_IntervalSelectedEvent> = new TeamAppsEvent<UiTimeGraph_IntervalSelectedEvent>(this);
	public readonly onZoomed: TeamAppsEvent<UiTimeGraph_ZoomedEvent> = new TeamAppsEvent<UiTimeGraph_ZoomedEvent>(this);

	public static readonly LOGSCALE_MIN_Y = 0.5;
	public static readonly DROP_SHADOW_ID = "drop-shadow";

	private mouseScrollZoomPanMode: UiLineChartMouseScrollZoomPanMode;
	private intervalX: UiLongIntervalConfig;
	private maxPixelsBetweenDataPoints: number;

	private seriesById: { [id: string]: Series } = {};

	private $main: HTMLElement;

	private $svg: d3.Selection<SVGElement, {}, null, undefined>;
	private $rootG: SVGGSelection;
	private $dropShadowFilter: Selection<BaseType, {}, null, undefined>;
	private $clipPath: d3.Selection<d3.BaseType, {}, null, undefined>;

	private scaleX: ScaleTime<number, number>;

	private dropShadowFilterId: string;

	private margin = {top: 20, right: 15, bottom: 25};
	private zoom: ZoomBehavior<ZoomedElementBaseType, any>;
	private xAxis: Axis<Date | number | { valueOf(): number }>;
	private $xAxis: SVGGSelection;
	private $horizontalPanRect: d3.Selection<SVGRectElement, {}, HTMLElement, undefined>;
	private zoomLevelIntervalManagers: IntervalManager[];
	private brush: BrushBehavior<number>;
	private $brush: SVGGSelection<number>;
	private $graphClipContainer: Selection<SVGGElement, {}, HTMLElement, undefined>;
	private xSelection: UiLongIntervalConfig;
	private zoomLevels: UiTimeChartZoomLevelConfig[];
	private $yAxisContainer: Selection<SVGGElement, {}, HTMLElement, undefined>;

	private lastDrawableWidth: number = 0;

	get drawableWidth() {
		return this.getWidth() - this.marginLeft - this.margin.right
	}

	private get drawableHeight() {
		return this.getHeight() - this.margin.top - this.margin.bottom
	}

	private get marginLeft() {
		return this.getAllSeries().reduce((sum, s) => sum + s.getYScaleWidth(), 0);
	}

	constructor(config: UiTimeGraphConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.zoomLevels = config.zoomLevels;
		this.maxPixelsBetweenDataPoints = config.maxPixelsBetweenDataPoints;

		this.$main = parseHtml('<div class="UiTimeGraph" id="' + this.getId() + '">');

		this.scaleX = d3.scaleTime()
			.range([0, this.drawableWidth]);

		this.$svg = d3.select(this.$main)
			.append<SVGElement>("svg");
		this.$rootG = this.$svg
			.append<SVGGElement>("g");

		this.setIntervalX(config.intervalX);

		this.$xAxis = this.$rootG.append<SVGGElement>("g")
			.classed("x-axis", true);
		this.$horizontalPanRect = this.$xAxis.append<SVGRectElement>("rect")
			.classed("horizontal-pan-rect", true)
			.attr("width", "100%")
			.attr("height", 20);
		let x = 0;
		const panZoom = d3.zoom()
			.scaleExtent([1, 1])
			.on("zoom", () => {
				const zoomTransform = d3.zoomTransform(this.$xAxis.node());
				const zoomTransform2 = d3.zoomTransform(this.$graphClipContainer.node());
				this.zoom.translateBy(this.$graphClipContainer, (zoomTransform.x - x) / zoomTransform2.k, 0);
				x = zoomTransform.x;
				this.redraw();
			});
		this.$xAxis.call(panZoom);
		this.xAxis = d3.axisBottom(this.scaleX);

		this.$yAxisContainer = this.$rootG.append<SVGGElement>("g")
			.classed("y-axis-container", true);
		this.dropShadowFilterId = `${UiTimeGraph.DROP_SHADOW_ID}-${this.getId()}`;
		let $defs = this.$svg.append<SVGDefsElement>("defs")
			.html(navigator.vendor.indexOf("Apple") === -1 ? `<filter id="${this.dropShadowFilterId}" y="-50">
	        <feGaussianBlur in="SourceAlpha" stdDeviation="1.5" result="blur"></feGaussianBlur>
	        <feComponentTransfer in="blur" result="fadedBlur">
	            <feFuncA type="linear" slope="0.3"></feFuncA>
	        </feComponentTransfer>
	        <feOffset in="fadedBlur" dx="0.7" dy="1.5" result="offsetBlur"></feOffset>
	        <feMerge>
	            <feMergeNode in="offsetBlur"></feMergeNode>
	            <feMergeNode in="SourceGraphic"></feMergeNode>
	        </feMerge>
	    </filter>` : '');
		this.$dropShadowFilter = $defs.select(`#${this.dropShadowFilterId}`);
		let clipPathId = this.getId() + "-clipping-path-" + generateUUID();
		this.$clipPath = $defs.append("clipPath")
			.attr("id", clipPathId)
			.append("rect")
			.attr("y", -50);

		this.$graphClipContainer = this.$rootG.append<SVGGElement>("g")
			.classed("graph-clipping-container", true)
			.attr("clip-path", `url(#${clipPathId})`);

		this.zoom = d3.zoom()
			.scaleExtent([1, this.calculateMaxZoomFactor()])
			.on("zoom", () => {
				this.redraw();
				this.getAllSeries().forEach(s => s.handleXBoundsChange());
				this.fireUiZoomEvent();
			});
		this.setMouseScrollZoomPanMode(config.mouseScrollZoomPanMode);

		Object.keys(config.lineFormats).forEach(seriesId => {
			let lineFormat = config.lineFormats[seriesId];
			this.seriesById[seriesId] = this.createAndAddSeries(seriesId, lineFormat);
		});

		this.initZoomLevelIntervalManagers();

		this.brush = d3.brushX<number>()
			.extent([[0, 0], [100, 100] /*provisional values!*/])
			.on("end", this.handleBrushSelection);

		this.$brush = this.$graphClipContainer.append<SVGGElement>("g")
			.classed("brush", true) as SVGGSelection<number>;
		this.$brush
			.call(this.brush);

		this.resetAllData(this.zoomLevels);
	}

	private createAndAddSeries(seriesId: string, lineFormat: UiLineChartLineFormatConfig) {
		let series = new Series(this, seriesId, lineFormat, this.$graphClipContainer, this.zoomLevels.length, this.dropShadowFilterId);
		series.scaleY.range([this.drawableHeight, 0]);
		this.$yAxisContainer.node().appendChild(series.$yAxis.node());
		return series;
	}

	getZoomBoundsX() {
		let transformedScaleX = this.getTransformedScaleX();
		return [+transformedScaleX.domain()[0], +transformedScaleX.domain()[1]];
	}

	getTransformedScaleX(): ScaleTime<number, number> {
		let zoomTransform = d3.zoomTransform(this.$graphClipContainer.node());
		return zoomTransform.rescaleX(this.scaleX as any);
	}

	private initZoomLevelIntervalManagers() {
		this.zoomLevelIntervalManagers = [];
		for (let i = 0; i < this.zoomLevels.length; i++) {
			this.zoomLevelIntervalManagers[i] = new IntervalManager();
		}
	}

	@executeWhenFirstDisplayed()
	private redraw() {
		if (this.getWidth() === 0 || this.getHeight() === 0) {
			return;
		}
		this.lastDrawableWidth = this.drawableWidth;

		this.$rootG.attr("transform", `translate(${this.marginLeft},${this.margin.top})`);

		let left = 0;
		this.getAllSeries().forEach(s => {
			s.$yAxis.attr("transform", `translate(${-left},0)`);
			left += s.getYScaleWidth();
		});

		let transformedScaleX = this.getTransformedScaleX();

		this.$xAxis.call(this.xAxis.scale(transformedScaleX));

		let domain = this.restrictDomainXToConfiguredInterval([+transformedScaleX.domain()[0], +transformedScaleX.domain()[1]]);

		const zoomLevel = this.getCurrentZoomLevel();
		// this.logger.debug("millisecondsPerPixel: " + millisecondsPerPixel + " zoomLevel: " + zoomLevel + " at scale: " + zoomTransform.k);

		let uncoveredIntervals = this.zoomLevelIntervalManagers[zoomLevel].getUncoveredIntervals(new Interval(domain[0], domain[1]));
		if (uncoveredIntervals.length > 0) {
			for (let uncoveredInterval of uncoveredIntervals) {
				if (uncoveredInterval.start >= uncoveredInterval.end) {
					continue; // do not request empty intervals. may happen if the domainX is [0, 0]
				}
				this.logger.debug("firing onDataNeeded: " + uncoveredInterval);
				if (uncoveredInterval.start == null || isNaN(uncoveredInterval.start) || uncoveredInterval.end == null || isNaN(uncoveredInterval.end)) {
					this.logger.error("Uncovered interval is corrupt. Will not retrieve data!");
				} else {
					this.onDataNeeded.fire({
						zoomLevelIndex: zoomLevel,
						neededIntervalX: createUiLongIntervalConfig(uncoveredInterval.start, uncoveredInterval.end)
					});
					this.zoomLevelIntervalManagers[zoomLevel].addInterval(new Interval(uncoveredInterval.start, uncoveredInterval.end));
				}
			}
		}

		this.getAllSeries().forEach(series => {
			series.draw();
		});

		if (this.xSelection) {
			let brushX1 = Math.max(-10, transformedScaleX(this.xSelection.min));
			let brushX2 = Math.min(this.drawableWidth + 10, transformedScaleX(this.xSelection.max));
			if (brushX2 < 0 || brushX1 > this.drawableWidth) {
				this.brush.move(this.$brush, null);
			} else {
				this.brush.move(this.$brush, [brushX1, brushX2]);
			}
		} else {
			this.brush.move(this.$brush, null);
		}
	}

	private restrictDomainXToConfiguredInterval(domain: [number, number]) {
		return [Math.max(this.intervalX.min, +(domain[0])), Math.min(this.intervalX.max, +(domain[1]))];
	}

	getCurrentZoomLevel() {
		let transformedScaleX = this.getTransformedScaleX();
		let domain = this.restrictDomainXToConfiguredInterval([+transformedScaleX.domain()[0], +transformedScaleX.domain()[1]]);

		const millisecondsPerPixel = (domain[1] - domain[0]) / this.drawableWidth;
		let sortedZoomLevels = this.getSortedZoomLevels();
		let zoomLevelToApply = sortedZoomLevels
			.filter(indexedZoomLevel => {
				let minMillisecondsPerPixels = indexedZoomLevel.zoomLevel.approximateMillisecondsPerDataPoint / this.maxPixelsBetweenDataPoints;
				return minMillisecondsPerPixels <= millisecondsPerPixel;
			})[0] || sortedZoomLevels[sortedZoomLevels.length - 1];
		return zoomLevelToApply.index;
	}

	private getSortedZoomLevels() {
		return this.zoomLevels
			.map((zoomLevel, index) => {
				return {index, zoomLevel}
			})
			.sort((z1, z2) => z2.zoomLevel.approximateMillisecondsPerDataPoint - z1.zoomLevel.approximateMillisecondsPerDataPoint);
	}

	@bind
	private handleBrushSelection() {
		let transformedScaleX = this.getTransformedScaleX();

		if (d3.event.sourceEvent == null || d3.event.sourceEvent.type === "zoom") {
			return;  // handle only user-triggered brush changes!
		}
		let brushSelection = d3.brushSelection(this.$brush.node());
		if (brushSelection != null) {
			this.xSelection = createUiLongIntervalConfig(+transformedScaleX.invert(brushSelection[0] as number), +transformedScaleX.invert(brushSelection[1] as number));
			this.onIntervalSelected.fire({
				intervalX: this.xSelection
			});
		} else {
			this.xSelection = null;
			this.onIntervalSelected.fire({
				intervalX: null
			});
		}
	}

	public onResize(): void {
		if (this.getWidth() === 0 && this.getHeight() === 0) {
			return;
		}

		this.scaleX.range([0, this.drawableWidth]);

		// adjust the translation value of the zoom transformation, so the displayed interval stays the same despite the resize!
		if (this.lastDrawableWidth != null && this.lastDrawableWidth != this.drawableWidth) {
			const zoomTransform = d3.zoomTransform(this.$graphClipContainer.node());
			const newTranslateX = zoomTransform.x * this.drawableWidth / this.lastDrawableWidth;
			if (!isNaN(newTranslateX)) {
				this.zoom.transform(this.$graphClipContainer, d3.zoomIdentity.translate(newTranslateX, 0).scale(zoomTransform.k));
			}
		}

		this.getAllSeries().forEach(s => s.scaleY.range([this.drawableHeight, 0]));
		this.updateZoomExtents();

		this.$xAxis.attr("transform", "translate(0," + this.drawableHeight + ")");

		this.getAllSeries().forEach(s => s.updateYAxisTickFormat(this.getHeight()));

		this.$dropShadowFilter.attr("width", this.drawableWidth)
			.attr("height", this.getHeight() + 100);
		this.$clipPath.attr("width", this.drawableWidth)
			.attr("height", this.getHeight() + 100);

		this.brush.extent([[0, -5], [this.drawableWidth, this.drawableHeight]]);
		this.$brush.call(this.brush); // update size...

		this.redraw();
	}

	private updateZoomExtents() {
		if (this.zoom == null) { // not yet initialized
			return;
		}
		this.zoom
			.translateExtent([[0, -Infinity], [this.getWidth() /*zoom is attached to $svg, remember!*/, Infinity]])
			.scaleExtent([1, this.calculateMaxZoomFactor()]);
	}

	private getAllSeries() {
		return Object.keys(this.seriesById).map(id => this.seriesById[id]);
	}

	addData(zoomLevel: number, intervalX: UiLongIntervalConfig, data: { [seriesId: string]: UiTimeGraphDataPointConfig[] }): void {
		this.zoomLevelIntervalManagers[zoomLevel].addInterval(new Interval(intervalX.min, intervalX.max));
		Object.keys(data).forEach(seriesId => {
			let seriesData = data[seriesId];
			this.seriesById[seriesId] && this.seriesById[seriesId].addData(zoomLevel, intervalX, seriesData);
		});
		this.redraw();
		this.getAllSeries().forEach(s => s.handleXBoundsChange())
	}

	resetAllData(newZoomLevels: UiTimeChartZoomLevelConfig[]): void {
		this.zoomLevels = newZoomLevels;
		this.initZoomLevelIntervalManagers();
		this.getAllSeries().forEach(d => d.resetData(newZoomLevels.length));
		this.redraw();
	}

	@debouncedMethod(500, DebounceMode.BOTH)
	replaceAllData(newZoomLevels: UiTimeChartZoomLevelConfig[], zoomLevel: number, intervalX: UiLongIntervalConfig, data: { [seriesId: string]: UiTimeGraphDataPointConfig[] }): void {
		this.zoomLevels = newZoomLevels;
		this.initZoomLevelIntervalManagers();
		this.getAllSeries().forEach(d => d.resetData(newZoomLevels.length));
		this.addData(zoomLevel, intervalX, data);
	}

	setIntervalX(intervalX: UiLongIntervalConfig): void {
		this.intervalX = intervalX;
		this.updateZoomExtents();
		this.scaleX.domain([this.intervalX.min, this.intervalX.max]);
	}

	setIntervalY(lineId: string, intervalY: UiLongIntervalConfig): void {
		this.seriesById[lineId].setIntervalY(intervalY);
	}

	public setYScaleZoomMode(lineId: string, yScaleZoomMode: UiLineChartYScaleZoomMode): void {
		this.seriesById[lineId].setYScaleZoomMode(yScaleZoomMode);
	}

	setMouseScrollZoomPanMode(mouseScrollZoomPanMode: UiLineChartMouseScrollZoomPanMode): void {
		this.mouseScrollZoomPanMode = mouseScrollZoomPanMode;

		this.$graphClipContainer.on('.zoom', null);
		this.$graphClipContainer.call(this.zoom);

		let originalZoomWeelHandler = this.$graphClipContainer.on("wheel.zoom");
		let me = this;
		this.$graphClipContainer.on("wheel.zoom", function () {
			if (me.mouseScrollZoomPanMode === UiLineChartMouseScrollZoomPanMode.DISABLED) {
				return;
			} else if (me.mouseScrollZoomPanMode === UiLineChartMouseScrollZoomPanMode.WITH_MODIFIER_KEY && !(<MouseEvent>d3.event).ctrlKey && !(<MouseEvent>d3.event).altKey && !(<MouseEvent>d3.event).shiftKey && !(<MouseEvent>d3.event).metaKey) {
				return;
			}
			originalZoomWeelHandler.apply(this, arguments);
			me.zoom.translateBy(me.$graphClipContainer, -1 * d3.event.deltaX / d3.zoomTransform(me.$graphClipContainer.node()).k / 2, 0);
			d3.event.preventDefault(); // prevent MacOS' swipe pages back and forward
		});
	}

	@debouncedMethod(500, DebounceMode.LATER)
	private fireUiZoomEvent() {
		let transformedScaleX = this.getTransformedScaleX();
		let domain = transformedScaleX.domain();
		let currentZoomLevel = this.getCurrentZoomLevel();
		this.onZoomed.fire({
			intervalX: createUiLongIntervalConfig(+domain[0], +domain[1]),
			zoomLevelIndex: currentZoomLevel
		});
	}

	@executeWhenFirstDisplayed()
	setYScaleType(lineId: string, yScaleType: UiScaleType): void {
		this.seriesById[lineId].setYScaleType(yScaleType);
		this.onResize();
	}

	setSelectedInterval(intervalX: UiLongIntervalConfig): void {
		this.xSelection = intervalX;
		this.redraw();
	}

	setMaxPixelsBetweenDataPoints(maxPixelsBetweenDataPoints: number): void {
		this.maxPixelsBetweenDataPoints = maxPixelsBetweenDataPoints;
		this.updateZoomExtents();
		this.redraw();
	}

	destroy(): void {
		// nothing to do!
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	private calculateMaxZoomFactor(): number {
		if (this.getWidth() === 0 || this.getHeight() === 0) {
			return 1;
		}

		let displayedMilliseconds = this.intervalX.max - this.intervalX.min;
		let sortedZoomLevels = this.getSortedZoomLevels();
		let highestZoomLevelApproximateMillisPerDataPoint = sortedZoomLevels[sortedZoomLevels.length - 1].zoomLevel.approximateMillisecondsPerDataPoint;

		return this.maxPixelsBetweenDataPoints * displayedMilliseconds / (this.drawableWidth * highestZoomLevelApproximateMillisPerDataPoint);
	}

	setLineFormats(lineFormats: { [lineId: string]: UiLineChartLineFormatConfig }): void {
		Object.keys(lineFormats).forEach(lineId => {
			let lineFormat = lineFormats[lineId];
			let existingSeries = this.seriesById[lineId];
			if (existingSeries) {
				existingSeries.setLineFormat(lineFormat);
			} else {
				this.seriesById[lineId] = this.createAndAddSeries(lineId, lineFormat);
			}
		});
		Object.keys(this.seriesById).forEach(lineId => {
			if (!lineFormats[lineId]) {
				this.seriesById[lineId].destroy();
				delete this.seriesById[lineId];
			}
		});
		this.onResize();
	}

	setLineFormat(lineId: string, lineFormat: UiLineChartLineFormatConfig): void {
		let series = this.seriesById[lineId];
		if (series) {
			series.setLineFormat(lineFormat);
		} else {
			series = this.seriesById[lineId] = this.createAndAddSeries(lineId, lineFormat);
		}
		series.updateYAxisTickFormat(this.getHeight());
	}
}

var CurveTypeToCurveFactory = {
	[UiLineChartCurveType.LINEAR]: d3.curveLinear,
	[UiLineChartCurveType.STEP]: d3.curveStep,
	[UiLineChartCurveType.STEPBEFORE]: d3.curveStepBefore,
	[UiLineChartCurveType.STEPAFTER]: d3.curveStepAfter,
	[UiLineChartCurveType.BASIS]: d3.curveBasis,
	[UiLineChartCurveType.CARDINAL]: d3.curveCardinal,
	[UiLineChartCurveType.MONOTONE]: d3.curveMonotoneX,
	[UiLineChartCurveType.CATMULLROM]: d3.curveCatmullRom,
};

class Series {
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

	private intervalY: UiLongIntervalConfig;
	private yScaleType: UiScaleType;
	scaleY: ScaleContinuousNumeric<number, number>;
	private yScaleZoomMode: any;
	private $yZeroLine: Selection<SVGLineElement, {}, HTMLElement, undefined>;

	constructor(
		private timeGraph: UiTimeGraph,
		private id: string,
		private lineFormat: UiLineChartLineFormatConfig,
		$container: SVGGSelection,
		numberOfZoomLevels: number,
		private dropShadowFilterId: string,
	) {
		this.intervalY = lineFormat.intervalY;
		this.yScaleType = lineFormat.yScaleType;
		this.yScaleZoomMode = lineFormat.yScaleZoomMode;
		this.$yAxis = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement);
		this.updateYScaleType();
		this.yAxis = d3.axisLeft(this.scaleY);

		this.resetData(numberOfZoomLevels);
		this.$main = $container
			.append<SVGGElement>("g")
			.attr("data-series-id", `${this.timeGraph.getId()}-${id}`);
		this.initLinesAndColorScale();
		this.initDomNodes();
	}

	updateYScaleType(): ScaleContinuousNumeric<number, number> {
		if (this.yScaleType === UiScaleType.LOG10) {
			this.scaleY = d3.scaleLog();
		} else {
			this.scaleY = d3.scaleLinear();
		}
		let domainMin = fakeZeroIfLogScale(this.intervalY.min, this.yScaleType);
		this.scaleY.domain([domainMin, this.intervalY.max]);
		return this.scaleY;
	}

	private initLinesAndColorScale() {
		this.area = d3.area<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.lineFormat.graphType]);
		this.line = d3.line<DataPoint>()
			.curve(CurveTypeToCurveFactory[this.lineFormat.graphType]);
		this.colorScale = d3.scaleLinear<string, string>()
			.range([createUiColorCssString(this.lineFormat.lineColorScaleMin), createUiColorCssString(this.lineFormat.lineColorScaleMax)]);
	}

	private initDomNodes() {
		this.$area = this.$main.append<SVGPathElement>("path")
			.classed("area", true)
			.attr("fill", `url(#area-gradient-${this.timeGraph.getId()}-${this.id})`);
		this.$line = this.$main.append<SVGPathElement>("path")
			.classed("line", true)
			.attr("stroke", `url(#line-gradient-${this.timeGraph.getId()}-${this.id})`);
		this.$yZeroLine = this.$main.append<SVGLineElement>("line")
			.classed("y-zero-line", true);

		// .style("filter", `url("#${this.dropShadowFilterId}")`);
		this.$dots = this.$main.append<SVGGElement>("g")
			.classed("dots", true);

		this.$defs = this.$main.append<SVGDefsElement>("defs")
			.html(`<linearGradient class="line-gradient" id="line-gradient-${this.timeGraph.getId()}-${this.id}" x1="0" x2="0" y1="0" y2="100" gradientUnits="userSpaceOnUse">
	        <stop stop-color="${createUiColorCssString(this.lineFormat.lineColorScaleMax)}" offset="0" />
	        <stop stop-color="${createUiColorCssString(this.lineFormat.lineColorScaleMin)}" offset="1" />
	    </linearGradient>
	    <linearGradient class="area-gradient" id="area-gradient-${this.timeGraph.getId()}-${this.id}" x1="0" x2="0" y1="0" y2="100" gradientUnits="userSpaceOnUse">
	        <stop stop-color="${createUiColorCssString(this.lineFormat.areaColorScaleMax)}" offset="0" />
	        <stop stop-color="${createUiColorCssString(this.lineFormat.areaColorScaleMin)}" offset="1" />
	    </linearGradient>`);
	}

	public resetData(numberOfZoomLevels: number): any {
		this.numberOfZoomLevels = numberOfZoomLevels;
		this.zoomLevelData = [];
		for (let i = 0; i < this.numberOfZoomLevels; i++) {
			this.zoomLevelData[i] = [];
		}
	}

	public getDisplayedData(zoomLevel: number, xStart: number, xEnd: number, scale: UiScaleType): DataPoint[] {
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
		const zoomBoundsX = this.timeGraph.getZoomBoundsX();
		let displayedData = this.getDisplayedData(this.timeGraph.getCurrentZoomLevel(), zoomBoundsX[0], zoomBoundsX[1], this.yScaleType);
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
		let minOverlappingIndex:number = null;
		let maxOverlappingIndex:number = null;
		for (let i = 0; i < zoomLevelData.length; i++) {
			if (zoomLevelData[i].x >= intervalX.min && zoomLevelData[i].x <= intervalX.max) {
				if (minOverlappingIndex == null) {
					minOverlappingIndex = i;
				}
				maxOverlappingIndex = i;
			}
		}

		if (minOverlappingIndex != null && maxOverlappingIndex != null)  {
			zoomLevelData.splice(minOverlappingIndex, maxOverlappingIndex - minOverlappingIndex + 1, ...data);
		} else {
			this.zoomLevelData[zoomLevel] = zoomLevelData.concat(data);
		}
		this.zoomLevelData[zoomLevel].sort((a, b) => a.x - b.x);
	}

	public draw() {
		this.colorScale.domain(this.scaleY.domain());
		this.$defs.select(".line-gradient").attr("y2", this.scaleY.range()[0]);
		this.$defs.select(".area-gradient").attr("y2", this.scaleY.range()[0]);

		let data = this.getDisplayedData(this.timeGraph.getCurrentZoomLevel(), this.timeGraph.getZoomBoundsX()[0], this.timeGraph.getZoomBoundsX()[1], this.yScaleType);
		const scaleX = this.timeGraph.getTransformedScaleX();
		this.line
			.x(d => scaleX(d.x))
			.y(d => this.scaleY(fakeZeroIfLogScale(d.y, this.yScaleType)));
		this.$line.attr("d", this.line(data));

		if (this.lineFormat.areaColorScaleMin != null && this.lineFormat.areaColorScaleMin.alpha > 0
			|| this.lineFormat.areaColorScaleMax != null && this.lineFormat.areaColorScaleMax.alpha > 0) { // do not render transparent area!
			this.area
				.x(d => scaleX(d.x))
				.y0(this.scaleY.range()[0])
				.y1(d => this.scaleY(fakeZeroIfLogScale(d.y, this.yScaleType)));
			this.$area.attr("d", this.area(data));
		}

		let $dotsDataSelection = this.$dots.selectAll<SVGCircleElement, UiTimeGraphDataPointConfig>(".dot")
			.data(this.lineFormat.dataDotRadius > 0 ? data : [])
			// .attr("fill", d => this.colorScale(d.y))
			.attr("cx", d => scaleX(d.x))
			.attr("cy", d => this.scaleY(fakeZeroIfLogScale(d.y, this.yScaleType)));
		$dotsDataSelection
			.enter().append("circle") // Uses the enter().append() method
			.attr("class", "dot") // Assign a class for styling
			// .attr("fill", d => this.colorScale(d.y))
			.attr("cx", d => scaleX(d.x))
			.attr("cy", d => this.scaleY(fakeZeroIfLogScale(d.y, this.yScaleType)))
			.attr("r", this.lineFormat.dataDotRadius);
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

		this.$yAxis.style("color", createUiColorCssString(this.lineFormat.axisColor));

		this.$yZeroLine
			.attr("x1", 0)
			.attr("y1", this.scaleY(0))
			.attr("x2", this.timeGraph.drawableWidth)
			.attr("y2", this.scaleY(0))
			.attr("stroke", createUiColorCssString(this.lineFormat.axisColor))
			.attr("visibility", (this.lineFormat.yZeroLineVisible && this.scaleY.domain()[0] !== 0) ? "visible" : "hidden");
	}

	setLineFormat(lineFormat: UiLineChartLineFormatConfig) {
		this.initLinesAndColorScale();
		this.lineFormat = lineFormat;
		this.draw();
	}

	handleXBoundsChange() {
		if (this.isDynamicYScaleZooming()) {
			this.animatedZoomYScale();
		}
	}

	private animatedZoomYScale() {
		let minY: number, maxY: number;
		if (this.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC) {
			let displayedDataYBounds = this.getDisplayedDataYBounds();
			let delta = displayedDataYBounds[1] - displayedDataYBounds[0];
			minY = displayedDataYBounds[0] - delta * .05;
			maxY = displayedDataYBounds[1] + delta * .05;
		} else if (this.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO) {
			[minY, maxY] = this.getDisplayedDataYBounds();
			if (minY > 0) {
				minY = 0;
			}
			if (maxY < 0) {
				maxY = 0;
			}
		} else {
			minY = this.intervalY.min;
			maxY = this.intervalY.max;
		}
		minY = fakeZeroIfLogScale(minY, this.yScaleType);

		if (Math.abs(this.scaleY.domain()[0]) > 1e15 || Math.abs(this.scaleY.domain()[1]) > 1e15) { // see https://github.com/d3/d3-interpolate/pull/63
			this.scaleY.domain([0, 1])
		}

		d3.transition(`${this.timeGraph.getId()}-${this.id}-zoomYToDisplayedDomain`)
			.ease(d3.easeLinear)
			.duration(300)
			.tween(`${this.timeGraph.getId()}-${this.id}-zoomYToDisplayedDomain`, () => {
				// create interpolator and do not show nasty floating numbers
				let intervalInterpolator = d3.interpolateArray(this.scaleY.domain().map(date => +date), [minY, maxY]);
				return (t: number) => {
					this.scaleY.domain(intervalInterpolator(t));
					this.draw();
				}
			});
	}

	setIntervalY(intervalY: UiLongIntervalConfig) {
		this.intervalY = intervalY;
		if (this.scaleY.domain()[0] !== this.intervalY.min || this.scaleY.domain()[1] !== this.intervalY.max) {
			this.animatedZoomYScale();
		}
	}

	public getYScaleWidth(): number {
		return (this.intervalY.max > 10000 || this.intervalY.min < 10) ? 37
			: (this.intervalY.max > 100) ? 30
				: 25;
	}

	setYScaleZoomMode(yScaleZoomMode: UiLineChartYScaleZoomMode) {
		this.yScaleZoomMode = yScaleZoomMode;
		this.animatedZoomYScale();
	}

	private isDynamicYScaleZooming() {
		return this.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC || this.yScaleZoomMode === UiLineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO;
	}

	setYScaleType(yScaleType: UiScaleType) {
		if (yScaleType === UiScaleType.LOG10) {
			this.scaleY = d3.scaleLog();
		} else {
			this.scaleY = d3.scaleLinear();
		}
		let domainMin = fakeZeroIfLogScale(this.intervalY.min, this.yScaleType);
		this.scaleY.domain([domainMin, this.intervalY.max]);
	}

	public updateYAxisTickFormat(availableHeight: number) {
		let minY = this.scaleY.domain()[0];
		let maxY = this.scaleY.domain()[1];
		let delta = maxY - minY;
		let numberOfYTickGroups = Math.log10(delta) + 1;
		let heightPerYTickGroup = availableHeight / numberOfYTickGroups;
		if (this.yScaleType === UiScaleType.LOG10) {
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

TeamAppsUiComponentRegistry.registerComponentClass("UiTimeGraph", UiTimeGraph);
