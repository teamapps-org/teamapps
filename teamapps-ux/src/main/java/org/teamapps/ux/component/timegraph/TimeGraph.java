/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.jetbrains.annotations.NotNull;
import org.teamapps.common.format.Color;
import org.teamapps.dto.AbstractUiLineChartDataDisplay;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiLongInterval;
import org.teamapps.dto.UiTimeChartZoomLevel;
import org.teamapps.dto.UiTimeGraph;
import org.teamapps.dto.UiTimeGraphDataPoint;
import org.teamapps.event.Event;
import org.teamapps.event.EventListener;
import org.teamapps.ux.component.AbstractComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TimeGraph extends AbstractComponent {

	public final Event<ZoomEventData> onZoomed = new Event<>();
	public final Event<Interval> onIntervalSelected = new Event<>();
	private final List<AbstractLineChartDataDisplay> lines = new ArrayList<>();

	private int maxPixelsBetweenDataPoints = 50; // ... before switching to higher zoom level
	private TimeGraphModel model;
	private LineChartMouseScrollZoomPanMode mouseScrollZoomPanMode = LineChartMouseScrollZoomPanMode.ENABLED;
	private Interval selectedInterval;

	/**
	 * If null, when the model fires {@link TimeGraphModel#onDataChanged()}, the data on the client is reset and new data will be requested by the client according to the current
	 * client-side zoom level and displayed intervalX.
	 * <p>
	 * If not null, this supplier provides the required information (zoom level and intervalX) for retrieving data from the model, so no round trip through the client needed.
	 */
	private Supplier<EagerFetchingParameters> eagerFetchingParametersSupplier;

	// needs to be a field for reference equality (sad but true, java method references are only syntactic sugar for lambdas)
	private final EventListener<Void> onTimeGraphDataChangedListener = this::onTimeGraphDataChanged;

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

		Interval domainX = model.getDomainX(getLineDataIds());
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).createUiLongInterval();

		UiTimeGraph uiTimeGraph = new UiTimeGraph(
				uiIntervalX,
				uiZoomLevels,
				maxPixelsBetweenDataPoints,
				toUiLineFormats(lines)
		);
		mapAbstractUiComponentProperties(uiTimeGraph);
		uiTimeGraph.setMouseScrollZoomPanMode(mouseScrollZoomPanMode.toUiLineChartMouseScrollZoomPanMode());
		return uiTimeGraph;
	}

	@NotNull
	private List<UiTimeChartZoomLevel> createUiZoomlevels() {
		return this.model.getZoomLevels().stream()
				.map(TimeGraphZoomLevel::createUiTimeChartZoomLevel)
				.collect(Collectors.toList());
	}

	@Override
	protected void doDestroy() {
		unregisterModelListeners();
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TIME_GRAPH_DATA_NEEDED: {
				UiTimeGraph.DataNeededEvent dataNeededEvent = (UiTimeGraph.DataNeededEvent) event;
				Interval interval = new Interval(dataNeededEvent.getNeededIntervalX().getMin(), dataNeededEvent.getNeededIntervalX().getMax());
				TimeGraphZoomLevel zoomLevel = model.getZoomLevels().get(dataNeededEvent.getZoomLevelIndex());
				Map<String, LineChartDataPoints> data = model.getDataPoints(getLineDataIds(), zoomLevel, interval);
				queueCommandIfRendered(() -> new UiTimeGraph.AddDataCommand(this.getId(), dataNeededEvent.getZoomLevelIndex(), dataNeededEvent.getNeededIntervalX(), convertToUiData(data)));
				break;
			}
			case UI_TIME_GRAPH_ZOOMED: {
				UiTimeGraph.ZoomedEvent zoomedEvent = (UiTimeGraph.ZoomedEvent) event;
				Interval interval = new Interval(zoomedEvent.getIntervalX().getMin(), zoomedEvent.getIntervalX().getMax());
				this.onZoomed.fire(new ZoomEventData(interval, zoomedEvent.getZoomLevelIndex()));
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
		if (this.eagerFetchingParametersSupplier != null) {
			queueCommandIfRendered(() -> {
				EagerFetchingParameters eagerFetchingConfig = this.eagerFetchingParametersSupplier.get();
				TimeGraphZoomLevel zoomLevel = model.getZoomLevels().get(eagerFetchingConfig.zoomLevelIndex);
				Map<String, List<UiTimeGraphDataPoint>> uiData = convertToUiData(model.getDataPoints(getLineDataIds(), zoomLevel, eagerFetchingConfig.intervalX));
				return new UiTimeGraph.ReplaceAllDataCommand(getId(), createUiZoomlevels(), eagerFetchingConfig.zoomLevelIndex, eagerFetchingConfig.intervalX.createUiLongInterval(), uiData);
			});
		} else {
			queueCommandIfRendered(() -> new UiTimeGraph.ResetAllDataCommand(getId(), createUiZoomlevels()));
			// the time graph will query for data
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
		Interval domainX = model.getDomainX(getLineDataIds());
		UiLongInterval uiIntervalX = new Interval(domainX.getMin(), domainX.getMax()).createUiLongInterval();
		queueCommandIfRendered(() -> new UiTimeGraph.SetIntervalXCommand(getId(), uiIntervalX));
		refresh();
	}

	public void setEagerFetchingParametersSupplier(Supplier<EagerFetchingParameters> eagerFetchingParameterSupplier) {
		this.eagerFetchingParametersSupplier = eagerFetchingParameterSupplier;
	}

	public Supplier<EagerFetchingParameters> getEagerFetchingParametersSupplier() {
		return eagerFetchingParametersSupplier;
	}

	public static class EagerFetchingParameters {
		private final int zoomLevelIndex;
		private final Interval intervalX;

		public EagerFetchingParameters(int zoomLevelIndex, Interval intervalX) {
			this.zoomLevelIndex = zoomLevelIndex;
			this.intervalX = intervalX;
		}

		public int getZoomLevelIndex() {
			return zoomLevelIndex;
		}

		public Interval getIntervalX() {
			return intervalX;
		}
	}
}
