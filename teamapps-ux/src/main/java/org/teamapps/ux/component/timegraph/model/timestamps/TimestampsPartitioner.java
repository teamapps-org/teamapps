/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

import org.teamapps.ux.component.timegraph.TimePartitioning;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TimestampsPartitioner {

	public static List<TimestampsPartition> partition(long startTime, long endTime, List<Long> eventTimeStamps, ZoneId zoneId, TimePartitioning partitionUnit, boolean returnEmptyPartitions) {
		return partition(startTime, endTime, eventTimeStamps.stream().mapToLong(l -> l).toArray(), zoneId, partitionUnit, returnEmptyPartitions);
	}

	public static List<TimestampsPartition> partition(long startTime, long endTime, long[] eventTimeStamps, ZoneId zoneId, TimePartitioning partitionUnit, boolean returnEmptyPartitions) {
		TreeMap<Long, Integer> partitions = new TreeMap<>(Comparator.comparingLong(value -> value));

		if (returnEmptyPartitions) {
			for (ZonedDateTime partitionZonedDateTime = Instant.ofEpochMilli(getPartition(startTime, zoneId, partitionUnit)).atZone(zoneId);
			     partitionZonedDateTime.toInstant().toEpochMilli() <= endTime;
			     partitionZonedDateTime = partitionUnit.increment(partitionZonedDateTime)) {
				partitions.put(partitionZonedDateTime.toInstant().toEpochMilli(), 0);
			}
		}

		long searchStartTime = partitionUnit.getPartitionStart(Instant.ofEpochMilli(startTime).atZone(zoneId)).toInstant().toEpochMilli();
		ZonedDateTime endTimeZonedDateTime = Instant.ofEpochMilli(endTime).atZone(zoneId);
		long searchEndTime = partitionUnit.increment(partitionUnit.increment(partitionUnit.getPartitionStart(endTimeZonedDateTime))).toInstant().toEpochMilli();

		Arrays.stream(eventTimeStamps)
				.filter(e -> e >= searchStartTime && e <= searchEndTime)
				.forEach(e -> partitions.compute(getPartition(e, zoneId, partitionUnit), (partition, count) -> count == null ? 1 : count + 1));

		return partitions.entrySet().stream()
				.map(entry -> new TimestampsPartition(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private static long getPartition(Long eventTime, ZoneId zoneId, TimePartitioning partitionUnit) {
		ZonedDateTime zonedDateTime = Instant.ofEpochMilli(eventTime).atZone(zoneId);
		return partitionUnit.getPartitionStart(zonedDateTime).toInstant().toEpochMilli();
	}





}
