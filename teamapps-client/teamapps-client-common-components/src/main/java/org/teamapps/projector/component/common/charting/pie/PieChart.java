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
package org.teamapps.projector.component.common.charting.pie;

import org.teamapps.projector.component.common.dto.DtoChartNamedDataPoint;
import org.teamapps.projector.component.common.dto.DtoComponent;
import org.teamapps.projector.component.common.dto.DtoPieChart;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.projector.component.common.charting.ChartLegendStyle;
import org.teamapps.projector.component.common.charting.DataPointWeighting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PieChart extends AbstractComponent {

	public final ProjectorEvent<ChartNamedDataPoint> onDataPointClicked = new ProjectorEvent<>(clientObjectChannel::toggleDataPointClickedEvent);

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
	public DtoComponent createDto() {
		DtoPieChart uiPieChart = new DtoPieChart();
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
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoPieChart.DataPointClickedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoPieChart.DataPointClickedEventWrapper.class);
				dataPoints.stream()
						.filter(p -> Objects.equals(p.getName(), clickEvent.getDataPointName()))
						.findFirst()
						.ifPresent(onDataPointClicked::fire);
			}
		}
	}

	private List<DtoChartNamedDataPoint> createUiDataPoints() {
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
		clientObjectChannel.setLegendStyle(LegendStyle.ToUiChartLegendStyle());
	}

	public DataPointWeighting getDataPointWeighting() {
		return dataPointWeighting;
	}

	public void setDataPointWeighting(DataPointWeighting dataPointWeighting) {
		this.dataPointWeighting = dataPointWeighting;
		clientObjectChannel.setDataPointWeighting(DataPointWeighting.ToUiDataPointWeighting());
	}

	public float getRotation3D() {
		return rotation3D;
	}

	public void setRotation3D(float rotation3D) {
		this.rotation3D = rotation3D;
		clientObjectChannel.setRotation3D(Rotation3D);
	}

	public float getHeight3D() {
		return height3D;
	}

	public void setHeight3D(float height3D) {
		this.height3D = height3D;
		clientObjectChannel.setHeight3D(Height3D);
	}

	public float getRotationClockwise() {
		return rotationClockwise;
	}

	public void setRotationClockwise(float rotationClockwise) {
		this.rotationClockwise = rotationClockwise;
		clientObjectChannel.setRotationClockwise(RotationClockwise);
	}

	public float getInnerRadiusProportion() {
		return innerRadiusProportion;
	}

	public void setInnerRadiusProportion(float innerRadiusProportion) {
		this.innerRadiusProportion = innerRadiusProportion;
		clientObjectChannel.setInnerRadiusProportion(InnerRadiusProportion);
	}

	public List<ChartNamedDataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<ChartNamedDataPoint> dataPoints) {
		this.dataPoints.clear();
		this.dataPoints.addAll(dataPoints);
		clientObjectChannel.setDataPoints(CreateUiDataPoints(), AnimationDuration);
	}
}
