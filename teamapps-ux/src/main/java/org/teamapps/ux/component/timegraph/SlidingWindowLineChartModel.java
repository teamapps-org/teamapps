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

import org.teamapps.event.Event;

import java.util.*;
import java.util.stream.Collectors;

public class SlidingWindowLineChartModel implements TimeGraphModel {
	
	public final Event<Void> onTimeGraphDataChanged = new Event<>();

	private final int maxDataPoints;

	private final Map<String, List<LineChartDataPoint>> dataPointsByLineId = new HashMap<>();

	private long minX;
	private long maxX;

	public SlidingWindowLineChartModel(int maxDataPoints) {
		this.maxDataPoints = maxDataPoints;
	}

	public void addLinePoint(String lineId, long xValue, double yValue, boolean fireModelDataChange) {
		LineChartDataPoint dataPoint = new LineChartDataPoint(xValue, yValue);

		synchronized (this) {
			List<LineChartDataPoint> lineChartDataPoints = dataPointsByLineId.computeIfAbsent(lineId, k -> new ArrayList<>());
			lineChartDataPoints.add(dataPoint);
			if (lineChartDataPoints.size() > maxDataPoints) {
				lineChartDataPoints.remove(0);
			}
		}
		updateMinMaxX();
		if (fireModelDataChange) {
			fireModelDataChange();
		}
	}

	public void fireModelDataChange() {
		onTimeGraphDataChanged.fire(null);
	}

	private void updateMinMaxX() {
		this.minX = dataPointsByLineId.values().stream()
				.filter(points -> !points.isEmpty())
				.map(points -> ((long) points.get(0).getX()))
				.min(Long::compare).orElse(0L);
		this.maxX = dataPointsByLineId.values().stream()
				.filter(points -> !points.isEmpty())
				.map(points -> ((long) points.get(points.size() - 1).getX()))
				.max(Long::compare).orElse(1L);
	}

	@Override
	public List<TimeGraphZoomLevel> getZoomLevels() {
		return Collections.singletonList(new TimeGraphZoomLevel(1));
	}

	@Override
	public Event<Void> onDataChanged() {
		return onTimeGraphDataChanged;
	}

	@Override
	public Map<String, List<LineChartDataPoint>> getDataPoints(Collection<String> lineIds, TimeGraphZoomLevel zoomLevel, Interval neededIntervalX) {
		synchronized (this) {
			return dataPointsByLineId.entrySet().stream()
					.filter(e -> lineIds.contains(e.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.asList(entry.getValue().toArray(new LineChartDataPoint[0]))));
		}
	}

	@Override
	public Interval getDomainX(Collection<String> lineIds) {
		return new Interval(minX, maxX);
	}
}
