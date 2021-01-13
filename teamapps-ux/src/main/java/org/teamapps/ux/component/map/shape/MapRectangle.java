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

import org.teamapps.dto.UiMapRectangle;
import org.teamapps.ux.component.map.Location;

public class MapRectangle extends AbstractMapShape {

	private Location location1;
	private Location location2;

	public MapRectangle(Location location1, Location location2) {
		this.location1 = location1;
		this.location2 = location2;
	}

	public MapRectangle(Location location1, Location location2, ShapeProperties properties) {
		super(properties);
		this.location1 = location1;
		this.location2 = location2;
	}

	public UiMapRectangle createUiMapShape() {
		UiMapRectangle uiRect = new UiMapRectangle();
		mapAbstractUiShapeProperties(uiRect);
		uiRect.setL1(location1.createUiLocation());
		uiRect.setL2(location2.createUiLocation());
		return uiRect;
	}

	public Location getLocation1() {
		return location1;
	}

	public void setLocation1(Location location1) {
		this.location1 = location1;
		listener.handleChanged(this);
	}

	public Location getLocation2() {
		return location2;
	}

	public void setLocation2(Location location2) {
		this.location2 = location2;
		listener.handleChanged(this);
	}
}
