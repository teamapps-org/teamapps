/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
import {parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {
	UiMap2_LocationChangedEvent,
	UiMap2_MapClickedEvent,
	UiMap2_MarkerClickedEvent,
	UiMap2_ShapeDrawnEvent,
	UiMap2_ZoomLevelChangedEvent,
	UiMap2CommandHandler,
	UiMap2Config,
	UiMap2EventSource
} from "../generated/UiMap2Config";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import mapboxgl, {GeoJSONSource, LngLatLike, Map as MapBoxMap, Marker} from "maplibre-gl";
import {createUiMapLocationConfig, UiMapLocationConfig} from "../generated/UiMapLocationConfig";
import {createUiMapAreaConfig} from "../generated/UiMapAreaConfig";
import {UiMapMarkerClientRecordConfig} from "../generated/UiMapMarkerClientRecordConfig";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {AbstractUiMapShapeConfig} from "../generated/AbstractUiMapShapeConfig";
import {isUiMapCircle, isUiMapPolygon, isUiMapPolyline, isUiMapRectangle} from "./UiMap";
import {Feature, Point, Position} from "geojson";
import {UiMapMarkerClusterConfig} from "../generated/UiMapMarkerClusterConfig";
import * as d3 from "d3";
import {DeferredExecutor} from "./util/DeferredExecutor";
import {AbstractUiMapShapeChangeConfig} from "../generated/AbstractUiMapShapeChangeConfig";
import {UiPolylineAppendConfig} from "../generated/UiPolylineAppendConfig";
import {UiMapPolylineConfig} from "../generated/UiMapPolylineConfig";

export class UiMap2 extends AbstractUiComponent<UiMap2Config> implements UiMap2EventSource, UiMap2CommandHandler {

	public readonly onZoomLevelChanged: TeamAppsEvent<UiMap2_ZoomLevelChangedEvent> = new TeamAppsEvent({throttlingMode: "throttle", delay: 500});
	public readonly onLocationChanged: TeamAppsEvent<UiMap2_LocationChangedEvent> = new TeamAppsEvent({throttlingMode: "throttle", delay: 500});
	public readonly onMapClicked: TeamAppsEvent<UiMap2_MapClickedEvent> = new TeamAppsEvent();
	public readonly onMarkerClicked: TeamAppsEvent<UiMap2_MarkerClickedEvent> = new TeamAppsEvent();
	public readonly onShapeDrawn: TeamAppsEvent<UiMap2_ShapeDrawnEvent> = new TeamAppsEvent();

	private $map: HTMLElement;
	private map: MapBoxMap;
	private markerTemplateRenderers: { [templateName: string]: Renderer } = {};
	private markersByClientId: { [id: number]: Marker } = {};
	private shapesById: Map<string, { config: AbstractUiMapShapeConfig }> = new Map();

	private deferredExecutor: DeferredExecutor = new DeferredExecutor();

	constructor(config: UiMap2Config, context: TeamAppsUiContext) {
		super(config, context);
		this.$map = parseHtml('<div class="UiMap2">');

		mapboxgl.baseApiUrl = config.baseApiUrl;
		mapboxgl.accessToken = config.accessToken;

		this.map = new MapBoxMap({
			container: this.$map,
			style: config.styleUrl,
			center: this.convertToLngLatLike(config.mapPosition),
			hash: false, // don't change the URL!
			zoom: 9, // starting zoom
			attributionControl: config.displayAttributionControl,
		});
		this.map.on('load', () => {
			this.onResize();
			this.deferredExecutor.ready = true;
		});

		this.map.on("zoom", ev => {
			this.onZoomLevelChanged.fire({zoomLevel: this.map.getZoom()})
		});
		this.map.on("move", ev => {
			let center = this.map.getCenter();
			let bounds = this.map.getBounds();
			this.onLocationChanged.fire({
				center: createUiMapLocationConfig(center.lat, center.lng),
				displayedArea: createUiMapAreaConfig(bounds.getNorth(), bounds.getSouth(), bounds.getWest(), bounds.getEast())
			});
		})
		this.map.on("click", ev => {
			this.onMapClicked.fire({location: createUiMapLocationConfig(ev.lngLat.lat, ev.lngLat.lng)})
		})

		Object.keys(config.markerTemplates).forEach(templateName => this.registerTemplate(templateName, config.markerTemplates[templateName]));

		Object.keys(config.shapes).forEach(shapeId => {
			this.addShape(shapeId, config.shapes[shapeId]);
		});

		if (config.markerCluster != null) {
			this.setMapMarkerCluster(config.markerCluster);
		}

	}

	setStyleUrl(styleUrl: string): void {
		this.map.setStyle(styleUrl);
	}

	public addShape(shapeId: string, shapeConfig: AbstractUiMapShapeConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.shapesById.set(shapeId, {config: shapeConfig});
			if (isUiMapCircle(shapeConfig)) {
				this.map.addSource(shapeId, {
					type: "geojson",
					data: {
						type: "FeatureCollection",
						features: [{
							type: "Feature",
							geometry: {
								type: "Point",
								coordinates: [shapeConfig.center.longitude, shapeConfig.center.latitude]
							},
							properties: {
								radius: shapeConfig.radius,
							},
						}]
					}
				});
				let paintProperties = {} as any;
				if (shapeConfig.shapeProperties.fillColor != null) paintProperties['circle-color'] = shapeConfig.shapeProperties.fillColor;
				if (shapeConfig.shapeProperties.strokeColor != null) paintProperties['circle-stroke-color'] = shapeConfig.shapeProperties.strokeColor;
				if (shapeConfig.shapeProperties.strokeWeight != null) paintProperties['circle-stroke-width'] = shapeConfig.shapeProperties.strokeWeight;
				this.map.addLayer({
					id: shapeId,
					source: shapeId,
					type: "circle",
					paint: {
						"circle-radius": ["get", "radius"],
						...paintProperties,
					},
				});
			} else if (isUiMapPolyline(shapeConfig)) {
				shapeConfig.shapeProperties.strokeColor;
				shapeConfig.shapeProperties.strokeWeight;
				shapeConfig.shapeProperties.strokeDashArray;
				shapeConfig.shapeProperties.fillColor;
				this.map.addSource(shapeId, {
					'type': 'geojson',
					'data': this.toGeoJsonFeature(shapeConfig)
				});
				let paintProperties = {} as any;
				if (shapeConfig.shapeProperties.strokeColor != null) paintProperties['line-color'] = shapeConfig.shapeProperties.strokeColor;
				if (shapeConfig.shapeProperties.strokeWeight != null) paintProperties['line-width'] = shapeConfig.shapeProperties.strokeWeight;
				if (shapeConfig.shapeProperties.strokeDashArray != null) paintProperties["line-dasharray"] = shapeConfig.shapeProperties.strokeDashArray;
				this.map.addLayer({
					'id': shapeId,
					'type': 'line',
					'source': shapeId,
					'layout': {
						'line-join': 'round',
						'line-cap': 'round'
					},
					'paint': paintProperties
				});
			} else if (isUiMapPolygon(shapeConfig)) {
				let path = shapeConfig.path.map(loc => this.convertToPosition(loc));
				if (path[0] != path[path.length - 1]) {
					path.push(path[0]); // geojson spec demands that polygons be closed!
				}
				this.map.addSource(shapeId, {
					'type': 'geojson',
					'data': {
						'type': 'Feature',
						'geometry': {
							'type': 'Polygon',
							'coordinates': [
								path
							]
						},
						"properties": {}
					}
				});
				let paintProperties = {} as any;
				if (shapeConfig.shapeProperties.fillColor != null) paintProperties['fill-color'] = shapeConfig.shapeProperties.fillColor;
				if (shapeConfig.shapeProperties.strokeColor != null) paintProperties['fill-outline-color'] = shapeConfig.shapeProperties.strokeColor;
				this.map.addLayer({
					'id': shapeId,
					'type': 'fill',
					'source': shapeId,
					'layout': {},
					'paint': paintProperties
				});
			} else if (isUiMapRectangle(shapeConfig)) {
				this.map.addSource(shapeId, {
					'type': 'geojson',
					'data': {
						'type': 'Feature',
						'geometry': {
							'type': 'Polygon',
							'coordinates': [[
								this.convertToPosition(shapeConfig.l1),
								[shapeConfig.l1.longitude, shapeConfig.l2.latitude],
								this.convertToPosition(shapeConfig.l2),
								[shapeConfig.l2.longitude, shapeConfig.l1.latitude],
								this.convertToPosition(shapeConfig.l1) // geojson spec demands that polygons be closed!
							]]
						},
						"properties": {}
					}
				});
				let paintProperties = {} as any;
				if (shapeConfig.shapeProperties.fillColor != null) paintProperties['fill-color'] = shapeConfig.shapeProperties.fillColor;
				if (shapeConfig.shapeProperties.strokeColor != null) paintProperties['fill-outline-color'] = shapeConfig.shapeProperties.strokeColor;
				this.map.addLayer({
					'id': shapeId,
					'type': 'fill',
					'source': shapeId,
					'layout': {},
					'paint': paintProperties
				});
			}
		});
	}

	private toGeoJsonFeature(shapeConfig: UiMapPolylineConfig) {
		let data: GeoJSON.Feature<GeoJSON.Geometry> = {
			'type': 'Feature',
			'properties': {},
			'geometry': {
				'type': 'LineString',
				'coordinates': shapeConfig.path.map(loc => this.convertToPosition(loc))
			}
		};
		return data;
	}

	updateShape(shapeId: string, shape: AbstractUiMapShapeConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.removeShape(shapeId);
			this.addShape(shapeId, shape);
		});
	}

	changeShape(shapeId: string, change: AbstractUiMapShapeChangeConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			if (isPolyLineAppend(change)) {
				let config = this.shapesById.get(shapeId).config as UiMapPolylineConfig;
				config.path = config.path.concat(change.appendedPath);
				let source = this.map.getSource(shapeId) as GeoJSONSource;
				source.setData(this.toGeoJsonFeature(config));
			}
		});
	}

	removeShape(shapeId: string): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.map.removeLayer(shapeId);
			this.map.removeSource(shapeId);
			this.shapesById.delete(shapeId);
		});
	}

	clearShapes(): void {
		this.shapesById.forEach((shape, id) => this.removeShape(id))
	}

	public setMapMarkerCluster(clusterConfig: UiMapMarkerClusterConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			if (this.map.getSource('cluster') == null) {
				this.map.addSource('cluster', {
					type: 'geojson',
					data: this.createMarkersFeatureCollection([]),
					cluster: true,
					clusterRadius: 100
				});
				this.map.addLayer({
					'id': 'powerplant_query_layer',
					'type': 'circle',
					'source': 'cluster',
					'filter': ['==', ['get', 'cluster'], true],
					'paint': {
						'circle-radius': 0
					}
				});

				let markersById: { [id: string]: Marker } = {};
				let markersOnScreen: { [id: string]: Marker } = {};
				let point_counts: number[] = [];
				let totals: number[];

				const getPointCount = (features: Feature[]) => {
					features.forEach(f => {
						if (f.properties.cluster) {
							point_counts.push(f.properties.point_count)
						}
					})
					return point_counts;
				};

				const updateMarkers = () => {
					let newMarkers: { [id: string]: Marker } = {};
					const features = this.map.querySourceFeatures('cluster');
					totals = getPointCount(features);
					features.forEach((feature) => {
						const coordinates = (feature.geometry as Point).coordinates;
						const props = feature.properties;
						const id = props.cluster_id != null ? "cluster-" + props.cluster_id : "individual-" + props.id;
						let marker = markersById[id];
						if (!marker) {
							if (props.cluster) {
								marker = new mapboxgl.Marker({element: createClusterNode(feature, totals)})
									.setLngLat(coordinates as LngLatLike)
							} else if (props.id != null) {
								marker = this.createMarker(JSON.parse(props.marker));
							}
						}
						markersById[id] = newMarkers[id] = marker;

						if (!markersOnScreen[id]) {
							marker.addTo(this.map);
						}
					});
					for (let id in markersOnScreen) {
						if (!newMarkers[id]) {
							markersOnScreen[id].remove();
						}
					}
					markersOnScreen = newMarkers;
				};

				const createClusterNode = (feature: Feature, totals: number[]) => {
					const coordinates = (feature.geometry as Point).coordinates;
					const props = feature.properties;

					const div = document.createElement('div');

					const scale = d3.scaleLinear()
						.domain([d3.min(totals), d3.max(totals)])
						.range([500, d3.max(totals)])
					const radius = Math.sqrt(scale(props.point_count));

					const svg = d3.select(div)
						.append('svg')
						.attr('class', 'pie')
						.attr('width', radius * 2)
						.attr('height', radius * 2);

					//center
					const g = svg.append('g')
						.attr('transform', `translate(${radius}, ${radius})`);

					const circle = g.append('circle')
						.attr('r', radius)
						.attr('fill', '#ff0000aa')
						.attr('class', 'center-circle')

					const text = g
						.append("text")
						.attr("class", "total")
						.text(props.point_count_abbreviated)
						.attr('text-anchor', 'middle')
						.attr('dy', 5)
						.attr('fill', 'white')

					svg.on('click', (e: any) => {
						d3.event.stopPropagation();
						var clusterId = props.cluster_id;
						(this.map.getSource('cluster') as GeoJSONSource).getClusterExpansionZoom(
							clusterId,
							(err, zoom) => {
								if (err) return;

								this.map.once('moveend', () => setTimeout(updateMarkers, 100)) // make sure this happens after everything else, so we can actually update the map
								this.map.easeTo({
									center: coordinates as LngLatLike,
									zoom: zoom
								});
							}
						);
					})


					return div;
				}

				this.map.on('data', (e) => {
					if (e.sourceId === 'cluster' && e.isSourceLoaded) {
						updateMarkers();
					}
				});

				this.map.on('moveend', updateMarkers);
			}

			(this.map.getSource("cluster") as GeoJSONSource).setData(this.createMarkersFeatureCollection(clusterConfig.markers));
		})
	}


	private createMarkersFeatureCollection(markers: UiMapMarkerClientRecordConfig[]): GeoJSON.FeatureCollection {
		return {
			type: "FeatureCollection",
			features: markers.map(this.createMarkerFeature)
		};
	}

	private createMarkerFeature(m: UiMapMarkerClientRecordConfig): Feature {
		return {
			type: "Feature",
			properties: {
				id: m.id,
				marker: m
			},
			geometry: {
				type: "Point",
				coordinates: [
					m.location.longitude,
					m.location.latitude
				]
			}
		};
	}

	private convertToPosition(loc: UiMapLocationConfig): Position {
		return [loc.longitude, loc.latitude];
	}

	private convertToLngLatLike(loc: UiMapLocationConfig): LngLatLike {
		return this.convertToPosition(loc) as LngLatLike;
	}

	public addMarker(markerConfig: UiMapMarkerClientRecordConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			let marker = this.createMarker(markerConfig);
			this.markersByClientId[markerConfig.id] = marker;
			marker.addTo(this.map);
		});
	}

	removeMarker(id: number): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.markersByClientId[id]?.remove();
			delete this.markersByClientId[id];
		});
	}

	clearMarkers(): void {
		this.deferredExecutor.invokeWhenReady(() => {
			Object.values(this.markersByClientId).forEach(m => m.remove());
			this.markersByClientId = {};
		});
	}

	private createMarker(markerConfig: UiMapMarkerClientRecordConfig) {
		let renderer = this.markerTemplateRenderers[markerConfig.templateId] || this._context.templateRegistry.getTemplateRendererByName(markerConfig.templateId);
		let marker = new Marker(parseHtml(renderer.render(markerConfig.values)), {
			anchor: markerConfig.anchor,
			offset: [markerConfig.offsetPixelsX, markerConfig.offsetPixelsY]
		})
			.setLngLat([markerConfig.location.longitude, markerConfig.location.latitude]);
		marker.getElement().addEventListener('click', e => {
			this.onMarkerClicked.fire({markerId: markerConfig.id})
			e.stopPropagation();
		});
		return marker;
	};

	// TODO
	setHeatMap(data: import("../generated/UiHeatMapDataConfig").UiHeatMapDataConfig): void {
		throw new Error("Method not implemented.");
	}

	// TODO
	startDrawingShape(shapeType: import("../generated/UiMapShapeType").UiMapShapeType, shapeProperties: import("../generated/UiShapePropertiesConfig").UiShapePropertiesConfig): void {
		throw new Error("Method not implemented.");
	}

	// TODO
	stopDrawingShape(): void {
		throw new Error("Method not implemented.");
	}

	setZoomLevel(zoom: number): void {
		this.map.zoomTo(zoom)
	}

	setLocation(location: UiMapLocationConfig, animationDurationMillis: number, targetZoomLevel: number): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.map.easeTo({
				center: [location.longitude, location.latitude],
				duration: animationDurationMillis,
				zoom: targetZoomLevel
			});
		});
	}

	fitBounds(southWest: UiMapLocationConfig, northEast: UiMapLocationConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.map.fitBounds([
				[southWest.longitude, southWest.latitude],
				[northEast.longitude, northEast.latitude],
			]);
		});
	}

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.markerTemplateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	public doGetMainElement(): HTMLElement {
		return this.$map;
	}

	onResize() {
		this.map.resize();
	}
}

function isPolyLineAppend(change: AbstractUiMapShapeChangeConfig): change is UiPolylineAppendConfig {
	return change._type === "UiPolylineAppend";
}

TeamAppsUiComponentRegistry.registerComponentClass("UiMap2", UiMap2);
