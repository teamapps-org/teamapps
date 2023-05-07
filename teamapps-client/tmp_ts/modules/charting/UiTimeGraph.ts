/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import {AbstractComponent} from "teamapps-client-core";
import {TeamAppsEvent} from "teamapps-client-core";
import {TeamAppsUiContext} from "teamapps-client-core";
import {executeWhenFirstDisplayed} from "../util/ExecuteWhenFirstDisplayed";

import * as d3 from "d3";
import {Selection} from "d3-selection";
import {ScaleTime} from "d3-scale";
import {bind} from "../util/Bind";
import {ZoomBehavior, ZoomedElementBaseType} from "d3-zoom";
import {Axis} from "d3-axis";
import {generateUUID, parseHtml} from "../Common";
import {
	UiTimeGraph_IntervalSelectedEvent,
	UiTimeGraph_ZoomedEvent,
	UiTimeGraphCommandHandler,
	DtoTimeGraph,
	UiTimeGraphEventSource
} from "../../generated/DtoTimeGraph";
import {createDtoLongInterval, DtoLongInterval} from "../../generated/DtoLongInterval";
import {BrushBehavior} from "d3-brush";
import {debouncedMethod, DebounceMode} from "../util/debounce";
import {UiLineChartMouseScrollZoomPanMode} from "../../generated/UiLineChartMouseScrollZoomPanMode";
import {DtoTimeChartZoomLevel} from "../../generated/DtoTimeChartZoomLevel";
import {UiLineGraph} from "./UiLineGraph";
import {UiHoseGraph} from "./UiHoseGraph";
import {UiGraphGroup} from "./UiGraphGroup";
import {DateTime} from 'luxon';
import {scaleZoned} from "d3-luxon";
import {SVGGSelection, SVGSelection} from "./Charting";
import {UiGraph} from "./UiGraph";
import {UiIncidentGraph} from "./UiIncidentGraph";
import {TimeGraphPopper} from "./TimeGraphPopper";
import {DtoGraphData} from "../../generated/DtoGraphData";
import {DtoGraph} from "../../generated/DtoGraph";
import {DtoAbstractGraph} from "./DtoAbstractGraph";
import {GraphContext, PopperHandle} from "./GraphContext";
import {DtoLineGraphData} from "../../generated/DtoLineGraphData";
import {DtoHoseGraphData} from "../../generated/DtoHoseGraphData";

export const yTickFormat = d3.format("-,.2s");

export class UiTimeGraph extends AbstractComponent<DtoTimeGraph> implements UiTimeGraphCommandHandler, UiTimeGraphEventSource {

	public readonly onIntervalSelected: TeamAppsEvent<UiTimeGraph_IntervalSelectedEvent> = new TeamAppsEvent<UiTimeGraph_IntervalSelectedEvent>();
	public readonly onZoomed: TeamAppsEvent<UiTimeGraph_ZoomedEvent> = new TeamAppsEvent<UiTimeGraph_ZoomedEvent>();

	public static readonly LOGSCALE_MIN_Y = 0.5;
	public static readonly DROP_SHADOW_ID = "drop-shadow";

	private mouseScrollZoomPanMode: UiLineChartMouseScrollZoomPanMode;
	private intervalX: DtoLongInterval;
	private maxPixelsBetweenDataPoints: number;

	private graphById: { [id: string]: UiGraph } = {};

	private $main: HTMLElement;

	private $svg: Selection<SVGSVGElement, {}, null, undefined>;
	private $rootG: SVGSelection<any>;
	private $dropShadowFilter: SVGSelection<any>;
	private $clipPath: SVGSelection<any>;

	private scaleX: ScaleTime<number, number>;

	private dropShadowFilterId: string;

	private margin = {top: 20, right: 15, bottom: 25};
	private zoom: ZoomBehavior<ZoomedElementBaseType, any>;
	private xAxis: Axis<Date | number | { valueOf(): number }>;
	private $xAxis: SVGSelection<any>;
	private $horizontalPanRect: SVGSelection<any>;
	private brush: BrushBehavior<number>;
	private $brush: SVGGSelection<number>;
	private $graphClipContainer: SVGGSelection<any>;
	private xSelection: DtoLongInterval;
	private zoomLevels: DtoTimeChartZoomLevel[];
	private $yAxisContainer: SVGSelection<any>;

	private lastDrawableWidth: number = 0;

	private eventsPopper: TimeGraphPopper = new TimeGraphPopper();
	private graphContext: GraphContext;

	get drawableWidth() {
		return this.getWidth() - this.marginLeft - this.margin.right
	}

	private get drawableHeight() {
		return this.getHeight() - this.margin.top - this.margin.bottom
	}

	private get marginLeft() {
		return this.getAllSeries().reduce((sum, s) => sum + (s.getYAxis()?.getWidth() ?? 0), 0);
	}

