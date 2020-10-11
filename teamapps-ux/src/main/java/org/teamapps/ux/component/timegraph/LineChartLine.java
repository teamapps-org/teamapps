/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.dto.UiLineChartLine;

import java.util.Collections;
import java.util.List;

public class LineChartLine extends AbstractLineChartDataDisplay {

	private final String dataSeriesId;

	private LineChartCurveType graphType = LineChartCurveType.MONOTONE;
	private float dataDotRadius = 2;
	private Color lineColorScaleMin = new RgbaColor(73, 128, 192);
	private Color lineColorScaleMax = new RgbaColor(73, 128, 192);
	private Color areaColorScaleMin = new RgbaColor(255, 255, 255, 0);
	private Color areaColorScaleMax = new RgbaColor(255, 255, 255, 0);


	public LineChartLine(String dataSeriesId) {
		this.dataSeriesId = dataSeriesId;
	}

	public LineChartLine(String dataSeriesId, LineChartCurveType graphType, float dataDotRadius, Color lineColor) {
		this(dataSeriesId, graphType, dataDotRadius, lineColor, lineColor, null, null);
	}

	public LineChartLine(String dataSeriesId, LineChartCurveType graphType, float dataDotRadius, Color lineColor, Color areaColor) {
		this(dataSeriesId, graphType, dataDotRadius, lineColor, lineColor, (areaColor instanceof RgbaColor) ? ((RgbaColor) areaColor).withAlpha(0.0f) : null, areaColor);
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
		ui.setLineColorScaleMin(lineColorScaleMin != null ? lineColorScaleMin.toHtmlColorString() : null);
		ui.setLineColorScaleMax(lineColorScaleMax != null ? lineColorScaleMax.toHtmlColorString() : null);
		ui.setAreaColorScaleMin(areaColorScaleMin != null ? areaColorScaleMin.toHtmlColorString() : null);
		ui.setAreaColorScaleMax(areaColorScaleMax != null ? areaColorScaleMax.toHtmlColorString() : null);

		return ui;
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

	@Override
	public void setChangeListener(LineChartDataDisplayChangeListener listener) {
		this.changeListener = listener;
	}

}
