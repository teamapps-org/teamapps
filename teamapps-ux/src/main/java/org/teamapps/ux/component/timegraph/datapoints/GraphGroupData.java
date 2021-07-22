package org.teamapps.ux.component.timegraph.datapoints;

import org.teamapps.dto.UiGraphData;
import org.teamapps.dto.UiGraphGroupData;

import java.util.Map;
import java.util.stream.Collectors;

public interface GraphGroupData extends GraphData {

	Map<String, GraphData> getGraphData();

	@Override
	default UiGraphGroupData toUiGraphData() {
		final Map<String, UiGraphData> uiGraphDataMap = getGraphData().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toUiGraphData()));
		return new UiGraphGroupData(uiGraphDataMap);
	}
}
