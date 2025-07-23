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
import {
	HexColor,
	TerraDraw,
	TerraDrawCircleMode,
	TerraDrawLineStringMode,
	TerraDrawPolygonMode,
	TerraDrawRectangleMode
} from "terra-draw";
import {TerraDrawMapLibreGLAdapter} from "terra-draw-maplibre-gl-adapter";
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
import {createUiMapCircleConfig} from "../generated/UiMapCircleConfig";
import {createUiMapLocationConfig, UiMapLocationConfig} from "../generated/UiMapLocationConfig";
import {UiMapMarkerAnchor} from "../generated/UiMapMarkerAnchor";
import {UiMapMarkerClientRecordConfig} from "../generated/UiMapMarkerClientRecordConfig";
import {UiMapMarkerClusterConfig} from "../generated/UiMapMarkerClusterConfig";
import {createUiMapPolygonConfig} from "../generated/UiMapPolygonConfig";
import {createUiMapPolylineConfig, UiMapPolylineConfig} from "../generated/UiMapPolylineConfig";
import {createUiMapRectangleConfig} from "../generated/UiMapRectangleConfig";
import {UiMapShapeType} from "../generated/UiMapShapeType";
import {UiPolylineAppendConfig} from "../generated/UiPolylineAppendConfig";
import {UiShapePropertiesConfig} from "../generated/UiShapePropertiesConfig";
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
	private draw: TerraDraw;
	private drawingProperties: UiShapePropertiesConfig;
	private markerTemplateRenderers: { [templateName: string]: Renderer } = {};
	private markersByClientId: { [id: string]: Marker } = {};
	private shapesById: Map<string, { config: AbstractUiMapShapeConfig }> = new Map();

	private deferredExecutor: DeferredExecutor = new DeferredExecutor();

	private readonly fillOpacity = 0.75;

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
		const styles = {
			fillOpacity: this.fillOpacity,
		};
		this.draw = new TerraDraw({
			adapter: new TerraDrawMapLibreGLAdapter({ map: this.map }),
			modes: [
				new TerraDrawCircleMode({styles}),
				new TerraDrawRectangleMode({styles}),
				new TerraDrawPolygonMode({styles}),
				new TerraDrawLineStringMode(),
			],
		});

		this.draw.on('finish', (shapeId: string, ctx: { action: string, mode: string }) => {
			if (ctx.action === 'draw') {
				let shape: AbstractUiMapShapeConfig;
				const drawFeature = this.draw.getSnapshotFeature(shapeId);
				if (drawFeature) {
					const positions = this.flattenPositionArray(drawFeature.geometry.coordinates);
					switch (ctx.mode) {
						case 'circle':
							//const src = this.map.getSource('td-polygon') as GeoJSONSource;
							shape = createUiMapCircleConfig({center: this.calcCenterUiLocation(positions), radius: Number(drawFeature.properties?.radiusKilometers) * 1000});
							break;
						case 'rectangle':
							//const src = this.map.getSource('td-polygon') as GeoJSONSource;
							shape = createUiMapRectangleConfig({
								l1: this.convertToUiLocation(positions[0]),
								l2: this.convertToUiLocation(positions[2]),
							});
							break;
						case 'polygon':
							//const src = this.map.getSource('td-polygon') as GeoJSONSource;
							shape = createUiMapPolygonConfig({path: positions.map(this.convertToUiLocation)});
							break;
						case 'linestring':
							//const src = this.map.getSource('td-linestring') as GeoJSONSource;
							shape = createUiMapPolylineConfig({path: positions.map(this.convertToUiLocation)});
							break;
						default:
							throw new Error(`Unknown draw mode: ${ctx.mode}`);
					}
				}
				shape.shapeProperties = this.drawingProperties;
				this.shapesById.set(shapeId, {config: shape});
				this.onShapeDrawn.fire({shapeId, shape});
			}
		});
	}

	public setStyleUrl(styleUrl: string): void {
		this.map.setStyle(styleUrl);
	}

	private createShapeFeatureByConfig(shapeId: string, shapeConfig: AbstractUiMapShapeConfig) {
		if (isUiMapCircle(shapeConfig)) {
			return TurfCircle(this.createPointFeature(shapeConfig.center, shapeId), shapeConfig.radius, {
				units: 'meters',
				steps: 90
			});
		} else if (isUiMapPolyline(shapeConfig)) {
			return this.createLineStringFeature(shapeConfig, shapeId);
		} else if (isUiMapPolygon(shapeConfig)) {
			const path = shapeConfig.path.map(loc => this.convertToPosition(loc));
			if (String(path[0]) !== String(path[path.length - 1])) {
				path.push(path[0]); // geojson spec demands that polygons be closed!
			}
			return this.createPolygonFeature(path, shapeId, shapeConfig);
		} else if (isUiMapRectangle(shapeConfig)) {
			const path = [
				this.convertToPosition(shapeConfig.l1),
				[shapeConfig.l1.longitude, shapeConfig.l2.latitude],
				this.convertToPosition(shapeConfig.l2),
				[shapeConfig.l2.longitude, shapeConfig.l1.latitude],
				this.convertToPosition(shapeConfig.l1) // geojson spec demands that polygons be closed!
			];
			return this.createPolygonFeature(path, shapeId, shapeConfig);
		}
	}

	@Queued()
	public async addShape(shapeId: string, shapeConfig: AbstractUiMapShapeConfig) {
		return this.deferredExecutor.invokeWhenReady(async () => {
			this.shapesById.set(shapeId, {config: shapeConfig});
			if (this.map.getSource('shapes') == null) {
				this.map.addSource('shapes', {
					type: 'geojson',
					data: this.createFeatureCollection([]),
				});
			}
			const shapeSrc = this.map.getSource('shapes') as GeoJSONSource;
			const srcFeatures = await shapeSrc.getData() as GeoJSON.FeatureCollection;
			srcFeatures.features = srcFeatures.features.filter(f => f.id !== shapeId);
			srcFeatures.features.push(this.createShapeFeatureByConfig(shapeId, shapeConfig));
			shapeSrc.setData(srcFeatures);
			const paintProperties = {} as any;
			if (shapeConfig.shapeProperties.strokeColor != null) {
				paintProperties['line-color'] = shapeConfig.shapeProperties.strokeColor;
			}
			if (shapeConfig.shapeProperties.strokeWeight != null && shapeConfig.shapeProperties.strokeWeight !== 0) {
				paintProperties['line-width'] = shapeConfig.shapeProperties.strokeWeight;
			}
			if (shapeConfig.shapeProperties.strokeDashArray != null) {
				paintProperties["line-dasharray"] = shapeConfig.shapeProperties.strokeDashArray;
			}
			this.map.addLayer({
				id: shapeId + '-outline',
				type: 'line',
				source: 'shapes',
				filter: ['==', 'id', shapeId],
				layout: {
					'line-join': 'round',
					'line-cap': 'round'
				},
				paint: paintProperties
			});
			if (isUiMapCircle(shapeConfig) || isUiMapPolygon(shapeConfig) || isUiMapRectangle(shapeConfig)) {
				if (shapeConfig.shapeProperties.fillColor != null) {
					this.map.addLayer({
						id: shapeId,
						source: 'shapes',
						filter: ['==', 'id', shapeId],
						type: 'fill',
						paint: {
							'fill-color': shapeConfig.shapeProperties.fillColor,
							'fill-opacity': this.fillOpacity
						},
					});
				}
			}
		});
	}

	@Queued()
	public updateShape(shapeId: string, shape: AbstractUiMapShapeConfig) {
		return this.deferredExecutor.invokeWhenReady(async () => {
			await this.removeShape(shapeId);
			await this.addShape(shapeId, shape);
		});
	}

	@Queued()
	public async changeShape(shapeId: string, change: AbstractUiMapShapeChangeConfig) {
		return this.deferredExecutor.invokeWhenReady(() => {
			if (isPolyLineAppend(change)) {
				const config = this.shapesById.get(shapeId).config as UiMapPolylineConfig;
				config.path = config.path.concat(change.appendedPath);
				const source = this.map.getSource(shapeId) as GeoJSONSource;
				source.setData(this.createLineStringFeature(config));
			}
		});
	}

	@Queued()
	public async removeShape(shapeId: string) {
		return this.deferredExecutor.invokeWhenReady(async () => {
			if (this.map.getLayer(shapeId)) {
				this.map.removeLayer(shapeId);
			}
			if (this.map.getLayer(shapeId + '-outline')) {
				this.map.removeLayer(shapeId + '-outline');
			}
			await this.removeIdFromSource('shapes', shapeId);
			await this.removeShapeFromTerraDraw(shapeId);
			this.shapesById.delete(shapeId);
		});
	}

	@Queued()
	private async removeShapeFromTerraDraw(shapeId: string) {
		if (this.draw.hasFeature(shapeId)) {
			await this.removeIdFromSource('td-polygon', shapeId);
			await this.removeIdFromSource('td-linestring', shapeId);
			this.draw.removeFeatures([shapeId]);
		}
	}

	@Queued()
	private async removeIdFromSource(sourceName: string, id: string) {
		const source = this.map.getSource(sourceName) as GeoJSONSource;
		if (source != null) {
			const features = (await source.getData() as GeoJSON.FeatureCollection).features ?? [];
			source.setData(this.createFeatureCollection(features.filter(f => f.id !== id)));
		}
	}

	public async clearShapes() {
		const shapeIds = Array.from(this.shapesById.keys());
		await Promise.all(shapeIds.map(id => this.removeShape(id)));
	}

	private initializeMarkerCluster(sourceName: string): void {
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

		const updateMarkers = makeQueue(async () => {
			const newMarkersOnScreen: { [id: string]: Marker } = {};
			const srcFeatures = this.map.querySourceFeatures(sourceName);
			const coordinatesMap = srcFeatures.reduce((map, f) => {
				const key = (f.geometry as Point).coordinates.join('x');
				if (!map[key]) {
					map[key] = {};
				}
				f.properties.id = f.properties.cluster_id != null ? "cluster-" + f.properties.cluster_id : f.properties.id;
				map[key][String(f.properties.id)] = f;
				return map;
			}, {} as { [key: string]: { [id: string]: Feature } });
			const spiderLegFeatures: Feature[] = [];
			Object.values(coordinatesMap).forEach(features => {
				if (Object.keys(features).length > 1) {
					const center = (Object.values(features)[0].geometry as Point).coordinates as Position;
					const markers = spiderfyOverlappingMarkers(features);
					Object.entries(markers).forEach(([id, m]) => {
						const marker = this.markersByClientId[id] ?? m;
						newMarkersOnScreen[id] = marker;
						if (!this.markersByClientId[id]) {
							this.addMarkerToMap(id, marker);
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
					let marker = this.markersByClientId[id];
					if (!marker) {
						if (props.cluster) {
							marker = new maplibregl.Marker({element: createClusterNode(feature), anchor: UiMapMarkerAnchor.CENTER})
								.setLngLat(coordinates as LngLatLike);
						} else if (props.id != null) {
							marker = this.createMarker(JSON.parse(props.marker));
						}
					}
					newMarkersOnScreen[id] = marker;
					if (!this.markersByClientId[id]) {
						this.addMarkerToMap(id, marker);
					}
				}
			});
			for (const id in this.markersByClientId) {
				if (!newMarkersOnScreen[id]) {
					await this.removeMarker(id, true);
				}
			}
			(this.map.getSource(sourceName + '_spiderLegs') as GeoJSONSource).setData(this.createFeatureCollection(spiderLegFeatures));
		}, true);

		const createClusterNode = (feature: Feature) => {
			const coordinates = (feature.geometry as Point).coordinates;
			const props = feature.properties;

			const div = document.createElement('div');
			div.setAttribute('data-point-count', String(props.point_count));
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
			const angleStep = (2 * Math.PI) / Math.min(Math.max(3, leaves.length), 10.7);
			let legLength = isSpiral ? 0.0001 : 0.0002;
			const legLengthFactor = 0.000005;

			return Object.entries(features).reduce((markers, feature, i) => {
				const [id, leaf] = feature;
				if (leaf.properties.cluster || leaf.properties.marker == null) {
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
				this.initializeMarkerCluster('markers');
			}

			const markerFeatures = clusterConfig.markers.map(this.createMarkerFeature);
			(this.map.getSource('markers') as GeoJSONSource).setData(this.createFeatureCollection(markerFeatures));
		});
	}

	@Queued()
	public async addMarkerToCluster(marker: UiMapMarkerClientRecordConfig) {
		return this.deferredExecutor.invokeWhenReady(async () => {
			if (this.map.getSource('markers') == null) {
				this.initializeMarkerCluster('markers');
			}
			const source = this.map.getSource('markers') as GeoJSONSource;
			const features = (await source.getData() as GeoJSON.FeatureCollection).features ?? [];
			features.push(this.createMarkerFeature(marker));
			source.setData(this.createFeatureCollection(features));
		});
	}

	@Queued()
	private async removeMarkerFromCluster(id?: string) {
		const source = this.map.getSource('markers') as GeoJSONSource;
		if (source != null) {
			const data = await source.getData() as GeoJSON.FeatureCollection;
			if (data.features && data.features.length > 0) {
				if (id == null) {
					source.setData(this.createFeatureCollection([]));
				} else {
					source.setData(this.createFeatureCollection(data.features.filter(f => String(f.properties?.id) !== id)));
				}
			}
		}
	}

	private createFeatureCollection(features: Feature[]): GeoJSON.FeatureCollection {
		return {
			type: "FeatureCollection",
			features
		};
	}

	private createPolygonFeature(path: Position[], id?: string, config?: AbstractUiMapShapeConfig): Feature<GeoJSON.Polygon> {
		return {
			type: 'Feature',
			geometry: {
				type: 'Polygon',
				coordinates: [
					path
				]
			},
			id,
			properties: {id, config}
		};
	}

	private createPointFeature(config: UiMapLocationConfig, id?: string): Feature<GeoJSON.Point> {
		return {
			type: "Feature",
			id,
			properties: {id, config},
			geometry: {
				type: "Point",
				coordinates: [
					config.longitude,
					config.latitude
				]
			}
		};
	}

	private createMarkerFeature(m: UiMapMarkerClientRecordConfig): Feature<GeoJSON.Point> {
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

	private createHeatMapDataElementFeature(c: UiHeatMapDataElementConfig): Feature<GeoJSON.Point> {
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

	private createLineStringFeature(config: UiMapPolylineConfig, id?: string): Feature<GeoJSON.LineString> {
		return {
			type: "Feature",
			id,
			properties: {id, config},
			geometry: {
				type: "LineString",
				coordinates: config.path.map(loc => this.convertToPosition(loc))
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
			this.addMarkerToMap(String(markerConfig.id), marker);
		});
	}

	private addMarkerToMap(id: string, marker: Marker): void {
		if (this.markersByClientId[id]) {
			this.markersByClientId[id].remove(); // just in case...
		}
		this.markersByClientId[id] = marker;
		marker.addTo(this.map);
	}

	@Queued()
	public async removeMarker(id: number | string, temporaryRemoval = false) {
		return this.deferredExecutor.invokeWhenReady(async () => {
			id = String(id);
			this.markersByClientId[id]?.remove();
			delete this.markersByClientId[id];
			if (!temporaryRemoval) {
				await this.removeMarkerFromCluster(id);
			}
		});
	}

	@Queued()
	public async clearMarkers() {
		return this.deferredExecutor.invokeWhenReady(async () => {
			Object.values(this.markersByClientId).forEach(m => m.remove());
			this.markersByClientId = {};
			await this.removeMarkerFromCluster();
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
			this.fillOpacity,
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

	private convertToUiLocation(pos: Position): UiMapLocationConfig {
		return createUiMapLocationConfig(pos[1], pos[0]);
	}
	private flattenPositionArray(arr: Position | Position[] | Position[][] | Position[][][]): Position[] {
		if (!Array.isArray(arr) || arr.length === 0) {
			return [];
		}
		if (arr.length === 2 && (typeof arr[0] === 'number' || typeof arr[1] === 'number')) {
			return [arr] as Position[];
		}
		return (arr as any[]).reduce((acc, cur) => acc.concat(this.flattenPositionArray(cur)), []);
	}

	private calcCenterUiLocation(positions: Position[]): UiMapLocationConfig {
		const lngs = positions.map(m => m[0]);
		const lats = positions.map(m => m[1]);
		return createUiMapLocationConfig(
			(Math.min(...lats) + Math.max(...lats)) / 2,
			(Math.min(...lngs) + Math.max(...lngs)) / 2,
		);
	}

	private createDrawShapeStyles(drawMode: string, shapeProperties: UiShapePropertiesConfig): Record<string, number | HexColor> {
		let styles: any = {
			fillColor: shapeProperties.fillColor as HexColor,
			outlineColor: shapeProperties.strokeColor as HexColor,
			outlineWidth: shapeProperties.strokeWeight,
		};
		if (drawMode === 'linestring') {
			styles = {
				lineStringColor: shapeProperties.strokeColor as HexColor,
				lineStringWidth: shapeProperties.strokeWeight,
			};
		}
		if (shapeProperties.strokeColor == null) {
			delete styles.outlineColor;
			delete styles.lineStringColor;
		}
		if (shapeProperties.strokeWeight == null || shapeProperties.strokeWeight === 0) {
			delete styles.outlineWidth;
			delete styles.lineStringWidth;
		}
		if (shapeProperties.fillColor == null) {
			delete styles.fillColor;
		}
		return styles;
	}

	private mapShapeTypeToTerraDrawMode(shapeType: UiMapShapeType): string {
		switch (shapeType) {
			case UiMapShapeType.CIRCLE:
				return 'circle';
			case UiMapShapeType.RECTANGLE:
				return 'rectangle';
			case UiMapShapeType.POLYGON:
				return 'polygon';
			case UiMapShapeType.POLYLINE:
				return 'linestring';
			default:
				return 'static'; // just to be sure
		}
	}

	public startDrawingShape(shapeType: UiMapShapeType, shapeProperties: UiShapePropertiesConfig): void {
		this.deferredExecutor.invokeWhenReady(() => {
			if (this.draw.getMode() !== 'static') {
				this.stopDrawingShape();
			}
			const mode = this.mapShapeTypeToTerraDrawMode(shapeType);
			const styles = this.createDrawShapeStyles(mode, shapeProperties);
			this.drawingProperties = shapeProperties;
			this.draw.start();
			this.draw.setMode(mode);
			this.draw.updateModeOptions(mode, {styles});
		});
	}

	public stopDrawingShape(): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.draw.setMode('static');
			//this.draw.stop();
			this.drawingProperties = null;
		});
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

/**
 * Wraps an async function so that:
 * - queueOnlyLast=false: every call is queued (FIFO) until fn finishes
 * - queueOnlyLast=true: queues only the last call (FILO) and previous calls are solved by it as well
 *
 * @param fn your async function
 * @param queueOnlyLast true | false
 * @returns a scheduler with the same parameters, returning the original promise
 */
function makeQueue<T extends any[], R>(
	fn: (...args: T) => Promise<R>,
	queueOnlyLast: boolean = false
): (...args: T) => Promise<R> {
	interface IJob {
		args: T;
		resolvers: Array<{ resolve: (v: R) => void; reject: (e: any) => void; }>;
	}
	let running = false;
	const queue: IJob[] = [];
	const run = (job: IJob) => {
		running = true;
		fn(...job.args)
			.then(res => job.resolvers.forEach(r => r.resolve(res)), err => job.resolvers.forEach(r => r.reject(err)))
			.finally(() => {
				const next = queue.shift();
				running = false;
				if (next) {
					run(next);
				}
			});
	};

	return (...args: T): Promise<R> => {
		return new Promise<R>((resolve, reject) => {
			if (running) {
				if (queueOnlyLast && queue.length > 0) {
					// single: keep one queued job, append resolvers & replace args
					const job = queue[0];
					job.args = args;
					job.resolvers.push({resolve, reject});
				} else {
					queue.push({args, resolvers: [{resolve, reject}]});
				}
			} else {
				// idle → run immediately
				run({args, resolvers: [{resolve, reject}]});
			}
		});
	};
}

/**
 * Decorator to queue a method call if it is called while the method is already running.
 */
function Queued() {
	return (
		target: any,
		propertyKey: string,
		descriptor: PropertyDescriptor
	) => {
		const original = descriptor.value;
		const wrapperKey = Symbol(`__queue_${propertyKey}`);

		descriptor.value = function(...args: any[]) {
			// on first call, create & store the queued wrapper bound to this instance
			if (!this[wrapperKey]) {
				this[wrapperKey] = makeQueue(original.bind(this), false);
			}
			// delegate to the queued wrapper
			return this[wrapperKey](...args);
		};
	};
}

TeamAppsUiComponentRegistry.registerComponentClass("UiMap2", UiMap2);
