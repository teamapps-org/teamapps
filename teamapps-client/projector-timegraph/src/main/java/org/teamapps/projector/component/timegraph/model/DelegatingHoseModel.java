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
import org.teamapps.projector.component.timegraph.datapoints.HoseGraphData;
import org.teamapps.projector.component.timegraph.datapoints.LineGraphData;

import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Stream;

public class DelegatingHoseModel extends AbstractHoseGraphModel {

	private final GraphModel<LineGraphData> minModel;
	private final GraphModel<LineGraphData> avgModel;
	private final GraphModel<LineGraphData> maxModel;

	public DelegatingHoseModel(GraphModel<LineGraphData> minModel, GraphModel<LineGraphData> avgModel, GraphModel<LineGraphData> maxModel) {
		this.minModel = minModel;
		if (this.minModel != null) {
			this.minModel.onDataChanged().addListener(() -> this.onDataChanged().fire());
		}
		this.avgModel = avgModel;
		if (this.avgModel != null) {
			this.avgModel.onDataChanged().addListener(() -> this.onDataChanged().fire());
		}
		this.maxModel = maxModel;
		if (this.maxModel != null) {
			this.maxModel.onDataChanged().addListener(() -> this.onDataChanged().fire());
		}
	}

	public DelegatingHoseModel(GraphModel<LineGraphData> minModel, GraphModel<LineGraphData> maxModel) {
		this(minModel, null, maxModel);
	}

	@Override
	public Interval getDomainX() {
		return Stream.of(minModel, avgModel, maxModel)
				.filter(Objects::nonNull)
				.map(GraphModel::getDomainX)
				.reduce(Interval::union)
				.orElse(new Interval(0, 1));
	}

	@Override
	public HoseGraphData getData(TimePartitioning zoomLevel, ZoneId zoneId, Interval neededInterval, Interval displayedInterval) {
		LineGraphData minDataPoints = getDataPointsOrNull(minModel, zoomLevel, zoneId, neededInterval, displayedInterval);
		LineGraphData avgDataPoints = getDataPointsOrNull(avgModel, zoomLevel, zoneId, neededInterval, displayedInterval);
		LineGraphData maxDataPoints = getDataPointsOrNull(maxModel, zoomLevel, zoneId, neededInterval, displayedInterval);
		return new HoseGraphData() {
			@Override
			public LineGraphData getMiddleLineData() {
				return avgDataPoints;
			}

			@Override
			public LineGraphData getLowerLineData() {
				return minDataPoints;
			}

			@Override
			public LineGraphData getUpperLineData() {
				return maxDataPoints;
			}
		};
	}

	private LineGraphData getDataPointsOrNull(GraphModel<LineGraphData> model, TimePartitioning zoomLevel, ZoneId zoneId, Interval neededInterval, Interval displayedInterval) {
		return model != null ? model.getData(zoomLevel, zoneId, neededInterval, displayedInterval) : null;
	}
}
