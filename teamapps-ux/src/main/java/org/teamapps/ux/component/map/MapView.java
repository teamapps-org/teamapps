/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
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

	public final Event<LocationChangedEventData> onLocationChanged = new Event<>();
	public final Event<Integer> onZoomLevelChanged = new Event<>();
	public final Event<Location> onMapClicked = new Event<>();
	public final Event<Marker<RECORD>> onMarkerClicked = new Event<>();
	public final Event<AbstractMapShape> onShapeDrawn = new Event<>();

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
			clientRecord.setValues(markerPropertyProvider.getValues(marker.getData(), template.getDataKeys()));
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
				Marker<RECORD> marker = markersByClientId.get(markerClickedEvent.getMarkerId());
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
					case UI_MAP_CIRCLE: {
						UiMapCircle uiCircle = (UiMapCircle) drawnEvent.getShape();
						shape = new MapCircle(Location.fromUiMapLocation(uiCircle.getCenter()), uiCircle.getRadius());
						break;
					}
					case UI_MAP_POLYGON: {
						UiMapPolygon uiPolygon = (UiMapPolygon) drawnEvent.getShape();
						shape = new MapPolygon(uiPolygon.getPath().stream().map(Location::fromUiMapLocation).collect(Collectors.toList()), null);
						break;
					}
					case UI_MAP_POLYLINE: {
						UiMapPolyline uiPolyLine = (UiMapPolyline) drawnEvent.getShape();
						shape = new MapPolyline(uiPolyLine.getPath().stream().map(Location::fromUiMapLocation).collect(Collectors.toList()), null);
						break;                                                       
					}
					case UI_MAP_RECTANGLE: {
						UiMapRectangle uiRect = (UiMapRectangle) drawnEvent.getShape();
						shape = new MapRectangle(Location.fromUiMapLocation(uiRect.getL1()), Location.fromUiMapLocation(uiRect.getL2()), null);
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

	public void addPolyLine(MapPolyline polyline) {
		addShape(polyline);
	}
	
	public void addShape(AbstractMapShape shape) {
		shape.setListenerInternal(shapeListener);
		shapesByClientId.put(shape.getClientIdInternal(), shape);
		queueCommandIfRendered(() -> new UiMap.AddShapeCommand(getId(), shape.getClientIdInternal(), shape.createUiMapShape()));
	}

	public void removeShape(AbstractMapShape shape) {
		shapesByClientId.remove(shape.getClientIdInternal());
		queueCommandIfRendered(() -> new UiMap.RemoveShapeCommand(getId(), shape.getClientIdInternal()));
	}

	public void clearShapes() {
		shapesByClientId.clear();
		queueCommandIfRendered(() -> new UiMap.ClearShapesCommand(getId()));
	}

	public void setMarkerCluster(List<Marker<RECORD>> markers) {
		clusterMarkers = markers;
		markers.forEach(m -> this.markersByClientId.put(clientIdCounter++, m));
		if (isRendered()) {
			queueCommandIfRendered(() -> {
				UiMapMarkerCluster markerCluster = new UiMapMarkerCluster(clusterMarkers.stream()
						.map(marker -> createUiMarkerRecord(marker, markersByClientId.getKey(marker)))
						.collect(Collectors.toList()));
				return new UiMap.SetMapMarkerClusterCommand(getId(),
						markerCluster);
			});
		}
	}

	public void clearMarkerCluster() {
		clusterMarkers.forEach(this.markersByClientId::removeValue);
		clusterMarkers.clear();
		queueCommandIfRendered(() -> new UiMap.ClearMarkerClusterCommand(getId()));
	}

	public void unCacheMarkers(List<Marker<RECORD>> markers) {
		markers.forEach(this.markersByClientId::removeValue);
	}

	public void setHeatMap(List<Location> locations) {
		List<UiHeatMapDataElement> heatMapElements = locations.stream().map(loc -> new UiHeatMapDataElement((float) loc.getLatitude(), (float) loc.getLongitude(), 1)).collect(Collectors.toList());
		UiHeatMapData heatMap = new UiHeatMapData(heatMapElements);
		queueCommandIfRendered(() -> new UiMap.SetHeatMapCommand(getId(), heatMap));
	}

	public void setHeatMap(UiHeatMapData heatMap) {
		queueCommandIfRendered(() -> new UiMap.SetHeatMapCommand(getId(), heatMap));
	}

	public void clearHeatMap() {
		queueCommandIfRendered(() -> new UiMap.ClearHeatMapCommand(getId()));
	}

	private Template getTemplateForRecord(Marker<RECORD> record, TemplateDecider<Marker<RECORD>> templateDecider) {
		Template template = templateDecider.getTemplate(record);
		if (template != null && !templateIdsByTemplate.containsKey(template)) {
			String uuid = "" + templateIdCounter++;
			this.templateIdsByTemplate.put(template, uuid);
			queueCommandIfRendered(() -> new UiMap.RegisterTemplateCommand(getId(), uuid, template.createUiTemplate()));
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
		int clientId = clientIdCounter++;
		this.markersByClientId.put(clientId, marker);
		if (isRendered()) {
			getSessionContext().queueCommand(new UiMap.AddMarkerCommand(getId(), createUiMarkerRecord(marker, clientId)));
		}
	}

	public void removeMarker(Marker<RECORD> marker) {
		Integer clientId = markersByClientId.removeValue(marker);
		if (clientId != null) {
			if (isRendered()) {
				getSessionContext().queueCommand(new UiMap.RemoveMarkerCommand(getId(), clientId));
			}
		}
	}

	public void clearMarkers() {
		this.markersByClientId.values().removeIf(m -> !clusterMarkers.contains(m));
		queueCommandIfRendered(() -> new UiMap.ClearMarkersCommand(getId()));
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
		queueCommandIfRendered(() -> new UiMap.StartDrawingShapeCommand(getId(), shapeType.toUiMapShapeType(), shapeProperties.createUiShapeProperties()));
	}

	public void stopDrawingShape() {
		queueCommandIfRendered(() -> new UiMap.StopDrawingShapeCommand(getId()));
	}

}
