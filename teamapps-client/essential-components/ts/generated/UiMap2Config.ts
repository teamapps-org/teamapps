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
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiMapLocationConfig} from "./UiMapLocationConfig";
import {AbstractUiMapShapeConfig} from "./AbstractUiMapShapeConfig";
import {UiMapMarkerClientRecordConfig} from "./UiMapMarkerClientRecordConfig";
import {UiMapMarkerClusterConfig} from "./UiMapMarkerClusterConfig";
import {UiHeatMapDataConfig} from "./UiHeatMapDataConfig";
import {UiShapePropertiesConfig} from "./UiShapePropertiesConfig";
import {UiMapAreaConfig} from "./UiMapAreaConfig";
import {UiMapShapeType} from "./UiMapShapeType";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiMap2Config extends UiComponentConfig {
	_type?: string;
	markerTemplates: {[name: string]: UiTemplateConfig};
	baseApiUrl?: string;
	accessToken?: string;
	styleUrl?: string;
	displayAttributionControl?: boolean;
	zoomLevel?: number;
	mapPosition?: UiMapLocationConfig;
	shapes?: {[name: string]: AbstractUiMapShapeConfig};
	markers?: UiMapMarkerClientRecordConfig[];
	markerCluster?: UiMapMarkerClusterConfig
}

export interface UiMap2CommandHandler extends UiComponentCommandHandler {
	registerTemplate(id: string, template: UiTemplateConfig): any;
	addMarker(marker: UiMapMarkerClientRecordConfig): any;
	removeMarker(id: number): any;
	clearMarkers(): any;
	setMapMarkerCluster(cluster: UiMapMarkerClusterConfig): any;
	setHeatMap(data: UiHeatMapDataConfig): any;
	addShape(shapeId: string, shape: AbstractUiMapShapeConfig): any;
	updateShape(shapeId: string, shape: AbstractUiMapShapeConfig): any;
	removeShape(shapeId: string): any;
	clearShapes(): any;
	startDrawingShape(shapeType: UiMapShapeType, shapeProperties: UiShapePropertiesConfig): any;
	stopDrawingShape(): any;
	setZoomLevel(zoom: number): any;
	setLocation(location: UiMapLocationConfig, animationDurationMillis: number, targetZoomLevel: number): any;
	setStyleUrl(styleUrl: string): any;
	fitBounds(southWest: UiMapLocationConfig, northEast: UiMapLocationConfig): any;
}

export interface UiMap2EventSource {
	onZoomLevelChanged: TeamAppsEvent<UiMap2_ZoomLevelChangedEvent>;
	onLocationChanged: TeamAppsEvent<UiMap2_LocationChangedEvent>;
	onMapClicked: TeamAppsEvent<UiMap2_MapClickedEvent>;
	onMarkerClicked: TeamAppsEvent<UiMap2_MarkerClickedEvent>;
	onShapeDrawn: TeamAppsEvent<UiMap2_ShapeDrawnEvent>;
}

export interface UiMap2_ZoomLevelChangedEvent extends UiEvent {
	zoomLevel: number
}

export interface UiMap2_LocationChangedEvent extends UiEvent {
	center: UiMapLocationConfig;
	displayedArea: UiMapAreaConfig
}

export interface UiMap2_MapClickedEvent extends UiEvent {
	location: UiMapLocationConfig
}

export interface UiMap2_MarkerClickedEvent extends UiEvent {
	markerId: number
}

export interface UiMap2_ShapeDrawnEvent extends UiEvent {
	shapeId: string;
	shape: AbstractUiMapShapeConfig
}

