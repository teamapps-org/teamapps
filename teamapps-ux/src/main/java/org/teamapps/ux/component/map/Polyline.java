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

import org.teamapps.dto.UiMapLocation;
import org.teamapps.dto.UiMapPolyline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Polyline {

	private final String clientId = UUID.randomUUID().toString();
	private final ShapeProperties properties;
	private final List<Location> locations;
	private PolylineListener listener;

	public Polyline(ShapeProperties properties, List<Location> locations) {
		this.properties = properties;
		this.locations = locations;
	}

	public UiMapPolyline createUiMapPolyline() {
		List<UiMapLocation> uiLocations = new ArrayList<>();
		for (Location loc : locations) {
			uiLocations.add(loc.createUiLocation());
		}
		UiMapPolyline uiPolyline = new UiMapPolyline(uiLocations);
		uiPolyline.setShapeProperties(properties.createUiShapeProperties());
		return uiPolyline;
	}

	public Polyline addPoint(Location location) {
		this.locations.add(location);
		this.listener.handlePolyLinePointsAdded(this, Collections.singletonList(location));
		return this;
	}

	public Polyline addPoints(List<Location> locations) {
		this.locations.addAll(locations);
		this.listener.handlePolyLinePointsAdded(this, locations);
		return this;
	}

	public ShapeProperties getProperties() {
		return properties;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public String getClientId() {
		return clientId;
	}

	/*package-private*/ void setListener(PolylineListener listener) {
		this.listener = listener;
	}

	public static interface PolylineListener {
		void handlePolyLinePointsAdded(Polyline polyline, List<Location> points);
	}
}
