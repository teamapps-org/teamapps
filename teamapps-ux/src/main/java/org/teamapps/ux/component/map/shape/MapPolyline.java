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

import org.teamapps.dto.DtoMapLocation;
import org.teamapps.dto.DtoMapPolyline;
import org.teamapps.dto.DtoPolylineAppend;
import org.teamapps.ux.component.map.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapPolyline extends AbstractMapShape {

	private List<Location> points;

	public MapPolyline(List<Location> points, ShapeProperties properties) {
		super(properties);
		this.points = new ArrayList<>(points);
	}

	public DtoMapPolyline createUiMapShape() {
		DtoMapPolyline uiPolyline = new DtoMapPolyline();
		mapAbstractUiShapeProperties(uiPolyline);
		uiPolyline.setPath(toUiMapLocations(points));
		return uiPolyline;
	}

	private List<DtoMapLocation> toUiMapLocations(List<Location> locations) {
		return locations.stream()
				.map(Location::createUiLocation)
				.collect(Collectors.toList());
	}

	public MapPolyline addPoint(Location location) {
		return addPoints(List.of(location));
	}

	public MapPolyline addPoints(List<Location> points) {
		this.points.addAll(points);
		this.listener.handleShapeChanged(this, new DtoPolylineAppend(toUiMapLocations(points)));
		return this;
	}

	public List<Location> getPoints() {
		return points;
	}

	public void setPoints(List<Location> points) {
		this.points = points;
		listener.handleShapeChanged(this);
	}
}
