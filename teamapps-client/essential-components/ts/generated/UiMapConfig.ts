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
import {UiMapConfigConfig} from "./UiMapConfigConfig";
import {UiMapLocationConfig} from "./UiMapLocationConfig";
import {AbstractUiMapShapeConfig} from "./AbstractUiMapShapeConfig";
import {UiMapMarkerClientRecordConfig} from "./UiMapMarkerClientRecordConfig";
import {UiMapMarkerClusterConfig} from "./UiMapMarkerClusterConfig";
import {UiShapePropertiesConfig} from "./UiShapePropertiesConfig";
import {UiHeatMapDataConfig} from "./UiHeatMapDataConfig";
import {UiMapAreaConfig} from "./UiMapAreaConfig";
import {UiMapType} from "./UiMapType";
import {UiMapShapeType} from "./UiMapShapeType";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiMapConfig extends UiComponentConfig {
	_type?: string;
	markerTemplates: {[name: string]: UiTemplateConfig};
	accessToken?: string;
	mapConfig?: UiMapConfigConfig;
	mapType?: UiMapType;
	zoomLevel?: number;
	mapPosition?: UiMapLocationConfig;
	shapes?: {[name: string]: AbstractUiMapShapeConfig};
	markers?: UiMapMarkerClientRecordConfig[];
	markerCluster?: UiMapMarkerClusterConfig
}

export interface UiMapCommandHandler extends UiComponentCommandHandler {
	registerTemplate(id: string, template: UiTemplateConfig): any;
	addMarker(marker: UiMapMarkerClientRecordConfig): any;
	removeMarker(id: number): any;
	setMapMarkerCluster(cluster: UiMapMarkerClusterConfig): any;
	addShape(shapeId: string, shape: AbstractUiMapShapeConfig): any;
	updateShape(shapeId: string, shape: AbstractUiMapShapeConfig): any;
	removeShape(shapeId: string): any;
	clearShapes(): any;
	clearMarkers(): any;
	clearMarkerCluster(): any;
	clearHeatMap(): any;
	startDrawingShape(shapeType: UiMapShapeType, shapeProperties: UiShapePropertiesConfig): any;
	stopDrawingShape(): any;
	setZoomLevel(zoom: number): any;
	setLocation(location: UiMapLocationConfig): any;
	setMapType(mapType: UiMapType): any;
	setHeatMap(data: UiHeatMapDataConfig): any;
	fitBounds(southWest: UiMapLocationConfig, northEast: UiMapLocationConfig): any;
}

export interface UiMapEventSource {
	onZoomLevelChanged: TeamAppsEvent<UiMap_ZoomLevelChangedEvent>;
	onLocationChanged: TeamAppsEvent<UiMap_LocationChangedEvent>;
	onMapClicked: TeamAppsEvent<UiMap_MapClickedEvent>;
	onMarkerClicked: TeamAppsEvent<UiMap_MarkerClickedEvent>;
	onShapeDrawn: TeamAppsEvent<UiMap_ShapeDrawnEvent>;
}

export interface UiMap_ZoomLevelChangedEvent extends UiEvent {
	zoomLevel: number
}

export interface UiMap_LocationChangedEvent extends UiEvent {
	center: UiMapLocationConfig;
	displayedArea: UiMapAreaConfig
}

export interface UiMap_MapClickedEvent extends UiEvent {
	location: UiMapLocationConfig
}

export interface UiMap_MarkerClickedEvent extends UiEvent {
	markerId: number
}

export interface UiMap_ShapeDrawnEvent extends UiEvent {
	shapeId: string;
	shape: AbstractUiMapShapeConfig
}

