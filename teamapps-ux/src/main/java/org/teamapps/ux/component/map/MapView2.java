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

import java.util.*;
import java.util.stream.Collectors;

public class MapView2<RECORD> extends AbstractComponent {

	public final Event<LocationChangedEventData> onLocationChanged = new Event<>();
	public final Event<Float> onZoomLevelChanged = new Event<>();
	public final Event<Location> onMapClicked = new Event<>();
	public final Event<Marker<RECORD>> onMarkerClicked = new Event<>();
	public final Event<AbstractMapShape> onShapeDrawn = new Event<>();


	private String styleUrl;
	private boolean displayAttributionControl = false;
	private boolean displayNavigationControl = false;
	private float zoomLevel = 10f;
	private Location location = new Location(0, 0);
	private final Map<String, AbstractMapShape> shapesByClientId = new HashMap<>();
	private List<Marker<RECORD>> clusterMarkers = new ArrayList<>();
	private UiHeatMapData heatMapData = null;

	private int clientIdCounter = 0;
	private final BidiMap<Integer, Marker<RECORD>> markersByClientId = new DualHashBidiMap<>();

	private Template defaultTemplate;
	private TemplateDecider<Marker<RECORD>> templateDecider = m -> defaultTemplate;
	private final Map<Template, String> templateIdsByTemplate = new HashMap<>();
	private int templateIdCounter = 0;

	private PropertyProvider<RECORD> markerPropertyProvider = new BeanPropertyExtractor<>();
	private final AbstractMapShape.MapShapeListener shapeListener = new AbstractMapShape.MapShapeListener() {
		@Override
		public void handleShapeChanged(AbstractMapShape shape) {
			queueCommandIfRendered(() -> new UiMap2.UpdateShapeCommand(getId(), shape.getClientIdInternal(), shape.createUiMapShape()));
		}

		@Override
		public void handleShapeChanged(AbstractMapShape shape, AbstractUiMapShapeChange change) {
			queueCommandIfRendered(() -> new UiMap2.ChangeShapeCommand(getId(), shape.getClientIdInternal(), change));
		}

		@Override
		public void handleShapeRemoved(AbstractMapShape shape) {
			queueCommandIfRendered(() -> new UiMap2.RemoveShapeCommand(getId(), shape.getClientIdInternal()));
		}
	};

	/**
	 * @deprecated parameters baseApiUrl and accessToken are obsolete.
	 */
	public MapView2(String baseApiUrl, String accessToken, String styleUrl) {
		this(styleUrl);
	}

	public MapView2(String styleUrl) {
		this.styleUrl = styleUrl;
	}

	@Override
	public UiComponent createUiComponent() {
		UiMap2 uiMap = new UiMap2(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));
		mapAbstractUiComponentProperties(uiMap);

