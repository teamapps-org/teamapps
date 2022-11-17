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
package org.teamapps.ux.component.map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.teamapps.ux.data.extraction.BeanPropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyProvider;
import org.teamapps.dto.*;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.field.combobox.TemplateDecider;
import org.teamapps.ux.component.map.shape.*;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapView<RECORD> extends AbstractComponent {

	public final ProjectorEvent<LocationChangedEventData> onLocationChanged = createProjectorEventBoundToUiEvent(UiMap.LocationChangedEvent.TYPE_ID);
	public final ProjectorEvent<Integer> onZoomLevelChanged = createProjectorEventBoundToUiEvent(UiMap.ZoomLevelChangedEvent.TYPE_ID);
	public final ProjectorEvent<Location> onMapClicked = createProjectorEventBoundToUiEvent(UiMap.MapClickedEvent.TYPE_ID);
	public final ProjectorEvent<Marker<RECORD>> onMarkerClicked = createProjectorEventBoundToUiEvent(UiMap.MarkerClickedEvent.TYPE_ID);
	public final ProjectorEvent<AbstractMapShape> onShapeDrawn = createProjectorEventBoundToUiEvent(UiMap.ShapeDrawnEvent.TYPE_ID);

	private MapType mapType = MapType.MAP_BOX_STREETS_SATELLITE;
	private String accessToken = null;
	private int zoomLevel = 5;
	private Location location = new Location(0, 0);
	private final Map<String, AbstractMapShape> shapesByClientId = new HashMap<>();
	private List<Marker<RECORD>> clusterMarkers = new ArrayList<>();

	private int clientIdCounter = 0;
	private final BidiMap<Integer, Marker<RECORD>> markersByClientId = new DualHashBidiMap<>();

	private Template defaultTemplate;
	private TemplateDecider<Marker<RECORD>> templateDecider = m -> defaultTemplate;
	private final Map<Template, String> templateIdsByTemplate = new HashMap<>();
	private int templateIdCounter = 0;
	private UiMapConfig mapConfig;

	private PropertyProvider<RECORD> markerPropertyProvider = new BeanPropertyExtractor<>();
	private final AbstractMapShape.MapShapeListener shapeListener = new AbstractMapShape.MapShapeListener() {
		@Override
		public void handleShapeChanged(AbstractMapShape shape) {
			sendCommandIfRendered(() -> new UiMap.UpdateShapeCommand(shape.getClientIdInternal(), shape.createUiMapShape()));
		}

		@Override
		public void handleShapeChanged(AbstractMapShape shape, AbstractUiMapShapeChange change) {
			handleShapeChanged(shape); // this is a lazy implementation, but since this is the legacy map implementation...
		}

		@Override
		public void handleShapeRemoved(AbstractMapShape shape) {
			sendCommandIfRendered(() -> new UiMap.RemoveShapeCommand(shape.getClientIdInternal()));
		}
	};

	public MapView(String accessToken) {
		this.accessToken = accessToken;
	}

	public MapView() {
	}

	@Override
	public UiComponent createUiClientObject() {
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
			uiMap.setMarkerCluster(new UiMapMarkerCluster(clusterMarkers.stream()
					.map(marker -> createUiMarkerRecord(marker, markersByClientId.getKey(marker)))
					.collect(Collectors.toList())));
		}
		uiMap.setMarkers(markersByClientId.entrySet().stream()
				.map(e -> createUiMarkerRecord(e.getValue(), e.getKey()))
				.collect(Collectors.toList()));
		return uiMap;
	}

	private UiMapMarkerClientRecord createUiMarkerRecord(Marker<RECORD> marker, int clientId) {
		UiMapMarkerClientRecord clientRecord = new UiMapMarkerClientRecord();
		clientRecord.setId(clientId);
		clientRecord.setLocation(marker.getLocation().createUiLocation());
		Template template = getTemplateForRecord(marker, templateDecider);
		if (template != null) {
			clientRecord.setTemplateId(templateIdsByTemplate.get(template));
			clientRecord.setValues(markerPropertyProvider.getValues(marker.getData(), template.getPropertyNames()));
		} else {
			clientRecord.setAsString("" + marker.getData());
		}
		clientRecord.setOffsetPixelsX(marker.getOffsetPixelsX());
		clientRecord.setOffsetPixelsY(marker.getOffsetPixelsY());
		return clientRecord;
	}

	@Override
	public void handleUiEvent(UiEventWrapper event) {
		switch (event.getTypeId()) {
			case UiMap.MapClickedEvent.TYPE_ID -> {
				var mapClickedEvent = event.as(UiMap.MapClickedEventWrapper.class);
				this.onMapClicked.fire(new Location(mapClickedEvent.getLocation().getLatitude(), mapClickedEvent.getLocation().getLongitude()));
			}
			case UiMap.MarkerClickedEvent.TYPE_ID -> {
				var markerClickedEvent = event.as(UiMap.MarkerClickedEventWrapper.class);
				Marker<RECORD> marker = markersByClientId.get(markerClickedEvent.getMarkerId());
				this.onMarkerClicked.fire(marker);
			}
			case UiMap.ZoomLevelChangedEvent.TYPE_ID -> {
				var zoomEvent = event.as(UiMap.ZoomLevelChangedEventWrapper.class);
				this.zoomLevel = zoomEvent.getZoomLevel();
				this.onZoomLevelChanged.fire(zoomLevel);
			}
			case UiMap.LocationChangedEvent.TYPE_ID -> {
				var locationEvent = event.as(UiMap.LocationChangedEventWrapper.class);
				this.location = Location.fromUiMapLocationWrapper(locationEvent.getCenter());
				UiMapAreaWrapper displayedUiArea = locationEvent.getDisplayedArea();
				Area displayedArea = new Area(displayedUiArea.getMinLatitude(), displayedUiArea.getMaxLatitude(), displayedUiArea.getMinLongitude(), displayedUiArea.getMaxLongitude());
				this.onLocationChanged.fire(new LocationChangedEventData(this.location, displayedArea));
			}
			case UiMap.ShapeDrawnEvent.TYPE_ID -> {
				var drawnEvent = event.as(UiMap.ShapeDrawnEventWrapper.class);
				AbstractUiMapShapeWrapper uiShape = drawnEvent.getShape();
				AbstractMapShape shape = switch (uiShape.getTypeId()) {
					case UiMapCircle.TYPE_ID -> {
						var uiCircle = uiShape.as(UiMapCircleWrapper.class);
						yield new MapCircle(Location.fromUiMapLocationWrapper(uiCircle.getCenter()), uiCircle.getRadius());
					}
					case UiMapPolygon.TYPE_ID -> {
						var uiPolygon = uiShape.as(UiMapPolygonWrapper.class);
						yield new MapPolygon(uiPolygon.getPath().stream().map(Location::fromUiMapLocationWrapper).collect(Collectors.toList()), null);
					}
					case UiMapPolyline.TYPE_ID -> {
						var uiPolyLine = uiShape.as(UiMapPolylineWrapper.class);
						yield new MapPolyline(uiPolyLine.getPath().stream().map(Location::fromUiMapLocationWrapper).collect(Collectors.toList()), null);
					}
					case UiMapRectangle.TYPE_ID -> {
						var uiRect = uiShape.as(UiMapRectangleWrapper.class);
						yield new MapRectangle(Location.fromUiMapLocationWrapper(uiRect.getL1()), Location.fromUiMapLocationWrapper(uiRect.getL2()), null);
					}
					default -> {
						throw new IllegalArgumentException("Unknown shape type from UI: " + drawnEvent.getShape().getClass());
					}
				};
				shape.setClientIdInternal(drawnEvent.getShapeId());
				shapesByClientId.put(drawnEvent.getShapeId(), shape);
				shape.setListenerInternal(shapeListener);
				this.onShapeDrawn.fire(shape);
			}

		}
	}

	public void setMapConfig(UiMapConfig mapConfig) {
		this.mapConfig = mapConfig;
	}

	public void addPolyLine(MapPolyline polyline) {
		addShape(polyline);
	}

	public void addShape(AbstractMapShape shape) {
		shape.setListenerInternal(shapeListener);
		shapesByClientId.put(shape.getClientIdInternal(), shape);
		sendCommandIfRendered(() -> new UiMap.AddShapeCommand(shape.getClientIdInternal(), shape.createUiMapShape()));
	}

	public void removeShape(AbstractMapShape shape) {
		shapesByClientId.remove(shape.getClientIdInternal());
		sendCommandIfRendered(() -> new UiMap.RemoveShapeCommand(shape.getClientIdInternal()));
	}

	public void clearShapes() {
		shapesByClientId.clear();
		sendCommandIfRendered(() -> new UiMap.ClearShapesCommand());
	}

	public void setMarkerCluster(List<Marker<RECORD>> markers) {
		clusterMarkers = markers;
		markers.forEach(m -> this.markersByClientId.put(clientIdCounter++, m));
		if (isRendered()) {
			sendCommandIfRendered(() -> {
				UiMapMarkerCluster markerCluster = new UiMapMarkerCluster(clusterMarkers.stream()
						.map(marker -> createUiMarkerRecord(marker, markersByClientId.getKey(marker)))
						.collect(Collectors.toList()));
				return new UiMap.SetMapMarkerClusterCommand(markerCluster);
			});
		}
	}

	public void clearMarkerCluster() {
		clusterMarkers.forEach(this.markersByClientId::removeValue);
		clusterMarkers.clear();
		sendCommandIfRendered(() -> new UiMap.ClearMarkerClusterCommand());
	}

	public void unCacheMarkers(List<Marker<RECORD>> markers) {
		markers.forEach(this.markersByClientId::removeValue);
	}

	public void setHeatMap(List<Location> locations) {
		List<UiHeatMapDataElement> heatMapElements = locations.stream().map(loc -> new UiHeatMapDataElement((float) loc.getLatitude(), (float) loc.getLongitude(), 1)).collect(Collectors.toList());
		UiHeatMapData heatMap = new UiHeatMapData(heatMapElements);
		sendCommandIfRendered(() -> new UiMap.SetHeatMapCommand(heatMap));
	}

	public void setHeatMap(UiHeatMapData heatMap) {
		sendCommandIfRendered(() -> new UiMap.SetHeatMapCommand(heatMap));
	}

	public void clearHeatMap() {
		sendCommandIfRendered(() -> new UiMap.ClearHeatMapCommand());
	}

	private Template getTemplateForRecord(Marker<RECORD> record, TemplateDecider<Marker<RECORD>> templateDecider) {
		Template template = templateDecider.getTemplate(record);
		if (template != null && !templateIdsByTemplate.containsKey(template)) {
			String uuid = "" + templateIdCounter++;
			this.templateIdsByTemplate.put(template, uuid);
			sendCommandIfRendered(() -> new UiMap.RegisterTemplateCommand(uuid, template.createUiTemplate()));
		}
		return template;
	}

	public MapType getMapType() {
		return mapType;
	}

	public void setMapType(MapType mapType) {
		this.mapType = mapType;
		sendCommandIfRendered(() -> new UiMap.SetMapTypeCommand(this.mapType.toUiMapType()));
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		sendCommandIfRendered(() -> new UiMap.SetZoomLevelCommand(zoomLevel));
	}

	public void setLocation(Location location) {
		this.location = location;
		sendCommandIfRendered(() -> new UiMap.SetLocationCommand(location.createUiLocation()));
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
		int clientId = clientIdCounter++;
		this.markersByClientId.put(clientId, marker);
		if (isRendered()) {
			getSessionContext().sendCommand(getId(), new UiMap.AddMarkerCommand(createUiMarkerRecord(marker, clientId)));
		}
	}

	public void removeMarker(Marker<RECORD> marker) {
		Integer clientId = markersByClientId.removeValue(marker);
		if (clientId != null) {
			if (isRendered()) {
				getSessionContext().sendCommand(getId(), new UiMap.RemoveMarkerCommand(clientId));
			}
		}
	}

	public void clearMarkers() {
		this.markersByClientId.values().removeIf(m -> !clusterMarkers.contains(m));
		sendCommandIfRendered(() -> new UiMap.ClearMarkersCommand());
	}

	public void fitBounds(Location southWest, Location northEast) {
		this.location = new Location((southWest.getLatitude() + northEast.getLatitude()) / 2, (southWest.getLongitude() + northEast.getLongitude()) / 2);
		sendCommandIfRendered(() -> new UiMap.FitBoundsCommand(southWest.createUiLocation(), northEast.createUiLocation()));
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

	public PropertyProvider<RECORD> getMarkerPropertyProvider() {
		return markerPropertyProvider;
	}

	public void setMarkerPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.markerPropertyProvider = propertyProvider;
	}

	public void setMarkerPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.setMarkerPropertyProvider(propertyExtractor);
	}

	public void startDrawingShape(MapShapeType shapeType, ShapeProperties shapeProperties) {
		sendCommandIfRendered(() -> new UiMap.StartDrawingShapeCommand(shapeType.toUiMapShapeType(), shapeProperties.createUiShapeProperties()));
	}

	public void stopDrawingShape() {
		sendCommandIfRendered(() -> new UiMap.StopDrawingShapeCommand());
	}

}
