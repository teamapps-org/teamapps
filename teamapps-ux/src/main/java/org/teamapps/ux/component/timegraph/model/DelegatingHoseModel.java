package org.teamapps.ux.component.timegraph.model;

import org.teamapps.ux.component.timegraph.Interval;
import org.teamapps.ux.component.timegraph.TimePartitioning;
import org.teamapps.ux.component.timegraph.datapoints.HoseGraphData;
import org.teamapps.ux.component.timegraph.datapoints.LineGraphData;

import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Stream;

public class DelegatingHoseModel extends AbstractHoseGraphModel {

	private final GraphModel<LineGraphData> minModel;
	private final GraphModel<LineGraphData> avgModel;
	private final GraphModel<LineGraphData> maxModel;

	public DelegatingHoseModel(GraphModel<LineGraphData> minModel, GraphModel<LineGraphData> avgModel, GraphModel<LineGraphData> maxModel) {
		this.minModel = minModel;
		this.avgModel = avgModel;
		this.maxModel = maxModel;
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
