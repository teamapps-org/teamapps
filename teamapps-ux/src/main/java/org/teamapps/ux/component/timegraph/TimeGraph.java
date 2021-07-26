/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.timegraph.datapoints.GraphData;
import org.teamapps.ux.component.timegraph.graph.AbstractGraph;
import org.teamapps.ux.session.SessionContext;

import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TimeGraph extends AbstractComponent {

	public final Event<ZoomEventData> onZoomed = new Event<>();
	public final Event<Interval> onIntervalSelected = new Event<>();
	private final List<AbstractGraph<?>> graphs = new ArrayList<>();

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

	// needs to be a field for reference equality (sad but true, java method references are only syntactic sugar for lambdas)
	private final Consumer<Void> onTimeGraphDataChangedListener = this::onTimeGraphDataChanged;

	private ULocale locale = SessionContext.current().getULocale();
	private ZoneId timeZoneId = SessionContext.current().getTimeZone();

	public TimeGraph() {
		super();
	}

	public void addGraph(AbstractGraph<?> graph) {
		this.graphs.add(graph);
		setGraphs(List.copyOf(this.graphs));
	}

	public void setGraphs(List<? extends AbstractGraph<?>> graphs) {
		this.graphs.forEach(g -> g.getModel().onDataChanged().removeListener(onTimeGraphDataChangedListener));
		this.graphs.clear();
		graphs.forEach(graph -> {
			this.graphs.add(graph);
			graph.setChangeListener(display -> queueCommandIfRendered(() -> new UiTimeGraph.AddOrUpdateGraphCommand(getId(), display.createUiFormat())));
			graph.getModel().onDataChanged().addListener(onTimeGraphDataChangedListener);
		});
		queueCommandIfRendered(() -> new UiTimeGraph.SetGraphsCommand(getId(), toUiLineFormats(this.graphs)));
		refresh();
	}

	private List<UiGraph> toUiLineFormats(List<? extends AbstractGraph<?>> lineFormats) {
		return lineFormats.stream()
				.map(AbstractGraph::createUiFormat)
				.collect(Collectors.toList());
	}

	@Override
	public UiComponent createUiComponent() {
		List<UiTimeChartZoomLevel> uiZoomLevels = createUiZoomlevels();

		Interval domainX = retrieveDomainX();

		this.displayedInterval = domainX;
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).toUiLongInterval();

		UiTimeGraph uiTimeGraph = new UiTimeGraph(
				uiIntervalX,
				uiZoomLevels,
				maxPixelsBetweenDataPoints,
				toUiLineFormats(graphs)
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
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TIME_GRAPH_ZOOMED: {
				UiTimeGraph.ZoomedEvent zoomedEvent = (UiTimeGraph.ZoomedEvent) event;

				Interval displayedInterval = new Interval(zoomedEvent.getDisplayedInterval().getMin(), zoomedEvent.getDisplayedInterval().getMax());
				TimePartitioning timePartitioning = zoomLevels.get(zoomedEvent.getZoomLevelIndex());

				if (zoomedEvent.getNeededIntervalsByGraphId() != null) {
					final Map<String, List<Interval>> neededIntervalsByGraphId = zoomedEvent.getNeededIntervalsByGraphId().entrySet().stream()
							.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(i -> new Interval(i.getMin(), i.getMax())).collect(Collectors.toList())));
					Map<String, GraphData> data = retrieveData(displayedInterval, timePartitioning, neededIntervalsByGraphId);
					queueCommandIfRendered(() -> new UiTimeGraph.AddDataCommand(this.getId(), zoomedEvent.getZoomLevelIndex(), convertToUiData(data)));
				}

				this.displayedInterval = displayedInterval;
				this.millisecondsPerPixel = zoomedEvent.getMillisecondsPerPixel();
				this.onZoomed.fire(new ZoomEventData(displayedInterval, zoomedEvent.getMillisecondsPerPixel(), timePartitioning));
				break;
			}
			case UI_TIME_GRAPH_INTERVAL_SELECTED: {
				UiTimeGraph.IntervalSelectedEvent selectedEvent = (UiTimeGraph.IntervalSelectedEvent) event;
				Interval interval = selectedEvent.getIntervalX() != null ? new Interval(selectedEvent.getIntervalX().getMin(), selectedEvent.getIntervalX().getMax()) : null;
				this.selectedInterval = interval;
				this.onIntervalSelected.fire(interval);
				break;
			}
		}
	}

	private Interval retrieveDomainX() {
		return graphs.stream()
				.map(g -> g.getModel().getDomainX())
				.reduce(Interval::union)
				.orElse(new Interval(0, 1));
	}

	private Map<String, GraphData> retrieveData(Interval displayedInterval, TimePartitioning timePartitioning, Map<String, List<Interval>> neededIntervalsByGraphId) {
		return graphs.stream()
				.filter(g -> neededIntervalsByGraphId.containsKey(g.getId()) && neededIntervalsByGraphId.get(g.getId()).size() > 0)
				.collect(Collectors.toMap(
						AbstractGraph::getId,
						g -> neededIntervalsByGraphId.get(g.getId()).stream()
								.reduce(Interval::union)
								.map(interval -> g.getModel().getData(timePartitioning, timeZoneId, interval, displayedInterval))
								.orElseThrow()
				));
	}

	private Map<String, UiGraphData> convertToUiData(Map<String, GraphData> data) {
		return data.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toUiGraphData()));
	}

	public void refresh() {
		queueCommandIfRendered(() -> new UiTimeGraph.ResetAllDataCommand(getId(), createUiZoomlevels()));
	}

	public void zoomTo(long minX, long maxX) {
		if (isRendered()) {
			getSessionContext().queueCommand(new UiTimeGraph.ZoomToCommand(getId(), new UiLongInterval(minX, maxX)),
					aVoid -> this.displayedInterval = new Interval(minX, maxX));
		} else {
			this.displayedInterval = new Interval(minX, maxX);
		}
	}

	public int getMaxPixelsBetweenDataPoints() {
		return maxPixelsBetweenDataPoints;
	}

	public void setMaxPixelsBetweenDataPoints(int maxPixelsBetweenDataPoints) {
		this.maxPixelsBetweenDataPoints = maxPixelsBetweenDataPoints;
		queueCommandIfRendered(() -> new UiTimeGraph.SetMaxPixelsBetweenDataPointsCommand(getId(), maxPixelsBetweenDataPoints));
	}

	public LineChartMouseScrollZoomPanMode getMouseScrollZoomPanMode() {
		return mouseScrollZoomPanMode;
	}

	public void setMouseScrollZoomPanMode(LineChartMouseScrollZoomPanMode mouseScrollZoomPanMode) {
		this.mouseScrollZoomPanMode = mouseScrollZoomPanMode;
		queueCommandIfRendered(() -> new UiTimeGraph.SetMouseScrollZoomPanModeCommand(getId(), mouseScrollZoomPanMode.toUiLineChartMouseScrollZoomPanMode()));
	}

	public Interval getSelectedInterval() {
		return selectedInterval;
	}

	public void setSelectedInterval(Interval selectedInterval) {
		this.selectedInterval = selectedInterval;
		queueCommandIfRendered(() -> new UiTimeGraph.SetSelectedIntervalCommand(getId(), selectedInterval.toUiLongInterval()));
	}

	private void onTimeGraphDataChanged(Void aVoid) {
		Interval domainX = retrieveDomainX();
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).toUiLongInterval();
		queueCommandIfRendered(() -> new UiTimeGraph.SetIntervalXCommand(getId(), uiIntervalX));
		refresh();
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
