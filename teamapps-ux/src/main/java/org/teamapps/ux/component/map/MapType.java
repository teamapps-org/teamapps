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
package org.teamapps.ux.component.map;

import org.teamapps.dto.DtoMapType;

public enum MapType {
	INTERNAL,
	INTERNAL_DARK,
	INTERNAL_DARK_HIGH_RES,
	MAP_BOX_STREETS,
	MAP_BOX_STREETS_BASIC,
	MAP_BOX_STREETS_SATELLITE,
	MAP_BOX_SATELLITE,
	MAP_BOX_RUN_BIKE_HIKE,
	MAP_BOX_DARK,
	MAP_BOX_EMERALD,
	MAP_BOX_OUTDOORS,
	MAP_QUEST_OSM,
	MAP_QUEST_SATELLITE,
	OSM_TOPO_MAP,
	NASA_EARTH_AT_NIGHT,
	INFO_WEATHER_TEMPERATURE,
	THUNDERFOREST_DARK,
	THUNDERFOREST_TRANSPORT,
	WIKIMEDIA;

	public DtoMapType toUiMapType() {
		return DtoMapType.valueOf(this.name());
	}
}
