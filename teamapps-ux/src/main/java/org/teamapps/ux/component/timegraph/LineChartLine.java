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

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiLineChartLine;
import org.teamapps.dto.UiLongInterval;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.teamapps.util.UiUtil.createUiColor;

public class LineChartLine implements LineChartDataDisplay {

	private final String id = UUID.randomUUID().toString();

	private final String dataSeriesId;
	private LineChartDataDisplayChangeListener changeListener;

	private LineChartCurveType graphType = LineChartCurveType.MONOTONE;
	private float dataDotRadius = 2;
	private Color yAxisColor = Color.BLACK;
	private Color lineColorScaleMin = new Color(73, 128, 192);
	private Color lineColorScaleMax = new Color(73, 128, 192);
	private Color areaColorScaleMin = new Color(255, 255, 255, 0);
	private Color areaColorScaleMax = new Color(255, 255, 255, 0);

	private Interval intervalY;
	private ScaleType yScaleType = ScaleType.LINEAR;
	private LineChartYScaleZoomMode yScaleZoomMode = LineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO;
	private boolean yZeroLineVisible = false;


	public LineChartLine(String dataSeriesId) {
		this.dataSeriesId = dataSeriesId;
	}

	public LineChartLine(String dataSeriesId, LineChartCurveType graphType, float dataDotRadius, Color lineColor) {
		this(dataSeriesId, graphType, dataDotRadius, lineColor, lineColor, null, null);
	}

	public LineChartLine(String dataSeriesId, LineChartCurveType graphType, float dataDotRadius, Color lineColor, Color areaColor) {
		this(dataSeriesId, graphType, dataDotRadius, lineColor, lineColor, Color.withAlpha(areaColor, 0.0f), areaColor);
	}

	public LineChartLine(String dataSeriesId, LineChartCurveType graphType, float dataDotRadius, Color lineColorScaleMin, Color lineColorScaleMax, Color areaColorScaleMin, Color areaColorScaleMax) {
		this.dataSeriesId = dataSeriesId;
		this.graphType = graphType;
		this.dataDotRadius = dataDotRadius;
		this.lineColorScaleMin = lineColorScaleMin;
		this.lineColorScaleMax = lineColorScaleMax;
		this.areaColorScaleMin = areaColorScaleMin;
		this.areaColorScaleMax = areaColorScaleMax;
	}

	@Override
	public UiLineChartLine createUiFormat() {
		UiLineChartLine ui = new UiLineChartLine();
		mapAbstractLineChartDataDisplayProperties(ui);

		ui.setDataSeriesId(dataSeriesId);

		ui.setGraphType(graphType.toUiLineChartCurveType());
		ui.setDataDotRadius(dataDotRadius);
		ui.setLineColorScaleMin(lineColorScaleMin != null ? createUiColor(lineColorScaleMin) : null);
		ui.setLineColorScaleMax(lineColorScaleMax != null ? createUiColor(lineColorScaleMax) : null);
		ui.setAreaColorScaleMin(areaColorScaleMin != null ? createUiColor(areaColorScaleMin) : null);
		ui.setAreaColorScaleMax(areaColorScaleMax != null ? createUiColor(areaColorScaleMax) : null);
		ui.setAxisColor(yAxisColor != null ? createUiColor(yAxisColor) : null);
		ui.setIntervalY(intervalY != null ? intervalY.createUiLongInterval() : new UiLongInterval(0, 1000));
		ui.setYScaleType(yScaleType.toUiScaleType());
		ui.setYScaleZoomMode(yScaleZoomMode.toUiLineChartYScaleZoomMode());
		ui.setYZeroLineVisible(yZeroLineVisible);

		return ui;
	}

	public String getId() {
		return id;
	}

	@Override
	public List<String> getDataSeriesIds() {
		return Collections.singletonList(dataSeriesId);
	}

	public LineChartCurveType getGraphType() {
		return graphType;
	}

	public LineChartLine setGraphType(LineChartCurveType graphType) {
		this.graphType = graphType;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public float getDataDotRadius() {
		return dataDotRadius;
	}

	public LineChartLine setDataDotRadius(float dataDotRadius) {
		this.dataDotRadius = dataDotRadius;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getLineColorScaleMin() {
		return lineColorScaleMin;
	}

	public LineChartLine setLineColorScaleMin(Color lineColorScaleMin) {
		this.lineColorScaleMin = lineColorScaleMin;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getLineColorScaleMax() {
		return lineColorScaleMax;
	}

	public LineChartLine setLineColorScaleMax(Color lineColorScaleMax) {
		this.lineColorScaleMax = lineColorScaleMax;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getAreaColorScaleMin() {
		return areaColorScaleMin;
	}

	public LineChartLine setAreaColorScaleMin(Color areaColorScaleMin) {
		this.areaColorScaleMin = areaColorScaleMin;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getAreaColorScaleMax() {
		return areaColorScaleMax;
	}

	public LineChartLine setAreaColorScaleMax(Color areaColorScaleMax) {
		this.areaColorScaleMax = areaColorScaleMax;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Interval getIntervalY() {
		return intervalY;
	}

	public LineChartLine setIntervalY(Interval intervalY) {
		this.intervalY = intervalY;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public ScaleType getyScaleType() {
		return yScaleType;
	}

	public LineChartLine setYScaleType(ScaleType yScaleType) {
		this.yScaleType = yScaleType;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public LineChartYScaleZoomMode getYScaleZoomMode() {
		return yScaleZoomMode;
	}

	public LineChartLine setYScaleZoomMode(LineChartYScaleZoomMode yScaleZoomMode) {
		this.yScaleZoomMode = yScaleZoomMode;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	@Override
	public void setChangeListener(LineChartDataDisplayChangeListener listener) {
		this.changeListener = listener;
	}

	public Color getYAxisColor() {
		return yAxisColor;
	}

	public LineChartLine setYAxisColor(Color yAxisColor) {
		this.yAxisColor = yAxisColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public boolean isYZeroLineVisible() {
		return yZeroLineVisible;
	}

	public LineChartLine setYZeroLineVisible(boolean yZeroLineVisible) {
		this.yZeroLineVisible = yZeroLineVisible;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}
}
