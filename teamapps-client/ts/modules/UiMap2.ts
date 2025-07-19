/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import TurfCircle from "@turf/circle";
import * as d3 from "d3";
import {Feature, Point, Position} from "geojson";
import maplibregl, {GeoJSONSource, LngLatLike, Marker} from "maplibre-gl";
import {AbstractUiMapShapeChangeConfig} from "../generated/AbstractUiMapShapeChangeConfig";
import {AbstractUiMapShapeConfig} from "../generated/AbstractUiMapShapeConfig";
import {UiHeatMapDataConfig} from "../generated/UiHeatMapDataConfig";
import {UiHeatMapDataElementConfig} from "../generated/UiHeatMapDataElementConfig";
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
import {createUiMapAreaConfig} from "../generated/UiMapAreaConfig";
import {createUiMapLocationConfig, UiMapLocationConfig} from "../generated/UiMapLocationConfig";
import {UiMapMarkerAnchor} from "../generated/UiMapMarkerAnchor";
import {UiMapMarkerClientRecordConfig} from "../generated/UiMapMarkerClientRecordConfig";
import {UiMapMarkerClusterConfig} from "../generated/UiMapMarkerClusterConfig";
import {UiMapPolylineConfig} from "../generated/UiMapPolylineConfig";
import {UiPolylineAppendConfig} from "../generated/UiPolylineAppendConfig";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {parseHtml, Renderer} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {isUiMapCircle, isUiMapPolygon, isUiMapPolyline, isUiMapRectangle} from "./UiMap";
import {DeferredExecutor} from "./util/DeferredExecutor";
import {TeamAppsEvent} from "./util/TeamAppsEvent";

export class UiMap2 extends AbstractUiComponent<UiMap2Config> implements UiMap2EventSource, UiMap2CommandHandler {

	public readonly onZoomLevelChanged: TeamAppsEvent<UiMap2_ZoomLevelChangedEvent> = new TeamAppsEvent({throttlingMode: "throttle", delay: 500});
	public readonly onLocationChanged: TeamAppsEvent<UiMap2_LocationChangedEvent> = new TeamAppsEvent({throttlingMode: "throttle", delay: 500});
	public readonly onMapClicked: TeamAppsEvent<UiMap2_MapClickedEvent> = new TeamAppsEvent();
	public readonly onMarkerClicked: TeamAppsEvent<UiMap2_MarkerClickedEvent> = new TeamAppsEvent();
	public readonly onShapeDrawn: TeamAppsEvent<UiMap2_ShapeDrawnEvent> = new TeamAppsEvent();

	private $map: HTMLElement;
	private map: maplibregl.Map;
	private markerTemplateRenderers: { [templateName: string]: Renderer } = {};
	private markersByClientId: { [id: number]: Marker } = {};
	private shapesById: Map<string, { config: AbstractUiMapShapeConfig }> = new Map();

	private deferredExecutor: DeferredExecutor = new DeferredExecutor();

