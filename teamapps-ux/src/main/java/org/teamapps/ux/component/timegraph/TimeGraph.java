/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.ux.component.timegraph;

import com.ibm.icu.util.ULocale;
import org.teamapps.dto.*;
import org.teamapps.event.Disposable;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.timegraph.datapoints.GraphData;
import org.teamapps.ux.component.timegraph.graph.AbstractGraph;
import org.teamapps.ux.session.SessionContext;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class TimeGraph extends AbstractComponent {

	public final ProjectorEvent<ZoomEventData> onZoomed = createProjectorEventBoundToUiEvent(UiTimeGraph.ZoomedEvent.TYPE_ID);
	public final ProjectorEvent<Interval> onIntervalSelected = createProjectorEventBoundToUiEvent(UiTimeGraph.IntervalSelectedEvent.TYPE_ID);
	private final List<GraphListenInfo> graphsAndListeners = new ArrayList<>();

	private static class GraphListenInfo {
		final AbstractGraph<?, ?> graph;
		final Disposable disposable;

		public GraphListenInfo(AbstractGraph<?, ?> graph, Disposable disposable) {
			this.graph = graph;
			this.disposable = disposable;
		}
	}

	private List<TimePartitioning> zoomLevels = Arrays.asList(
			TimePartitioningUnit.YEAR,
			TimePartitioningUnit.HALF_YEAR,
			TimePartitioningUnit.QUARTER,
			TimePartitioningUnit.MONTH,
			TimePartitioningUnit.WEEK_MONDAY,
			TimePartitioningUnit.DAY,
			TimePartitioningUnit.HOURS_12,
			TimePartitioningUnit.HOURS_6,
			TimePartitioningUnit.HOURS_3,
			TimePartitioningUnit.HOUR,
			TimePartitioningUnit.MINUTES_30,
			TimePartitioningUnit.MINUTES_15,
			TimePartitioningUnit.MINUTES_5,
			TimePartitioningUnit.MINUTES_2,
			TimePartitioningUnit.MINUTE,
			TimePartitioningUnit.SECONDS_30,
			TimePartitioningUnit.SECONDS_10,
			TimePartitioningUnit.SECONDS_5,
			TimePartitioningUnit.SECONDS_2,
			TimePartitioningUnit.SECOND,
			TimePartitioningUnit.MILLISECOND_500,
			TimePartitioningUnit.MILLISECOND_200,
			TimePartitioningUnit.MILLISECOND_100,
			TimePartitioningUnit.MILLISECOND_50,
			TimePartitioningUnit.MILLISECOND_20,
			TimePartitioningUnit.MILLISECOND_10,
			TimePartitioningUnit.MILLISECOND_5,
			TimePartitioningUnit.MILLISECOND_2,
			TimePartitioningUnit.MILLISECOND
	);

	private int maxPixelsBetweenDataPoints = 50; // ... before switching to higher zoom level
	private LineChartMouseScrollZoomPanMode mouseScrollZoomPanMode = LineChartMouseScrollZoomPanMode.ENABLED;

	// client-side state
	private Interval displayedInterval;
	private double millisecondsPerPixel;
	private Interval selectedInterval;

	private ULocale locale = SessionContext.current().getULocale();
	private ZoneId timeZoneId = SessionContext.current().getTimeZone();

	public TimeGraph() {
		super();
	}

	private ArrayList<AbstractGraph<?, ?>> getGraphs() {
		return this.graphsAndListeners.stream()
				.map(graphListenInfo -> graphListenInfo.graph)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public void addGraph(AbstractGraph<?, ?> graph) {
		final ArrayList<AbstractGraph<?, ?>> graphs = getGraphs();
		graphs.add(graph);
		setGraphs(graphs);
	}

	public void setGraphs(List<? extends AbstractGraph<?, ?>> graphs) {
		this.graphsAndListeners.forEach(g -> g.disposable.dispose());
		this.graphsAndListeners.clear();
		graphs.forEach(graph -> {
			graph.setChangeListener(display -> sendCommandIfRendered(() -> new UiTimeGraph.AddOrUpdateGraphCommand(display.createUiFormat())));
			Disposable disposable = graph.getModel().onDataChanged().addListener(aVoid -> handleGraphDataChanged(graph));
			this.graphsAndListeners.add(new GraphListenInfo(graph, disposable));
		});
		sendCommandIfRendered(() -> new UiTimeGraph.SetGraphsCommand(toUiLineFormats(graphs)));
		refresh();
	}

	private List<UiGraph> toUiLineFormats(List<? extends AbstractGraph<?, ?>> lineFormats) {
		return lineFormats.stream()
				.map(AbstractGraph::createUiFormat)
				.collect(Collectors.toList());
	}

	@Override
	public UiComponent createUiClientObject() {
		List<UiTimeChartZoomLevel> uiZoomLevels = createUiZoomlevels();

		Interval domainX = retrieveDomainX();

		this.displayedInterval = domainX;
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).toUiLongInterval();

		UiTimeGraph uiTimeGraph = new UiTimeGraph(
				uiIntervalX,
				uiZoomLevels,
				maxPixelsBetweenDataPoints,
				toUiLineFormats(getGraphs())
		);
		uiTimeGraph.setLocale(locale.toLanguageTag());
		uiTimeGraph.setTimeZoneId(timeZoneId.getId());
		mapAbstractUiComponentProperties(uiTimeGraph);
		uiTimeGraph.setMouseScrollZoomPanMode(mouseScrollZoomPanMode.toUiLineChartMouseScrollZoomPanMode());
		return uiTimeGraph;
	}

	private List<UiTimeChartZoomLevel> createUiZoomlevels() {
		return this.zoomLevels.stream()
				.map(timePartitioning -> new UiTimeChartZoomLevel(timePartitioning.getApproximateMillisecondsPerPartition()))
				.collect(Collectors.toList());
	}

	@Override
	public void handleUiEvent(UiEventWrapper event) {
		switch (event.getTypeId()) {
			case UiTimeGraph.ZoomedEvent.TYPE_ID -> {
				var zoomedEvent = event.as(UiTimeGraph.ZoomedEventWrapper.class);
				Interval displayedInterval = new Interval(zoomedEvent.getDisplayedInterval().getMin(), zoomedEvent.getDisplayedInterval().getMax());
				TimePartitioning timePartitioning = zoomLevels.get(zoomedEvent.getZoomLevelIndex());

				if (zoomedEvent.getNeededIntervalsByGraphId() != null) {
					final Map<String, List<Interval>> neededIntervalsByGraphId = zoomedEvent.getNeededIntervalsByGraphId().entrySet().stream()
							.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(i -> new Interval(i.getMin(), i.getMax())).collect(Collectors.toList())));
					Map<String, GraphData> data = retrieveData(displayedInterval, timePartitioning, neededIntervalsByGraphId);
					sendCommandIfRendered(() -> new UiTimeGraph.AddDataCommand(zoomedEvent.getZoomLevelIndex(), convertToUiData(data)));
				}

				this.displayedInterval = displayedInterval;
				this.millisecondsPerPixel = zoomedEvent.getMillisecondsPerPixel();
				this.onZoomed.fire(new ZoomEventData(displayedInterval, zoomedEvent.getMillisecondsPerPixel(), timePartitioning));
			}
			case UiTimeGraph.IntervalSelectedEvent.TYPE_ID -> {
				var selectedEvent = event.as(UiTimeGraph.IntervalSelectedEventWrapper.class);
				Interval interval = selectedEvent.getIntervalX() != null ? new Interval(selectedEvent.getIntervalX().getMin(), selectedEvent.getIntervalX().getMax()) : null;
				this.selectedInterval = interval;
				this.onIntervalSelected.fire(interval);
			}

		}
	}

	private Interval retrieveDomainX() {
		return graphsAndListeners.stream()
				.map(g -> g.graph.getModel().getDomainX())
				.reduce(Interval::union)
				.orElse(new Interval(0, 1));
	}

	private Map<String, GraphData> retrieveData(Interval displayedInterval, TimePartitioning timePartitioning, Map<String, List<Interval>> neededIntervalsByGraphId) {
		return graphsAndListeners.stream()
				.filter(g -> neededIntervalsByGraphId.containsKey(g.graph.getId()) && neededIntervalsByGraphId.get(g.graph.getId()).size() > 0)
				.collect(Collectors.toMap(
						g -> g.graph.getId(),
						g -> neededIntervalsByGraphId.get(g.graph.getId()).stream()
								.reduce(Interval::union)
								.map(interval -> g.graph.getModel().getData(timePartitioning, timeZoneId, interval, displayedInterval))
								.orElseThrow()
				));
	}

	private Map<String, UiGraphData> convertToUiData(Map<String, GraphData> data) {
		return data.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toUiGraphData()));
	}

	public void refresh() {
		Interval domainX = retrieveDomainX();
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).toUiLongInterval();
		sendCommandIfRendered(() -> new UiTimeGraph.ResetAllDataCommand(uiIntervalX, createUiZoomlevels()));
	}

	public void zoomTo(long minX, long maxX) {
		if (isRendered()) {
			getSessionContext().sendCommand(getId(), new UiTimeGraph.ZoomToCommand(new UiLongInterval(minX, maxX)), aVoid -> this.displayedInterval = new Interval(minX, maxX));
		} else {
			this.displayedInterval = new Interval(minX, maxX);
		}
	}

	public int getMaxPixelsBetweenDataPoints() {
		return maxPixelsBetweenDataPoints;
	}

	public void setMaxPixelsBetweenDataPoints(int maxPixelsBetweenDataPoints) {
		this.maxPixelsBetweenDataPoints = maxPixelsBetweenDataPoints;
		sendCommandIfRendered(() -> new UiTimeGraph.SetMaxPixelsBetweenDataPointsCommand(maxPixelsBetweenDataPoints));
	}

	public LineChartMouseScrollZoomPanMode getMouseScrollZoomPanMode() {
		return mouseScrollZoomPanMode;
	}

	public void setMouseScrollZoomPanMode(LineChartMouseScrollZoomPanMode mouseScrollZoomPanMode) {
		this.mouseScrollZoomPanMode = mouseScrollZoomPanMode;
		sendCommandIfRendered(() -> new UiTimeGraph.SetMouseScrollZoomPanModeCommand(mouseScrollZoomPanMode.toUiLineChartMouseScrollZoomPanMode()));
	}

	public Interval getSelectedInterval() {
		return selectedInterval;
	}

	public void setSelectedInterval(Interval selectedInterval) {
		this.selectedInterval = selectedInterval;
		sendCommandIfRendered(() -> new UiTimeGraph.SetSelectedIntervalCommand(selectedInterval.toUiLongInterval()));
	}

	private void handleGraphDataChanged(AbstractGraph<?, ?> graph) {
		Interval domainX = retrieveDomainX();
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).toUiLongInterval();
		sendCommandIfRendered(() -> new UiTimeGraph.SetIntervalXCommand(uiIntervalX));
		sendCommandIfRendered(() -> new UiTimeGraph.ResetGraphDataCommand(graph.getId()));
	}

	public Locale getLocale() {
		return locale.toLocale();
	}

	public ULocale getULocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		setULocale(ULocale.forLocale(locale));
	}

	public void setULocale(ULocale locale) {
		this.locale = locale;
		reRenderIfRendered();
	}

	public ZoneId getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(ZoneId timeZoneId) {
		this.timeZoneId = timeZoneId;
		reRenderIfRendered();
	}

	public List<TimePartitioning> getZoomLevels() {
		return zoomLevels;
	}

	public void setZoomLevels(List<TimePartitioning> zoomLevels) {
		this.zoomLevels = zoomLevels.stream()
				.sorted(Comparator.comparing(TimePartitioning::getApproximateMillisecondsPerPartition).reversed())
				.collect(Collectors.toList());
		refresh();
	}
}
