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
package org.teamapps.projector.component.timegraph.model;

import org.teamapps.projector.component.timegraph.TimePartitioning;
import org.teamapps.projector.component.timegraph.Interval;
import org.teamapps.projector.component.timegraph.datapoints.LineGraphData;
import org.teamapps.projector.component.timegraph.datapoints.LineGraphDataPoint;
import org.teamapps.projector.component.timegraph.datapoints.ListLineGraphData;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class AggregatingLineGraphModel extends AbstractLineGraphModel {

	private LineGraphData graphData;
	private AggregationType aggregationType = AggregationType.FIRST_VALUE;

	private boolean addDataPointBeforeAndAfterQueryResult = true;

	public AggregatingLineGraphModel() {
	}

	public AggregatingLineGraphModel(LineGraphData graphData, AggregationType aggregationType) {
		this.graphData = graphData;
		this.aggregationType = aggregationType;
	}

	public void setGraphData(LineGraphData graphData) {
		this.graphData = graphData;
		onDataChanged.fire(null);
	}

	public void setAggregationPolicy(AggregationType aggregationType) {
		this.aggregationType = aggregationType;
		onDataChanged.fire(null);
	}

	@Override
	public LineGraphData getData(TimePartitioning zoomLevel, ZoneId zoneId, Interval neededIntervalX, Interval displayedInterval) {
		final long queryStart = getPartitionStartMilli(displayedInterval.getMin(), zoomLevel, zoneId);
		final long queryEnd = getPartitionEndMilli(displayedInterval.getMax(), zoomLevel, zoneId);
		return getAggregateDataPoints(graphData, zoomLevel, new Interval(queryStart, queryEnd), aggregationType, zoneId, addDataPointBeforeAndAfterQueryResult);
	}

	public static LineGraphData getAggregateDataPoints(
			LineGraphData dataPoints,
			TimePartitioning zoomLevel,
			Interval alignedInterval,
			AggregationType aggregationType,
			ZoneId timeZone,
			boolean addDataPointBeforeAndAfterQueryResult
	) {
		List<LineGraphDataPoint> result = new ArrayList<>();
		long startPartitionStartMilli = alignedInterval.getMin();
		if (addDataPointBeforeAndAfterQueryResult) {
			startPartitionStartMilli = zoomLevel.decrement(ZonedDateTime.ofInstant(Instant.ofEpochMilli(startPartitionStartMilli), timeZone)).toInstant().toEpochMilli();
		}
		long endPartitionEndMilli = alignedInterval.getMax();
		if (addDataPointBeforeAndAfterQueryResult) {
			endPartitionEndMilli = zoomLevel.increment(Instant.ofEpochMilli(alignedInterval.getMax()).atZone(timeZone)).toInstant().toEpochMilli();
		}
		long currentPartitionStartMilli = startPartitionStartMilli;
		long nextPartitionStartMilli = zoomLevel.increment(ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentPartitionStartMilli), timeZone)).toInstant().toEpochMilli();
		int i = 0;
		do {
			Double aggregateValue = null;
			int count = 0;
			for (; i < dataPoints.size(); i++) {
				long tsMilli = (long) dataPoints.getX(i);
				if (tsMilli >= nextPartitionStartMilli) {
					break;
				}
				if (tsMilli >= currentPartitionStartMilli) {
					count++;
					double y = dataPoints.getY(i);
					if (aggregationType == AggregationType.FIRST_VALUE) {
						aggregateValue = aggregateValue == null ? y : aggregateValue;
						break;
					} else if (aggregationType == AggregationType.MAX) {
						aggregateValue = aggregateValue == null || aggregateValue < y ? y : aggregateValue;
					} else if (aggregationType == AggregationType.MIN) {
						aggregateValue = aggregateValue == null || aggregateValue > y ? y : aggregateValue;
					} else if (aggregationType == AggregationType.AVERAGE) {
						aggregateValue = aggregateValue == null ? y : aggregateValue + y;
					}
				}
			}

			if (count > 0 && aggregateValue != null /*double-checking for no reason except preventing compiler warnings*/) {
				double y = aggregationType == AggregationType.AVERAGE ? aggregateValue / count : aggregateValue;
				result.add(new LineGraphDataPoint(currentPartitionStartMilli, y));
			}

			currentPartitionStartMilli = nextPartitionStartMilli;
			nextPartitionStartMilli = zoomLevel.increment(getPartitionStart(nextPartitionStartMilli, zoomLevel, timeZone)).toInstant().toEpochMilli();
		} while (currentPartitionStartMilli < endPartitionEndMilli);
		return new ListLineGraphData(result, new Interval(startPartitionStartMilli, endPartitionEndMilli));
	}

	private static ZonedDateTime getPartitionStart(long timestampMillis, TimePartitioning partitionUnit, ZoneId timeZone) {
		return partitionUnit.getPartitionStart(ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), timeZone));
	}

	private static long getPartitionStartMilli(long timestampMillis, TimePartitioning partitionUnit, ZoneId timeZone) {
		return partitionUnit.getPartitionStart(Instant.ofEpochMilli(timestampMillis).atZone(timeZone)).toInstant().toEpochMilli();
	}

	private static long getPartitionEndMilli(long timestampMillis, TimePartitioning partitionUnit, ZoneId timeZone) {
		return partitionUnit.getPartitionEnd(Instant.ofEpochMilli(timestampMillis).atZone(timeZone)).toInstant().toEpochMilli();
	}

	@Override
	public Interval getDomainX() {
		long minX = graphData.streamX()
				.mapToLong(x -> (long) x)
				.min().orElse(0);
		long maxX = graphData.streamX()
				.mapToLong(x -> (long) x)
				.max().orElse(1);
		return new Interval(minX, maxX);
	}

	public boolean isAddDataPointBeforeAndAfterQueryResult() {
		return addDataPointBeforeAndAfterQueryResult;
	}

	public void setAddDataPointBeforeAndAfterQueryResult(boolean addDataPointBeforeAndAfterQueryResult) {
		this.addDataPointBeforeAndAfterQueryResult = addDataPointBeforeAndAfterQueryResult;
	}
}
