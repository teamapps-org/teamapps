package org.teamapps.ux.component.timegraph.datapoints;

import org.teamapps.dto.UiGraphData;
import org.teamapps.dto.UiGraphGroupData;
import org.teamapps.ux.component.timegraph.Interval;

import java.util.Map;
import java.util.stream.Collectors;

public interface GraphGroupData extends GraphData {

	Map<String, GraphData> getGraphData();

	default Interval getInterval() {
		return getGraphData().values().stream()
				.map(GraphData::getInterval)
				.reduce(Interval::intersection)
				.orElse(Interval.empty());
	}

	@Override
	default UiGraphGroupData toUiGraphData() {
		final Map<String, UiGraphData> uiGraphDataMap = getGraphData().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toUiGraphData()));
		return new UiGraphGroupData(uiGraphDataMap, getInterval().toUiLongInterval());
	}
}
