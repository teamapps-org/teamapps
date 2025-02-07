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


import {
	AbstractLegacyComponent,
	DeferredExecutor,
	parseHtml,
	ServerObjectChannel,
	ProjectorEvent,
	Template
} from "projector-client-object-api";
import {
	createArea,
	createLocation,
	DtoAbstractMapShape,
	DtoMapView,
	Location,
	DtoMapCircle,
	DtoMapPolygon,
	DtoMapPolyline,
	DtoMapRectangle,
	DtoAbstractMapShapeChange,
	DtoPolylineAppend,
	DtoMapMarkerCluster,
	DtoMapMarkerClientRecord,
	DtoHeatMapData,
	DtoShapeProperties,
	ShapeType,
	DtoMapViewCommandHandler,
	DtoMapViewEventSource,
	DtoMapView_ZoomLevelChangedEvent,
	DtoMapView_ShapeDrawnEvent,
	DtoMapView_MarkerClickedEvent,
	DtoMapView_MapClickedEvent,
	DtoMapView_LocationChangedEvent
} from "./generated";
import mapboxgl, {GeoJSONSource, LngLatLike, Map as MapBoxMap, Marker} from "maplibre-gl";
import {Feature, Point, Position} from "geojson";
import * as d3 from "d3";

export function isCircle(shapeConfig: DtoAbstractMapShape): shapeConfig is DtoMapCircle {
	return shapeConfig._type === "MapCircle";
}

export function isPolygon(shapeConfig: DtoAbstractMapShape): shapeConfig is DtoMapPolygon {
	return shapeConfig._type === "MapPolygon";
}

export function isPolyline(shapeConfig: DtoAbstractMapShape): shapeConfig is DtoMapPolyline {
	return shapeConfig._type === "MapPolyline";
}

export function isRectangle(shapeConfig: DtoAbstractMapShape): shapeConfig is DtoMapRectangle {
	return shapeConfig._type === "MapRectangle";
}

export class MapView extends AbstractLegacyComponent<DtoMapView> implements DtoMapViewEventSource, DtoMapViewCommandHandler {

	public readonly onZoomLevelChanged: ProjectorEvent<DtoMapView_ZoomLevelChangedEvent> = ProjectorEvent.createThrottled(500);
	public readonly onLocationChanged: ProjectorEvent<DtoMapView_LocationChangedEvent> = ProjectorEvent.createThrottled(500);
	public readonly onMapClicked: ProjectorEvent<DtoMapView_MapClickedEvent> = new ProjectorEvent();
	public readonly onMarkerClicked: ProjectorEvent<DtoMapView_MarkerClickedEvent> = new ProjectorEvent();
	public readonly onShapeDrawn: ProjectorEvent<DtoMapView_ShapeDrawnEvent> = new ProjectorEvent();

	private $map: HTMLElement;
	private map: MapBoxMap;
	private markerTemplateRenderers: { [templateName: string]: Template } = {};
	private markersByClientId: { [id: number]: Marker } = {};
	private shapesById: Map<string, { config: DtoAbstractMapShape }> = new Map();

	private deferredExecutor: DeferredExecutor = new DeferredExecutor();

	constructor(config: DtoMapView, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.$map = parseHtml('<div class="Map">');

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
				center: createLocation(center.lat, center.lng),
				displayedArea: createArea(bounds.getNorth(), bounds.getSouth(), bounds.getWest(), bounds.getEast())
			});
		})
		this.map.on("click", ev => {
			this.onMapClicked.fire({location: createLocation(ev.lngLat.lat, ev.lngLat.lng)})
		})

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

	public addShape(shapeId: string, shapeConfig: DtoAbstractMapShape): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.shapesById.set(shapeId, {config: shapeConfig});
			if (isCircle(shapeConfig)) {
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
			} else if (isPolyline(shapeConfig)) {
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
			} else if (isPolygon(shapeConfig)) {
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
			} else if (isRectangle(shapeConfig)) {
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

	private toGeoJsonFeature(shapeConfig: DtoMapPolyline) {
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

	updateShape(shapeId: string, shape: DtoAbstractMapShape): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.removeShape(shapeId);
			this.addShape(shapeId, shape);
		});
	}

	changeShape(shapeId: string, change: DtoAbstractMapShapeChange): void {
		this.deferredExecutor.invokeWhenReady(() => {
			if (isPolyLineAppend(change)) {
				let config = this.shapesById.get(shapeId).config as DtoMapPolyline;
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

	public setMapMarkerCluster(clusterConfig: DtoMapMarkerCluster): void {
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
						e.stopPropagation();
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


	private createMarkersFeatureCollection(markers: DtoMapMarkerClientRecord[]): GeoJSON.FeatureCollection {
		return {
			type: "FeatureCollection",
			features: markers.map(this.createMarkerFeature)
		};
	}

	private createMarkerFeature(m: DtoMapMarkerClientRecord): Feature {
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

	private convertToPosition(loc: Location): Position {
		return [loc.longitude, loc.latitude];
	}

	private convertToLngLatLike(loc: Location): LngLatLike {
		return this.convertToPosition(loc) as LngLatLike;
	}

	public addMarker(markerConfig: DtoMapMarkerClientRecord): void {
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

	private createMarker(markerConfig: DtoMapMarkerClientRecord) {
		let renderer = markerConfig.template as Template;
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
	setHeatMap(data: DtoHeatMapData): void {
		throw new Error("Method not implemented.");
	}

	// TODO
	startDrawingShape(shapeType: ShapeType, shapeProperties: DtoShapeProperties): void {
		throw new Error("Method not implemented.");
	}

	// TODO
	stopDrawingShape(): void {
		throw new Error("Method not implemented.");
	}

	setZoomLevel(zoom: number): void {
		this.map.zoomTo(zoom)
	}

	setLocation(location: Location, animationDurationMillis: number, targetZoomLevel: number): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.map.easeTo({
				center: [location.longitude, location.latitude],
				duration: animationDurationMillis,
				zoom: targetZoomLevel
			});
		});
	}

	fitBounds(southWest: Location, northEast: Location): void {
		this.deferredExecutor.invokeWhenReady(() => {
			this.map.fitBounds([
				[southWest.longitude, southWest.latitude],
				[northEast.longitude, northEast.latitude],
			]);
		});
	}

	public doGetMainElement(): HTMLElement {
		return this.$map;
	}

	onResize() {
		this.map.resize();
	}
}

function isPolyLineAppend(change: DtoAbstractMapShapeChange): change is DtoPolylineAppend {
	return change._type === "DtoPolylineAppend";
}


