/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiMap;
import org.teamapps.dto.UiMapArea;
import org.teamapps.dto.UiMapMarkerClientRecord;
import org.teamapps.dto.UiMapPolyline;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.CacheManipulationHandle;
import org.teamapps.ux.cache.ClientRecordCache;
import org.teamapps.ux.cache.ClientTemplateCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.field.combobox.TemplateDecider;
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

	private MapType mapType = MapType.MAP_BOX_STREETS_SATELLITE;
	private int zoomLevel = 5;
	private Location location = new Location(0, 0);
	private Map<String, Polyline> polylinesByClientId = new HashMap<>();
	private List<Marker<RECORD>> markers = new ArrayList<>();

	private final ClientRecordCache<Marker<RECORD>, UiMapMarkerClientRecord> recordCache = new ClientRecordCache<>(this::createUiRecord);
	private ClientTemplateCache<Marker<RECORD>> templateCache = new ClientTemplateCache<>(this::registerTemplate);
	private PropertyExtractor<RECORD> markerPropertyExtractor = new BeanPropertyExtractor<>();

	@Override
	public UiComponent createUiComponent() {
		UiMap uiMap = new UiMap(new HashMap<>());
		mapAbstractUiComponentProperties(uiMap);
		uiMap.setMapType(mapType.toUiMapType());
		uiMap.setZoomLevel(zoomLevel);
		Map<String, UiMapPolyline> uiPolylines = new HashMap<>();
		polylinesByClientId.forEach((id, polyline) -> uiPolylines.put(id, polyline.createUiMapPolyline()));
		uiMap.setPolylines(uiPolylines);
		if (location != null) {
			uiMap.setMapPosition(location.createUiLocation());
		}
		CacheManipulationHandle<List<UiMapMarkerClientRecord>> cacheResponse = recordCache.replaceRecords(markers);
		uiMap.setMarkers(cacheResponse.getResult());
		cacheResponse.commit();
		return uiMap;
	}

	private UiMapMarkerClientRecord createUiRecord(Marker<RECORD> marker) {
		UiMapMarkerClientRecord clientRecord = new UiMapMarkerClientRecord();
		clientRecord.setLocation(location.createUiLocation());
		ClientTemplateCache.TemplateWithClientId templateWithClientId = templateCache.getTemplateIdForRecord(marker);
		if (templateWithClientId.getTemplate() != null) {
			clientRecord.setTemplateId("" + templateWithClientId.getClientId());
			clientRecord.setValues(markerPropertyExtractor.getValues(marker.getData(), templateWithClientId.getTemplate().getDataKeys()));
		} else {
			clientRecord.setAsString("" + marker.getData());
		}
		return clientRecord;
	}

	private void registerTemplate(int id, Template template) {
		queueCommandIfRendered(() -> new UiMap.RegisterTemplateCommand(getId(), "" + id, template.createUiTemplate()));
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
				this.location = new Location(locationEvent.getCenter().getLatitude(), locationEvent.getCenter().getLongitude());
				UiMapArea displayedUiArea = locationEvent.getDisplayedArea();
				Area displayedArea = new Area(displayedUiArea.getMinLatitude(), displayedUiArea.getMaxLatitude(), displayedUiArea.getMinLongitude(), displayedUiArea.getMaxLongitude());
				this.onLocationChanged.fire(new LocationChangedEventData(this.location, displayedArea));
				break;
			}
		}
	}

	public Polyline addPolyLine(Polyline polyline) {
		polyline.setListener((polyline1, newPoints) -> {
			queueCommandIfRendered(() -> new UiMap.AddPolylinePointsCommand(getId(), polyline.getClientId(), newPoints.stream().map(Location::createUiLocation).collect(Collectors.toList())));
		});
		polylinesByClientId.put(polyline.getClientId(), polyline);
		queueCommandIfRendered(() -> new UiMap.AddPolylineCommand(getId(), polyline.getClientId(), polyline.createUiMapPolyline()));
		return polyline;
	}

	public void removePolyline(Polyline polyline) {
		queueCommandIfRendered(() -> new UiMap.RemovePolylineCommand(getId(), polyline.getClientId()));
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
		}   else {
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

	public void setDefaultMarkerTemplate(Template defaultTemplate) {
		templateCache.setDefaultTemplate(defaultTemplate);
	}

	public void setMarkerTemplateDecider(TemplateDecider<Marker<RECORD>> templateDecider) {
		templateCache.setTemplateDecider(templateDecider);
	}

	public PropertyExtractor<RECORD> getMarkerPropertyExtractor() {
		return markerPropertyExtractor;
	}

	public void setMarkerPropertyExtractor(PropertyExtractor<RECORD> markerPropertyExtractor) {
		this.markerPropertyExtractor = markerPropertyExtractor;
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}
}
