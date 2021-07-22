package org.teamapps.ux.component.timegraph.datapoints;

import java.util.stream.Stream;

public interface IncidentGraphData extends GraphData {

	int size();

	IncidentGraphDataPoint getDataPoint(int index);

	default Stream<IncidentGraphDataPoint> streamDataPoints() {
		int[] i = {0};
		return Stream.generate(() -> getDataPoint(i[0]++))
				.limit(size());
	}

}
