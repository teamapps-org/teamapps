package org.teamapps.ux.component.timegraph.datapoints;

import org.teamapps.dto.UiGraphData;
import org.teamapps.dto.UiHoseGraphData;

public interface HoseGraphData extends GraphData {

	LineGraphData getMiddleLineData();

	LineGraphData getLowerLineData();

	LineGraphData getUpperLineData();

	@Override
	default UiGraphData toUiGraphData() {
		return new UiHoseGraphData(getLowerLineData().toUiGraphData(), getMiddleLineData().toUiGraphData(), getUpperLineData().toUiGraphData());
	}
}
