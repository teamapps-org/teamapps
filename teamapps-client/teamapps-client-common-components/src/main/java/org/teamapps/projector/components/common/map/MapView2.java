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
package org.teamapps.projector.components.common.map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.teamapps.dto.*;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.projector.components.common.map.shape.*;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.map.shape.*;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.component.template.TemplateDecider;
import org.teamapps.ux.data.extraction.BeanPropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyProvider;

import java.util.*;
import java.util.stream.Collectors;

public class MapView2<RECORD> extends AbstractComponent {

	public final ProjectorEvent<LocationChangedEventData> onLocationChanged = createProjectorEventBoundToUiEvent(DtoMap2.LocationChangedEvent.TYPE_ID);
	public final ProjectorEvent<Double> onZoomLevelChanged = createProjectorEventBoundToUiEvent(DtoMap2.ZoomLevelChangedEvent.TYPE_ID);
	public final ProjectorEvent<Location> onMapClicked = createProjectorEventBoundToUiEvent(DtoMap2.MapClickedEvent.TYPE_ID);
	public final ProjectorEvent<Marker<RECORD>> onMarkerClicked = createProjectorEventBoundToUiEvent(DtoMap2.MarkerClickedEvent.TYPE_ID);
	public final ProjectorEvent<AbstractMapShape> onShapeDrawn = createProjectorEventBoundToUiEvent(DtoMap2.ShapeDrawnEvent.TYPE_ID);


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

	private PropertyProvider<RECORD> markerPropertyProvider = new BeanPropertyExtractor<>();
	private final AbstractMapShape.MapShapeListener shapeListener = new AbstractMapShape.MapShapeListener() {
		@Override
		public void handleShapeChanged(AbstractMapShape shape) {
			sendCommandIfRendered(() -> new DtoMap2.UpdateShapeCommand(shape.getClientIdInternal(), shape.createUiMapShape()));
		}

		@Override
		public void handleShapeChanged(AbstractMapShape shape, DtoAbstractMapShapeChange change) {
			sendCommandIfRendered(() -> new DtoMap2.ChangeShapeCommand(shape.getClientIdInternal(), change));
		}

		@Override
		public void handleShapeRemoved(AbstractMapShape shape) {
			sendCommandIfRendered(() -> new DtoMap2.RemoveShapeCommand(shape.getClientIdInternal()));
		}
	};

	public MapView2(String baseApiUrl, String accessToken, String styleUrl) {
		this.baseApiUrl = baseApiUrl;
		this.accessToken = accessToken;
		this.styleUrl = styleUrl;
	}

	@Override
	public DtoComponent createDto() {
		DtoMap2 uiMap = new DtoMap2();
		mapAbstractUiComponentProperties(uiMap);

		uiMap.setBaseApiUrl(baseApiUrl);
		uiMap.setAccessToken(accessToken);
		uiMap.setStyleUrl(styleUrl);
		uiMap.setDisplayAttributionControl(displayAttributionControl);

		uiMap.setZoomLevel(zoomLevel);
		Map<String, DtoAbstractMapShape> uiShapes = new HashMap<>();
		shapesByClientId.forEach((id, shape) -> uiShapes.put(id, shape.createUiMapShape()));
		uiMap.setShapes(uiShapes);
		if (location != null) {
			uiMap.setMapPosition(location.createUiLocation());
		}
		if (clusterMarkers != null && !clusterMarkers.isEmpty()) {
			uiMap.setMarkerCluster(new DtoMapMarkerCluster(clusterMarkers.stream()
					.map(marker -> createUiMarkerRecord(marker, markersByClientId.getKey(marker)))
					.collect(Collectors.toList())));
		}
		uiMap.setMarkers(markersByClientId.entrySet().stream()
				.map(e -> createUiMarkerRecord(e.getValue(), e.getKey()))
				.collect(Collectors.toList()));
		return uiMap;
	}

