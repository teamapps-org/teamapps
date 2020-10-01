/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.map.shape;

import org.teamapps.dto.UiMapCircle;
import org.teamapps.ux.component.map.Location;

public class MapCircle extends AbstractMapShape {

	private Location center;
	private int radiusMeters;

	public MapCircle(Location center, int radiusMeters) {
		this.center = center;
		this.radiusMeters = radiusMeters;
	}

	public MapCircle(ShapeProperties properties, Location center, int radiusMeters) {
		super(properties);
		this.center = center;
		this.radiusMeters = radiusMeters;
	}

	public UiMapCircle createUiMapShape() {
		UiMapCircle uiCircle = new UiMapCircle();
		mapAbstractUiShapeProperties(uiCircle);
		uiCircle.setCenter(center.createUiLocation());
		uiCircle.setRadius(radiusMeters);
		return uiCircle;
	}

	public Location getCenter() {
		return center;
	}

	public void setCenter(Location center) {
		this.center = center;
		listener.handleChanged(this);
	}

	public int getRadiusMeters() {
		return radiusMeters;
	}

	public void setRadiusMeters(int radiusMeters) {
		this.radiusMeters = radiusMeters;
		listener.handleChanged(this);
	}
}
