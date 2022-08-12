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
///<reference types="leaflet.heat"/>


import {AbstractUiComponent} from "./AbstractUiComponent";
import {generateUUID, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {createUiMapPolylineConfig, UiMapPolylineConfig} from "../generated/UiMapPolylineConfig";
import {UiMapMarkerClusterConfig} from "../generated/UiMapMarkerClusterConfig";
import {UiHeatMapDataConfig} from "../generated/UiHeatMapDataConfig";
import {
	UiMap_LocationChangedEvent,
	UiMap_MapClickedEvent,
	UiMap_MarkerClickedEvent,
	UiMap_ShapeDrawnEvent,
	UiMap_ZoomLevelChangedEvent,
	UiMapCommandHandler,
	UiMapConfig,
	UiMapEventSource
} from "../generated/UiMapConfig";
import {UiMapType} from "../generated/UiMapType";
import L, {Circle, LatLngBounds, LatLngExpression, Layer, Marker, PathOptions, Polygon, Polyline, Rectangle} from "leaflet";
import "leaflet.markercluster";
import "leaflet.heat";
import "leaflet-draw";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {isGridTemplate} from "./TemplateRegistry";
import {isUiGlyphIconElement, isUiIconElement, isUiImageElement} from "./util/UiGridTemplates";
import {UiMapMarkerClientRecordConfig} from "../generated/UiMapMarkerClientRecordConfig";
import {createUiMapLocationConfig, UiMapLocationConfig} from "../generated/UiMapLocationConfig";
import {createUiMapAreaConfig} from "../generated/UiMapAreaConfig";
import {UiMapShapeType} from "../generated/UiMapShapeType";
import {createUiMapCircleConfig, UiMapCircleConfig} from "../generated/UiMapCircleConfig";
import {createUiMapPolygonConfig, UiMapPolygonConfig} from "../generated/UiMapPolygonConfig";
import {createUiMapRectangleConfig, UiMapRectangleConfig} from "../generated/UiMapRectangleConfig";
import {AbstractUiMapShapeConfig} from "../generated/AbstractUiMapShapeConfig";
import {UiShapePropertiesConfig} from "../generated/UiShapePropertiesConfig";
import {UiMapConfigConfig} from "../generated/UiMapConfigConfig";

export function isUiMapCircle(shapeConfig: AbstractUiMapShapeConfig): shapeConfig is UiMapCircleConfig {
	return shapeConfig._type === "UiMapCircle";
}

export function isUiMapPolygon(shapeConfig: AbstractUiMapShapeConfig): shapeConfig is UiMapPolygonConfig {
	return shapeConfig._type === "UiMapPolygon";
}

export function isUiMapPolyline(shapeConfig: AbstractUiMapShapeConfig): shapeConfig is UiMapPolylineConfig {
	return shapeConfig._type === "UiMapPolyline";
}

export function isUiMapRectangle(shapeConfig: AbstractUiMapShapeConfig): shapeConfig is UiMapRectangleConfig {
	return shapeConfig._type === "UiMapRectangle";
}

export class UiMap extends AbstractUiComponent<UiMapConfig> implements UiMapCommandHandler, UiMapEventSource {

	public readonly onZoomLevelChanged: TeamAppsEvent<UiMap_ZoomLevelChangedEvent> = new TeamAppsEvent();
	public readonly onLocationChanged: TeamAppsEvent<UiMap_LocationChangedEvent> = new TeamAppsEvent();
	public readonly onMapClicked: TeamAppsEvent<UiMap_MapClickedEvent> = new TeamAppsEvent();
	public readonly onMarkerClicked: TeamAppsEvent<UiMap_MarkerClickedEvent> = new TeamAppsEvent();
	public readonly onShapeDrawn: TeamAppsEvent<UiMap_ShapeDrawnEvent> = new TeamAppsEvent();

	private id: any;
	private leaflet: L.Map;
	private tileLayer: any;
	private clusterLayer: any;
	private heatMapLayer: any;
	private markerTemplateRenderers: { [templateName: string]: Renderer } = {};

	private $map: HTMLElement;
	private shapesById: { [lineId: string]: Layer } = {};
	private markersByClientId: { [id: number]: Marker } = {};

	private drawCircleFeature: L.Draw.Circle;
	private drawPolygonFeature: L.Draw.Polygon;
	private drawPolylineFeature: L.Draw.Polyline;
	private drawRectangleFeature: L.Draw.Rectangle;

	constructor(config: UiMapConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$map = parseHtml('<div class="UiMap">');
		this.id = this.getId();
		this.createLeafletMap();
		Object.keys(config.markerTemplates).forEach(templateName => this.registerTemplate(templateName, config.markerTemplates[templateName]));
		Object.keys(config.shapes).forEach(shapeId => {
			this.addShape(shapeId, config.shapes[shapeId]);
		});
		config.markers.forEach(m => this.addMarker(m));
		this.setMapMarkerCluster(config.markerCluster);
	}

	public doGetMainElement(): HTMLElement {
		return this.$map;
	}

	@executeWhenFirstDisplayed()
	private createLeafletMap(): void {
		this.leaflet = L.map(this.$map, {
			zoomControl: false,
			attributionControl: false,
			preferCanvas: true
		});

		let center: LatLngExpression = [51.505, -0.09];
		if (this._config.mapPosition != null) {
			center = [this._config.mapPosition.latitude, this._config.mapPosition.longitude];
		}
		this.leaflet.setView(center, this._config.zoomLevel);
		if (this._config.mapConfig != null) {
			this.setMapConfig(this._config.mapConfig);
		} else {
			this.setMapType(this._config.mapType);
		}
		this.leaflet.on('click', (event) => {
			this.onMapClicked.fire({
				location: createUiMapLocationConfig((event as any).latlng.lat, (event as any).latlng.lng)
			});
		});
		this.leaflet.on('zoomend', (event) => {
			this.onZoomLevelChanged.fire({
				zoomLevel: this.leaflet.getZoom()
			});
		});
		this.leaflet.on('moveend', (event) => {
			const location = this.leaflet.getCenter();
			const bounds = this.leaflet.getBounds();
			this.onLocationChanged.fire({
				center: createUiMapLocationConfig(location.lat, location.lng),
				displayedArea: createUiMapAreaConfig(bounds.getNorth(), bounds.getSouth(), bounds.getWest(), bounds.getEast())
			});
		});

		this.drawCircleFeature = new L.Draw.Circle(this.leaflet as L.DrawMap, {});
		this.drawPolygonFeature = new L.Draw.Polygon(this.leaflet as L.DrawMap, {});
		this.drawPolylineFeature = new L.Draw.Polyline(this.leaflet as L.DrawMap, {});
		this.drawRectangleFeature = new L.Draw.Rectangle(this.leaflet as L.DrawMap, {});

		//let layerGroup = L.layerGroup().addTo(this.leaflet);

		this.leaflet.on(L.Draw.Event.CREATED, (e: L.DrawEvents.Created) => {
			var type = e.layerType,
				layer = e.layer;
			let shapeId = generateUUID();
			this.shapesById[shapeId] = layer;
			if (type === 'circle') {
				let circle = layer as Circle;
				this.onShapeDrawn.fire({
					shapeId: shapeId,
					shape: createUiMapCircleConfig({center: toUiLocation(circle.getLatLng()), radius: circle.getRadius()})
				});
			} else if (type === 'polygon') {
				let polygon = layer as Polygon;
				let path = flattenArray(polygon.getLatLngs()).map(ll => toUiLocation(ll));
				this.onShapeDrawn.fire({shapeId: shapeId, shape: createUiMapPolygonConfig({path})});
			} else if (type === 'polyline') {
				let polyline = layer as Polyline;
				let path = flattenArray(polyline.getLatLngs()).map(ll => toUiLocation(ll));
				this.onShapeDrawn.fire({shapeId: shapeId, shape: createUiMapPolylineConfig({path})});
			} else if (type === 'rectangle') {
				let rectangle = layer as Rectangle;
				this.onShapeDrawn.fire({
					shapeId: shapeId,
					shape: createUiMapRectangleConfig({
						l1: toUiLocation(rectangle.getBounds().getNorthWest()),
						l2: toUiLocation(rectangle.getBounds().getSouthEast())
					})
				});
			}
			this.leaflet.addLayer(layer);
		});
	}

	@executeWhenFirstDisplayed()
	public addShape(shapeId: string, shapeConfig: AbstractUiMapShapeConfig): void {
		if (isUiMapCircle(shapeConfig)) {
			this.shapesById[shapeId] = L.circle(
				this.createLeafletLatLng(shapeConfig.center), shapeConfig.radius, createPathOptions(shapeConfig.shapeProperties)
			).addTo(this.leaflet);
		} else if (isUiMapPolygon(shapeConfig)) {
			let polyPath = new Array(shapeConfig.path.length);
			for (let i = 0; i < shapeConfig.path.length; i++) {
				let loc = shapeConfig.path[i];
				polyPath[i] = this.convertToLatLng(loc);
			}
			this.shapesById[shapeId] = L.polygon(polyPath, createPathOptions(shapeConfig.shapeProperties)).addTo(this.leaflet);
		} else if (isUiMapPolyline(shapeConfig)) {
			let polyPath = new Array(shapeConfig.path.length);
			for (let i = 0; i < shapeConfig.path.length; i++) {
				let loc = shapeConfig.path[i];
				polyPath[i] = this.convertToLatLng(loc);
			}
			this.shapesById[shapeId] = L.polyline(polyPath, createPathOptions(shapeConfig.shapeProperties)).addTo(this.leaflet);
		} else if (isUiMapRectangle(shapeConfig)) {
			this.shapesById[shapeId] = L.rectangle(
				new LatLngBounds(this.createLeafletLatLng(shapeConfig.l1), this.createLeafletLatLng(shapeConfig.l2)),
				createPathOptions(shapeConfig.shapeProperties)
			).addTo(this.leaflet);
		}
		return;
	}

	@executeWhenFirstDisplayed()
	removeShape(shapeId: string): void {
		let shape = this.shapesById[shapeId];
		if (shape == null) {
			this.logger.warn(`There is no shape with id ${shapeId}`);
			return;
		}
		this.leaflet.removeLayer(shape);
	}

	@executeWhenFirstDisplayed()
	updateShape(shapeId: string, shapeConfig: AbstractUiMapShapeConfig): void {
		let shape = this.shapesById[shapeId];
		if (shape == null) {
			this.logger.warn(`There is no shape with id ${shapeId}`);
			return;
		}
		this.removeShape(shapeId);
		this.addShape(shapeId, shapeConfig); // todo might get optimized...
	}

	@executeWhenFirstDisplayed()
	public addPolylinePoints(lineId: string, points: UiMapLocationConfig[]): void {
		let polyline = this.shapesById[lineId] as Polyline;
		if (polyline == null) {
			this.logger.warn(`There is no polyline with id ${lineId}`);
			return;
		}
		points.forEach(p => polyline.addLatLng(this.convertToLatLng(p)));
	}

	private convertToLatLng(loc: UiMapLocationConfig): LatLngExpression {
		return [loc.latitude, loc.longitude];
	}

	@executeWhenFirstDisplayed()
	public addMarker(markerConfig: UiMapMarkerClientRecordConfig): void {
		this.createMarker(markerConfig).addTo(this.leaflet);
	}

	@executeWhenFirstDisplayed()
	removeMarker(id: number): void {
		const marker = this.markersByClientId[id];
		if (marker != null) {
			this.leaflet.removeLayer(marker);
		}
		delete this.markersByClientId[id];
	}

	@executeWhenFirstDisplayed()
	clearMarkers() {
		Object.keys(this.markersByClientId).forEach(clientId => {
			this.removeMarker(Number(clientId));
		});
	}

	@executeWhenFirstDisplayed()
	clearShapes() {
		Object.keys(this.shapesById).forEach(clientId => {
			this.removeShape(clientId);
		});
	}

	private createMarker(markerConfig: UiMapMarkerClientRecordConfig) {
		let renderer = this.markerTemplateRenderers[markerConfig.templateId] || this._context.templateRegistry.getTemplateRendererByName(markerConfig.templateId);
		let iconWidth: number = 0;
		if (isGridTemplate(renderer.template)) {
			let iconElement = renderer.template.elements.filter(e => isUiGlyphIconElement(e) || isUiImageElement(e) || isUiIconElement(e))[0];
			if (!iconElement || !markerConfig.values[iconElement.property]) {
				iconWidth = 0;
			} else if (iconElement) {
				if (isUiGlyphIconElement(iconElement)) {
					iconWidth = iconElement.size;
				} else if (isUiImageElement(iconElement)) {
					iconWidth = iconElement.width;
				} else if (isUiIconElement(iconElement)) {
					iconWidth = iconElement.size;
				}
			}
		}

		let divIcon = L.divIcon({
			html: renderer.render(markerConfig.values),
			className: "custom-div-icon",
			iconAnchor: [markerConfig.offsetPixelsX, markerConfig.offsetPixelsY],
			popupAnchor: [(iconWidth / 2) - 6, -5]
		} as L.DivIconOptions);
		let marker = L.marker(new L.LatLng(markerConfig.location.latitude, markerConfig.location.longitude), {
			title: markerConfig.asString,
			icon: divIcon
		});
		marker.bindPopup(markerConfig.asString);
		marker.on("click", event1 => this.onMarkerClicked.fire({
			markerId: markerConfig.id
		}));
		this.markersByClientId[markerConfig.id] = marker;
		return marker;
	};

	@executeWhenFirstDisplayed()
	public setZoomLevel(zoomLevel: number): void {
		this.leaflet.setZoom(zoomLevel);
	}

	public setMapConfig(mapConfig: UiMapConfigConfig): void {
		const token = this._config.accessToken;
		let removeLayer = true;
		let layer = L.tileLayer(mapConfig.urlTemplate, {
			minZoom: mapConfig.minZoom,
			maxZoom: mapConfig.maxZoom,
			attribution: mapConfig.attribution,
		});
		if (removeLayer && this.tileLayer) {
			this.leaflet.removeLayer(this.tileLayer);
		}
		this.tileLayer = layer;
		this.leaflet.addLayer(this.tileLayer);
	}

	public setMapType(mapType: UiMapType): void {
		const token = this._config.accessToken;
		let layer;
		let removeLayer = true;
		switch (mapType) {
			case UiMapType.INTERNAL:
				layer = L.tileLayer('tiles/{z}/{x}/{y}.png', {
					maxZoom: 9,
				});
				break;
			case UiMapType.INTERNAL_DARK:
				layer = L.tileLayer('http://localhost/styles/dark-matter/{z}/{x}/{y}.png', {
					maxZoom: 20,
				});
				break;
			case UiMapType.INTERNAL_DARK_HIGH_RES:
				layer = L.tileLayer('http://localhost/styles/dark-matter/{z}/{x}/{y}@2x.png', {
					maxZoom: 20,
				});
				break;
			case UiMapType.MAP_BOX_STREETS:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.streets',
					accessToken: token
				} as any);
				break;
			case UiMapType.MAP_BOX_STREETS_BASIC:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.streets-basic',
					accessToken: token
				} as any);
				break;
			case UiMapType.MAP_BOX_STREETS_SATELLITE:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.streets-satellite',
					accessToken: token
				} as any);
				break;
			case UiMapType.MAP_BOX_SATELLITE:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.satellite',
					accessToken: token
				} as any);
				break;
			case UiMapType.MAP_BOX_RUN_BIKE_HIKE:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.run-bike-hike',
					accessToken: token
				} as any);
				break;
			case UiMapType.MAP_BOX_DARK:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.dark',
					accessToken: token
				} as any);
				break;
			case UiMapType.MAP_BOX_OUTDOORS:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.outdoors',
					accessToken: token
				} as any);
				break;
			case UiMapType.MAP_BOX_EMERALD:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.emerald',
					accessToken: token
				} as any);
				break;
			case UiMapType.MAP_QUEST_OSM:
				layer = L.tileLayer('http://otile{s}.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png', {
					maxZoom: 18,
					subdomains: ['1234']
				});
				break;
			case UiMapType.MAP_QUEST_SATELLITE:
				layer = L.tileLayer('http://otile{s}.mqcdn.com/tiles/1.0.0/sat/{z}/{x}/{y}.png', {
					maxZoom: 11,
					subdomains: ['1234']
				});
				break;
			case UiMapType.NASA_EARTH_AT_NIGHT:
				layer = L.tileLayer('http://map1.vis.earthdata.nasa.gov/wmts-webmerc/VIIRS_CityLights_2012/default/{time}/{tilematrixset}{maxZoom}/{z}/{y}/{x}.{format}', {
					attribution: 'Imagery provided by services from the Global Imagery Browse Services (GIBS), operated by the NASA/GSFC/Earth Science Data and Information System (<a href="https://earthdata.nasa.gov">ESDIS</a>) with funding provided by NASA/HQ.',
					minZoom: 1,
					maxZoom: 8,
					format: 'jpg',
					time: '',
					tilematrixset: 'GoogleMapsCompatible_Level'
				} as any);
				break;
			case UiMapType.OSM_TOPO_MAP:
				layer = L.tileLayer('http://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
					maxZoom: 15,
					attribution: 'Map data: &copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>, <a href="http://viewfinderpanoramas.org">SRTM</a> | Map style: &copy; <a href="https://opentopomap.org">OpenTopoMap</a> (<a href="https://creativecommons.org/licenses/by-sa/3.0/">CC-BY-SA</a>)'
				});
				break;
			case UiMapType.INFO_WEATHER_TEMPERATURE:
				layer = L.tileLayer('http://{s}.tile.openweathermap.org/map/temp/{z}/{x}/{y}.png', {
					maxZoom: 19,
					attribution: 'Map data &copy; <a href="http://openweathermap.org">OpenWeatherMap</a>',
					opacity: 0.5
				});
				removeLayer = false;
				break;

			case UiMapType.THUNDERFOREST_DARK:
				layer = L.tileLayer('https://{s}.tile.thunderforest.com/{id}/{z}/{x}/{y}.png?apikey={accessToken}', {
					//attribution: '&copy; <a href="http://www.thunderforest.com/">Thunderforest</a>, &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
					maxZoom: 22,
					id: 'transport-dark',
					accessToken: token
				} as any);
				break;
			case UiMapType.THUNDERFOREST_TRANSPORT:
				layer = L.tileLayer('https://{s}.tile.thunderforest.com/{id}/{z}/{x}/{y}.png?apikey={accessToken}', {
					//attribution: '&copy; <a href="http://www.thunderforest.com/">Thunderforest</a>, &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
					maxZoom: 22,
					id: 'transport',
					accessToken: token
				} as any);
				break;

			case UiMapType.WIKIMEDIA:
				layer = L.tileLayer('https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}{r}.png', {
					//attribution: '<a href="https://wikimediafoundation.org/wiki/Maps_Terms_of_Use">Wikimedia</a>',
					minZoom: 1,
					maxZoom: 19,
				} as any);
				break;
		}
		if (removeLayer && this.tileLayer) {
			this.leaflet.removeLayer(this.tileLayer);
		}
		this.tileLayer = layer;
		this.leaflet.addLayer(this.tileLayer);
	}

	@executeWhenFirstDisplayed()
	public setLocation(location: UiMapLocationConfig): void {
		this.leaflet.setView(this.createLeafletLatLng(location), this.leaflet.getZoom());
	}

	private createLeafletLatLng(location: UiMapLocationConfig) {
		return new L.LatLng(location.latitude, location.longitude);
	}

	@executeWhenFirstDisplayed()
	public setMapMarkerCluster(clusterConfig: UiMapMarkerClusterConfig): void {
		this.clearMarkerCluster();
		if (clusterConfig) {
			this.clusterLayer = (L as any).markerClusterGroup();
			for (let i = 0; i < clusterConfig.markers.length; i++) {
				let markerConfig = clusterConfig.markers[i];
				this.clusterLayer.addLayer(this.createMarker(markerConfig));
			}
			this.leaflet.addLayer(this.clusterLayer);
		}
	}

	@executeWhenFirstDisplayed()
	public clearMarkerCluster() {
		if (this.clusterLayer) {
			this.leaflet.removeLayer(this.clusterLayer);
		}
	}

	@executeWhenFirstDisplayed()
	public setHeatMap(data: UiHeatMapDataConfig): void {
		this.clearHeatMap();
		if (data) {
			let elements = new Array(data.elements.length);
			for (let i = 0; i < data.elements.length; i++) {
				let el = data.elements[i];
				elements[i] = [el.latitude, el.longitude, el.count];
			}
			this.heatMapLayer = L.heatLayer(elements, {
				radius: data.radius,
				blur: data.blur,
				max: data.maxCount
			});
			this.leaflet.addLayer(this.heatMapLayer);
		}
	}

	@executeWhenFirstDisplayed()
	public clearHeatMap() {
		if (this.heatMapLayer) {
			this.leaflet.removeLayer(this.heatMapLayer);
		}
	}

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.markerTemplateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	fitBounds(southWest: UiMapLocationConfig, northEast: UiMapLocationConfig): void {
		this.leaflet.fitBounds(L.latLngBounds(this.createLeafletLatLng(southWest), this.createLeafletLatLng(northEast)))
	}

	public onResize(): void {
		this.leaflet.invalidateSize();
	}

	startDrawingShape(shapeType: UiMapShapeType, shapeProperties: UiShapePropertiesConfig): void {
		let drawFeature: L.Draw.Feature;
		switch (shapeType) {
			case UiMapShapeType.CIRCLE      :
				drawFeature = this.drawCircleFeature;
				break;
			case UiMapShapeType.POLYGON     :
				drawFeature = this.drawPolygonFeature;
				break;
			case UiMapShapeType.POLYLINE    :
				drawFeature = this.drawPolylineFeature;
				break;
			case UiMapShapeType.RECTANGLE   :
				drawFeature = this.drawRectangleFeature;
				break;
		}
		drawFeature.setOptions({shapeOptions: createPathOptions(shapeProperties)})
		drawFeature.enable();
	}

	stopDrawingShape(): void {
		this.drawCircleFeature.disable();
		this.drawPolygonFeature.disable();
		this.drawPolylineFeature.disable();
		this.drawRectangleFeature.disable();
	}
}

function createPathOptions(shapePropertiesConfig: UiShapePropertiesConfig): PathOptions {
	return {
		color: shapePropertiesConfig.strokeColor,
		fill: shapePropertiesConfig.fillColor != null,
		fillColor: shapePropertiesConfig.fillColor,
		dashArray: shapePropertiesConfig.strokeDashArray,
		weight: shapePropertiesConfig.strokeWeight
	};
}

function toUiLocation(latlng: L.LatLng) {
	return createUiMapLocationConfig(latlng.lat, latlng.lng);
}

function flattenArray<T>(arr: T[] | T[][] | T[][][]): T[] {
	return (arr as any[]).reduce((acc, cur) => {
		if (Array.isArray(cur)) {
			acc.push.apply(acc, flattenArray(cur));
		} else {
			acc.push(cur);
		}

		return acc;
	}, []);
}

TeamAppsUiComponentRegistry.registerComponentClass("UiMap", UiMap);
