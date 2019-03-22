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

import org.teamapps.dto.UiLineChartLineFormat;
import org.teamapps.dto.UiLongInterval;
import org.teamapps.common.format.Color;

import static org.teamapps.util.UiUtil.createUiColor;

public class LineChartLine {

	private static Color[] LINE_BASE_COLORS = new Color[]{Color.MATERIAL_BLUE_500, Color.MATERIAL_TEAL_500, Color.MATERIAL_RED_500, Color.MATERIAL_DEEP_PURPLE_500, Color.MATERIAL_GREEN_500,
			Color.MATERIAL_GREY_500};

	public static Color getBaseColor(int index) {
		return LINE_BASE_COLORS[index % LINE_BASE_COLORS.length];
	}

	private final String id;
	private LineChartLineListener changeListener;

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


	public LineChartLine(String id) {
		this.id = id;
	}

	public LineChartLine(String id, LineChartCurveType graphType, float dataDotRadius, Color lineColor) {
		this(id, graphType, dataDotRadius, lineColor, lineColor, null, null);
	}

	public LineChartLine(String id, LineChartCurveType graphType, float dataDotRadius, Color lineColor, Color areaColor) {
		this(id, graphType, dataDotRadius, lineColor, lineColor, Color.withAlpha(areaColor, 0.0f), areaColor);
	}

	public LineChartLine(String id, LineChartCurveType graphType, float dataDotRadius, Color lineColorScaleMin, Color lineColorScaleMax, Color areaColorScaleMin, Color areaColorScaleMax) {
		this.id = id;
		this.graphType = graphType;
		this.dataDotRadius = dataDotRadius;
		this.lineColorScaleMin = lineColorScaleMin;
		this.lineColorScaleMax = lineColorScaleMax;
		this.areaColorScaleMin = areaColorScaleMin;
		this.areaColorScaleMax = areaColorScaleMax;
	}

	public UiLineChartLineFormat createUiLineChartLineFormat() {
		UiLineChartLineFormat definition = new UiLineChartLineFormat();
		definition.setGraphType(graphType.toUiLineChartCurveType());
		definition.setDataDotRadius(dataDotRadius);
		definition.setLineColorScaleMin(lineColorScaleMin != null ? createUiColor(lineColorScaleMin) : null);
		definition.setLineColorScaleMax(lineColorScaleMax != null ? createUiColor(lineColorScaleMax) : null);
		definition.setAreaColorScaleMin(areaColorScaleMin != null ? createUiColor(areaColorScaleMin) : null);
		definition.setAreaColorScaleMax(areaColorScaleMax != null ? createUiColor(areaColorScaleMax) : null);
		definition.setYAxisColor(yAxisColor != null ? createUiColor(yAxisColor) : null);
		definition.setIntervalY(intervalY != null ? intervalY.createUiLongInterval() : new UiLongInterval(0, 1000));
		definition.setYScaleType(yScaleType.toUiScaleType());
		definition.setYScaleZoomMode(yScaleZoomMode.toUiLineChartYScaleZoomMode());

		return definition;
	}

	public String getId() {
		return id;
	}

	public LineChartCurveType getGraphType() {
		return graphType;
	}

	public LineChartLine setGraphType(LineChartCurveType graphType) {
		this.graphType = graphType;
		if (this.changeListener != null) {
			changeListener.handleGraphTypeChanged(this, graphType);
		}
		return this;
	}

	public float getDataDotRadius() {
		return dataDotRadius;
	}

	public LineChartLine setDataDotRadius(float dataDotRadius) {
		this.dataDotRadius = dataDotRadius;
		if (this.changeListener != null) {
			changeListener.handleDataDotRadiusChanged(this, dataDotRadius);
		}
		return this;
	}

	public Color getLineColorScaleMin() {
		return lineColorScaleMin;
	}

	public LineChartLine setLineColorScaleMin(Color lineColorScaleMin) {
		this.lineColorScaleMin = lineColorScaleMin;
		if (this.changeListener != null) {
			changeListener.handleLineColorScaleMinChanged(this, lineColorScaleMin);
		}
		return this;
	}

	public Color getLineColorScaleMax() {
		return lineColorScaleMax;
	}

	public LineChartLine setLineColorScaleMax(Color lineColorScaleMax) {
		this.lineColorScaleMax = lineColorScaleMax;
		if (this.changeListener != null) {
			changeListener.handleLineColorScaleMaxChanged(this, lineColorScaleMax);
		}
		return this;
	}

	public Color getAreaColorScaleMin() {
		return areaColorScaleMin;
	}

	public LineChartLine setAreaColorScaleMin(Color areaColorScaleMin) {
		this.areaColorScaleMin = areaColorScaleMin;
		if (this.changeListener != null) {
			changeListener.handleAreaColorScaleMinChanged(this, areaColorScaleMin);
		}
		return this;
	}

	public Color getAreaColorScaleMax() {
		return areaColorScaleMax;
	}

	public LineChartLine setAreaColorScaleMax(Color areaColorScaleMax) {
		this.areaColorScaleMax = areaColorScaleMax;
		if (this.changeListener != null) {
			changeListener.handleAreaColorScaleMaxChanged(this, areaColorScaleMax);
		}
		return this;
	}

	public Interval getIntervalY() {
		return intervalY;
	}

	public LineChartLine setIntervalY(Interval intervalY) {
		this.intervalY = intervalY;
		if (this.changeListener != null) {
			changeListener.handleIntervalYChanged(this, intervalY);
		}
		return this;
	}

	public ScaleType getyScaleType() {
		return yScaleType;
	}

	public LineChartLine setYScaleType(ScaleType yScaleType) {
		this.yScaleType = yScaleType;
		if (this.changeListener != null) {
			changeListener.handleYScaleTypeChanged(this, yScaleType);
		}
		return this;
	}

	public LineChartYScaleZoomMode getYScaleZoomMode() {
		return yScaleZoomMode;
	}

	public LineChartLine setYScaleZoomMode(LineChartYScaleZoomMode yScaleZoomMode) {
		this.yScaleZoomMode = yScaleZoomMode;
		if (this.changeListener != null) {
			changeListener.handleYScaleZoomModeChanged(this, yScaleZoomMode);
		}
		return this;
	}

	void setChangeListener(LineChartLineListener listener) {
		this.changeListener = listener;
	}

	public Color getYAxisColor() {
		return yAxisColor;
	}

	public LineChartLine setYAxisColor(Color yAxisColor) {
		this.yAxisColor = yAxisColor;
		if (this.changeListener != null) {
			changeListener.handleYAxisColorChanged(this, yAxisColor);
		}
		return this;
	}
}
