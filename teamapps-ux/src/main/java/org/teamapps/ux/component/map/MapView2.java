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
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.*;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.field.combobox.TemplateDecider;
import org.teamapps.ux.component.map.shape.*;
import org.teamapps.ux.component.template.Template;

import java.util.*;
import java.util.stream.Collectors;

public class MapView2<RECORD> extends AbstractComponent {

	public final ProjectorEvent<LocationChangedEventData> onLocationChanged = createProjectorEventBoundToUiEvent(UiMap2.LocationChangedEvent.NAME);
	public final ProjectorEvent<Double> onZoomLevelChanged = createProjectorEventBoundToUiEvent(UiMap2.ZoomLevelChangedEvent.NAME);
	public final ProjectorEvent<Location> onMapClicked = createProjectorEventBoundToUiEvent(UiMap2.MapClickedEvent.NAME);
	public final ProjectorEvent<Marker<RECORD>> onMarkerClicked = createProjectorEventBoundToUiEvent(UiMap2.MarkerClickedEvent.NAME);
	public final ProjectorEvent<AbstractMapShape> onShapeDrawn = createProjectorEventBoundToUiEvent(UiMap2.ShapeDrawnEvent.NAME);


	private final String baseApiUrl;
	private final String accessToken;
	private String styleUrl;
	private boolean displayAttributionControl = true;
	private double zoomLevel = 10f;
	private Location location = new Location(0, 0);
	private final Map<String, AbstractMapShape> shapesByClientId = new HashMap<>();
	private List<Marker<RECORD>> clusterMarkers = new ArrayList<>();

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
			sendCommandIfRendered(() -> new UiMap2.UpdateShapeCommand(shape.getClientIdInternal(), shape.createUiMapShape()));
		}

		@Override
		public void handleShapeChanged(AbstractMapShape shape, AbstractUiMapShapeChange change) {
			sendCommandIfRendered(() -> new UiMap2.ChangeShapeCommand(shape.getClientIdInternal(), change));
		}

		@Override
		public void handleShapeRemoved(AbstractMapShape shape) {
			sendCommandIfRendered(() -> new UiMap2.RemoveShapeCommand(shape.getClientIdInternal()));
		}
	};

	public MapView2(String baseApiUrl, String accessToken, String styleUrl) {
		this.baseApiUrl = baseApiUrl;
		this.accessToken = accessToken;
		this.styleUrl = styleUrl;
	}

	@Override
	public UiComponent createUiClientObject() {
		UiMap2 uiMap = new UiMap2(templateIdsByTemplate.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> entry.getKey().createUiTemplate())));
		mapAbstractUiComponentProperties(uiMap);

		uiMap.setBaseApiUrl(baseApiUrl);
		uiMap.setAccessToken(accessToken);
		uiMap.setStyleUrl(styleUrl);
		uiMap.setDisplayAttributionControl(displayAttributionControl);

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
		clientRecord.setAnchor(marker.getMarkerAnchor().toUiMapMarkerAnchor());
		return clientRecord;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiMap2.MapClickedEvent) {
			UiMap2.MapClickedEvent mapClickedEvent = (UiMap2.MapClickedEvent) event;
			this.onMapClicked.fire(new Location(mapClickedEvent.getLocation().getLatitude(), mapClickedEvent.getLocation().getLongitude()));
		} else if (event instanceof UiMap2.MarkerClickedEvent) {
			UiMap2.MarkerClickedEvent markerClickedEvent = (UiMap2.MarkerClickedEvent) event;
			Marker<RECORD> marker = markersByClientId.get(markerClickedEvent.getMarkerId());
			this.onMarkerClicked.fire(marker);
		}       else if (event instanceof UiMap2.ZoomLevelChangedEvent) {
			UiMap2.ZoomLevelChangedEvent zoomEvent = (UiMap2.ZoomLevelChangedEvent) event;
			this.zoomLevel = zoomEvent.getZoomLevel();
			this.onZoomLevelChanged.fire(zoomLevel);
		} else if (event instanceof UiMap2.LocationChangedEvent) {
			UiMap2.LocationChangedEvent locationEvent = (UiMap2.LocationChangedEvent) event;
			this.location = Location.fromUiMapLocation(locationEvent.getCenter());
			UiMapArea displayedUiArea = locationEvent.getDisplayedArea();
			Area displayedArea = new Area(displayedUiArea.getMinLatitude(), displayedUiArea.getMaxLatitude(), displayedUiArea.getMinLongitude(), displayedUiArea.getMaxLongitude());
			this.onLocationChanged.fire(new LocationChangedEventData(this.location, displayedArea));
		} else if (event instanceof UiMap2.ShapeDrawnEvent) {
			UiMap2.ShapeDrawnEvent drawnEvent = (UiMap2.ShapeDrawnEvent) event;
			AbstractMapShape shape;
			AbstractUiMapShape uiShape = drawnEvent.getShape();
			if (uiShape instanceof UiMapCircle) {
				UiMapCircle uiCircle = (UiMapCircle) uiShape;
				shape = new MapCircle(Location.fromUiMapLocation(uiCircle.getCenter()), uiCircle.getRadius());
			} else if (uiShape instanceof UiMapPolygon) {
				UiMapPolygon uiPolygon = (UiMapPolygon) uiShape;
				shape = new MapPolygon(uiPolygon.getPath().stream().map(Location::fromUiMapLocation).collect(Collectors.toList()), null);
			} else if (uiShape instanceof UiMapPolyline) {
				UiMapPolyline uiPolyLine = (UiMapPolyline) uiShape;
				shape = new MapPolyline(uiPolyLine.getPath().stream().map(Location::fromUiMapLocation).collect(Collectors.toList()), null);
			} else if (uiShape instanceof UiMapRectangle) {
				UiMapRectangle uiRect = (UiMapRectangle) uiShape;
				shape = new MapRectangle(Location.fromUiMapLocation(uiRect.getL1()), Location.fromUiMapLocation(uiRect.getL2()), null);
			} else {
				throw new IllegalArgumentException("Unknown shape type from UI: " + drawnEvent.getShape().getClass());
			}
			shape.setClientIdInternal(drawnEvent.getShapeId());
			shapesByClientId.put(drawnEvent.getShapeId(), shape);
			shape.setListenerInternal(shapeListener);
			this.onShapeDrawn.fire(shape);
		}
	}

	public void addPolyLine(MapPolyline polyline) {
		addShape(polyline);
	}

	public void addShape(AbstractMapShape shape) {
		shape.setListenerInternal(shapeListener);
		shapesByClientId.put(shape.getClientIdInternal(), shape);
		sendCommandIfRendered(() -> new UiMap2.AddShapeCommand(shape.getClientIdInternal(), shape.createUiMapShape()));
	}

	public void removeShape(AbstractMapShape shape) {
		sendCommandIfRendered(() -> new UiMap2.RemoveShapeCommand(shape.getClientIdInternal()));
	}

	public void clearShapes() {
		sendCommandIfRendered(() -> new UiMap2.ClearShapesCommand());
	}

	public void setMarkerCluster(List<Marker<RECORD>> markers) {
		clusterMarkers = markers;
		markers.forEach(m -> this.markersByClientId.put(clientIdCounter++, m));
		sendCommandIfRendered(() -> {
			UiMapMarkerCluster markerCluster = new UiMapMarkerCluster(clusterMarkers.stream()
					.map(marker -> createUiMarkerRecord(marker, markersByClientId.getKey(marker)))
					.collect(Collectors.toList()));
			return new UiMap2.SetMapMarkerClusterCommand(markerCluster);
		});
	}

	public void clearMarkerCluster() {
		markersByClientId.values().removeAll(clusterMarkers);
		clusterMarkers.clear();
		sendCommandIfRendered(() -> new UiMap2.SetMapMarkerClusterCommand(new UiMapMarkerCluster(Collections.emptyList())));
	}