		uiMap.setStyleUrl(styleUrl);
		uiMap.setDisplayAttributionControl(displayAttributionControl);
		uiMap.setDisplayNavigationControl(displayNavigationControl);

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
		if (heatMapData != null) {
			uiMap.setHeatMap(heatMapData);
		}
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
		clientRecord.setAnchor(marker.getMarkerAnchor().toUiMapMarkerAnchor());
		return clientRecord;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_MAP2_MAP_CLICKED: {
				UiMap2.MapClickedEvent mapClickedEvent = (UiMap2.MapClickedEvent) event;
				this.onMapClicked.fire(new Location(mapClickedEvent.getLocation().getLatitude(), mapClickedEvent.getLocation().getLongitude()));
				break;
			}
			case UI_MAP2_MARKER_CLICKED: {
				UiMap2.MarkerClickedEvent markerClickedEvent = (UiMap2.MarkerClickedEvent) event;
				Marker<RECORD> marker = markersByClientId.get(markerClickedEvent.getMarkerId());
				this.onMarkerClicked.fire(marker);
				break;
			}
			case UI_MAP2_ZOOM_LEVEL_CHANGED: {
				UiMap2.ZoomLevelChangedEvent zoomEvent = (UiMap2.ZoomLevelChangedEvent) event;
				this.zoomLevel = zoomEvent.getZoomLevel();
				this.onZoomLevelChanged.fire(zoomLevel);
				break;
			}
			case UI_MAP2_LOCATION_CHANGED: {
				UiMap2.LocationChangedEvent locationEvent = (UiMap2.LocationChangedEvent) event;
				this.location = Location.fromUiMapLocation(locationEvent.getCenter());
				UiMapArea displayedUiArea = locationEvent.getDisplayedArea();
				Area displayedArea = new Area(displayedUiArea.getMinLatitude(), displayedUiArea.getMaxLatitude(), displayedUiArea.getMinLongitude(), displayedUiArea.getMaxLongitude());
				this.onLocationChanged.fire(new LocationChangedEventData(this.location, displayedArea));
				break;
			}
			case UI_MAP2_SHAPE_DRAWN: {
				UiMap2.ShapeDrawnEvent drawnEvent = (UiMap2.ShapeDrawnEvent) event;
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

	public void addPolyLine(MapPolyline polyline) {
		addShape(polyline);
	}

	public void addShape(AbstractMapShape shape) {
		shape.setListenerInternal(shapeListener);
		shapesByClientId.put(shape.getClientIdInternal(), shape);
		queueCommandIfRendered(() -> new UiMap2.AddShapeCommand(getId(), shape.getClientIdInternal(), shape.createUiMapShape()));
	}

	public void removeShape(AbstractMapShape shape) {
		queueCommandIfRendered(() -> new UiMap2.RemoveShapeCommand(getId(), shape.getClientIdInternal()));
	}

	public void clearShapes() {
		queueCommandIfRendered(() -> new UiMap2.ClearShapesCommand(getId()));
	}

	public void setMarkerCluster(List<Marker<RECORD>> markers) {
		clusterMarkers = markers;
		markers.forEach(m -> this.markersByClientId.put(clientIdCounter++, m));
		queueCommandIfRendered(() -> {
			UiMapMarkerCluster markerCluster = new UiMapMarkerCluster(clusterMarkers.stream()
					.map(marker -> createUiMarkerRecord(marker, markersByClientId.getKey(marker)))
					.collect(Collectors.toList()));
			return new UiMap2.SetMapMarkerClusterCommand(getId(),
					markerCluster);
		});
	}

	public void clearMarkerCluster() {
		markersByClientId.values().removeAll(clusterMarkers);
		clusterMarkers.clear();
		queueCommandIfRendered(() -> new UiMap2.SetMapMarkerClusterCommand(getId(), new UiMapMarkerCluster(Collections.emptyList())));
	}

	public void setHeatMap(List<Location> locations) {
		this.setHeatMap(locations, 10, 30, 20);
	}

	public void setHeatMap(List<Location> locations, int maxCount) {
		this.setHeatMap(locations, maxCount, 30, 20);
	}

	public void setHeatMap(List<Location> locations, int maxCount, int radius, int blurZoomLevel) {
		List<UiHeatMapDataElement> heatMapElements = locations.stream().map(loc -> new UiHeatMapDataElement((float) loc.getLatitude(), (float) loc.getLongitude(), 1)).collect(Collectors.toList());
		this.setHeatMap(new UiHeatMapData(heatMapElements).setMaxCount(maxCount).setRadius(radius).setBlur(blurZoomLevel));
	}

	public void setHeatMap(UiHeatMapData heatMap) {
		heatMapData = heatMap;
		queueCommandIfRendered(() -> new UiMap.SetHeatMapCommand(getId(), heatMapData));
	}

	public void clearHeatMap() {
		queueCommandIfRendered(() -> new UiMap2.SetHeatMapCommand(getId(), new UiHeatMapData(Collections.emptyList())));
	}

	private Template getTemplateForRecord(Marker<RECORD> record, TemplateDecider<Marker<RECORD>> templateDecider) {
		Template template = templateDecider.getTemplate(record);
		if (template != null && !templateIdsByTemplate.containsKey(template)) {
			String uuid = "" + templateIdCounter++;
			this.templateIdsByTemplate.put(template, uuid);
			queueCommandIfRendered(() -> new UiMap2.RegisterTemplateCommand(getId(), uuid, template.createUiTemplate()));
		}
		return template;
	}

	public String getStyleUrl() {
		return styleUrl;
	}

	public void setStyleUrl(String styleUrl) {
		this.styleUrl = styleUrl;
		queueCommandIfRendered(() -> new UiMap2.SetStyleUrlCommand(getId(), styleUrl));
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		queueCommandIfRendered(() -> new UiMap2.SetZoomLevelCommand(getId(), zoomLevel));
	}

	public void setLocation(Location location) {
		setLocation(location, 2000, (int) getZoomLevel());
	}

	public void setLocation(double latitude, double longitude) {
		setLocation(new Location(latitude, longitude));
	}

	public void setLocation(Location location, long animationDurationMillis, int targetZoomLevel) {
		this.location = location;
		this.zoomLevel = targetZoomLevel;
		queueCommandIfRendered(() -> new UiMap2.SetLocationCommand(getId(), location.createUiLocation(), animationDurationMillis, targetZoomLevel));
	}

	public void setLatitude(double latitude) {
		setLocation(new Location(latitude, location.getLongitude()));
	}

	public void setLongitude(double longitude) {
		setLocation(new Location(location.getLatitude(), longitude));
	}

	public float getZoomLevel() {
		return zoomLevel;
	}

	public Location getLocation() {
		return location;
	}

	public void addMarker(Marker<RECORD> marker) {
		int clientId = clientIdCounter++;
		this.markersByClientId.put(clientId, marker);
		queueCommandIfRendered(() -> new UiMap2.AddMarkerCommand(getId(), createUiMarkerRecord(marker, clientId)));
	}

	public void removeMarker(Marker<RECORD> marker) {
		Integer clientId = markersByClientId.removeValue(marker);
		if (clientId != null) {
			queueCommandIfRendered(() -> new UiMap2.RemoveMarkerCommand(getId(), clientId));
		}
	}

	public void clearMarkers() {
		markersByClientId.clear();
		queueCommandIfRendered(() -> new UiMap2.ClearMarkersCommand(getId()));
	}

	public void fitBounds(Location southWest, Location northEast) {
		this.location = new Location((southWest.getLatitude() + northEast.getLatitude()) / 2, (southWest.getLongitude() + northEast.getLongitude()) / 2);
		queueCommandIfRendered(() -> new UiMap2.FitBoundsCommand(this.getId(), southWest.createUiLocation(), northEast.createUiLocation()));
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

	public boolean isDisplayAttributionControl() {
		return displayAttributionControl;
	}

	public void setDisplayAttributionControl(boolean displayAttributionControl) {
		this.displayAttributionControl = displayAttributionControl;
	}

	public boolean isDisplayNavigationControl() {
		return displayNavigationControl;
	}

	public void setDisplayNavigationControl(boolean displayNavigationControl) {
		this.displayNavigationControl = displayNavigationControl;
	}

	//  TODO
//	public void startDrawingShape(MapShapeType shapeType, ShapeProperties shapeProperties) {
//		queueCommandIfRendered(() -> new UiMap2.StartDrawingShapeCommand(getId(), shapeType.toUiMapShapeType(), shapeProperties.createUiShapeProperties()));
//	}
//
//	public void stopDrawingShape() {
//		queueCommandIfRendered(() -> new UiMap2.StopDrawingShapeCommand(getId()));
//	}

}
