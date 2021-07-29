package org.teamapps.ux.component.timegraph.datapoints;

import org.teamapps.dto.UiGraphData;
import org.teamapps.ux.component.timegraph.Interval;

public interface GraphData {

	Interval getInterval();

	UiGraphData toUiGraphData();

}
