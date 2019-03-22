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
package org.teamapps.ux.component.timegraph.partitioning;

import org.teamapps.event.Event;
import org.teamapps.ux.component.timegraph.Interval;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StaticRawTimedDataModel implements RawTimedDataModel {

	public final Event<Void> onDataChanged = new Event<>();

	private Map<String, long[]> eventTimestampsByLineId = new HashMap<>();
	private Interval staticDomainX;

	public void setEventTimestampsByLineId(Map<String, long[]> eventTimestampsByLineId) {
		this.eventTimestampsByLineId = new HashMap<>(eventTimestampsByLineId);
		onDataChanged.fire(null);
	}

	public void setEventTimestampsForLineId(String lineId, long[] eventTimestamps) {
		eventTimestampsByLineId.put(lineId, eventTimestamps);
		onDataChanged.fire(null);
	}

	@Override
	public Event<Void> onDataChanged() {
		return onDataChanged;
	}

	@Override
	public Map<String, long[]> getRawEventTimes(Collection<String> lineIds, Interval neededIntervalX) {
		return eventTimestampsByLineId.entrySet().stream()
				.filter(e -> lineIds.contains(e.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public Interval getDomainX(Collection<String> lineIds) {
		if (staticDomainX != null) {
			return staticDomainX;
		} else {
			long min = eventTimestampsByLineId.entrySet().stream()
					.filter(e -> lineIds.contains(e.getKey()))
					.flatMapToLong(e -> Arrays.stream(e.getValue()))
					.min().orElse(0);
			long max = eventTimestampsByLineId.entrySet().stream()
					.filter(e -> lineIds.contains(e.getKey()))
					.flatMapToLong(e -> Arrays.stream(e.getValue()))
					.max().orElse(1);
			return new Interval(min, max);
		}
	}
}