	private DtoMapMarkerClientRecord createUiMarkerRecord(Marker<RECORD> marker, int clientId) {
		DtoMapMarkerClientRecord clientRecord = new DtoMapMarkerClientRecord();
		clientRecord.setId(clientId);
		clientRecord.setLocation(marker.getLocation().createUiLocation());
		Template template = getTemplateForRecord(marker, templateDecider);
		if (template != null) {
			clientRecord.setTemplate(template.createDtoReference());
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
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoMap2.MapClickedEvent.TYPE_ID -> {
				var mapClickedEvent = event.as(DtoMap2.MapClickedEventWrapper.class);
				this.onMapClicked.fire(new Location(mapClickedEvent.getLocation().getLatitude(), mapClickedEvent.getLocation().getLongitude()));
			}
			case DtoMap2.MarkerClickedEvent.TYPE_ID -> {
				var markerClickedEvent = event.as(DtoMap2.MarkerClickedEventWrapper.class);
				Marker<RECORD> marker = markersByClientId.get(markerClickedEvent.getMarkerId());
				this.onMarkerClicked.fire(marker);
			}
			case DtoMap2.ZoomLevelChangedEvent.TYPE_ID -> {
				var zoomEvent = event.as(DtoMap2.ZoomLevelChangedEventWrapper.class);
				this.zoomLevel = zoomEvent.getZoomLevel();
				this.onZoomLevelChanged.fire(zoomLevel);
			}
			case DtoMap2.LocationChangedEvent.TYPE_ID -> {
				var locationEvent = event.as(DtoMap2.LocationChangedEventWrapper.class);
				this.location = Location.fromUiMapLocationWrapper(locationEvent.getCenter());
				DtoMapAreaWrapper displayedUiArea = locationEvent.getDisplayedArea();
				Area displayedArea = new Area(displayedUiArea.getMinLatitude(), displayedUiArea.getMaxLatitude(), displayedUiArea.getMinLongitude(), displayedUiArea.getMaxLongitude());
				this.onLocationChanged.fire(new LocationChangedEventData(this.location, displayedArea));
			}
			case DtoMap2.ShapeDrawnEvent.TYPE_ID -> {
				var drawnEvent = event.as(DtoMap2.ShapeDrawnEventWrapper.class);
				DtoAbstractMapShapeWrapper uiShape = drawnEvent.getShape();
				AbstractMapShape shape = switch (uiShape.getTypeId()) {
					case DtoMapCircle.TYPE_ID -> {
						var uiCircle = uiShape.as(DtoMapCircleWrapper.class);
						yield new MapCircle(Location.fromUiMapLocationWrapper(uiCircle.getCenter()), uiCircle.getRadius());
					}
					case DtoMapPolygon.TYPE_ID -> {
						var uiPolygon = uiShape.as(DtoMapPolygonWrapper.class);
						yield new MapPolygon(uiPolygon.getPath().stream().map(Location::fromUiMapLocationWrapper).collect(Collectors.toList()), null);
					}
					case DtoMapPolyline.TYPE_ID -> {
						var uiPolyLine = uiShape.as(DtoMapPolylineWrapper.class);
						yield new MapPolyline(uiPolyLine.getPath().stream().map(Location::fromUiMapLocationWrapper).collect(Collectors.toList()), null);
					}
					case DtoMapRectangle.TYPE_ID -> {
						var uiRect = uiShape.as(DtoMapRectangleWrapper.class);
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

	public void addPolyLine(MapPolyline polyline) {
		addShape(polyline);
	}

	public void addShape(AbstractMapShape shape) {
		shape.setListenerInternal(shapeListener);
		shapesByClientId.put(shape.getClientIdInternal(), shape);
		sendCommandIfRendered(() -> new DtoMap2.AddShapeCommand(shape.getClientIdInternal(), shape.createUiMapShape()));
	}

	public void removeShape(AbstractMapShape shape) {
		sendCommandIfRendered(() -> new DtoMap2.RemoveShapeCommand(shape.getClientIdInternal()));
	}

	public void clearShapes() {
		sendCommandIfRendered(() -> new DtoMap2.ClearShapesCommand());
	}

	public void setMarkerCluster(List<Marker<RECORD>> markers) {
		clusterMarkers = markers;
		markers.forEach(m -> this.markersByClientId.put(clientIdCounter++, m));
		sendCommandIfRendered(() -> {
			DtoMapMarkerCluster markerCluster = new DtoMapMarkerCluster(clusterMarkers.stream()
					.map(marker -> createUiMarkerRecord(marker, markersByClientId.getKey(marker)))
					.collect(Collectors.toList()));
			return new DtoMap2.SetMapMarkerClusterCommand(markerCluster);
		});
	}

	public void clearMarkerCluster() {
		markersByClientId.values().removeAll(clusterMarkers);
		clusterMarkers.clear();
		sendCommandIfRendered(() -> new DtoMap2.SetMapMarkerClusterCommand(new DtoMapMarkerCluster(Collections.emptyList())));
	}

//	TODO
//	public void setHeatMap(List<Location> locations) {
//		List<DtoHeatMapDataElement> heatMapElements = locations.stream().map(loc -> new DtoHeatMapDataElement((float) loc.getLatitude(), (float) loc.getLongitude(), 1)).collect(Collectors.toList());
//		DtoHeatMapData heatMap = new DtoHeatMapData(heatMapElements);
//		queueCommandIfRendered(() -> new DtoMap2.SetHeatMapCommand(heatMap));
//	}

	private Template getTemplateForRecord(Marker<RECORD> record, TemplateDecider<Marker<RECORD>> templateDecider) {
		return templateDecider.getTemplate(record);
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
		sendCommandIfRendered(() -> new DtoMap2.SetStyleUrlCommand(styleUrl));
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		sendCommandIfRendered(() -> new DtoMap2.SetZoomLevelCommand(zoomLevel));
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
		sendCommandIfRendered(() -> new DtoMap2.SetLocationCommand(location.createUiLocation(), animationDurationMillis, targetZoomLevel));
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
		sendCommandIfRendered(() -> new DtoMap2.AddMarkerCommand(createUiMarkerRecord(marker, clientId)));
	}

	public void removeMarker(Marker<RECORD> marker) {
		Integer clientId = markersByClientId.removeValue(marker);
		if (clientId != null) {
			sendCommandIfRendered(() -> new DtoMap2.RemoveMarkerCommand(clientId));
		}
	}

	public void clearMarkers() {
		markersByClientId.clear();
		sendCommandIfRendered(() -> new DtoMap2.ClearMarkersCommand());
	}

	public void fitBounds(Location southWest, Location northEast) {
		this.location = new Location((southWest.getLatitude() + northEast.getLatitude()) / 2, (southWest.getLongitude() + northEast.getLongitude()) / 2);
		sendCommandIfRendered(() -> new DtoMap2.FitBoundsCommand(southWest.createUiLocation(), northEast.createUiLocation()));
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
//		queueCommandIfRendered(() -> new DtoMap2.StartDrawingShapeCommand(shapeType.toUiMapShapeType(), shapeProperties.createUiShapeProperties()));
//	}
//
//	public void stopDrawingShape() {
//		queueCommandIfRendered(() -> new DtoMap2.StopDrawingShapeCommand());
//	}

}
