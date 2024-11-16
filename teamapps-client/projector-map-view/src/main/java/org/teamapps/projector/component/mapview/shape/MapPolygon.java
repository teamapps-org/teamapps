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
package org.teamapps.projector.component.mapview.shape;

import org.teamapps.projector.component.mapview.DtoMapPolygon;
import org.teamapps.projector.component.mapview.Location;

import java.util.List;

public class MapPolygon extends AbstractMapShape {

	private List<Location> locations;

	public MapPolygon(List<Location> locations, ShapeProperties properties) {
		super(properties);
		this.locations = locations;
	}

	public DtoMapPolygon createDtoMapShape() {
		DtoMapPolygon uiPolygon = new DtoMapPolygon();
		mapAbstractUiShapeProperties(uiPolygon);
		uiPolygon.setPath(List.copyOf(locations));
		return uiPolygon;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
		listener.handleShapeChanged(this);
	}
}
