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
package org.teamapps.ux.component.timegraph.graph;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.UiHoseGraph;
import org.teamapps.ux.component.timegraph.LineChartCurveType;
import org.teamapps.ux.component.timegraph.datapoints.HoseGraphData;
import org.teamapps.ux.component.timegraph.model.HoseGraphModel;

public class HoseGraph extends AbstractGraph<HoseGraphData, HoseGraphModel> {

	private LineChartCurveType curveType;
	private float dataDotRadius;
	private Color centerLineColor;
	private Color lowerLineColor;
	private Color upperLineColor;
	private Color areaColor;

	public HoseGraph(HoseGraphModel model) {
		this(model, LineChartCurveType.MONOTONE, 2, Color.fromRgb(73, 128, 192));
	}

	public HoseGraph(HoseGraphModel model, LineChartCurveType curveType, float dataDotRadius, Color centerLineColor) {
		this(model, curveType, dataDotRadius, centerLineColor, null);
	}

	public HoseGraph(HoseGraphModel model, LineChartCurveType curveType, float dataDotRadius, Color centerLineColor, Color areaColor) {
		super(model);
		this.curveType = curveType;
		this.dataDotRadius = dataDotRadius;
		this.centerLineColor = centerLineColor;
		this.lowerLineColor = (centerLineColor instanceof RgbaColor) ? ((RgbaColor) centerLineColor).withAlpha(((RgbaColor) centerLineColor).getAlpha() / 4) : RgbaColor.TRANSPARENT;
		this.upperLineColor = (centerLineColor instanceof RgbaColor) ? ((RgbaColor) centerLineColor).withAlpha(((RgbaColor) centerLineColor).getAlpha() / 4) : RgbaColor.TRANSPARENT;
		this.areaColor = areaColor;
	}

	public UiHoseGraph createUiFormat() {
		UiHoseGraph ui = new UiHoseGraph();
		mapAbstractLineChartDataDisplayProperties(ui);

		ui.setGraphType(curveType.toUiLineChartCurveType());
		ui.setDataDotRadius(dataDotRadius);
		ui.setMiddleLineColor(centerLineColor != null ? centerLineColor.toHtmlColorString() : null);
		ui.setLowerLineColor(lowerLineColor != null ? lowerLineColor.toHtmlColorString() : null);
		ui.setUpperLineColor(upperLineColor != null ? upperLineColor.toHtmlColorString() : null);
		ui.setAreaColor(areaColor != null ? areaColor.toHtmlColorString() : null);

		return ui;
	}

	public LineChartCurveType getCurveType() {
		return curveType;
	}

	public HoseGraph setCurveType(LineChartCurveType curveType) {
		this.curveType = curveType;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public float getDataDotRadius() {
		return dataDotRadius;
	}

	public HoseGraph setDataDotRadius(float dataDotRadius) {
		this.dataDotRadius = dataDotRadius;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getCenterLineColor() {
		return centerLineColor;
	}

	public HoseGraph setCenterLineColor(Color centerLineColor) {
		this.centerLineColor = centerLineColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getAreaColor() {
		return areaColor;
	}

	public HoseGraph setAreaColor(Color areaColor) {
		this.areaColor = areaColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getLowerLineColor() {
		return lowerLineColor;
	}

	public HoseGraph setLowerLineColor(Color lowerLineColor) {
		this.lowerLineColor = lowerLineColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getUpperLineColor() {
		return upperLineColor;
	}

	public HoseGraph setUpperLineColor(Color upperLineColor) {
		this.upperLineColor = upperLineColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}
}
