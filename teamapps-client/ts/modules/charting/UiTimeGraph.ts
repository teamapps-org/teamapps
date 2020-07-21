/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import {AbstractUiComponent} from "../AbstractUiComponent";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "../util/ExecuteWhenFirstDisplayed";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import * as d3 from "d3";
import {BaseType} from "d3";
import {Selection} from "d3-selection";
import {ScaleTime} from "d3-scale";
import {bind} from "../util/Bind";
import {ZoomBehavior, ZoomedElementBaseType} from "d3-zoom";
import {Axis} from "d3-axis";
import {Interval, IntervalManager} from "../util/IntervalManager";
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {generateUUID, parseHtml} from "../Common";
import {
	UiTimeGraph_DataNeededEvent,
	UiTimeGraph_IntervalSelectedEvent,
	UiTimeGraph_ZoomedEvent,
	UiTimeGraphCommandHandler,
	UiTimeGraphConfig,
	UiTimeGraphEventSource
} from "../../generated/UiTimeGraphConfig";
import {createUiLongIntervalConfig, UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {BrushBehavior} from "d3-brush";
import {UiLineChartLineConfig} from "../../generated/UiLineChartLineConfig";
import {debouncedMethod, DebounceMode} from "../util/debounce";
import {UiLineChartMouseScrollZoomPanMode} from "../../generated/UiLineChartMouseScrollZoomPanMode";
import {UiTimeChartZoomLevelConfig} from "../../generated/UiTimeChartZoomLevelConfig";
import {UiLineChartLine} from "./UiLineChartLine";
import {AbstractUiLineChartDataDisplay} from "./AbstractUiLineChartDataDisplay";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiLineChartBand} from "./UiLineChartBand";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {UiLineChartDataDisplayGroup} from "./UiLineChartDataDisplayGroup";

type SVGGSelection<DATUM = {}> = Selection<SVGGElement, DATUM, HTMLElement, undefined>;

export const yTickFormat = d3.format("-,.2s");

export class UiTimeGraph extends AbstractUiComponent<UiTimeGraphConfig> implements UiTimeGraphCommandHandler, UiTimeGraphEventSource {

	public readonly onDataNeeded: TeamAppsEvent<UiTimeGraph_DataNeededEvent> = new TeamAppsEvent<UiTimeGraph_DataNeededEvent>(this);
	public readonly onIntervalSelected: TeamAppsEvent<UiTimeGraph_IntervalSelectedEvent> = new TeamAppsEvent<UiTimeGraph_IntervalSelectedEvent>(this);
	public readonly onZoomed: TeamAppsEvent<UiTimeGraph_ZoomedEvent> = new TeamAppsEvent<UiTimeGraph_ZoomedEvent>(this);

	public static readonly LOGSCALE_MIN_Y = 0.5;
	public static readonly DROP_SHADOW_ID = "drop-shadow";

	private mouseScrollZoomPanMode: UiLineChartMouseScrollZoomPanMode;
	private intervalX: UiLongIntervalConfig;
	private maxPixelsBetweenDataPoints: number;

	private linesById: { [id: string]: AbstractUiLineChartDataDisplay } = {};

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

	private dataStore: TimeGraphDataStore = new TimeGraphDataStore();

	get drawableWidth() {
		return this.getWidth() - this.marginLeft - this.margin.right
	}

	private get drawableHeight() {
		return this.getHeight() - this.margin.top - this.margin.bottom
	}

