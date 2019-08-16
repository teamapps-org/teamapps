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
///<reference types="leaflet.heat"/>


import {AbstractUiComponent} from "./AbstractUiComponent";
import {parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiMapPolylineConfig} from "../generated/UiMapPolylineConfig";
import {UiMapMarkerClusterConfig} from "../generated/UiMapMarkerClusterConfig";
import {UiHeatMapDataConfig} from "../generated/UiHeatMapDataConfig";
import {UiMap_LocationChangedEvent, UiMap_MapClickedEvent, UiMap_MarkerClickedEvent, UiMap_ZoomLevelChangedEvent, UiMapCommandHandler, UiMapConfig, UiMapEventSource} from "../generated/UiMapConfig";
import {UiMapType} from "../generated/UiMapType";
import * as L from "leaflet";
import {LatLngExpression, Marker, Polyline} from "leaflet";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {isGridTemplate} from "./TemplateRegistry";
import {isUiGlyphIconElement, isUiIconElement, isUiImageElement} from "./util/UiGridTemplates";
import {UiMapMarkerClientRecordConfig} from "../generated/UiMapMarkerClientRecordConfig";
import {createUiMapLocationConfig, UiMapLocationConfig} from "../generated/UiMapLocationConfig";
import {createUiMapAreaConfig} from "../generated/UiMapAreaConfig";

export class UiMap extends AbstractUiComponent<UiMapConfig> implements UiMapCommandHandler, UiMapEventSource {

	public onZoomLevelChanged: TeamAppsEvent<UiMap_ZoomLevelChangedEvent> = new TeamAppsEvent(this);
	public onLocationChanged: TeamAppsEvent<UiMap_LocationChangedEvent> = new TeamAppsEvent(this);
	public onMapClicked: TeamAppsEvent<UiMap_MapClickedEvent> = new TeamAppsEvent(this);
	public onMarkerClicked: TeamAppsEvent<UiMap_MarkerClickedEvent> = new TeamAppsEvent(this);

	private id: any;
	private leaflet: L.Map;
	private tileLayer: any;
	private clusterLayer: any;
	private heatMapLayer: any;
	private markerTemplateRenderers: { [templateName: string]: Renderer } = {};

	private $map: HTMLElement;
	private polyLinesById: {[lineId: string]: Polyline} = {};
	private markersByClientId: { [id: number]: Marker } = {};

	constructor(config: UiMapConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$map = parseHtml('<div class="UiMap">');
		this.id = this.getId();
		this.createLeafletMap();
		Object.keys(config.markerTemplates).forEach(templateName => this.registerTemplate(templateName, config.markerTemplates[templateName]));
		Object.keys(config.polylines).forEach(lineId => {
			this.addPolyline(lineId, config.polylines[lineId]);
		});
		config.markers.forEach(m => this.addMarker(m));
	}

	public getMainDomElement(): HTMLElement {
		return this.$map;
	}

	@executeWhenFirstDisplayed()
	private createLeafletMap(): void {
        this.leaflet = L.map(this.$map, {
			zoomControl: false,
	        attributionControl: false
		});

        let center:LatLngExpression = [51.505, -0.09];
		if (this._config.mapPosition != null) {
            center = [this._config.mapPosition.latitude, this._config.mapPosition.longitude];
        }
		this.leaflet.setView(center, this._config.zoomLevel);
		this.setMapType(this._config.mapType);
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
	}

	@executeWhenFirstDisplayed()
	public addPolyline(lineId: string, polylineConfig: UiMapPolylineConfig): void {
		let polyPath = new Array(polylineConfig.path.length);
		for (let i = 0; i < polylineConfig.path.length; i++) {
			let loc = polylineConfig.path[i];
			polyPath[i] = this.convertToLatLng(loc);
		}
		this.polyLinesById[lineId] = L.polyline(
			polyPath, {
				color: polylineConfig.shapeProperties.strokeColor,
				fill: false,
				dashArray: polylineConfig.shapeProperties.strokeDashArray,
				weight: polylineConfig.shapeProperties.strokeWeight
			}
		).addTo(this.leaflet);
		return;
	}

