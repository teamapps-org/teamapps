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
package org.teamapps.projector.component.chart.pie;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.chart.ChartLibrary;
import org.teamapps.projector.event.ProjectorEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ClientObjectLibrary(ChartLibrary.class)
public class PieChart extends AbstractComponent implements DtoPieChartEventHandler {

	private final DtoPieChartClientObjectChannel clientObjectChannel = new DtoPieChartClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<NamedDataPoint> onDataPointClicked = new ProjectorEvent<>(clientObjectChannel::toggleDataPointClickedEvent);

	private long animationDuration = 500;
	private ChartLegendStyle legendStyle;
	private DataPointWeighting dataPointWeighting = DataPointWeighting.RELATIVE;
	private float rotation3D = 90;
	private float height3D = 0;
	private float rotationClockwise = 0;
	private float innerRadiusProportion = 0;
	private final List<NamedDataPoint> dataPoints = new ArrayList<>();

	public PieChart() {
	}

	public PieChart(List<NamedDataPoint> dataPoints) {
		this.dataPoints.addAll(dataPoints);
	}

	@Override
	public DtoComponent createConfig() {
		DtoPieChart uiPieChart = new DtoPieChart();
		mapAbstractConfigProperties(uiPieChart);
		uiPieChart.setDataPointWeighting(dataPointWeighting);
		uiPieChart.setRotation3D(rotation3D);
		uiPieChart.setHeight3D(height3D);
		uiPieChart.setRotationClockwise(rotationClockwise);
		uiPieChart.setInnerRadiusProportion(innerRadiusProportion);
		uiPieChart.setDataPoints(createDtoDataPoints());
		return uiPieChart;
	}

	@Override
	public void handleDataPointClicked(String dataPointName) {
		dataPoints.stream()
				.filter(p -> Objects.equals(p.getName(), dataPointName))
				.findFirst()
				.ifPresent(onDataPointClicked::fire);
	}

	private List<DtoChartNamedDataPoint> createDtoDataPoints() {
		return dataPoints != null ? dataPoints.stream()
				.map(NamedDataPoint::createDtoChartNamedDataPoint)
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
		clientObjectChannel.setLegendStyle(legendStyle);
	}

	public DataPointWeighting getDataPointWeighting() {
		return dataPointWeighting;
	}

	public void setDataPointWeighting(DataPointWeighting dataPointWeighting) {
		this.dataPointWeighting = dataPointWeighting;
		clientObjectChannel.setDataPointWeighting(dataPointWeighting);
	}

	public float getRotation3D() {
		return rotation3D;
	}

	public void setRotation3D(float rotation3D) {
		this.rotation3D = rotation3D;
		clientObjectChannel.setRotation3D(rotation3D);
	}

	public float getHeight3D() {
		return height3D;
	}

	public void setHeight3D(float height3D) {
		this.height3D = height3D;
		clientObjectChannel.setHeight3D(height3D);
	}

	public float getRotationClockwise() {
		return rotationClockwise;
	}

	public void setRotationClockwise(float rotationClockwise) {
		this.rotationClockwise = rotationClockwise;
		clientObjectChannel.setRotationClockwise(rotationClockwise);
	}

	public float getInnerRadiusProportion() {
		return innerRadiusProportion;
	}

	public void setInnerRadiusProportion(float innerRadiusProportion) {
		this.innerRadiusProportion = innerRadiusProportion;
		clientObjectChannel.setInnerRadiusProportion(innerRadiusProportion);
	}

	public List<NamedDataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<NamedDataPoint> dataPoints) {
		this.dataPoints.clear();
		this.dataPoints.addAll(dataPoints);
		clientObjectChannel.setDataPoints(createDtoDataPoints(), animationDuration);
	}
}