//	TODO
//	public void setHeatMap(List<Location> locations) {
//		List<UiHeatMapDataElement> heatMapElements = locations.stream().map(loc -> new UiHeatMapDataElement((float) loc.getLatitude(), (float) loc.getLongitude(), 1)).collect(Collectors.toList());
//		UiHeatMapData heatMap = new UiHeatMapData(heatMapElements);
//		queueCommandIfRendered(() -> new UiMap2.SetHeatMapCommand(heatMap));
//	}

	private Template getTemplateForRecord(Marker<RECORD> record, TemplateDecider<Marker<RECORD>> templateDecider) {
		Template template = templateDecider.getTemplate(record);
		if (template != null && !templateIdsByTemplate.containsKey(template)) {
			String uuid = "" + templateIdCounter++;
			this.templateIdsByTemplate.put(template, uuid);
			sendCommandIfRendered(() -> new UiMap2.RegisterTemplateCommand(uuid, template.createUiTemplate()));
		}
		return template;
	}

	public String getBaseApiUrl() {
		return baseApiUrl;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getStyleUrl() {
		return styleUrl;
	}

	public void setStyleUrl(String styleUrl) {
		this.styleUrl = styleUrl;
		sendCommandIfRendered(() -> new UiMap2.SetStyleUrlCommand(styleUrl));
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		sendCommandIfRendered(() -> new UiMap2.SetZoomLevelCommand(zoomLevel));
	}

	public void setLocation(Location location) {
		setLocation(location, 2000, 3);
	}

	public void setLocation(double latitude, double longitude) {
		setLocation(new Location(latitude, longitude));
	}

	public void setLocation(Location location, long animationDurationMillis, int targetZoomLevel) {
		this.location = location;
		this.zoomLevel = targetZoomLevel;
		sendCommandIfRendered(() -> new UiMap2.SetLocationCommand(location.createUiLocation(), animationDurationMillis, targetZoomLevel));
	}

	public void setLatitude(double latitude) {
		setLocation(new Location(latitude, location.getLongitude()));
	}

	public void setLongitude(double longitude) {
		setLocation(new Location(location.getLatitude(), longitude));
	}

	public double getZoomLevel() {
		return zoomLevel;
	}

	public Location getLocation() {
		return location;
	}

	public void addMarker(Marker<RECORD> marker) {
		int clientId = clientIdCounter++;
		this.markersByClientId.put(clientId, marker);
		sendCommandIfRendered(() -> new UiMap2.AddMarkerCommand(createUiMarkerRecord(marker, clientId)));
	}

	public void removeMarker(Marker<RECORD> marker) {
		Integer clientId = markersByClientId.removeValue(marker);
		if (clientId != null) {
			sendCommandIfRendered(() -> new UiMap2.RemoveMarkerCommand(clientId));
		}
	}

	public void clearMarkers() {
		markersByClientId.clear();
		sendCommandIfRendered(() -> new UiMap2.ClearMarkersCommand());
	}

	public void fitBounds(Location southWest, Location northEast) {
		this.location = new Location((southWest.getLatitude() + northEast.getLatitude()) / 2, (southWest.getLongitude() + northEast.getLongitude()) / 2);
		sendCommandIfRendered(() -> new UiMap2.FitBoundsCommand(southWest.createUiLocation(), northEast.createUiLocation()));
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

	//  TODO
//	public void startDrawingShape(MapShapeType shapeType, ShapeProperties shapeProperties) {
//		queueCommandIfRendered(() -> new UiMap2.StartDrawingShapeCommand(shapeType.toUiMapShapeType(), shapeProperties.createUiShapeProperties()));
//	}
//
//	public void stopDrawingShape() {
//		queueCommandIfRendered(() -> new UiMap2.StopDrawingShapeCommand());
//	}

}
