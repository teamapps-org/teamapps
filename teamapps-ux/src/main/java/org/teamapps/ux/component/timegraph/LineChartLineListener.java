/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

public interface LineChartLineListener {
	void handleGraphTypeChanged(LineChartLine lineChartLine, LineChartCurveType graphType);

	void handleDataDotRadiusChanged(LineChartLine lineChartLine, float dataDotRadius);

	void handleLineColorScaleMinChanged(LineChartLine lineChartLine, Color lineColorScaleMin);

	void handleLineColorScaleMaxChanged(LineChartLine lineChartLine, Color lineColorScaleMax);

	void handleAreaColorScaleMinChanged(LineChartLine lineChartLine, Color areaColorScaleMin);

	void handleAreaColorScaleMaxChanged(LineChartLine lineChartLine, Color areaColorScaleMax);

	void handleYAxisColorChanged(LineChartLine lineChartLine, Color yAxisColor);

	void handleIntervalYChanged(LineChartLine lineChartLine, Interval intervalY);

	void handleYScaleTypeChanged(LineChartLine lineChartLine, ScaleType yScaleType);

	void handleYScaleZoomModeChanged(LineChartLine lineChartLine, LineChartYScaleZoomMode yScaleZoomMode);
}
