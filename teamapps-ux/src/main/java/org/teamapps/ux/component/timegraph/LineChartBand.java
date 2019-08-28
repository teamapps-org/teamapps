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
import org.teamapps.dto.UiLineChartBand;
import org.teamapps.dto.UiLongInterval;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.teamapps.util.UiUtil.createUiColor;

public class LineChartBand implements LineChartDataDisplay {

	private final String id = UUID.randomUUID().toString();

	private String upperBoundDataSeriesId;
	private String middleLineDataSeriesId;
	private String lowerBoundDataSeriesId;

	private LineChartDataDisplayChangeListener changeListener;

	private LineChartCurveType curveType;
	private float dataDotRadius;
	private Color yAxisColor = Color.BLACK;
	private Color lineColor;
	private Color areaColor;

	private Interval intervalY;
	private ScaleType yScaleType = ScaleType.LINEAR;
	private LineChartYScaleZoomMode yScaleZoomMode = LineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO;
	private boolean yZeroLineVisible = false;


	public LineChartBand(String upperBoundDataSeriesId, String middleLineDataSeriesId, String lowerBoundDataSeriesId) {
		this(upperBoundDataSeriesId, middleLineDataSeriesId, lowerBoundDataSeriesId, LineChartCurveType.MONOTONE, 2, new Color(73, 128, 192));
	}

	public LineChartBand(String upperBoundDataSeriesId, String middleLineDataSeriesId, String lowerBoundDataSeriesId, LineChartCurveType curveType, float dataDotRadius, Color lineColor) {
		this(upperBoundDataSeriesId, middleLineDataSeriesId, lowerBoundDataSeriesId, curveType, dataDotRadius, lineColor, null);
	}

	public LineChartBand(String upperBoundDataSeriesId, String middleLineDataSeriesId, String lowerBoundDataSeriesId, LineChartCurveType curveType, float dataDotRadius, Color lineColor, Color areaColor) {
		this.upperBoundDataSeriesId = upperBoundDataSeriesId;
		this.middleLineDataSeriesId = middleLineDataSeriesId;
		this.lowerBoundDataSeriesId = lowerBoundDataSeriesId;
		this.curveType = curveType;
		this.dataDotRadius = dataDotRadius;
		this.lineColor = lineColor;
		this.areaColor = areaColor;
	}

	public UiLineChartBand createUiFormat() {
		UiLineChartBand ui = new UiLineChartBand();
		mapAbstractLineChartDataDisplayProperties(ui);

		ui.setUpperBoundDataSeriesId(upperBoundDataSeriesId);
		ui.setMiddleLineDataSeriesId(middleLineDataSeriesId);
		ui.setLowerBoundDataSeriesId(lowerBoundDataSeriesId);

		ui.setGraphType(curveType.toUiLineChartCurveType());
		ui.setDataDotRadius(dataDotRadius);
		ui.setLineColor(lineColor != null ? createUiColor(lineColor) : null);
		ui.setAreaColor(areaColor != null ? createUiColor(areaColor) : null);
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
		return Arrays.asList(upperBoundDataSeriesId, middleLineDataSeriesId, lowerBoundDataSeriesId);
	}

	@Override
	public void setChangeListener(LineChartDataDisplayChangeListener listener) {
		this.changeListener = listener;
	}

	public LineChartCurveType getCurveType() {
		return curveType;
	}

	public LineChartBand setCurveType(LineChartCurveType curveType) {
		this.curveType = curveType;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public float getDataDotRadius() {
		return dataDotRadius;
	}

	public LineChartBand setDataDotRadius(float dataDotRadius) {
		this.dataDotRadius = dataDotRadius;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public LineChartBand setLineColor(Color lineColor) {
		this.lineColor = lineColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getAreaColor() {
		return areaColor;
	}

	public LineChartBand setAreaColor(Color areaColor) {
		this.areaColor = areaColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Interval getIntervalY() {
		return intervalY;
	}

	public LineChartBand setIntervalY(Interval intervalY) {
		this.intervalY = intervalY;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public ScaleType getyScaleType() {
		return yScaleType;
	}

	public LineChartBand setYScaleType(ScaleType yScaleType) {
		this.yScaleType = yScaleType;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public LineChartYScaleZoomMode getYScaleZoomMode() {
		return yScaleZoomMode;
	}

	public LineChartBand setYScaleZoomMode(LineChartYScaleZoomMode yScaleZoomMode) {
		this.yScaleZoomMode = yScaleZoomMode;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getYAxisColor() {
		return yAxisColor;
	}

	public LineChartBand setYAxisColor(Color yAxisColor) {
		this.yAxisColor = yAxisColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public boolean isYZeroLineVisible() {
		return yZeroLineVisible;
	}

	public LineChartBand setYZeroLineVisible(boolean yZeroLineVisible) {
		this.yZeroLineVisible = yZeroLineVisible;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}
}