	constructor(config: UiMap2Config, context: TeamAppsUiContext) {
		super(config, context);
		this.$map = parseHtml('<div class="UiMap2">');

		this.map = new maplibregl.Map({
			container: this.$map,
			style: config.styleUrl,
			center: this.convertToLngLatLike(config.mapPosition),
			hash: false, // don't change the URL!
			zoom: config.zoomLevel, // starting zoom
			attributionControl: false,
		});

		if (config.displayAttributionControl) {
			this.map.addControl(new maplibregl.AttributionControl());
		}
		if (config.displayNavigationControl) {
			this.map.addControl(new maplibregl.NavigationControl());
		}
		this.map.on('load', () => {
			this.onResize();
			this.deferredExecutor.ready = true;
		});

		this.map.on("zoom", ev => {
			this.onZoomLevelChanged.fire({zoomLevel: this.map.getZoom()});
		});
		this.map.on("move", ev => {
			const center = this.map.getCenter();
			const bounds = this.map.getBounds();
			this.onLocationChanged.fire({
				center: createUiMapLocationConfig(center.lat, center.lng),
				displayedArea: createUiMapAreaConfig(bounds.getNorth(), bounds.getSouth(), bounds.getWest(), bounds.getEast())
			});
		});
		this.map.on("click", ev => {
			this.onMapClicked.fire({location: createUiMapLocationConfig(ev.lngLat.lat, ev.lngLat.lng)});
		});

		Object.keys(config.markerTemplates).forEach(templateName => this.registerTemplate(templateName, config.markerTemplates[templateName]));

		Object.keys(config.shapes).forEach(shapeId => {
			this.addShape(shapeId, config.shapes[shapeId]);
		});

		if (config.markerCluster != null) {
			this.setMapMarkerCluster(config.markerCluster);
			// initiate markers that are not part of the cluster
			const markerIds = config.markerCluster.markers.map(m => m.id);
			config.markers.filter(m => !markerIds.includes(m.id)).forEach(marker => this.addMarker(marker));
		} else {
			config.markers.forEach(marker => this.addMarker(marker));
		}

		if (config.heatMap != null) {
			this.setHeatMap(config.heatMap);
		}
	}

	public setStyleUrl(styleUrl: string): void {
		this.map.setStyle(styleUrl);
	}

