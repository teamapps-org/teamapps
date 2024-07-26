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
package org.teamapps.projector.component.mapview;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.ComponentConfig;
import org.teamapps.projector.component.mapview.shape.*;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.TemplateDecider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapView<RECORD> extends AbstractComponent implements DtoMapViewEventHandler {

	private final DtoMapViewClientObjectChannel clientObjectChannel = new DtoMapViewClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<LocationChangedEventData> onLocationChanged = new ProjectorEvent<>(clientObjectChannel::toggleLocationChangedEvent);
	public final ProjectorEvent<Double> onZoomLevelChanged = new ProjectorEvent<>(clientObjectChannel::toggleZoomLevelChangedEvent);
	public final ProjectorEvent<Location> onMapClicked = new ProjectorEvent<>(clientObjectChannel::toggleMapClickedEvent);
	public final ProjectorEvent<Marker<RECORD>> onMarkerClicked = new ProjectorEvent<>(clientObjectChannel::toggleMarkerClickedEvent);
	public final ProjectorEvent<AbstractMapShape> onShapeDrawn = new ProjectorEvent<>(clientObjectChannel::toggleShapeDrawnEvent);


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
			clientObjectChannel.updateShape(shape.getClientIdInternal(), shape.createUiMapShape());
		}

		@Override
		public void handleShapeChanged(AbstractMapShape shape, DtoAbstractMapShapeChange change) {
			clientObjectChannel.changeShape(shape.getClientIdInternal(), change);
		}

		@Override
		public void handleShapeRemoved(AbstractMapShape shape) {
			clientObjectChannel.removeShape(shape.getClientIdInternal());
		}
	};

	public MapView(String baseApiUrl, String accessToken, String styleUrl) {
		this.baseApiUrl = baseApiUrl;
		this.accessToken = accessToken;
		this.styleUrl = styleUrl;
	}

	@Override
	public ComponentConfig createConfig() {
		DtoMapView uiMap = new DtoMapView();
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
			uiMap.setMapPosition(location);
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

	@Override
	public void handleZoomLevelChanged(double zoomLevel) {
		this.onZoomLevelChanged.fire(zoomLevel);
	}

	@Override
	public void handleLocationChanged(DtoMapView.LocationChangedEventWrapper event) {
		this.location = event.getCenter().unwrap();
		this.onLocationChanged.fire(new LocationChangedEventData(this.location, event.getDisplayedArea().unwrap()));
	}

	@Override
	public void handleMapClicked(LocationWrapper location) {
		this.onMapClicked.fire(new Location(location.getLatitude(), location.getLongitude()));
	}

	@Override
	public void handleMarkerClicked(int markerId) {
		Marker<RECORD> marker = markersByClientId.get(markerId);
		this.onMarkerClicked.fire(marker);
	}

	@Override
	public void handleShapeDrawn(DtoMapView.ShapeDrawnEventWrapper event) {
		DtoAbstractMapShapeWrapper uiShape = event.getShape();
		AbstractMapShape shape = switch (uiShape.getTypeId()) {
			case DtoMapCircle.TYPE_ID -> {
				var uiCircle = uiShape.as(DtoMapCircleWrapper::new);
				yield new MapCircle(uiCircle.getCenter().unwrap(), uiCircle.getRadius());
			}
			case DtoMapPolygon.TYPE_ID -> {
				var uiPolygon = uiShape.as(DtoMapPolygonWrapper::new);
				yield new MapPolygon(uiPolygon.getPath().stream()
						.map(LocationWrapper::unwrap)
						.collect(Collectors.toList()), null);
			}
			case DtoMapPolyline.TYPE_ID -> {
				var uiPolyLine = uiShape.as(DtoMapPolylineWrapper::new);
				yield new MapPolyline(uiPolyLine.getPath().stream()
						.map(LocationWrapper::unwrap)
						.collect(Collectors.toList()), null);
			}
			case DtoMapRectangle.TYPE_ID -> {
				var uiRect = uiShape.as(DtoMapRectangleWrapper::new);
				yield new MapRectangle(uiRect.getL1().unwrap(), uiRect.getL2().unwrap(), null);
			}
			default -> {
				throw new IllegalArgumentException("Unknown shape type from UI: " + event.getShape().getClass());
			}
		};
		shape.setClientIdInternal(event.getShapeId());
		shapesByClientId.put(event.getShapeId(), shape);
		shape.setListenerInternal(shapeListener);
		this.onShapeDrawn.fire(shape);
	}

	private DtoMapMarkerClientRecord createUiMarkerRecord(Marker<RECORD> marker, int clientId) {
		DtoMapMarkerClientRecord clientRecord = new DtoMapMarkerClientRecord();
		clientRecord.setId(clientId);
		clientRecord.setLocation(marker.getLocation());
		Template template = getTemplateForRecord(marker, templateDecider);
		if (template != null) {
			clientRecord.setTemplate(template);
			clientRecord.setValues(markerPropertyProvider.getValues(marker.getData(), template.getPropertyNames()));
		} else {
			clientRecord.setAsString("" + marker.getData());
		}
		clientRecord.setOffsetPixelsX(marker.getOffsetPixelsX());
		clientRecord.setOffsetPixelsY(marker.getOffsetPixelsY());
		clientRecord.setAnchor(marker.getMarkerAnchor());
		return clientRecord;
	}

	public void addPolyLine(MapPolyline polyline) {
		addShape(polyline);
	}

	public void addShape(AbstractMapShape shape) {
		shape.setListenerInternal(shapeListener);
		shapesByClientId.put(shape.getClientIdInternal(), shape);
		clientObjectChannel.addShape(shape.getClientIdInternal(), shape.createUiMapShape());
	}

	public void removeShape(AbstractMapShape shape) {
		clientObjectChannel.removeShape(shape.getClientIdInternal());
	}

	public void clearShapes() {
		clientObjectChannel.clearShapes();
	}

	public void setMarkerCluster(List<Marker<RECORD>> markers) {
		clusterMarkers = markers;
		markers.forEach(m -> this.markersByClientId.put(clientIdCounter++, m));
		if (clientObjectChannel.isRendered()) {
			clientObjectChannel.setMapMarkerCluster(new DtoMapMarkerCluster(clusterMarkers.stream()
					.map(marker -> createUiMarkerRecord(marker, markersByClientId.getKey(marker)))
					.collect(Collectors.toList())));
		}
	}

	public void clearMarkerCluster() {
		markersByClientId.values().removeAll(clusterMarkers);
		clusterMarkers.clear();
		clientObjectChannel.setMapMarkerCluster(new DtoMapMarkerCluster(List.of()));
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
		clientObjectChannel.setStyleUrl(styleUrl);
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		clientObjectChannel.setZoomLevel(zoomLevel);
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
		clientObjectChannel.setLocation(location, animationDurationMillis, targetZoomLevel);
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
		clientObjectChannel.addMarker(createUiMarkerRecord(marker, clientId));
	}

	public void removeMarker(Marker<RECORD> marker) {
		Integer clientId = markersByClientId.removeValue(marker);
		if (clientId != null) {
			clientObjectChannel.removeMarker(clientId);
		}
	}

	public void clearMarkers() {
		markersByClientId.clear();
		clientObjectChannel.clearMarkers();
	}

	public void fitBounds(Location southWest, Location northEast) {
		this.location = new Location((southWest.getLatitude() + northEast.getLatitude()) / 2, (southWest.getLongitude() + northEast.getLongitude()) / 2);
		clientObjectChannel.fitBounds(southWest, northEast);
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
//	public void startDrawingShape(ShapeType shapeType, ShapeProperties shapeProperties) {
//		queueCommandIfRendered(() -> new DtoMap2.StartDrawingShapeCommand(shapeType.toUiMapShapeType(), shapeProperties.createUiShapeProperties()));
//	}
//
//	public void stopDrawingShape() {
//		queueCommandIfRendered(() -> new DtoMap2.StopDrawingShapeCommand());
//	}

}
