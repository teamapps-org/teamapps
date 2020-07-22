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

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiLineChartBand;

import java.util.ArrayList;
import java.util.List;

public class LineChartBand extends AbstractLineChartDataDisplay {

	private final String upperBoundDataSeriesId;
	private final String middleLineDataSeriesId;
	private final String lowerBoundDataSeriesId;

	private LineChartCurveType curveType;
	private float dataDotRadius;
	private Color centerLineColor;
	private Color lowerLineColor;
	private Color upperLineColor;
	private Color areaColor;

	public LineChartBand(String upperBoundDataSeriesId, String middleLineDataSeriesId, String lowerBoundDataSeriesId) {
		this(upperBoundDataSeriesId, middleLineDataSeriesId, lowerBoundDataSeriesId, LineChartCurveType.MONOTONE, 2, new Color(73, 128, 192));
	}

	public LineChartBand(String upperBoundDataSeriesId, String middleLineDataSeriesId, String lowerBoundDataSeriesId, LineChartCurveType curveType, float dataDotRadius, Color centerLineColor) {
		this(upperBoundDataSeriesId, middleLineDataSeriesId, lowerBoundDataSeriesId, curveType, dataDotRadius, centerLineColor, null);
	}

	public LineChartBand(String upperBoundDataSeriesId, String middleLineDataSeriesId, String lowerBoundDataSeriesId, LineChartCurveType curveType, float dataDotRadius, Color centerLineColor,
	                     Color areaColor) {
		this.upperBoundDataSeriesId = upperBoundDataSeriesId;
		this.middleLineDataSeriesId = middleLineDataSeriesId;
		this.lowerBoundDataSeriesId = lowerBoundDataSeriesId;
		this.curveType = curveType;
		this.dataDotRadius = dataDotRadius;
		this.centerLineColor = centerLineColor;
		this.lowerLineColor = centerLineColor.withAlpha(centerLineColor.getAlpha() / 4);
		this.upperLineColor = centerLineColor.withAlpha(centerLineColor.getAlpha() / 4);
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
		ui.setMiddleLineColor(centerLineColor != null ? centerLineColor.toHtmlColorString() : null);
		ui.setLowerLineColor(lowerLineColor != null ? lowerLineColor.toHtmlColorString() : null);
		ui.setUpperLineColor(upperLineColor != null ? upperLineColor.toHtmlColorString() : null);
		ui.setAreaColor(areaColor != null ? areaColor.toHtmlColorString() : null);

		return ui;
	}

	@Override
	public List<String> getDataSeriesIds() {
		ArrayList<String> dataSeriesIds = new ArrayList<>();
		if (upperBoundDataSeriesId != null) {
			dataSeriesIds.add(upperBoundDataSeriesId);
		}
		if (middleLineDataSeriesId != null) {
			dataSeriesIds.add(middleLineDataSeriesId);
		}
		if (lowerBoundDataSeriesId != null) {
			dataSeriesIds.add(lowerBoundDataSeriesId);
		}
		return dataSeriesIds;
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

	public Color getCenterLineColor() {
		return centerLineColor;
	}

	public LineChartBand setCenterLineColor(Color centerLineColor) {
		this.centerLineColor = centerLineColor;
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

	public Color getLowerLineColor() {
		return lowerLineColor;
	}

	public LineChartBand setLowerLineColor(Color lowerLineColor) {
		this.lowerLineColor = lowerLineColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getUpperLineColor() {
		return upperLineColor;
	}

	public LineChartBand setUpperLineColor(Color upperLineColor) {
		this.upperLineColor = upperLineColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}
}
