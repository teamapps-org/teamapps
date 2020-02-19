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
package org.teamapps.ux.component.charting.pie;

import org.teamapps.dto.UiChartNamedDataPoint;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiPieChart;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.charting.ChartLegendStyle;
import org.teamapps.ux.component.charting.DataPointWeighting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PieChart extends AbstractComponent {

	public final Event<ChartNamedDataPoint> onDataPointClicked = new Event<>();

	private long animationDuration = 500;
	private ChartLegendStyle legendStyle;
	private DataPointWeighting dataPointWeighting = DataPointWeighting.RELATIVE;
	private float rotation3D = 90;
	private float height3D = 0;
	private float rotationClockwise = 0;
	private float innerRadiusProportion = 0;
	private final List<ChartNamedDataPoint> dataPoints = new ArrayList<>();

	public PieChart() {
	}

	public PieChart(List<ChartNamedDataPoint> dataPoints) {
		this.dataPoints.addAll(dataPoints);
	}

	@Override
	public UiComponent createUiComponent() {
		UiPieChart uiPieChart = new UiPieChart();
		mapAbstractUiComponentProperties(uiPieChart);
		uiPieChart.setDataPointWeighting(dataPointWeighting.toUiDataPointWeighting());
		uiPieChart.setRotation3D(rotation3D);
		uiPieChart.setHeight3D(height3D);
		uiPieChart.setRotationClockwise(rotationClockwise);
		uiPieChart.setInnerRadiusProportion(innerRadiusProportion);
		uiPieChart.setDataPoints(createUiDataPoints());
		return uiPieChart;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_PIE_CHART_DATA_POINT_CLICKED:
				UiPieChart.DataPointClickedEvent clickEvent = (UiPieChart.DataPointClickedEvent) event;
				dataPoints.stream()
						.filter(p -> Objects.equals(p.getName(), clickEvent.getDataPointName()))
						.findFirst()
						.ifPresent(onDataPointClicked::fire);
				break;
		}
	}

	private List<UiChartNamedDataPoint> createUiDataPoints() {
		return dataPoints != null ? dataPoints.stream()
				.map(ChartNamedDataPoint::createUiChartNamedDataPoint)
				.collect(Collectors.toList()) : Collections.emptyList();
	}

	public long getAnimationDuration() {
		return animationDuration;
	}

	public void setAnimationDuration(long animationDuration) {
		this.animationDuration = animationDuration;
	}

	public ChartLegendStyle getLegendStyle() {
		return legendStyle;
	}

	public void setLegendStyle(ChartLegendStyle legendStyle) {
		this.legendStyle = legendStyle;
		queueCommandIfRendered(() -> new UiPieChart.SetLegendStyleCommand(getId(), legendStyle.toUiChartLegendStyle()));
	}

	public DataPointWeighting getDataPointWeighting() {
		return dataPointWeighting;
	}

	public void setDataPointWeighting(DataPointWeighting dataPointWeighting) {
		this.dataPointWeighting = dataPointWeighting;
		queueCommandIfRendered(() -> new UiPieChart.SetDataPointWeightingCommand(getId(), dataPointWeighting.toUiDataPointWeighting()));
	}

	public float getRotation3D() {
		return rotation3D;
	}

	public void setRotation3D(float rotation3D) {
		this.rotation3D = rotation3D;
		queueCommandIfRendered(() -> new UiPieChart.SetRotation3DCommand(getId(), rotation3D));
	}

	public float getHeight3D() {
		return height3D;
	}

	public void setHeight3D(float height3D) {
		this.height3D = height3D;
		queueCommandIfRendered(() -> new UiPieChart.SetHeight3DCommand(getId(), height3D));
	}

	public float getRotationClockwise() {
		return rotationClockwise;
	}

	public void setRotationClockwise(float rotationClockwise) {
		this.rotationClockwise = rotationClockwise;
		queueCommandIfRendered(() -> new UiPieChart.SetRotationClockwiseCommand(getId(), rotationClockwise));
	}

	public float getInnerRadiusProportion() {
		return innerRadiusProportion;
	}

	public void setInnerRadiusProportion(float innerRadiusProportion) {
		this.innerRadiusProportion = innerRadiusProportion;
		queueCommandIfRendered(() -> new UiPieChart.SetInnerRadiusProportionCommand(getId(), innerRadiusProportion));
	}

	public List<ChartNamedDataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<ChartNamedDataPoint> dataPoints) {
		this.dataPoints.clear();
		this.dataPoints.addAll(dataPoints);
		queueCommandIfRendered(() -> new UiPieChart.SetDataPointsCommand(getId(), createUiDataPoints(), animationDuration));
	}
}
