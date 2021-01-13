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
package org.teamapps.ux.component.map.shape;

import org.teamapps.dto.UiMapPolyline;
import org.teamapps.ux.component.map.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapPolyline extends AbstractMapShape {

	private List<Location> locations;

	public MapPolyline(List<Location> locations, ShapeProperties properties) {
		super(properties);
		this.locations = new ArrayList<>(locations);
	}

	public UiMapPolyline createUiMapShape() {
		UiMapPolyline uiPolyline = new UiMapPolyline();
		mapAbstractUiShapeProperties(uiPolyline);
		uiPolyline.setPath(locations.stream()
				.map(Location::createUiLocation)
				.collect(Collectors.toList()));
		return uiPolyline;
	}

	public MapPolyline addPoint(Location location) {
		this.locations.add(location);
		this.listener.handleChanged(this);
		return this;
	}

	public MapPolyline addPoints(List<Location> locations) {
		this.locations.addAll(locations);
		this.listener.handleChanged(this);
		return this;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
		listener.handleChanged(this);
	}
}
