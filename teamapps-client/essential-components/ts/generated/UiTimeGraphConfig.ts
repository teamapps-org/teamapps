/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiLongIntervalConfig} from "./UiLongIntervalConfig";
import {UiTimeChartZoomLevelConfig} from "./UiTimeChartZoomLevelConfig";
import {UiGraphConfig} from "./UiGraphConfig";
import {UiGraphDataConfig} from "./UiGraphDataConfig";
import {UiLineChartYScaleZoomMode} from "./UiLineChartYScaleZoomMode";
import {UiScaleType} from "./UiScaleType";
import {UiLineChartMouseScrollZoomPanMode} from "./UiLineChartMouseScrollZoomPanMode";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiTimeGraphConfig extends UiComponentConfig {
	_type?: string;
	yScaleZoomMode?: UiLineChartYScaleZoomMode;
	intervalX: UiLongIntervalConfig;
	zoomLevels: UiTimeChartZoomLevelConfig[];
	maxPixelsBetweenDataPoints: number;
	graphs: UiGraphConfig[];
	yScaleType?: UiScaleType;
	mouseScrollZoomPanMode?: UiLineChartMouseScrollZoomPanMode;
	locale?: string;
	timeZoneId?: string
}

export interface UiTimeGraphCommandHandler extends UiComponentCommandHandler {
	setIntervalX(intervalX: UiLongIntervalConfig): any;
	setMaxPixelsBetweenDataPoints(maxPixelsBetweenDataPoints: number): any;
	addData(zoomLevel: number, data: {[name: string]: UiGraphDataConfig}): any;
	resetGraphData(graphId: string): any;
	resetAllData(intervalX: UiLongIntervalConfig, newZoomLevels: UiTimeChartZoomLevelConfig[]): any;
	setMouseScrollZoomPanMode(mouseScrollZoomPanMode: UiLineChartMouseScrollZoomPanMode): any;
	setSelectedInterval(intervalX: UiLongIntervalConfig): any;
	setGraphs(graphs: UiGraphConfig[]): any;
	addOrUpdateGraph(graph: UiGraphConfig): any;
	zoomTo(intervalX: UiLongIntervalConfig): any;
}

export interface UiTimeGraphEventSource {
	onZoomed: TeamAppsEvent<UiTimeGraph_ZoomedEvent>;
	onIntervalSelected: TeamAppsEvent<UiTimeGraph_IntervalSelectedEvent>;
}

export interface UiTimeGraph_ZoomedEvent extends UiEvent {
	displayedInterval: UiLongIntervalConfig;
	zoomLevelIndex: number;
	millisecondsPerPixel: number;
	neededIntervalsByGraphId: {[name: string]: UiLongIntervalConfig[]}
}

export interface UiTimeGraph_IntervalSelectedEvent extends UiEvent {
	intervalX: UiLongIntervalConfig
}

