/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.component.map;

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.CacheManipulationHandle;
import org.teamapps.ux.cache.ClientRecordCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.field.combobox.TemplateDecider;
import org.teamapps.ux.component.map.shape.AbstractMapShape;
import org.teamapps.ux.component.map.shape.MapCircle;
import org.teamapps.ux.component.map.shape.MapPolygon;
import org.teamapps.ux.component.map.shape.MapPolyline;
import org.teamapps.ux.component.map.shape.MapRectangle;
import org.teamapps.ux.component.map.shape.ShapeProperties;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapView<RECORD> extends AbstractComponent {

	public final Event<LocationChangedEventData> onLocationChanged = new Event<>();
	public final Event<Integer> onZoomLevelChanged = new Event<>();
	public final Event<Location> onMapClicked = new Event<>();
	public final Event<Marker<RECORD>> onMarkerClicked = new Event<>();
	public final Event<AbstractMapShape> onShapeDrawn = new Event<>();

	private MapType mapType = MapType.MAP_BOX_STREETS_SATELLITE;
	private String accessToken = null;
	private int zoomLevel = 5;
	private Location location = new Location(0, 0);
	private Map<String, AbstractMapShape> shapesByClientId = new HashMap<>();
	private List<Marker<RECORD>> markers = new ArrayList<>();
	private List<Marker<RECORD>> clusterMarkers = new ArrayList<>();

	private final ClientRecordCache<Marker<RECORD>, UiMapMarkerClientRecord> recordCache = new ClientRecordCache<>(this::createUiRecord);

	private Template defaultTemplate;
	private TemplateDecider<Marker<RECORD>> templateDecider = m -> defaultTemplate;
	private final Map<Template, String> templateIdsByTemplate = new HashMap<>();
	private int templateIdCounter = 0;
	private UiMapConfig mapConfig;

	private PropertyExtractor<RECORD> markerPropertyExtractor = new BeanPropertyExtractor<>();
	private AbstractMapShape.MapShapeListener shapeListener = new AbstractMapShape.MapShapeListener() {
		@Override
		public void handleChanged(AbstractMapShape shape) {
			queueCommandIfRendered(() -> new UiMap.UpdateShapeCommand(getId(), shape.getClientIdInternal(), shape.createUiMapShape()));
		}

		@Override
		public void handleRemoved(AbstractMapShape shape) {
			queueCommandIfRendered(() -> new UiMap.RemoveShapeCommand(getId(), shape.getClientIdInternal()));
		}
	};

	public MapView(String accessToken) {
		this.accessToken = accessToken;
	}

	public MapView() {
	}

	@Override
	public UiComponent createUiComponent() {
		UiMap uiMap = new UiMap(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));
		mapAbstractUiComponentProperties(uiMap);
		uiMap.setMapConfig(mapConfig);
		uiMap.setMapType(mapType.toUiMapType());
		uiMap.setAccessToken(accessToken);
		uiMap.setZoomLevel(zoomLevel);
		Map<String, AbstractUiMapShape> uiShapes = new HashMap<>();
		shapesByClientId.forEach((id, shape) -> uiShapes.put(id, shape.createUiMapShape()));
		uiMap.setShapes(uiShapes);
		if (location != null) {
			uiMap.setMapPosition(location.createUiLocation());
		}
		if (clusterMarkers != null && !clusterMarkers.isEmpty()) {
			uiMap.setMarkerCluster(createMarkerCluster(clusterMarkers));
		}
		CacheManipulationHandle<List<UiMapMarkerClientRecord>> cacheResponse = recordCache.replaceRecords(markers);
		uiMap.setMarkers(cacheResponse.getResult());
		cacheResponse.commit();
		return uiMap;
	}

	private UiMapMarkerClientRecord createUiRecord(Marker<RECORD> marker) {
		UiMapMarkerClientRecord clientRecord = new UiMapMarkerClientRecord();
		clientRecord.setLocation(marker.getLocation().createUiLocation());
		Template template = getTemplateForRecord(marker, templateDecider);
		if (template != null) {
			clientRecord.setTemplateId(templateIdsByTemplate.get(template));
			clientRecord.setValues(markerPropertyExtractor.getValues(marker.getData(), template.getDataKeys()));
		} else {
			clientRecord.setAsString("" + marker.getData());
		}
		clientRecord.setOffsetPixelsX(marker.getOffsetPixelsX());
		clientRecord.setOffsetPixelsY(marker.getOffsetPixelsY());
		return clientRecord;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_MAP_MAP_CLICKED: {
				UiMap.MapClickedEvent mapClickedEvent = (UiMap.MapClickedEvent) event;
				this.onMapClicked.fire(new Location(mapClickedEvent.getLocation().getLatitude(), mapClickedEvent.getLocation().getLongitude()));
				break;
			}
			case UI_MAP_MARKER_CLICKED: {
				UiMap.MarkerClickedEvent markerClickedEvent = (UiMap.MarkerClickedEvent) event;
				Marker<RECORD> marker = recordCache.getRecordByClientId(markerClickedEvent.getMarkerId());
				this.onMarkerClicked.fire(marker);
				break;
			}
			case UI_MAP_ZOOM_LEVEL_CHANGED: {
				UiMap.ZoomLevelChangedEvent zoomEvent = (UiMap.ZoomLevelChangedEvent) event;
				this.zoomLevel = zoomEvent.getZoomLevel();
				this.onZoomLevelChanged.fire(zoomLevel);
				break;
			}
			case UI_MAP_LOCATION_CHANGED: {
				UiMap.LocationChangedEvent locationEvent = (UiMap.LocationChangedEvent) event;
				this.location = Location.fromUiMapLocation(locationEvent.getCenter());
				UiMapArea displayedUiArea = locationEvent.getDisplayedArea();
				Area displayedArea = new Area(displayedUiArea.getMinLatitude(), displayedUiArea.getMaxLatitude(), displayedUiArea.getMinLongitude(), displayedUiArea.getMaxLongitude());
				this.onLocationChanged.fire(new LocationChangedEventData(this.location, displayedArea));
				break;
			}
			case UI_MAP_SHAPE_DRAWN: {
				UiMap.ShapeDrawnEvent drawnEvent = (UiMap.ShapeDrawnEvent) event;
				AbstractMapShape shape;
				switch (drawnEvent.getShape().getUiObjectType()) {
					case UI_MAP_CIRCLE:{
						UiMapCircle uiCircle = (UiMapCircle) drawnEvent.getShape();
						shape = new MapCircle(null, Location.fromUiMapLocation(uiCircle.getCenter()), uiCircle.getRadius());
						break;
					}
					case UI_MAP_POLYGON:{
						UiMapPolygon uiPolygon = (UiMapPolygon) drawnEvent.getShape();
						shape = new MapPolygon(null, uiPolygon.getPath().stream().map(Location::fromUiMapLocation).collect(Collectors.toList()));
						break;
					}
					case UI_MAP_POLYLINE:{
						UiMapPolyline uiPolyLine = (UiMapPolyline) drawnEvent.getShape();
						shape = new MapPolyline(null, uiPolyLine.getPath().stream().map(Location::fromUiMapLocation).collect(Collectors.toList()));
						break;
					}
					case UI_MAP_RECTANGLE:{
						UiMapRectangle uiRect = (UiMapRectangle) drawnEvent.getShape();
						shape = new MapRectangle(null, Location.fromUiMapLocation(uiRect.getL1()), Location.fromUiMapLocation(uiRect.getL2()));
						break;
					}
					default:
						throw new IllegalArgumentException("Unknown shape type from UI: " + drawnEvent.getShape().getUiObjectType());
				}
				shape.setClientIdInternal(drawnEvent.getShapeId());
				shapesByClientId.put(drawnEvent.getShapeId(), shape);
				shape.setListenerInternal(shapeListener);
				this.onShapeDrawn.fire(shape);
				break;
			}
		}
	}

	public void setMapConfig(UiMapConfig mapConfig) {
		this.mapConfig = mapConfig;
	}

	public void addPolyLine(AbstractMapShape shape) {
		shape.setListenerInternal(shapeListener);
		shapesByClientId.put(shape.getClientIdInternal(), shape);
		queueCommandIfRendered(() -> new UiMap.AddShapeCommand(getId(), shape.getClientIdInternal(), shape.createUiMapShape()));
	}

	public void removeShape(AbstractMapShape shape) {
		queueCommandIfRendered(() -> new UiMap.RemoveShapeCommand(getId(), shape.getClientIdInternal()));
	}

	public void setMarkerCluster(List<Marker<RECORD>> markers) {
		clusterMarkers = markers;
		CacheManipulationHandle<List<UiMapMarkerClientRecord>> cacheManipulationHandle = recordCache.addRecords(markers);
		cacheManipulationHandle.commit();
		List<UiMapMarkerClientRecord> result = cacheManipulationHandle.getResult();
		UiMapMarkerCluster cluster = new UiMapMarkerCluster(result);
		queueCommandIfRendered(() -> new UiMap.SetMapMarkerClusterCommand(getId(), cluster));
	}

	public void clearClusterMarkersFromCache(List<Marker<RECORD>> markers) {
		CacheManipulationHandle<List<Integer>> manipulationHandle = recordCache.removeRecords(markers);
		manipulationHandle.commit();
	}

	private UiMapMarkerCluster createMarkerCluster(List<Marker<RECORD>> markers) {
		List<UiMapMarkerClientRecord> uiMarkers = new ArrayList<>();
		markers.forEach(marker -> uiMarkers.add(createUiRecord(marker)));
		return new UiMapMarkerCluster(uiMarkers);
	}

	public void setHeatMap(List<Location> locations) {
		List<UiHeatMapDataElement> heatMapElements = locations.stream().map(loc -> new UiHeatMapDataElement((float) loc.getLatitude(), (float) loc.getLongitude(), 1)).collect(Collectors.toList());
		UiHeatMapData heatMap = new UiHeatMapData(heatMapElements);
		queueCommandIfRendered(() -> new UiMap.SetHeatMapCommand(getId(), heatMap));
	}

	public void setHeatMap(UiHeatMapData heatMap) {
		queueCommandIfRendered(() -> new UiMap.SetHeatMapCommand(getId(), heatMap));
	}

	private Template getTemplateForRecord(Marker<RECORD> record, TemplateDecider<Marker<RECORD>> templateDecider) {
		Template template = templateDecider.getTemplate(record);
		if (template != null && !templateIdsByTemplate.containsKey(template)) {
			String uuid = "" + templateIdCounter++;
			this.templateIdsByTemplate.put(template, uuid);
			queueCommandIfRendered(() -> new UiComboBox.RegisterTemplateCommand(getId(), uuid, template.createUiTemplate()));
		}
		return template;
	}

	public MapType getMapType() {
		return mapType;
	}

	public void setMapType(MapType mapType) {
		this.mapType = mapType;
		queueCommandIfRendered(() -> new UiMap.SetMapTypeCommand(getId(), this.mapType.toUiMapType()));
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		queueCommandIfRendered(() -> new UiMap.SetZoomLevelCommand(getId(), zoomLevel));
	}

	public void setLocation(Location location) {
		this.location = location;
		queueCommandIfRendered(() -> new UiMap.SetLocationCommand(getId(), location.createUiLocation()));
	}

	public void setLocation(double latitude, double longitude) {
		setLocation(new Location(latitude, longitude));
	}

	public void setLatitude(double latitude) {
		setLocation(new Location(latitude, location.getLongitude()));
	}

	public void setLongitude(double longitude) {
		setLocation(new Location(location.getLatitude(), longitude));
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public Location getLocation() {
		return location;
	}

	public void addMarker(Marker<RECORD> marker) {
		this.markers.add(marker);
		CacheManipulationHandle<UiMapMarkerClientRecord> cacheResponse = recordCache.addRecord(marker);
		if (isRendered()) {
			getSessionContext().queueCommand(new UiMap.AddMarkerCommand(getId(), cacheResponse.getResult()), aVoid -> cacheResponse.commit());
		} else {
			cacheResponse.commit();
		}
	}

	public void removeMarker(Marker<RECORD> marker) {
		boolean removed = markers.remove(marker);
		if (removed) {
			CacheManipulationHandle<Integer> cacheResponse = recordCache.removeRecord(marker);
			if (isRendered()) {
				getSessionContext().queueCommand(new UiMap.RemoveMarkerCommand(getId(), cacheResponse.getResult()), aVoid -> cacheResponse.commit());
			} else {
				cacheResponse.commit();
			}
		}
	}

	public void fitBounds(Location southWest, Location northEast) {
		this.location = new Location((southWest.getLatitude() + northEast.getLatitude()) / 2, (southWest.getLongitude() + northEast.getLongitude()) / 2);
		queueCommandIfRendered(() -> new UiMap.FitBoundsCommand(this.getId(), southWest.createUiLocation(), northEast.createUiLocation()));
	}

	public Template getDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultMarkerTemplate(Template defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	public TemplateDecider<Marker<RECORD>> getTemplateDecider() {
		return templateDecider;
	}

	public void setMarkerTemplateDecider(TemplateDecider<Marker<RECORD>> templateDecider) {
		this.templateDecider = templateDecider;
	}

	public PropertyExtractor<RECORD> getMarkerPropertyExtractor() {
		return markerPropertyExtractor;
	}

	public void setMarkerPropertyExtractor(PropertyExtractor<RECORD> markerPropertyExtractor) {
		this.markerPropertyExtractor = markerPropertyExtractor;
	}

	public void startDrawingShape(MapShapeType shapeType, ShapeProperties shapeProperties) {
		queueCommandIfRendered(() -> new UiMap.StartDrawingShapeCommand(getId(), shapeType.toUiMapShapeType(), shapeProperties.createUiShapeProperties()));
	}

	public void stopDrawingShape() {
		queueCommandIfRendered(() -> new UiMap.StopDrawingShapeCommand(getId()));
	}

}
