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
package org.teamapps.projector.components.timegraph.datapoints;

import org.teamapps.common.format.Color;

public class IncidentGraphDataPoint {

	private final double x1;
	private final double x2;
	private final double y;
	private final Color color;
	private final String tooltipHtml;

	public IncidentGraphDataPoint(double x1, double x2, double y, Color color, String tooltipHtml) {
		this.x1 = x1;
		this.x2 = x2;
		this.y = y;
		this.color = color;
		this.tooltipHtml = tooltipHtml;
	}

	public IncidentGraphDataPoint(double x1, double x2, double y, Color color) {
		this(x1, x2, y, color, null);
	}

	public double getX1() {
		return x1;
	}

	public double getX2() {
		return x2;
	}

	public double getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public String getTooltipHtml() {
		return tooltipHtml;
	}
}