	public addShape(shapeId: string, shapeConfig: AbstractUiMapShapeConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.shapesById.set(shapeId, {config: shapeConfig});
			if (isUiMapCircle(shapeConfig)) {
				const circleData = TurfCircle([shapeConfig.center.longitude, shapeConfig.center.latitude], shapeConfig.radius, {
					units: 'meters',
					steps: 90
				});
				this.map.addSource(shapeId, {
					type: "geojson",
					data: circleData
				});
				if (shapeConfig.shapeProperties.fillColor != null) {
					this.map.addLayer({
						id: shapeId + "-fill",
						source: shapeId,
						type: "fill",
						paint: {
							"fill-color": shapeConfig.shapeProperties.fillColor,
							"fill-opacity": 0.8
						},
					});
				}
				const paintProperties = {} as any;
				if (shapeConfig.shapeProperties.strokeColor != null) { paintProperties['line-color'] = shapeConfig.shapeProperties.strokeColor; }
				if (shapeConfig.shapeProperties.strokeWeight != null) { paintProperties['line-width'] = shapeConfig.shapeProperties.strokeWeight; }
				this.map.addLayer({
					id: shapeId + "-outline",
					source: shapeId,
					type: 'line',
					layout: {
						'line-join': 'round',
						'line-cap': 'round'
					},
					paint: {
						...paintProperties,
					},
				});
			} else if (isUiMapPolyline(shapeConfig)) {
				this.map.addSource(shapeId, {
					type: 'geojson',
					data: this.createLineStringFeature(shapeConfig)
				});
				const paintProperties = {} as any;
				if (shapeConfig.shapeProperties.strokeColor != null) { paintProperties['line-color'] = shapeConfig.shapeProperties.strokeColor; }
				if (shapeConfig.shapeProperties.strokeWeight != null) { paintProperties['line-width'] = shapeConfig.shapeProperties.strokeWeight; }
				if (shapeConfig.shapeProperties.strokeDashArray != null) { paintProperties["line-dasharray"] = shapeConfig.shapeProperties.strokeDashArray; }
				this.map.addLayer({
					id: shapeId,
					type: 'line',
					source: shapeId,
					layout: {
						'line-join': 'round',
						'line-cap': 'round'
					},
					paint: paintProperties
				});
			} else if (isUiMapPolygon(shapeConfig)) {
				const path = shapeConfig.path.map(loc => this.convertToPosition(loc));
				if (path[0] !== path[path.length - 1]) {
					path.push(path[0]); // geojson spec demands that polygons be closed!
				}
				this.map.addSource(shapeId, {
					type: 'geojson',
					data: {
						type: 'Feature',
						geometry: {
							type: 'Polygon',
							coordinates: [
								path
							]
						},
						properties: {}
					}
				});
				const paintProperties = {} as any;
				if (shapeConfig.shapeProperties.fillColor != null) { paintProperties['fill-color'] = shapeConfig.shapeProperties.fillColor; paintProperties['fill-opacity'] = 0.8; }
				if (shapeConfig.shapeProperties.strokeColor != null) { paintProperties['fill-outline-color'] = shapeConfig.shapeProperties.strokeColor; }
				this.map.addLayer({
					id: shapeId,
					type: 'fill',
					source: shapeId,
					paint: paintProperties
				});
				if (shapeConfig.shapeProperties.strokeWeight != null) {
					const paintProps = {
						"line-width": shapeConfig.shapeProperties.strokeWeight
					} as any;
					if (shapeConfig.shapeProperties.strokeColor != null) { paintProps['line-color'] = shapeConfig.shapeProperties.strokeColor; }
					this.map.addLayer({
						id: shapeId + "-outline",
						type: 'line',
						source: shapeId,
						layout: {
							'line-join': 'round',
							'line-cap': 'round'
						},
						paint: paintProps
					});
				}
			} else if (isUiMapRectangle(shapeConfig)) {
				this.map.addSource(shapeId, {
					type: 'geojson',
					data: {
						type: 'Feature',
						geometry: {
							type: 'Polygon',
							coordinates: [[
								this.convertToPosition(shapeConfig.l1),
								[shapeConfig.l1.longitude, shapeConfig.l2.latitude],
								this.convertToPosition(shapeConfig.l2),
								[shapeConfig.l2.longitude, shapeConfig.l1.latitude],
								this.convertToPosition(shapeConfig.l1) // geojson spec demands that polygons be closed!
							]]
						},
						properties: {}
					}
				});
				const paintProperties = {} as any;
				if (shapeConfig.shapeProperties.fillColor != null) { paintProperties['fill-color'] = shapeConfig.shapeProperties.fillColor; paintProperties['fill-opacity'] = 0.8; }
				if (shapeConfig.shapeProperties.strokeColor != null) { paintProperties['fill-outline-color'] = shapeConfig.shapeProperties.strokeColor; }
				this.map.addLayer({
					id: shapeId,
					type: 'fill',
					source: shapeId,
					paint: paintProperties
				});
				if (shapeConfig.shapeProperties.strokeWeight != null) {
					const paintProps = {
						"line-width": shapeConfig.shapeProperties.strokeWeight
					} as any;
					if (shapeConfig.shapeProperties.strokeColor != null) { paintProps['line-color'] = shapeConfig.shapeProperties.strokeColor; }
					this.map.addLayer({
						id: shapeId + "-outline",
						type: 'line',
						layout: {
							'line-join': 'round',
							'line-cap': 'round'
						},
						source: shapeId,
						paint: paintProps
					});
				}
			}
		});
	}

	public updateShape(shapeId: string, shape: AbstractUiMapShapeConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.removeShape(shapeId);
			this.addShape(shapeId, shape);
		});
	}

	public changeShape(shapeId: string, change: AbstractUiMapShapeChangeConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			if (isPolyLineAppend(change)) {
				const config = this.shapesById.get(shapeId).config as UiMapPolylineConfig;
				config.path = config.path.concat(change.appendedPath);
				const source = this.map.getSource(shapeId) as GeoJSONSource;
				source.setData(this.createLineStringFeature(config));
			}
		});
	}

	public removeShape(shapeId: string): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.map.removeLayer(shapeId);
			this.map.removeSource(shapeId);
			this.shapesById.delete(shapeId);
		});
	}

	public clearShapes(): void {
		this.shapesById.forEach((shape, id) => this.removeShape(id));
	}

	private initializeMarkerCluster(sourceName: string, clusterConfig: UiMapMarkerClusterConfig): void {
		this.map.addSource(sourceName, {
			type: 'geojson',
			data: this.createFeatureCollection([]),
			cluster: true,
			clusterRadius: 100
		});
		this.map.addLayer({
			id: sourceName + '_cluster_layer',
			type: 'circle',
			source: sourceName,
			filter: ['==', ['get', sourceName], true],
			paint: {
				'circle-radius': 0
			}
		});
		this.map.addSource(sourceName + '_spiderLegs', {
			type: 'geojson',
			data: this.createFeatureCollection([]),
		});
		this.map.addLayer({
			id: sourceName + '_spiderLegs',
			type: 'line',
			source: sourceName + '_spiderLegs',
			paint: {
				'line-color': '#ffffff',
				'line-width': 2,
				'line-opacity': 0.8,
			}
		});

		const markersById: { [id: string]: Marker } = {};
		let markersOnScreen: { [id: string]: Marker } = {};

		const updateMarkers = () => {
			const newMarkers: { [id: string]: Marker } = {};
			const srcFeatures = this.map.querySourceFeatures(sourceName);
			const coordinatesMap = srcFeatures.reduce((map, f) => {
				const key = (f.geometry as Point).coordinates.join('x');
				if (!map[key]) {
					map[key] = {};
				}
				f.properties.cache_id = f.properties.cluster_id != null ? "cluster-" + f.properties.cluster_id : "individual-" + f.properties.id;
				map[key][f.properties.cache_id] = f;
				return map;
			}, {} as { [key: string]: { [id: string]: Feature } });
			const spiderLegFeatures: Feature[] = [];
			Object.values(coordinatesMap).forEach(features => {
				if (Object.keys(features).length > 1) {
					const center = (Object.values(features)[0].geometry as Point).coordinates as Position;
					const markers = spiderfyOverlappingMarkers(features);
					Object.entries(markers).forEach(([id, m]) => {
						const marker = markersById[id] ?? m;
						markersById[id] = newMarkers[id] = marker;
						if (!markersOnScreen[id]) {
							marker.addTo(this.map);
						}
						spiderLegFeatures.push(this.createLineStringFeature({path: [
							{longitude: marker.getLngLat().lng, latitude: marker.getLngLat().lat},
							{longitude: center[0], latitude: center[1]}
						]}));
					});
				} else {
					const [id, feature] = Object.entries(features)[0];
					const coordinates = (feature.geometry as Point).coordinates;
					const props = feature.properties;
					let marker = markersById[id];
					if (!marker) {
						if (props.cluster) {
							marker = new maplibregl.Marker({element: createClusterNode(feature), anchor: UiMapMarkerAnchor.CENTER})
								.setLngLat(coordinates as LngLatLike);
						} else if (props.id != null) {
							marker = this.createMarker(JSON.parse(props.marker));
						}
					}
					markersById[id] = newMarkers[id] = marker;
					if (!markersOnScreen[id]) {
						marker.addTo(this.map);
					}
				}
			});
			for (const id in markersOnScreen) {
				if (!newMarkers[id]) {
					markersOnScreen[id].remove();
				}
			}
			markersOnScreen = newMarkers;
			(this.map.getSource(sourceName + '_spiderLegs') as GeoJSONSource).setData(this.createFeatureCollection(spiderLegFeatures));
		};

		const createClusterNode = (feature: Feature) => {
			const coordinates = (feature.geometry as Point).coordinates;
			const props = feature.properties;

			const div = document.createElement('div');
			const radius = 20;

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
				.attr('class', 'center-circle');
			const innerCircle = g.append('circle')
				.attr('r', radius / 3 * 2)
				.attr('class', 'inner-circle');
			if (props.point_count < 10) {
				circle.attr('fill', 'rgba(181, 226, 140, 0.6)');
				innerCircle.attr('fill', 'rgba(110, 204, 57, 0.6)');
			} else if (props.point_count < 100) {
				circle.attr('fill', 'rgba(241, 211, 87, 0.6)');
				innerCircle.attr('fill', 'rgba(240, 194, 12, 0.6)');
			} else {
				circle.attr('fill', 'rgba(253, 156, 115, 0.6)');
				innerCircle.attr('fill', 'rgba(241, 128, 23, 0.6)');
			}

			const text = g
				.append("text")
				.attr("class", "total")
				.text(props.point_count_abbreviated)
				.attr('text-anchor', 'middle')
				.attr('dy', 5)
				.attr('fill', 'black');

			svg.on('click', (e: any) => {
				d3.event.stopPropagation();
				const clusterId = props.cluster_id;
				(this.map.getSource(sourceName) as GeoJSONSource).getClusterExpansionZoom(clusterId).then(zoom => {

					this.map.once('moveend', () => setTimeout(updateMarkers, 100)); // make sure this happens after everything else, so we can actually update the map
					this.map.easeTo({
						center: coordinates as LngLatLike,
						zoom
					});
				});
			});

			return div;
		};

		const spiderfyOverlappingMarkers = (features: { [id: string]: Feature }) => {
			const leaves = Object.values(features);
			const centerPx = (leaves[0].geometry as Point).coordinates as Position;
			const isSpiral = leaves.length > 9;
			const angleStep = (2 * Math.PI) / Math.min(leaves.length, 10.7);
			let legLength = isSpiral ? 0.0001 : 0.0002;
			const legLengthFactor = 0.000005;

			return Object.entries(features).reduce((markers, feature, i) => {
				const [id, leaf] = feature;
				if (leaf.properties.cluster) {
					return markers;
				}
				const angle = (i + 1) * angleStep;
				const offsetPx = [
					legLength * Math.sin(angle),
					legLength * Math.cos(angle) / Math.PI * 2,
				];
				const lngLat = [
					centerPx[0] + offsetPx[0],
					centerPx[1] + offsetPx[1],
				] as LngLatLike;
				if (isSpiral) {
					legLength += ((Math.PI * 2) * legLengthFactor) / angle;
				}

				markers[id] = this.createMarker(JSON.parse(leaf.properties.marker), lngLat);
				return markers;
			}, {} as { [id: string]: Marker });
		};

		this.map.on('sourcedata', (e) => {
			if (e.sourceId === sourceName && e.isSourceLoaded) {
				updateMarkers();
			}
		});

		this.map.on('moveend', updateMarkers);
	}

	public setMapMarkerCluster(clusterConfig: UiMapMarkerClusterConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			if (this.map.getSource('markers') == null) {
				this.initializeMarkerCluster('markers', clusterConfig);
			}

			(this.map.getSource('markers') as GeoJSONSource).setData(this.createFeatureCollection(clusterConfig.markers.map(this.createMarkerFeature)));
		});
	}

	private createFeatureCollection(features: Feature[]): GeoJSON.FeatureCollection {
		return {
			type: "FeatureCollection",
			features
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

	private createHeatMapDataElementFeature(c: UiHeatMapDataElementConfig): Feature {
		return {
			type: "Feature",
			properties: {
				count: c.count
			},
			geometry: {
				type: "Point",
				coordinates: [
					c.longitude,
					c.latitude
				]
			}
		};
	}

	private createLineStringFeature(shapeConfig: UiMapPolylineConfig): Feature {
		return {
			type: "Feature",
			properties: {},
			geometry: {
				type: "LineString",
				coordinates: shapeConfig.path.map(loc => this.convertToPosition(loc))
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
			const marker = this.createMarker(markerConfig);
			this.markersByClientId[markerConfig.id] = marker;
			marker.addTo(this.map);
		});
	}

	public removeMarker(id: number): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.markersByClientId[id]?.remove();
			delete this.markersByClientId[id];
		});
	}

	public clearMarkers(): void {
		this.deferredExecutor.invokeWhenReady(() => {
			Object.values(this.markersByClientId).forEach(m => m.remove());
			this.markersByClientId = {};
		});
	}

	private createMarker(markerConfig: UiMapMarkerClientRecordConfig, lngLat?: LngLatLike) {
		const renderer = this.markerTemplateRenderers[markerConfig.templateId] || this._context.templateRegistry.getTemplateRendererByName(markerConfig.templateId);
		const marker = new Marker({
			element: parseHtml(renderer.render(markerConfig.values)),
			anchor: markerConfig.anchor,
			offset: [markerConfig.offsetPixelsX, markerConfig.offsetPixelsY]
		});
		marker.setLngLat(lngLat ?? [markerConfig.location.longitude, markerConfig.location.latitude]);
		marker.getElement().addEventListener('click', e => {
			this.onMarkerClicked.fire({markerId: markerConfig.id});
			e.stopPropagation();
		});
		return marker;
	}

	private initializeHeatMap(sourceName: string, data: UiHeatMapDataConfig): void {
		this.map.addSource(sourceName, {
			type: 'geojson',
			data: this.createFeatureCollection([]),
		});
		const paint: any = {};
		if (data.blur == null || data.blur < 1 || data.blur > 23) {
			data.blur = 23;
		}
		const maxzoom = data.blur;
		paint['heatmap-opacity'] = [
			'interpolate',
			['linear'],
			['zoom'],
			data.blur - 1,
			0.8,
			data.blur, // fade out at zoom level defined by parameter "blur"
			0
		];
		if (data.radius == null) {
			data.radius = 30;
		}
		paint['heatmap-radius'] = [
			'interpolate',
			['linear'],
			['zoom'],
			0,
			data.radius / 5,
			10,
			data.radius
		];
		if (data.maxCount == null || data.maxCount < 1) {
			data.maxCount = 1;
		}
		paint['heatmap-intensity'] = [
			'interpolate',
			['linear'],
			['zoom'],
			0,
			0.01 / data.maxCount,
			data.blur,
			1 / data.maxCount
		];
		paint['heatmap-weight'] = ['get', 'count'];
		this.map.addLayer({
			id: sourceName,
			type: 'heatmap',
			source: sourceName,
			maxzoom,
			paint,
		});
	}

	public setHeatMap(data: UiHeatMapDataConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			if (this.map.getSource('heatmap') == null) {
				this.initializeHeatMap('heatmap', data);
			}
			const elements = data.elements.map(el => this.createHeatMapDataElementFeature(el));
			(this.map.getSource('heatmap') as GeoJSONSource).setData(this.createFeatureCollection(elements));
		});
	}

	// TODO
	public startDrawingShape(shapeType: import("../generated/UiMapShapeType").UiMapShapeType, shapeProperties: import("../generated/UiShapePropertiesConfig").UiShapePropertiesConfig): void {
		throw new Error("Method not implemented.");
	}

	// TODO
	public stopDrawingShape(): void {
		throw new Error("Method not implemented.");
	}

	public setZoomLevel(zoom: number): void {
		this.map.zoomTo(zoom);
	}

	public setLocation(location: UiMapLocationConfig, animationDurationMillis: number, targetZoomLevel: number): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.map.easeTo({
				center: [location.longitude, location.latitude],
				duration: animationDurationMillis,
				zoom: targetZoomLevel
			});
		});
	}

	public fitBounds(southWest: UiMapLocationConfig, northEast: UiMapLocationConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.map.fitBounds([
				[southWest.longitude, southWest.latitude],
				[northEast.longitude, northEast.latitude],
			]);
		});
	}

	public registerTemplate(id: string, template: UiTemplateConfig): void {
		this.markerTemplateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	public doGetMainElement(): HTMLElement {
		return this.$map;
	}

	public onResize() {
		this.map.resize();
	}
}

function isPolyLineAppend(change: AbstractUiMapShapeChangeConfig): change is UiPolylineAppendConfig {
	return change._type === "UiPolylineAppend";
}

TeamAppsUiComponentRegistry.registerComponentClass("UiMap2", UiMap2);
