/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.component.timegraph.graph;

import org.teamapps.dto.UiGraphGroup;
import org.teamapps.ux.component.timegraph.Interval;
import org.teamapps.ux.component.timegraph.TimePartitioning;
import org.teamapps.ux.component.timegraph.datapoints.GraphData;
import org.teamapps.ux.component.timegraph.datapoints.GraphGroupData;
import org.teamapps.ux.component.timegraph.model.AbstractGraphGroupModel;
import org.teamapps.ux.component.timegraph.model.GraphGroupModel;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class GraphGroup extends AbstractGraph<GraphGroupData, GraphGroupModel> {

	private final Runnable subModelListener = () -> this.getModel().onDataChanged.fire();
	private List<AbstractGraph<?, ?>> graphs = new ArrayList<>();

	public GraphGroup(AbstractGraph<?, ?>... graphs) {
		this(Arrays.asList(graphs));
	}

	public GraphGroup(List<AbstractGraph<?, ?>> graphs) {
		super(null);
		setModel(new GraphGroupModel());
		this.setGraphs(graphs);
	}

	@Override
	public UiGraphGroup createUiFormat() {
		UiGraphGroup ui = new UiGraphGroup();
		mapAbstractLineChartDataDisplayProperties(ui);
		ui.setGraphs(graphs.stream().map(AbstractGraph::createUiFormat).collect(Collectors.toList()));
		return ui;
	}

	@Override
	public GraphGroupModel getModel() {
		return ((GraphGroupModel) super.getModel());
	}

	public void addGraph(AbstractGraph<?, ?> graph) {
		var newGraphs = new ArrayList<>(this.graphs);
		newGraphs.add(graph);
		setGraphs(newGraphs);
	}

	public void setGraphs(List<AbstractGraph<?, ?>> graphs) {
		for (AbstractGraph<?, ?> graph : this.graphs) {
			graph.setChangeListener(null);
			graph.getModel().onDataChanged().removeListener(subModelListener);
		}
		this.graphs = new ArrayList<>(graphs);
		for (AbstractGraph<?, ?> g : graphs) {
			g.setChangeListener(unused -> fireChange());

			g.getModel().onDataChanged().addListener(subModelListener);
		}
		fireChange();
		getModel().onDataChanged.fire();
	}

	public void removeGraph(AbstractGraph<?, ?> graph) {
		var newGraphs = new ArrayList<>(this.graphs);
		if (newGraphs.remove(graph)) {
			setGraphs(newGraphs);
		}
	}

	public List<AbstractGraph<?, ?>> getGraphs() {
		return List.copyOf(graphs);
	}

	private class GraphGroupModel extends AbstractGraphGroupModel {

		@Override
		public Interval getDomainX() {
			return graphs.stream()
					.map(g -> g.getModel().getDomainX())
					.reduce(Interval::union)
					.orElse(new Interval(0, 1));
		}

		@Override
		public GraphGroupData getData(TimePartitioning zoomLevel, ZoneId zoneId, Interval neededInterval, Interval displayedInterval) {
			final Map<String, GraphData> graphDataByGraphId = graphs.stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toMap(AbstractGraph::getId, g -> g.getModel().getData(zoomLevel, zoneId, neededInterval, displayedInterval)));
			return () -> graphDataByGraphId;
		}

	}
}
