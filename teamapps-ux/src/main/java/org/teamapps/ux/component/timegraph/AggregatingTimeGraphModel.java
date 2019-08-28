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

import org.jetbrains.annotations.NotNull;
import org.teamapps.ux.component.timegraph.partitioning.TimePartitionUnit;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatingTimeGraphModel extends AbstractTimeGraphModel {

	private final ZoneId timeZone;

	private List<TimePartitionUnit> zoomLevels = Arrays.asList(
			TimePartitionUnit.YEAR,
			TimePartitionUnit.HALF_YEAR,
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
			TimePartitionUnit.SECONDS_10,
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
	private Map<String, List<LineChartDataPoint>> dataPointsByLineId = new HashMap<>();

	public enum AggregationPolicy {
		FIRST_VALUE, MIN, AVERAGE, MAX
	}

	private Map<String, AggregationPolicy> aggregationPolicyByLineId = new HashMap<>();
	private AggregationPolicy defaultAggregationPolicy = AggregationPolicy.FIRST_VALUE;

	public AggregatingTimeGraphModel(ZoneId timeZone) {
		this.timeZone = timeZone;
	}

	public AggregatingTimeGraphModel(ZoneId timeZone, List<TimePartitionUnit> zoomLevels) {
		this.timeZone = timeZone;
		this.zoomLevels = zoomLevels;
	}

	public void setZoomLevels(List<TimePartitionUnit> zoomLevels) {
		this.zoomLevels = zoomLevels;
		onDataChanged.fire(null);
	}

	public void setDataPointsByLineId(Map<String, List<LineChartDataPoint>> dataPointsByLineId) {
		this.dataPointsByLineId.clear();
		this.dataPointsByLineId.putAll(dataPointsByLineId);
		onDataChanged.fire(null);
	}

	public void setDataPoints(String lineId, List<LineChartDataPoint> dataPoints) {
		this.dataPointsByLineId.put(lineId, dataPoints);
		onDataChanged.fire(null);
	}

	public void setAggregationPolicyByLineId(Map<String, AggregationPolicy> aggregationPolicyByLineId) {
		this.aggregationPolicyByLineId.clear();
		this.aggregationPolicyByLineId.putAll(aggregationPolicyByLineId);
		onDataChanged.fire(null);
	}

	public void setAggregationPolicy(String lineId, AggregationPolicy aggregationPolicy) {
		this.aggregationPolicyByLineId.put(lineId, aggregationPolicy);
		onDataChanged.fire(null);
	}

	public void setDefaultAggregationPolicy(AggregationPolicy defaultAggregationPolicy) {
		this.defaultAggregationPolicy = defaultAggregationPolicy;
		onDataChanged.fire(null);
	}

	@NotNull
	protected List<LineChartDataPoint> getDataPoints(String lineId, TimeGraphZoomLevel partitionUnit, Interval interval) {
		TimePartitionUnit zoomLevel = zoomLevels.stream()
				.filter(unit -> unit.getAverageMilliseconds() == partitionUnit.getApproximateMillisecondsPerDataPoint())
				.findFirst().orElse(zoomLevels.get(0));
		List<LineChartDataPoint> dataPoints = this.dataPointsByLineId.get(lineId);

		List<LineChartDataPoint> aggregateDataPoints = getAggregateDataPoints(dataPoints, zoomLevel, interval, aggregationPolicyByLineId.getOrDefault(lineId, defaultAggregationPolicy));
		return aggregateDataPoints;
	}

	@NotNull
	private List<LineChartDataPoint> getAggregateDataPoints(List<LineChartDataPoint> dataPoints, TimePartitionUnit zoomLevel, Interval interval, AggregationPolicy aggregationPolicy) {
		ArrayList<LineChartDataPoint> result = new ArrayList<>();
		long currentPartitionStartMilli = zoomLevel.decrement(getPartitionStart(interval.getMin(), zoomLevel)).toInstant().toEpochMilli(); // add one dataPoint to the left
		long nextPartitionStartMilli = zoomLevel.increment(ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentPartitionStartMilli), timeZone)).toInstant().toEpochMilli();
		int i = 0;
		do {
			Double aggregateValue = null;
			int count = 0;
			for (; i < dataPoints.size(); i++) {
				LineChartDataPoint dataPoint = dataPoints.get(i);
				long tsMilli = (long) dataPoint.getX();
				if (tsMilli >= nextPartitionStartMilli) {
					break;
				}
				if (tsMilli >= currentPartitionStartMilli) {
					count++;
					if (aggregationPolicy == AggregationPolicy.FIRST_VALUE) {
						aggregateValue = aggregateValue == null ? dataPoint.getY() : aggregateValue;
						break;
					} else if (aggregationPolicy == AggregationPolicy.MAX) {
						aggregateValue = aggregateValue == null || aggregateValue < dataPoint.getY() ? dataPoint.getY() : aggregateValue;
					} else if (aggregationPolicy == AggregationPolicy.MIN) {
						aggregateValue = aggregateValue == null || aggregateValue > dataPoint.getY() ? dataPoint.getY() : aggregateValue;
					} else if (aggregationPolicy == AggregationPolicy.AVERAGE) {
						aggregateValue = aggregateValue == null ? dataPoint.getY() : aggregateValue + dataPoint.getY();
					}
				}
			}

			if (count > 0) {
				if (aggregationPolicy == AggregationPolicy.AVERAGE) {
					result.add(new LineChartDataPoint(currentPartitionStartMilli, aggregateValue / count));
				} else {
					result.add(new LineChartDataPoint(currentPartitionStartMilli, aggregateValue));
				}
			}

			currentPartitionStartMilli = nextPartitionStartMilli;
			nextPartitionStartMilli = zoomLevel.increment(getPartitionStart(nextPartitionStartMilli, zoomLevel)).toInstant().toEpochMilli();
		} while (currentPartitionStartMilli <= interval.getMax() + zoomLevel.getAverageMilliseconds()); // add one more dataPoint to the right!
		return result;
	}

	private ZonedDateTime getPartitionStart(long timestampMillis, TimePartitionUnit partitionUnit) {
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), timeZone);
		return partitionUnit.getPartition(zonedDateTime);
	}

	@Override
	public List<? extends TimeGraphZoomLevel> getZoomLevels() {
		return zoomLevels;
	}

	@Override
	public Interval getDomainX(Collection<String> lineIds) {
		long minX = dataPointsByLineId.values().stream()
				.flatMap(lineChartDataPoints -> lineChartDataPoints.stream())
				.mapToLong(lineChartDataPoint -> (long) lineChartDataPoint.getX())
				.min().orElse(0);
		long maxX = dataPointsByLineId.values().stream()
				.flatMap(lineChartDataPoints -> lineChartDataPoints.stream())
				.mapToLong(lineChartDataPoint -> (long) lineChartDataPoint.getX())
				.max().orElse(1);
		return new Interval(minX, maxX);
	}

}
