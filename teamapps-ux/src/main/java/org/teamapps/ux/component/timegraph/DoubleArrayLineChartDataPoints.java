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
package org.teamapps.ux.component.timegraph;

public class DoubleArrayLineChartDataPoints implements LineChartDataPoints {

	private double[] x;
	private double[] y;

	public DoubleArrayLineChartDataPoints(double[] x, double[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("Arrays must have the same length!");
		}
		this.x = x;
		this.y = y;
	}

	@Override
	public int size() {
		return x.length;
	}

	@Override
	public double getX(int index) {
		return x[index];
	}

	@Override
	public double getY(int index) {
		return y[index];
	}

	@Override
	public LineChartDataPoint getDataPoint(int index) {
		return new LineChartDataPoint(x[index], y[index]);
	}
	
}