	constructor(config: DtoTimeGraph) {
		super(config);

		const me = this;
		this.graphContext = {
			getPopperHandle() {
				return {
					update(referenceElement: Element, content: (Element | string)): void {
						me.eventsPopper.update(referenceElement, content);
					},
					hide(): void {
						me.eventsPopper.update(null, null);
					},
					destroy(): void {
						me.eventsPopper.update(null, null);
					}
				} as PopperHandle;
			}
		}

		this.zoomLevels = config.zoomLevels;
		this.maxPixelsBetweenDataPoints = config.maxPixelsBetweenDataPoints;

		this.$main = parseHtml('<div class="UiTimeGraph" id="' + this.getId() + '">');

		this.scaleX = scaleZoned(config.timeZoneId, 1); // TODO first day of week...

		this.$svg = d3.select(this.$main)
			.append<SVGSVGElement>("svg");
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

		const formatMillisecond = (dateTime: DateTime) => dateTime.toFormat(".SSS"),
			formatSecond = (dateTime: DateTime) => dateTime.toFormat(":ss"),
			formatMinute = (dateTime: DateTime) => dateTime.toLocaleString({hour: "2-digit", minute: "2-digit"}),
			formatHour = (dateTime: DateTime) => dateTime.toLocaleString({hour: "2-digit", minute: "2-digit"}),
			formatDay = (dateTime: DateTime) => dateTime.toLocaleString({day: "2-digit", month: "2-digit"}),
			formatMonth = (dateTime: DateTime) => dateTime.toFormat("LLL"),
			formatYear = (dateTime: DateTime) => dateTime.toFormat("yyyy");

		const multiFormat = (date: Date) => {
			let dateTime = DateTime.fromJSDate(date).setZone(this.config.timeZoneId);
			let formatter = dateTime.startOf("second") < dateTime ? formatMillisecond
				: dateTime.startOf("minute") < dateTime ? formatSecond
					: dateTime.startOf("hour") < dateTime ? formatMinute
						: dateTime.startOf("day") < dateTime ? formatHour
							: dateTime.startOf("month") < dateTime ? formatDay
								: dateTime.startOf("year") < dateTime ? formatMonth
									: formatYear;
			return formatter(dateTime.setLocale(this.config.locale));
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
			.attr("clip-path", `url('#${clipPathId}')`);

		this.brush = d3.brushX<number>()
			.extent([[0, 0], [100, 100] /*provisional values!*/])
			.on("end", this.handleBrushSelection);
		this.$brush = this.$graphClipContainer.append<SVGGElement>("g")
			.classed("brush", true)
			.call(this.brush);

		this.zoom = d3.zoom()
			.scaleExtent([1, this.calculateMaxZoomFactor()])
			.on("zoom", () => {
				this.redraw();
				this.fireUiZoomEvent();
			});
		this.setMouseScrollZoomPanMode(config.mouseScrollZoomPanMode);

		config.graphs.forEach(line => {
			this.graphById[line.id] = this.createGraph(line);
		});

	}

	zoomTo(intervalX: DtoLongInterval): void {
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

	private createGraph(lineFormat: DtoGraph): DtoAbstractGraph {
		let display = UiTimeGraph.createDataDisplay(this.config.id, lineFormat, this.dropShadowFilterId, this.graphContext);
		this.$graphClipContainer.node().appendChild(display.getMainSelection().node());
		display.setYRange([this.drawableHeight, 0]);
		if (display.getYAxis() != null) {
			this.$yAxisContainer.node().appendChild(display.getYAxis().getSelection().node());
		}
		return display;
	}

	public static createDataDisplay(timeGraphId: string, graphConfig: DtoGraph, dropShadowFilterId: string, graphContext: GraphContext) {
		let display: DtoAbstractGraph;
		if (graphConfig._type === 'UiLineGraph') {
			display = new UiLineGraph(timeGraphId, graphConfig, dropShadowFilterId);
		} else if (graphConfig._type === 'UiHoseGraph') {
			display = new UiHoseGraph(timeGraphId, graphConfig, dropShadowFilterId);
		} else if (graphConfig._type === 'UiIncidentGraph') {
			display = new UiIncidentGraph(timeGraphId, graphConfig, graphContext);
		} else if (graphConfig._type === 'UiGraphGroup') {
			display = new UiGraphGroup(timeGraphId, graphConfig, dropShadowFilterId, graphContext);
		}
		return display;
	}

	getTransformedScaleX(): ScaleTime<number, number> {
		let zoomTransform = d3.zoomTransform(this.$graphClipContainer.node());
		return zoomTransform.rescaleX(this.scaleX as any);
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
			s.getYAxis()?.getSelection()?.attr("transform", `translate(${-left},0)`);
			left += s.getYAxis()?.getWidth() ?? 0;
		});

		let transformedScaleX = this.getTransformedScaleX();

		this.$xAxis.call(this.xAxis.scale(transformedScaleX));

		let domain = this.restrictDomainXToConfiguredInterval([+transformedScaleX.domain()[0], +transformedScaleX.domain()[1]]);

		const zoomLevel = this.getCurrentZoomLevel();
		// console.debug("millisecondsPerPixel: " + millisecondsPerPixel + " zoomLevel: " + zoomLevel + " at scale: " + zoomTransform.k);


		let uncoveredIntervalsByGraphId: { [graphId: string]: DtoLongInterval[] } = {};
		for (let [id, graph] of Object.entries(this.graphById)) {
			let uncoveredIntervals = graph.getUncoveredIntervals(zoomLevel, [domain[0], domain[1]]);
			uncoveredIntervals = uncoveredIntervals
				.filter(i => (i[0] < i[1])) // remove empty intervals
				.filter(i => !(i[0] == null || isNaN(i[0]) || i[1] == null || isNaN(i[1]))); // invalid intervals

			if (uncoveredIntervals.length > 0) {
				uncoveredIntervalsByGraphId[id] = uncoveredIntervals.map(i => createDtoLongInterval(i[0], i[1]));
				uncoveredIntervals.forEach(i => graph.markIntervalAsCovered(zoomLevel, i));
			}
		}
		if (Object.keys(uncoveredIntervalsByGraphId).length > 0) {
			console.debug("firing onDataNeeded: " + uncoveredIntervalsByGraphId);
			this.onZoomed.fire({
				zoomLevelIndex: zoomLevel,
				millisecondsPerPixel: this.getMillisecondsPerPixel(),
				neededIntervalsByGraphId: uncoveredIntervalsByGraphId,
				displayedInterval: createDtoLongInterval(domain[0], domain[1])
			});
		}

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

		this.getAllSeries().forEach(series => {
			series.updateZoomX(this.getCurrentZoomLevel(), this.getTransformedScaleX());
			series.redraw();
		});
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

	private getMillisecondsPerPixel() {
		let transformedScaleX = this.getTransformedScaleX();
		let domain = this.restrictDomainXToConfiguredInterval([+transformedScaleX.domain()[0], +transformedScaleX.domain()[1]]);
		return (domain[1] - domain[0]) / this.drawableWidth;
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
			this.xSelection = createDtoLongInterval(+transformedScaleX.invert(brushSelection[0] as number), +transformedScaleX.invert(brushSelection[1] as number));
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

		this.getAllSeries().forEach(s => s.setYRange([this.drawableHeight, 0]));
		this.updateZoomExtents();

		this.$xAxis.attr("transform", "translate(0," + this.drawableHeight + ")");

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
		return Object.keys(this.graphById).map(id => this.graphById[id]);
	}

	addData(zoomLevel: number, data: { [graphId: string]: DtoGraphData }): void {
		for (const [graphId, graphData] of Object.entries(data)) {
			let graph = this.graphById[graphId];
			if (graph != null) {
				graph.addData(zoomLevel, graphData);
			}
		}
		this.redraw();
	}

	resetAllData(intervalX: DtoLongInterval, newZoomLevels: DtoTimeChartZoomLevel[]): void {
		this.setIntervalX(intervalX);
		this.zoomLevels = newZoomLevels;
		Object.values(this.graphById).forEach(g => g.resetData());
		this.redraw();
	}

	resetGraphData(graphId: string): void {
		this.graphById[graphId]?.resetData();
		this.redraw();
	}

	setIntervalX(intervalX: DtoLongInterval): void {
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
			millisecondsPerPixel: this.getMillisecondsPerPixel(),
			displayedInterval: createDtoLongInterval(+domain[0], +domain[1]),
			zoomLevelIndex: currentZoomLevel,
			neededIntervalsByGraphId: null
		});
	}

	setSelectedInterval(intervalX: DtoLongInterval): void {
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

	setGraphs(graphs: DtoGraph[]) {
		graphs.forEach(lineConfig => {
			let existingSeries = this.graphById[lineConfig.id];
			if (existingSeries) {
				existingSeries.setConfig(lineConfig);
			} else {
				this.graphById[lineConfig.id] = this.createGraph(lineConfig);
			}
		});
		let lineConfigsById = graphs.reduce((previousValue, currentValue) => (previousValue[currentValue.id] = previousValue) && previousValue, {} as { [id: string]: DtoGraph });
		Object.keys(this.graphById).forEach(lineId => {
			if (!lineConfigsById[lineId]) {
				this.graphById[lineId].destroy();
				delete this.graphById[lineId];
			}
		});
		this.onResize();
	}

	addOrUpdateGraph(graph: DtoGraph) {
		let series = this.graphById[graph.id];
		if (series) {
			series.setConfig(graph);
		} else {
			this.graphById[graph.id] = this.createGraph(graph);
		}
		this.onResize();
	}
}



