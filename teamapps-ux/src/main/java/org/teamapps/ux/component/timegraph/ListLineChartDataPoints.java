/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.timegraph;

import java.util.List;

public class ListLineChartDataPoints implements LineChartDataPoints {

	private final List<LineChartDataPoint> dataPoints;

	public ListLineChartDataPoints(List<LineChartDataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}

	@Override
	public int size() {
		return dataPoints.size();
	}

	@Override
	public double getX(int index) {
		return dataPoints.get(index).getX();
	}

	@Override
	public double getY(int index) {
		return dataPoints.get(index).getY();
	}

	@Override
	public LineChartDataPoint getDataPoint(int index) {
		return dataPoints.get(index);
	}

}