	private convertToLatLng(loc: UiMapLocationConfig): LatLngExpression {
		return [loc.latitude, loc.longitude];
	}

	@executeWhenFirstDisplayed()
	public addPolylinePoints(lineId: string, points: UiMapLocationConfig[]): void {
		let polyline = this.polyLinesById[lineId];
		if (polyline == null) {
			this.logger.warn(`There is no polyline with id ${lineId}`);
			return;
		}
		points.forEach(p => polyline.addLatLng(this.convertToLatLng(p)));
	}

	@executeWhenFirstDisplayed()
	public removePolyline(lineId: string): void {
		let polyline = this.polyLinesById[lineId];
		if (polyline == null) {
			this.logger.warn(`There is no polyline with id ${lineId}`);
			return;
		}
		this.leaflet.removeLayer(polyline);
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

	private createMarker(markerConfig: UiMapMarkerClientRecordConfig) {
		let renderer = this.markerTemplateRenderers[markerConfig.templateId] || this._context.templateRegistry.getTemplateRendererByName(markerConfig.templateId);
		let iconWidth: number = 0;
		if (isGridTemplate(renderer.template)) {
			let iconElement = renderer.template.elements.filter(e => isUiGlyphIconElement(e) || isUiImageElement(e) || isUiIconElement(e))[0];
			if (!iconElement || !markerConfig.values[iconElement.dataKey]) {
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

	public setMapType(mapType: UiMapType): void {
		const mapBoxAccessToken = this._config.accessToken;
		let layer;
		let removeLayer = true;
		switch (mapType) {
			case UiMapType.INTERNAL:
				layer = L.tileLayer('tiles/{z}/{x}/{y}.png', {
					maxZoom: 9,
				});
				break;
			case UiMapType.MAP_BOX_STREETS:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.streets',
					accessToken: mapBoxAccessToken
				} as any);
				break;
			case UiMapType.MAP_BOX_STREETS_BASIC:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.streets-basic',
					accessToken: mapBoxAccessToken
				} as any);
				break;
			case UiMapType.MAP_BOX_STREETS_SATELLITE:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.streets-satellite',
					accessToken: mapBoxAccessToken
				} as any);
				break;
			case UiMapType.MAP_BOX_SATELLITE:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.satellite',
					accessToken: mapBoxAccessToken
				} as any);
				break;
			case UiMapType.MAP_BOX_RUN_BIKE_HIKE:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.run-bike-hike',
					accessToken: mapBoxAccessToken
				} as any);
				break;
			case UiMapType.MAP_BOX_DARK:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.dark',
					accessToken: mapBoxAccessToken
				} as any);
				break;
			case UiMapType.MAP_BOX_OUTDOORS:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.outdoors',
					accessToken: mapBoxAccessToken
				} as any);
				break;
			case UiMapType.MAP_BOX_EMERALD:
				layer = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
					maxZoom: 18,
					id: 'mapbox.emerald',
					accessToken: mapBoxAccessToken
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
		}
		if (removeLayer && this.tileLayer) {
			this.leaflet.removeLayer(this.tileLayer);
		}
		this.tileLayer = layer;
		this.leaflet.addLayer(this.tileLayer);
	}

	@executeWhenFirstDisplayed()
	public setLocation(location: UiMapLocationConfig): void {
		this.leaflet.setView(new L.LatLng(location.latitude, location.longitude), this.leaflet.getZoom());
	}

	@executeWhenFirstDisplayed()
	public setMapMarkerCluster(clusterConfig: UiMapMarkerClusterConfig): void {
		if (this.clusterLayer) {
			this.leaflet.removeLayer(this.clusterLayer);
		}
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
	public setHeatMap(data: UiHeatMapDataConfig): void {
		if (this.heatMapLayer) {
			this.leaflet.removeLayer(this.heatMapLayer);
		}
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

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.markerTemplateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	public onResize(): void {
		this.leaflet.invalidateSize();
	}

	public destroy(): void {
		// nothing to do
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMap", UiMap);
