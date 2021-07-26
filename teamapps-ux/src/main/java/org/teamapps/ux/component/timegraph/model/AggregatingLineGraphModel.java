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
package org.teamapps.ux.component.timegraph.model;

import org.teamapps.ux.component.timegraph.Interval;
import org.teamapps.ux.component.timegraph.TimePartitioning;
import org.teamapps.ux.component.timegraph.datapoints.LineGraphData;
import org.teamapps.ux.component.timegraph.datapoints.LineGraphDataPoint;
import org.teamapps.ux.component.timegraph.datapoints.ListLineGraphData;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class AggregatingLineGraphModel extends AbstractGraphModel<LineGraphData> {

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
		final long queryStart = zoomLevel.getPartitionStart(Instant.ofEpochMilli(displayedInterval.getMin()).atZone(zoneId)).toInstant().toEpochMilli();
		final long queryEnd = zoomLevel.getPartitionEnd(Instant.ofEpochMilli(displayedInterval.getMax()).atZone(zoneId)).toInstant().toEpochMilli();
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
		long currentPartitionStartMilli = getPartitionStart(alignedInterval.getMin(), zoomLevel, timeZone).toInstant().toEpochMilli();
		if (addDataPointBeforeAndAfterQueryResult) {
			currentPartitionStartMilli = zoomLevel.decrement(ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentPartitionStartMilli), timeZone)).toInstant().toEpochMilli();
		}
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
		} while (currentPartitionStartMilli <= alignedInterval.getMax() + (addDataPointBeforeAndAfterQueryResult ? zoomLevel.getApproximateMillisecondsPerPartition() : 0));
		return new ListLineGraphData(result, alignedInterval);
	}

	private static ZonedDateTime getPartitionStart(long timestampMillis, TimePartitioning partitionUnit, ZoneId timeZone) {
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), timeZone);
		return partitionUnit.getPartitionStart(zonedDateTime);
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
