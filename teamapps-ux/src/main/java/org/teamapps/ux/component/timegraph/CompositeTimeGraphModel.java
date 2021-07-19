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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.event.Event;

import java.time.ZoneId;
import java.util.*;

public class CompositeTimeGraphModel implements TimeGraphModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompositeTimeGraphModel.class);

	private final Event<Void> onDataChanged = new Event<>();

	private final List<TimeGraphModel> delegates;

	public CompositeTimeGraphModel(TimeGraphModel... delegates) {
		this.delegates = Arrays.asList(delegates);
		this.delegates.forEach(delegate -> delegate.onDataChanged().addListener((Runnable) onDataChanged::fire));
	}

	@Override
	public Event<Void> onDataChanged() {
		return onDataChanged;
	}

	@Override
	public Map<String, LineChartDataPoints> getDataPoints(Collection<String> dataSeriesIds, TimePartitioning partitioning, ZoneId zoneId, Interval neededInterval, Interval displayedInterval) {
		Map<String, LineChartDataPoints> points = delegates.stream()
				.map(delegate -> delegate.getDataPoints(dataSeriesIds, partitioning, zoneId, neededInterval, displayedInterval))
				.reduce((Map<String, LineChartDataPoints> map1,
				         Map<String, LineChartDataPoints> map2) -> {
					HashMap<String, LineChartDataPoints> m = new HashMap<>();
					m.putAll(map1);
					for (String lineName : map2.keySet()) {
						if (!m.containsKey(lineName) || m.get(lineName).size() <= 0) {
							m.put(lineName, map2.get(lineName));
						}
					}
					return m;
				}).orElseGet(HashMap::new);
		int count = points.values().stream()
				.mapToInt(LineChartDataPoints::size)
				.sum();
		if (count > 10_000) {
			LOGGER.debug("Number of datapoints {}", count);
		}
		return points;
	}

	@Override
	public Interval getDomainX() {
		return delegates.stream()
				.map(TimeGraphModel::getDomainX)
				.filter(Objects::nonNull)
				.reduce((Interval interval1, Interval interval2) -> new Interval(Math.min(interval1.getMin(), interval2.getMin()), Math.max(interval1.getMax(), interval2.getMax())))
				.orElseGet(() -> new Interval(0, 1));
	}
}
