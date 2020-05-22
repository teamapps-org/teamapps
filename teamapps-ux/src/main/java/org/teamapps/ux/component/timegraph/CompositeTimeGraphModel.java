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
package org.teamapps.ux.component.timegraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.event.Event;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompositeTimeGraphModel implements TimeGraphModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompositeTimeGraphModel.class);

	private Event<Void> onDataChanged = new Event<>();

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
	public List<TimeGraphZoomLevel> getZoomLevels() {
		return delegates.stream()
				.flatMap(timeGraphModel -> timeGraphModel.getZoomLevels().stream())
				.distinct()
				.sorted(Comparator.comparing(TimeGraphZoomLevel::getApproximateMillisecondsPerDataPoint).reversed())
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, LineChartDataPoints> getDataPoints(Collection<String> lineIds, TimeGraphZoomLevel zoomLevel, Interval neededIntervalX) {
		Map<String, LineChartDataPoints> points = delegates.stream()
				.map(delegate -> delegate.getDataPoints(lineIds, zoomLevel, neededIntervalX))
				.reduce((Map<String, LineChartDataPoints> map1,
				         Map<String, LineChartDataPoints> map2) -> {
					HashMap<String, LineChartDataPoints> m = new HashMap<>();
					m.putAll(map1);
					m.putAll(map2);
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
	public Interval getDomainX(Collection<String> lineIds) {
		return delegates.stream()
				.map(delegate -> delegate.getDomainX(lineIds))
				.filter(Objects::nonNull)
				.reduce((Interval interval1, Interval interval2) -> new Interval(Math.min(interval1.getMin(), interval2.getMin()), Math.max(interval1.getMax(), interval2.getMax())))
				.orElseGet(() -> new Interval(0, 1));
	}
}
