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
package org.teamapps.ux.component.timegraph.partitioning;

import org.teamapps.event.Event;
import org.teamapps.ux.component.timegraph.Interval;
import org.teamapps.ux.component.timegraph.LineChartDataPoint;
import org.teamapps.ux.component.timegraph.LineChartDataPoints;
import org.teamapps.ux.component.timegraph.ListLineChartDataPoints;
import org.teamapps.ux.component.timegraph.TimeGraphModel;
import org.teamapps.ux.component.timegraph.TimeGraphZoomLevel;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartitioningTimeGraphModel implements TimeGraphModel {

	public final Event<Void> onDataChanged = new Event<>();

	private final ZoneId timeZone;
	private List<TimePartitionUnit> zoomLevelPartitionUnits = Arrays.asList(
			TimePartitionUnit.YEAR,
			TimePartitionUnit.QUARTER,
			TimePartitionUnit.MONTH,
			TimePartitionUnit.WEEK_MONDAY,
			TimePartitionUnit.DAY,
			TimePartitionUnit.HOURS_6,
			TimePartitionUnit.HOUR,
			TimePartitionUnit.MINUTES_30,
			TimePartitionUnit.MINUTES_15,
			TimePartitionUnit.MINUTES_5,
			TimePartitionUnit.MINUTES_2,
			TimePartitionUnit.MINUTE,
			TimePartitionUnit.SECONDS_30,
			TimePartitionUnit.SECONDS_15,
			TimePartitionUnit.SECONDS_5,
			TimePartitionUnit.SECONDS_2,
			TimePartitionUnit.SECOND,
			TimePartitionUnit.MILLISECOND_500,
			TimePartitionUnit.MILLISECOND_200,
			TimePartitionUnit.MILLISECOND_100,
			TimePartitionUnit.MILLISECOND_50,
			TimePartitionUnit.MILLISECOND_20,
			TimePartitionUnit.MILLISECOND_10,
			TimePartitionUnit.MILLISECOND_5,
			TimePartitionUnit.MILLISECOND_2,
			TimePartitionUnit.MILLISECOND
	);
	private final RawTimedDataModel delegateModel;

	public PartitioningTimeGraphModel(ZoneId timeZone, RawTimedDataModel delegateModel) {
		this.timeZone = timeZone;
		this.delegateModel = delegateModel;
		this.delegateModel.onDataChanged().addListener((Runnable) onDataChanged::fire);
	}

	public void setZoomLevelPartitionUnits(List<TimePartitionUnit> zoomLevelPartitionUnits) {
		this.zoomLevelPartitionUnits = zoomLevelPartitionUnits;
		onDataChanged.fire(null);
	}

	@Override
	public Event<Void> onDataChanged() {
		return onDataChanged;
	}

	@Override
	public List<? extends TimeGraphZoomLevel> getZoomLevels() {
		return zoomLevelPartitionUnits;
	}

	@Override
	public Map<String, LineChartDataPoints> getDataPoints(Collection<String> dataSeriesIds, TimeGraphZoomLevel zoomLevel, Interval neededIntervalX) {
		TimePartitionUnit partitionUnit = zoomLevelPartitionUnits.stream()
				.filter(partitioningUnit -> partitioningUnit.getAverageMilliseconds() == zoomLevel.getApproximateMillisecondsPerDataPoint())
				.findFirst().orElse(null);
		Map<String, LineChartDataPoints> dataPointsByDataSeriesId = new HashMap<>();
		delegateModel.getRawEventTimes(dataSeriesIds, neededIntervalX).forEach((dataSeriesId, eventTimestamps) -> {
			if (dataSeriesIds.contains(dataSeriesId)) {
				ListLineChartDataPoints dataPoints = new ListLineChartDataPoints(TimedDataPartitioner.partition(neededIntervalX.getMin(), neededIntervalX.getMax(), eventTimestamps, timeZone, partitionUnit, true)
						.stream()
						.map(p -> new LineChartDataPoint(p.getTimestamp(), p.getCount()))
						.collect(Collectors.toList()));
				dataPointsByDataSeriesId.put(dataSeriesId, dataPoints);
			}
		});
		return dataPointsByDataSeriesId;
	}

	@Override
	public Interval getDomainX(Collection<String> dataSeriesId) {
		return delegateModel.getDomainX(dataSeriesId);
	}
}
