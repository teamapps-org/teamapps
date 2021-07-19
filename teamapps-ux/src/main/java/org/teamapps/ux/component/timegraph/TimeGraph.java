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
import org.teamapps.common.format.Color;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.timegraph.partitioning.TimePartitioningUnit;
import org.teamapps.ux.session.SessionContext;

import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TimeGraph extends AbstractComponent {

	public final Event<ZoomEventData> onZoomed = new Event<>();
	public final Event<Interval> onIntervalSelected = new Event<>();
	private final List<AbstractLineChartDataDisplay> lines = new ArrayList<>();

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
	private TimeGraphModel model;
	private LineChartMouseScrollZoomPanMode mouseScrollZoomPanMode = LineChartMouseScrollZoomPanMode.ENABLED;

	// client-side state
	private Interval displayedInterval;
	private double millisecondsPerPixel;
	private Interval selectedInterval;

	// needs to be a field for reference equality (sad but true, java method references are only syntactic sugar for lambdas)
	private final Consumer<Void> onTimeGraphDataChangedListener = this::onTimeGraphDataChanged;

	private ULocale locale = SessionContext.current().getULocale();
	private ZoneId timeZoneId = SessionContext.current().getTimeZone();

	public TimeGraph(TimeGraphModel model) {
		super();
		this.model = model;
		setModel(model);
	}

	public void addLine(String dataSeriesId, LineChartCurveType graphType, float dataDotRadius, Color lineColor) {
		addLine(new LineChartLine(dataSeriesId, graphType, dataDotRadius, lineColor, null));
	}

	public void addLine(String dataSeriesId, LineChartCurveType graphType, float dataDotRadius, Color lineColor, Color areaColor) {
		addLine(new LineChartLine(dataSeriesId, graphType, dataDotRadius, lineColor, areaColor));
	}

	public void addLine(AbstractLineChartDataDisplay lineFormat) {
		this.addDataDisplay(lineFormat);
	}

	public void addDataDisplay(AbstractLineChartDataDisplay lineFormat) {
		lines.add(lineFormat);
		setLines(new ArrayList<>(this.lines));
	}

	public void setLines(List<? extends AbstractLineChartDataDisplay> lineFormats) {
		lines.clear();
		lineFormats.forEach(line -> {
			lines.add(line);
			line.setChangeListener(display -> {
				queueCommandIfRendered(() -> new UiTimeGraph.SetLineCommand(getId(), display.getId(), display.createUiFormat()));
			});
		});
		queueCommandIfRendered(() -> new UiTimeGraph.SetLinesCommand(getId(), toUiLineFormats(lineFormats)));
		refresh();
	}

	private List<AbstractUiLineChartDataDisplay> toUiLineFormats(List<? extends AbstractLineChartDataDisplay> lineFormats) {
		return lineFormats.stream()
				.map(AbstractLineChartDataDisplay::createUiFormat)
				.collect(Collectors.toList());
	}

	public List<String> getLineDataIds() {
		return lines.stream()
				.flatMap(lineChartDataDisplay -> lineChartDataDisplay.getDataSeriesIds().stream())
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public UiComponent createUiComponent() {
		List<UiTimeChartZoomLevel> uiZoomLevels = createUiZoomlevels();

		Interval domainX = model.getDomainX();
		if (domainX == null) {
			domainX = new Interval(0, 1);
		}
		this.displayedInterval = domainX;
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).createUiLongInterval();

		UiTimeGraph uiTimeGraph = new UiTimeGraph(
				uiIntervalX,
				uiZoomLevels,
				maxPixelsBetweenDataPoints,
				toUiLineFormats(lines)
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

				if (zoomedEvent.getNeededInterval() != null) {
					Interval neededInterval = new Interval(zoomedEvent.getNeededInterval().getMin(), zoomedEvent.getNeededInterval().getMax());
					Map<String, LineChartDataPoints> data = model.getDataPoints(getLineDataIds(), timePartitioning, timeZoneId, neededInterval, displayedInterval);
					queueCommandIfRendered(() -> new UiTimeGraph.AddDataCommand(this.getId(), zoomedEvent.getZoomLevelIndex(), zoomedEvent.getNeededInterval(), convertToUiData(data)));
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

	private Map<String, List<UiTimeGraphDataPoint>> convertToUiData(Map<String, LineChartDataPoints> data) {
		Map<String, List<UiTimeGraphDataPoint>> uiData = new HashMap<>();
		data.forEach((dataSeriesId, dataPoints) -> {
			List<UiTimeGraphDataPoint> uiList = new ArrayList<>();
			for (int i = 0; i < dataPoints.size(); i++) {
				uiList.add(new UiTimeGraphDataPoint(dataPoints.getX(i), dataPoints.getY(i)));
			}
			uiData.put(dataSeriesId, uiList);
		});
		return uiData;
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
		queueCommandIfRendered(() -> new UiTimeGraph.SetSelectedIntervalCommand(getId(), selectedInterval.createUiLongInterval()));
	}

	public void setModel(TimeGraphModel model) {
		unregisterModelListeners();
		if (model != null) {
			model.onDataChanged().addListener(onTimeGraphDataChangedListener);
		}
		this.model = model;
		refresh();
	}

	private void unregisterModelListeners() {
		if (this.model != null) {
			this.model.onDataChanged().removeListener(onTimeGraphDataChangedListener);
		}
	}

	private void onTimeGraphDataChanged(Void aVoid) {
		Interval domainX = model.getDomainX();
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).createUiLongInterval();
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
