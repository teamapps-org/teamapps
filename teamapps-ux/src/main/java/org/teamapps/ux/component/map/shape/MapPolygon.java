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
package org.teamapps.ux.component.map.shape;

import org.teamapps.dto.UiMapPolygon;
import org.teamapps.ux.component.map.Location;

import java.util.List;
import java.util.stream.Collectors;

public class MapPolygon extends AbstractMapShape {

	private List<Location> locations;

	public MapPolygon(List<Location> locations, ShapeProperties properties) {
		super(properties);
		this.locations = locations;
	}

	public UiMapPolygon createUiMapShape() {
		UiMapPolygon uiPolygon = new UiMapPolygon();
		mapAbstractUiShapeProperties(uiPolygon);
		uiPolygon.setPath(locations.stream()
				.map(Location::createUiLocation)
				.collect(Collectors.toList()));
		return uiPolygon;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
		listener.handleChanged(this);
	}
}
