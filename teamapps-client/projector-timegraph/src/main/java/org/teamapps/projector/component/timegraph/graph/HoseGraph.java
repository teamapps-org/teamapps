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
package org.teamapps.projector.component.timegraph.graph;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.projector.component.timegraph.DtoHoseGraph;
import org.teamapps.projector.component.timegraph.LineChartCurveType;
import org.teamapps.projector.component.timegraph.datapoints.HoseGraphData;
import org.teamapps.projector.component.timegraph.model.HoseGraphModel;

public class HoseGraph extends AbstractGraph<HoseGraphData, HoseGraphModel> {

	private LineChartCurveType curveType;
	private float dataDotRadius;
	private Color centerLineColor;
	private Color lowerLineColor;
	private Color upperLineColor;
	private Color areaColor;
	private boolean stripedArea;

	public HoseGraph(HoseGraphModel model) {
		this(model, LineChartCurveType.MONOTONE, 2, Color.fromRgb(73, 128, 192));
	}

	public HoseGraph(HoseGraphModel model, LineChartCurveType curveType, float dataDotRadius, Color centerLineColor) {
		this(model, curveType, dataDotRadius, centerLineColor,
				(centerLineColor instanceof RgbaColor) ? ((RgbaColor) centerLineColor).withAlpha(((RgbaColor) centerLineColor).getAlpha() / 5) : RgbaColor.TRANSPARENT);
	}

	public HoseGraph(HoseGraphModel model, LineChartCurveType curveType, float dataDotRadius, Color centerLineColor, Color areaColor) {
		this(model, curveType, dataDotRadius, centerLineColor, areaColor, false);
	}

	public HoseGraph(HoseGraphModel model, LineChartCurveType curveType, float dataDotRadius, Color centerLineColor, Color areaColor, boolean stripedArea) {
		super(model);
		this.curveType = curveType;
		this.dataDotRadius = dataDotRadius;
		this.centerLineColor = centerLineColor;
		this.lowerLineColor = (centerLineColor instanceof RgbaColor) ? ((RgbaColor) centerLineColor).withAlpha(((RgbaColor) centerLineColor).getAlpha() / 5) : RgbaColor.TRANSPARENT;
		this.upperLineColor = (centerLineColor instanceof RgbaColor) ? ((RgbaColor) centerLineColor).withAlpha(((RgbaColor) centerLineColor).getAlpha() / 5) : RgbaColor.TRANSPARENT;
		this.areaColor = areaColor;
		this.stripedArea = stripedArea;
	}

	public DtoHoseGraph createUiFormat() {
		DtoHoseGraph ui = new DtoHoseGraph();
		mapAbstractLineChartDataDisplayProperties(ui);

		ui.setGraphType(curveType);
		ui.setDataDotRadius(dataDotRadius);
		ui.setMiddleLineColor(centerLineColor != null ? centerLineColor.toHtmlColorString() : null);
		ui.setLowerLineColor(lowerLineColor != null ? lowerLineColor.toHtmlColorString() : null);
		ui.setUpperLineColor(upperLineColor != null ? upperLineColor.toHtmlColorString() : null);
		ui.setAreaColor(areaColor != null ? areaColor.toHtmlColorString() : null);
		ui.setStripedArea(stripedArea);

		return ui;
	}

	public LineChartCurveType getCurveType() {
		return curveType;
	}

	public HoseGraph setCurveType(LineChartCurveType curveType) {
		this.curveType = curveType;
		fireChange();
		return this;
	}

	public float getDataDotRadius() {
		return dataDotRadius;
	}

	public HoseGraph setDataDotRadius(float dataDotRadius) {
		this.dataDotRadius = dataDotRadius;
		fireChange();
		return this;
	}

	public Color getCenterLineColor() {
		return centerLineColor;
	}

	public HoseGraph setCenterLineColor(Color centerLineColor) {
		this.centerLineColor = centerLineColor;
		fireChange();
		return this;
	}

	public Color getAreaColor() {
		return areaColor;
	}

	public HoseGraph setAreaColor(Color areaColor) {
		this.areaColor = areaColor;
		fireChange();
		return this;
	}

	public Color getLowerLineColor() {
		return lowerLineColor;
	}

	public HoseGraph setLowerLineColor(Color lowerLineColor) {
		this.lowerLineColor = lowerLineColor;
		fireChange();
		return this;
	}

	public Color getUpperLineColor() {
		return upperLineColor;
	}

	public HoseGraph setUpperLineColor(Color upperLineColor) {
		this.upperLineColor = upperLineColor;
		fireChange();
		return this;
	}
}
