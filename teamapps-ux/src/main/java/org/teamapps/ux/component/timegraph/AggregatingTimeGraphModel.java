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

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class AggregatingTimeGraphModel extends AbstractTimeGraphModel {

	private final Map<String, LineChartDataPoints> dataPointsByDataSeriesId = new HashMap<>();

	public enum AggregationPolicy {
		FIRST_VALUE, MIN, AVERAGE, MAX
	}

	private final Map<String, AggregationPolicy> aggregationPolicyByDataSeriesId = new HashMap<>();
	private AggregationPolicy defaultAggregationPolicy = AggregationPolicy.FIRST_VALUE;

	private boolean addDataPointBeforeAndAfterQueryResult = true;

	public AggregatingTimeGraphModel() {
	}

	public void setDataPointsByDataSeriesId(Map<String, LineChartDataPoints> dataPointsByDataSeriesId) {
		this.dataPointsByDataSeriesId.clear();
		this.dataPointsByDataSeriesId.putAll(dataPointsByDataSeriesId);
		onDataChanged.fire(null);
	}

	public void setDataPoints(String dataSeriesId, LineChartDataPoints dataPoints) {
		this.dataPointsByDataSeriesId.put(dataSeriesId, dataPoints);
		onDataChanged.fire(null);
	}

	public void setAggregationPolicyByDataSeriesId(Map<String, AggregationPolicy> aggregationPolicyByDataSeriesId) {
		this.aggregationPolicyByDataSeriesId.clear();
		this.aggregationPolicyByDataSeriesId.putAll(aggregationPolicyByDataSeriesId);
		onDataChanged.fire(null);
	}

	public void setAggregationPolicy(String dataSeriesId, AggregationPolicy aggregationPolicy) {
		this.aggregationPolicyByDataSeriesId.put(dataSeriesId, aggregationPolicy);
		onDataChanged.fire(null);
	}

	public void setDefaultAggregationPolicy(AggregationPolicy defaultAggregationPolicy) {
		this.defaultAggregationPolicy = defaultAggregationPolicy;
		onDataChanged.fire(null);
	}

	@Override
	protected LineChartDataPoints getDataPoints(String dataSeriesId, TimePartitioning zoomLevel, ZoneId zoneId, Interval neededIntervalX, Interval interval) {
		LineChartDataPoints dataPoints = this.dataPointsByDataSeriesId.get(dataSeriesId);

		return getAggregateDataPoints(dataPoints, zoomLevel, interval, aggregationPolicyByDataSeriesId.getOrDefault(dataSeriesId, defaultAggregationPolicy), zoneId, addDataPointBeforeAndAfterQueryResult);
	}

	public static LineChartDataPoints getAggregateDataPoints(
			LineChartDataPoints dataPoints,
			TimePartitioning zoomLevel,
			Interval interval,
			AggregationPolicy aggregationPolicy,
			ZoneId timeZone,
			boolean addDataPointBeforeAndAfterQueryResult
	) {
		DoubleList resultX = new DoubleArrayList(100);
		DoubleList resultY = new DoubleArrayList(100);
		long currentPartitionStartMilli = getPartitionStart(interval.getMin(), zoomLevel, timeZone).toInstant().toEpochMilli();
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
					if (aggregationPolicy == AggregationPolicy.FIRST_VALUE) {
						aggregateValue = aggregateValue == null ? y : aggregateValue;
						break;
					} else if (aggregationPolicy == AggregationPolicy.MAX) {
						aggregateValue = aggregateValue == null || aggregateValue < y ? y : aggregateValue;
					} else if (aggregationPolicy == AggregationPolicy.MIN) {
						aggregateValue = aggregateValue == null || aggregateValue > y ? y : aggregateValue;
					} else if (aggregationPolicy == AggregationPolicy.AVERAGE) {
						aggregateValue = aggregateValue == null ? y : aggregateValue + y;
					}
				}
			}

			if (count > 0 && aggregateValue != null /*double-checking for no reason except preventing compiler warnings*/) {
				resultX.add(currentPartitionStartMilli);
				if (aggregationPolicy == AggregationPolicy.AVERAGE) {
					resultY.add(aggregateValue / count);
				} else {
					resultY.add((double) aggregateValue);
				}
			}

			currentPartitionStartMilli = nextPartitionStartMilli;
			nextPartitionStartMilli = zoomLevel.increment(getPartitionStart(nextPartitionStartMilli, zoomLevel, timeZone)).toInstant().toEpochMilli();
		} while (currentPartitionStartMilli <= interval.getMax() + (addDataPointBeforeAndAfterQueryResult ? zoomLevel.getApproximateMillisecondsPerPartition() : 0));
		return new DoubleArrayLineChartDataPoints(resultX.toDoubleArray(), resultY.toDoubleArray());
	}

	private static ZonedDateTime getPartitionStart(long timestampMillis, TimePartitioning partitionUnit, ZoneId timeZone) {
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), timeZone);
		return partitionUnit.getPartitionStart(zonedDateTime);
	}

	@Override
	public Interval getDomainX() {
		long minX = dataPointsByDataSeriesId.values().stream()
				.flatMapToDouble(LineChartDataPoints::streamX)
				.mapToLong(x -> (long) x)
				.min().orElse(0);
		long maxX = dataPointsByDataSeriesId.values().stream()
				.flatMapToDouble(LineChartDataPoints::streamX)
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
