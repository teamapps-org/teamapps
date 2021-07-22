package org.teamapps.ux.component.timegraph.graph;

import org.teamapps.dto.UiIncidentGraph;
import org.teamapps.ux.component.timegraph.datapoints.IncidentGraphData;
import org.teamapps.ux.component.timegraph.model.GraphModel;

public class IncidentGraph extends AbstractGraph<IncidentGraphData> {

	public IncidentGraph(GraphModel<IncidentGraphData> graphModel) {
		super(graphModel);
	}

	@Override
	public UiIncidentGraph createUiFormat() {
		final UiIncidentGraph ui = new UiIncidentGraph();
		mapAbstractLineChartDataDisplayProperties(ui);
		return ui;
	}
}
