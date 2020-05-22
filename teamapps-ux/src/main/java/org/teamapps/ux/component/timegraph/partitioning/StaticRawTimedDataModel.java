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

import org.teamapps.ux.component.timegraph.Interval;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StaticRawTimedDataModel extends AbstractRawTimedDataModel {

	private Map<String, long[]> eventTimestampsByDataSeriesId = new HashMap<>();
	private Interval staticDomainX;

	public void setEventTimestampsByDataSeriesId(Map<String, long[]> eventTimestampsByDataSeriesId) {
		this.eventTimestampsByDataSeriesId = new HashMap<>(eventTimestampsByDataSeriesId);
		onDataChanged.fire(null);
	}

	public void setEventTimestampsForDataSeriesIds(String dataSeriesId, long[] eventTimestamps) {
		eventTimestampsByDataSeriesId.put(dataSeriesId, eventTimestamps);
		onDataChanged.fire(null);
	}

	@Override
	protected long[] getRawEventTimes(String dataSeriesId, Interval neededIntervalX) {
		return eventTimestampsByDataSeriesId.get(dataSeriesId);
	}

	@Override
	public Interval getDomainX(Collection<String> dataSeriesId) {
		if (staticDomainX != null) {
			return staticDomainX;
		} else {
			long min = eventTimestampsByDataSeriesId.entrySet().stream()
					.filter(e -> dataSeriesId.contains(e.getKey()))
					.flatMapToLong(e -> Arrays.stream(e.getValue()))
					.min().orElse(0);
			long max = eventTimestampsByDataSeriesId.entrySet().stream()
					.filter(e -> dataSeriesId.contains(e.getKey()))
					.flatMapToLong(e -> Arrays.stream(e.getValue()))
					.max().orElse(1);
			return new Interval(min, max);
		}
	}

	public void setStaticDomainX(Interval staticDomainX) {
		this.staticDomainX = staticDomainX;
	}
}
