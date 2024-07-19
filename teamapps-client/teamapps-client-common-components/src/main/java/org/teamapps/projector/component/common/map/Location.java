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
package org.teamapps.projector.component.common.map;

import org.teamapps.projector.component.common.dto.DtoMapLocation;
import org.teamapps.projector.component.common.dto.DtoMapLocationWrapper;

public class Location {

	private double latitude;
	private double longitude;

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static Location fromUiMapLocation(DtoMapLocation uiMapLocation) {
		return new Location(uiMapLocation.getLatitude(), uiMapLocation.getLongitude());
	}

	public static Location fromUiMapLocationWrapper(DtoMapLocationWrapper uiMapLocation) {
		return new Location(uiMapLocation.getLatitude(), uiMapLocation.getLongitude());
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public DtoMapLocation createUiLocation() {
		return new DtoMapLocation((float) latitude, (float)longitude);
	}

	@Override
	public String toString() {
		return latitude + "x" + longitude;
	}
}
