/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

public class Area {

	protected final float minLatitude;
	protected final float maxLatitude;
	protected final float minLongitude;
	protected final float maxLongitude;

	public Area(float minLatitude, float maxLatitude, float minLongitude, float maxLongitude) {
		this.minLatitude = minLatitude;
		this.maxLatitude = maxLatitude;
		this.minLongitude = minLongitude;
		this.maxLongitude = maxLongitude;
	}

	public float getMinLatitude() {
		return minLatitude;
	}

	public float getMaxLatitude() {
		return maxLatitude;
	}

	public float getMinLongitude() {
		return minLongitude;
	}

	public float getMaxLongitude() {
		return maxLongitude;
	}

	@Override
	public String toString() {
		return "[" + minLatitude + "x" + minLongitude + "] - [" + maxLatitude + "x" + maxLongitude + "]";
	}
}

