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
package org.teamapps.ux.component.timegraph.model;

import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.timegraph.Interval;
import org.teamapps.ux.component.timegraph.TimePartitioning;
import org.teamapps.ux.component.timegraph.datapoints.GraphData;

import java.time.ZoneId;

public interface GraphModel<D extends GraphData> {

	ProjectorEvent<Void> onDataChanged();

	Interval getDomainX();

	/**
	 * Retrieves graph data ({@link GraphData}) for the specified {@code zoomLevel} and {cod@ neededInterval}.
	 * <p>
	 * The model MAY choose return more data than needed, covering a larger interval than requested.<br>
	 * However, the result MUST at least cover {@code neededInterval}!<br>
	 * If the model delegates to multiple sub-models for different graphs, its aggregated {@link GraphData} object should use the
	 * intersection of the sub-model's {@link GraphData#getInterval()} as its own {@link GraphData#getInterval()}.
	 *
	 * @param zoomLevel the requested zoom level
	 * @param zoneId the zoneId to apply when partitioning over time
	 * @param neededInterval the time interval requested by the client
	 * @param displayedInterval the time interval displayed by the client
	 * @return graph data
	 */
	D getData(TimePartitioning zoomLevel, ZoneId zoneId, Interval neededInterval, Interval displayedInterval);

}