	private get marginLeft() {
		return this.getAllSeries().reduce((sum, s) => sum + s.yScaleWidth, 0);
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

		const hourFormat = this._context.config.timeFormat.indexOf('H') !== -1 || this._context.config.timeFormat.indexOf('k') !== -1 ? 'H' : 'I';
		var formatMillisecond = d3.timeFormat(".%L"),
			formatSecond = d3.timeFormat(":%S"),
			formatMinute = d3.timeFormat(`%${hourFormat}:%M`),
			formatHour = d3.timeFormat(`%${hourFormat}:00`),
			formatDay = d3.timeFormat("%a %d"),
			formatWeek = d3.timeFormat("%b %d"),
			formatMonth = d3.timeFormat("%B"),
			formatYear = d3.timeFormat("%Y");
		function multiFormat(date: Date) {
			return (d3.timeSecond(date) < date ? formatMillisecond
				: d3.timeMinute(date) < date ? formatSecond
					: d3.timeHour(date) < date ? formatMinute
						: d3.timeDay(date) < date ? formatHour
							: d3.timeMonth(date) < date ? (d3.timeWeek(date) < date ? formatDay : formatWeek)
								: d3.timeYear(date) < date ? formatMonth
									: formatYear)(date);
		}
		this.xAxis.tickFormat(multiFormat);

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
				this.getAllSeries().forEach(s => s.redraw());
				this.fireUiZoomEvent();
			});
		this.setMouseScrollZoomPanMode(config.mouseScrollZoomPanMode);

		config.lines.forEach(line => {
			this.linesById[line.id] = this.createSeries(line);
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

	zoomTo(intervalX: UiLongIntervalConfig): void {
		if (intervalX.min < this.intervalX.min) {
			intervalX.min = this.intervalX.min;
		}
		if (intervalX.min > this.intervalX.max) {
			intervalX.min = this.intervalX.max - 1;
		}
		if (intervalX.max < this.intervalX.min) {
			intervalX.max = this.intervalX.min + 1;
		}
		if (intervalX.max > this.intervalX.max) {
			intervalX.max = this.intervalX.max;
		}
		let k = (this.intervalX.max - this.intervalX.min) / (intervalX.max - intervalX.min);
		this.zoom.transform(this.$graphClipContainer, d3.zoomIdentity.scale(k).translate(-this.scaleX(intervalX.min), 0));
	}

	private createSeries(lineFormat: UiLineChartLineConfig) {
		let display = UiTimeGraph.createDataDisplay(this._config.id, lineFormat, this.$graphClipContainer, this.dropShadowFilterId, this.dataStore);
		display.setScaleYRange([this.drawableHeight, 0]);
		this.$yAxisContainer.node().appendChild(display.$yAxis.node());
		return display;
	}

	public static createDataDisplay(timeGraphId: string, lineFormat: AbstractUiLineChartDataDisplayConfig, $graphClipContainer: Selection<SVGGElement, {}, HTMLElement, undefined>, dropShadowFilterId: string, dataStore: TimeGraphDataStore) {
		let display: AbstractUiLineChartDataDisplay;
		if (lineFormat._type === 'UiLineChartLine') {
			display = new UiLineChartLine(timeGraphId, lineFormat, $graphClipContainer, dropShadowFilterId, dataStore);
		} else if (lineFormat._type === 'UiLineChartBand') {
			display = new UiLineChartBand(timeGraphId, lineFormat, $graphClipContainer, dropShadowFilterId, dataStore);
		} else if (lineFormat._type === 'UiLineChartDataDisplayGroup') {
			display = new UiLineChartDataDisplayGroup(timeGraphId, lineFormat, $graphClipContainer, dropShadowFilterId, dataStore);
		}
		return display;
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
			left += s.yScaleWidth;
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
			series.updateZoomX(this.getCurrentZoomLevel(), this.getTransformedScaleX());
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

		this.getAllSeries().forEach(s => s.setScaleYRange([this.drawableHeight, 0]));
		this.updateZoomExtents();

		this.$xAxis.attr("transform", "translate(0," + this.drawableHeight + ")");

		this.getAllSeries().forEach(s => (s as UiLineChartLine).updateYAxisTickFormat());

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
		return Object.keys(this.linesById).map(id => this.linesById[id]);
	}

	addData(zoomLevel: number, intervalX: UiLongIntervalConfig, data: { [seriesId: string]: UiTimeGraphDataPointConfig[] }): void {
		this.zoomLevelIntervalManagers[zoomLevel].addInterval(new Interval(intervalX.min, intervalX.max));
		this.dataStore.addData(zoomLevel, intervalX, data);
		this.redraw();
	}

	resetAllData(newZoomLevels: UiTimeChartZoomLevelConfig[]): void {
		this.zoomLevels = newZoomLevels;
		this.initZoomLevelIntervalManagers();
		this.dataStore.reset();
		this.redraw();
	}

	@debouncedMethod(500, DebounceMode.BOTH)
	replaceAllData(newZoomLevels: UiTimeChartZoomLevelConfig[], zoomLevel: number, intervalX: UiLongIntervalConfig, data: { [seriesId: string]: UiTimeGraphDataPointConfig[] }): void {
		this.zoomLevels = newZoomLevels;
		this.initZoomLevelIntervalManagers();
		// do NOT remove the data from the dataStore. It will be re-requested anyway and thereby overwritten. Otherwise, you would get zoom flickering due to y-min/max-value changes
		// this.dataStore.reset();
		this.addData(zoomLevel, intervalX, data);
	}

	setIntervalX(intervalX: UiLongIntervalConfig): void {
		this.intervalX = intervalX;
		this.updateZoomExtents();
		this.scaleX.domain([this.intervalX.min, this.intervalX.max]);
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

	setSelectedInterval(intervalX: UiLongIntervalConfig): void {
		this.xSelection = intervalX;
		this.redraw();
	}

	setMaxPixelsBetweenDataPoints(maxPixelsBetweenDataPoints: number): void {
		this.maxPixelsBetweenDataPoints = maxPixelsBetweenDataPoints;
		this.updateZoomExtents();
		this.redraw();
	}

	doGetMainElement(): HTMLElement {
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

	setLines(lineConfigs: UiLineChartLineConfig[]): void {
		lineConfigs.forEach(lineConfig => {
			let existingSeries = this.linesById[lineConfig.id];
			if (existingSeries) {
				existingSeries.setConfig(lineConfig);
			} else {
				this.linesById[lineConfig.id] = this.createSeries(lineConfig);
			}
		});
		let lineConfigsById = lineConfigs.reduce((previousValue, currentValue) => (previousValue[currentValue.id] = previousValue) && previousValue, {} as {[id: string]: UiLineChartLineConfig});
		Object.keys(this.linesById).forEach(lineId => {
			if (!lineConfigsById[lineId]) {
				this.linesById[lineId].destroy();
				delete this.linesById[lineId];
			}
		});
		this.onResize();
	}

	setLine(lineId: string, lineFormat: UiLineChartLineConfig): void {
		let series = this.linesById[lineId];
		if (series) {
			series.setConfig(lineFormat);
		} else {
			this.linesById[lineId] = this.createSeries(lineFormat);
		}
	}
}


TeamAppsUiComponentRegistry.registerComponentClass("UiTimeGraph", UiTimeGraph);
