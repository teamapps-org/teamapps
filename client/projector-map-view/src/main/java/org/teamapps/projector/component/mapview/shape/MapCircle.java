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

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.mapview.DtoMapCircle;
import org.teamapps.projector.component.mapview.Location;

public class MapCircle extends AbstractMapShape {

	private Location center;
	private int radiusMeters;

	public MapCircle(Location center, int radiusMeters) {
		this.center = center;
		this.radiusMeters = radiusMeters;
	}

	public MapCircle(Location center, int radiusMeters, ShapeProperties properties) {
		super(properties);
		this.center = center;
		this.radiusMeters = radiusMeters;
	}

	public MapCircle(Location center, int radiusMeters, Color fillColor) {
		this(center, radiusMeters, new ShapeProperties(Color.TRANSPARENT, 0, fillColor));
		this.center = center;
		this.radiusMeters = radiusMeters;
	}

	public MapCircle(Location center, int radiusMeters, Color fillColor, Color strokeColor) {
		this(center, radiusMeters, new ShapeProperties(strokeColor, 1, fillColor));
	}

	public MapCircle(Location center, int radiusMeters, Color fillColor, Color strokeColor, float strokeWidth) {
		this(center, radiusMeters, new ShapeProperties(strokeColor, strokeWidth, fillColor));
	}

	public DtoMapCircle createDtoMapShape() {
		DtoMapCircle uiCircle = new DtoMapCircle();
		mapAbstractUiShapeProperties(uiCircle);
		uiCircle.setCenter(center);
		uiCircle.setRadius(radiusMeters);
		return uiCircle;
	}

	public Location getCenter() {
		return center;
	}

	public void setCenter(Location center) {
		this.center = center;
		listener.handleShapeChanged(this);
	}

	public int getRadiusMeters() {
		return radiusMeters;
	}

	public void setRadiusMeters(int radiusMeters) {
		this.radiusMeters = radiusMeters;
		listener.handleShapeChanged(this);
	}
}
