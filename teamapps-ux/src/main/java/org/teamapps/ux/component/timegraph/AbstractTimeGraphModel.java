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
package org.teamapps.ux.component.timegraph;

import org.teamapps.event.Event;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractTimeGraphModel implements TimeGraphModel {

	public final Event<Void> onDataChanged = new Event<>();

	@Override
	public Event<Void> onDataChanged() {
		return onDataChanged;
	}

	@Override
	public Map<String, LineChartDataPoints> getDataPoints(Collection<String> dataSeriesIds, TimePartitioning partitioning, ZoneId zoneId, Interval neededInterval, Interval displayedInterval) {
		return dataSeriesIds.stream()
				.collect(Collectors.toMap(dataSeriesId -> dataSeriesId, dataSeriesId -> getDataPoints(dataSeriesId, partitioning, zoneId, neededInterval, displayedInterval)));
	}

	protected abstract LineChartDataPoints getDataPoints(String dataSeriesId, TimePartitioning zoomLevel, ZoneId zoneId, Interval neededIntervalX, Interval displayedInterval);

}
