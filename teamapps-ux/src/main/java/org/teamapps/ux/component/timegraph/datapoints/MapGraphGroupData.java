package org.teamapps.ux.component.timegraph.datapoints;

import java.util.Collections;
import java.util.Map;

public class MapGraphGroupData implements GraphGroupData {

	private final Map<String, GraphData> dataPointsByGraphId;

	public MapGraphGroupData(Map<String, GraphData> dataPointsByGraphId) {
		this.dataPointsByGraphId = dataPointsByGraphId;
	}

	@Override
	public Map<String, GraphData> getGraphData() {
		return Collections.unmodifiableMap(dataPointsByGraphId);
	}

}
