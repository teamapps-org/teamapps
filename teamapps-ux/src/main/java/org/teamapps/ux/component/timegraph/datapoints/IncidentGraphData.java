package org.teamapps.ux.component.timegraph.datapoints;

import org.teamapps.dto.UiGraphData;
import org.teamapps.dto.UiIncidentGraphData;
import org.teamapps.dto.UiIncidentGraphDataPoint;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IncidentGraphData extends GraphData {

	int size();

	IncidentGraphDataPoint getDataPoint(int index);

	default Stream<IncidentGraphDataPoint> streamDataPoints() {
		int[] i = {0};
		return Stream.generate(() -> getDataPoint(i[0]++))
				.limit(size());
	}

	@Override
	default UiGraphData toUiGraphData() {
		return new UiIncidentGraphData(streamDataPoints()
				.map(d -> new UiIncidentGraphDataPoint(d.getX1(), d.getX2(), d.getY(), d.getColor().toHtmlColorString(), d.getTooltipHtml()))
				.collect(Collectors.toList()), getInterval().toUiLongInterval());
	}
}
