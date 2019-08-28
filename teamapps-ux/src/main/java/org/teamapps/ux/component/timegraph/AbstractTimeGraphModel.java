/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.timegraph;

import org.teamapps.event.Event;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractTimeGraphModel implements TimeGraphModel {

	public final Event<Void> onDataChanged = new Event<>();

	@Override
	public Event<Void> onDataChanged() {
		return onDataChanged;
	}

	@Override
	public Map<String, List<LineChartDataPoint>> getDataPoints(Collection<String> lineIds, TimeGraphZoomLevel zoomLevel, Interval neededIntervalX) {
		return lineIds.stream()
				.collect(Collectors.toMap(lineId -> lineId, lineId -> getDataPoints(lineId, zoomLevel, neededIntervalX)));
	}

	protected abstract List<LineChartDataPoint> getDataPoints(String lineId, TimeGraphZoomLevel zoomLevel, Interval neededIntervalX);

}
