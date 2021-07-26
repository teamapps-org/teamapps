package org.teamapps.ux.component.timegraph.datapoints;

import org.teamapps.dto.UiGraphData;
import org.teamapps.dto.UiHoseGraphData;
import org.teamapps.ux.component.timegraph.Interval;

import java.util.Objects;
import java.util.stream.Stream;

public interface HoseGraphData extends GraphData {

	LineGraphData getMiddleLineData();

	LineGraphData getLowerLineData();

	LineGraphData getUpperLineData();

	@Override
	default Interval getInterval() {
		return Stream.of(getMiddleLineData(), getLowerLineData(), getUpperLineData())
				.filter(Objects::nonNull)
				.map(lineGraphData -> lineGraphData.getInterval())
				.reduce(Interval::intersection)
				.orElse(Interval.empty());
	}

	@Override
	default UiGraphData toUiGraphData() {
		return new UiHoseGraphData(
				getLowerLineData().toUiGraphData(),
				getMiddleLineData().toUiGraphData(),
				getUpperLineData().toUiGraphData(),
				getInterval().toUiLongInterval()
		);
	}
}
