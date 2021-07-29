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
package org.teamapps.ux.component.timegraph.model.timestamps;

import org.teamapps.event.Event;
import org.teamapps.ux.component.timegraph.Interval;
import org.teamapps.ux.component.timegraph.TimePartitioning;
import org.teamapps.ux.component.timegraph.datapoints.LineGraphData;
import org.teamapps.ux.component.timegraph.datapoints.LineGraphDataPoint;
import org.teamapps.ux.component.timegraph.datapoints.ListLineGraphData;
import org.teamapps.ux.component.timegraph.model.AbstractLineGraphModel;

import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Collectors;

public class PartitioningTimestampsLineGraphModel extends AbstractLineGraphModel {

	private final TimestampsModel timestampsModel;

	public PartitioningTimestampsLineGraphModel(TimestampsModel timestampsModel) {
		this.timestampsModel = timestampsModel;
		this.timestampsModel.onDataChanged().addListener((Runnable) onDataChanged::fire);
	}

	@Override
	public Event<Void> onDataChanged() {
		return onDataChanged;
	}

	@Override
	public LineGraphData getData(TimePartitioning zoomLevel, ZoneId zoneId, Interval neededInterval, Interval displayedInterval) {
		final long queryStart = zoomLevel.getPartitionStart(Instant.ofEpochMilli(displayedInterval.getMin()).atZone(zoneId)).toInstant().toEpochMilli();
		final long queryEnd = zoomLevel.getPartitionEnd(Instant.ofEpochMilli(displayedInterval.getMax()).atZone(zoneId)).toInstant().toEpochMilli();
		final long[] eventTimestamps = timestampsModel.getTimestamps(displayedInterval);
		return new ListLineGraphData(TimestampsPartitioner.partition(queryStart, queryEnd, eventTimestamps, zoneId, zoomLevel, true)
				.stream()
				.map(p -> new LineGraphDataPoint(p.getTimestamp(), p.getCount()))
				.collect(Collectors.toList()),
				new Interval(queryStart, queryEnd));
	}

	@Override
	public Interval getDomainX() {
		return timestampsModel.getDomainX();
	}
}
