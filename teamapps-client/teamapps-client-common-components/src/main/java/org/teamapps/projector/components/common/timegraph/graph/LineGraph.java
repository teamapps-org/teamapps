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
package org.teamapps.projector.components.common.timegraph.graph;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.projector.components.common.dto.DtoLineGraph;
import org.teamapps.projector.components.common.timegraph.model.LineGraphModel;
import org.teamapps.projector.components.common.timegraph.LineChartCurveType;
import org.teamapps.projector.components.common.timegraph.datapoints.LineGraphData;

public class LineGraph extends AbstractGraph<LineGraphData, LineGraphModel> {

	private LineChartCurveType graphType;
	private float dataDotRadius;
	private Color lineColorScaleMin;
	private Color lineColorScaleMax;
	private Color areaColorScaleMin;
	private Color areaColorScaleMax;


	public LineGraph(LineGraphModel model) {
		this(model, LineChartCurveType.MONOTONE, 2, new RgbaColor(73, 128, 192));
	}

	public LineGraph(LineGraphModel model, LineChartCurveType graphType, float dataDotRadius, Color lineColor) {
		this(model, graphType, dataDotRadius, lineColor, lineColor, null, null);
	}

	public LineGraph(LineGraphModel model, LineChartCurveType graphType, float dataDotRadius, Color lineColor, Color areaColor) {
		this(model, graphType, dataDotRadius, lineColor, lineColor, (areaColor instanceof RgbaColor) ? ((RgbaColor) areaColor).withAlpha(0.0f) : null, areaColor);
	}

	public LineGraph(LineGraphModel model, LineChartCurveType graphType, float dataDotRadius, Color lineColorScaleMin, Color lineColorScaleMax, Color areaColorScaleMin, Color areaColorScaleMax) {
		super(model);
		this.graphType = graphType;
		this.dataDotRadius = dataDotRadius;
		this.lineColorScaleMin = lineColorScaleMin;
		this.lineColorScaleMax = lineColorScaleMax;
		this.areaColorScaleMin = areaColorScaleMin;
		this.areaColorScaleMax = areaColorScaleMax;
	}

	@Override
	public DtoLineGraph createUiFormat() {
		DtoLineGraph ui = new DtoLineGraph();
		mapAbstractLineChartDataDisplayProperties(ui);
		ui.setGraphType(graphType.toUiLineChartCurveType());
		ui.setDataDotRadius(dataDotRadius);
		ui.setLineColorScaleMin(lineColorScaleMin != null ? lineColorScaleMin.toHtmlColorString() : null);
		ui.setLineColorScaleMax(lineColorScaleMax != null ? lineColorScaleMax.toHtmlColorString() : null);
		ui.setAreaColorScaleMin(areaColorScaleMin != null ? areaColorScaleMin.toHtmlColorString() : null);
		ui.setAreaColorScaleMax(areaColorScaleMax != null ? areaColorScaleMax.toHtmlColorString() : null);
		return ui;
	}

	public LineChartCurveType getGraphType() {
		return graphType;
	}

	public LineGraph setGraphType(LineChartCurveType graphType) {
		this.graphType = graphType;
		fireChange();
		return this;
	}

	public float getDataDotRadius() {
		return dataDotRadius;
	}

	public LineGraph setDataDotRadius(float dataDotRadius) {
		this.dataDotRadius = dataDotRadius;
		fireChange();
		return this;
	}

	public Color getLineColorScaleMin() {
		return lineColorScaleMin;
	}

	public LineGraph setLineColorScaleMin(Color lineColorScaleMin) {
		this.lineColorScaleMin = lineColorScaleMin;
		fireChange();
		return this;
	}

	public Color getLineColorScaleMax() {
		return lineColorScaleMax;
	}

	public LineGraph setLineColorScaleMax(Color lineColorScaleMax) {
		this.lineColorScaleMax = lineColorScaleMax;
		fireChange();
		return this;
	}

	public Color getAreaColorScaleMin() {
		return areaColorScaleMin;
	}

	public LineGraph setAreaColorScaleMin(Color areaColorScaleMin) {
		this.areaColorScaleMin = areaColorScaleMin;
		fireChange();
		return this;
	}

	public Color getAreaColorScaleMax() {
		return areaColorScaleMax;
	}

	public LineGraph setAreaColorScaleMax(Color areaColorScaleMax) {
		this.areaColorScaleMax = areaColorScaleMax;
		fireChange();
		return this;
	}

}
