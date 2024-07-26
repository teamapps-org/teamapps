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

public class Marker<RECORD> {

	private final Location location;
	private final RECORD data;
	private MarkerAnchorType markerAnchor = MarkerAnchorType.BOTTOM_LEFT;
	private int offsetPixelsX = 0;
	private int offsetPixelsY = 0;

	public Marker(Location location, RECORD data) {
		this(location, data, 0, 0);
	}

	public Marker(Location location, RECORD data, int offsetPixelsX, int offsetPixelsY) {
		this.location = location;
		this.data = data;
		this.offsetPixelsX = offsetPixelsX;
		this.offsetPixelsY = offsetPixelsY;
	}

	public Location getLocation() {
		return location;
	}

	public RECORD getData() {
		return data;
	}

	public MarkerAnchorType getMarkerAnchor() {
		return markerAnchor;
	}

	public void setMarkerAnchor(MarkerAnchorType markerAnchor) {
		this.markerAnchor = markerAnchor;
	}

	public int getOffsetPixelsX() {
		return offsetPixelsX;
	}

	public void setOffsetPixelsX(int offsetPixelsX) {
		this.offsetPixelsX = offsetPixelsX;
	}

	public int getOffsetPixelsY() {
		return offsetPixelsY;
	}

	public void setOffsetPixelsY(int offsetPixelsY) {
		this.offsetPixelsY = offsetPixelsY;
	}
}
