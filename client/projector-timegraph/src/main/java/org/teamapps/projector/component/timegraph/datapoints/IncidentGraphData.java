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
package org.teamapps.projector.component.timegraph.datapoints;

import org.teamapps.projector.component.timegraph.DtoGraphData;
import org.teamapps.projector.component.timegraph.DtoIncidentGraphData;
import org.teamapps.projector.component.timegraph.DtoIncidentGraphDataPoint;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IncidentGraphData extends org.teamapps.projector.component.timegraph.datapoints.GraphData {

	int size();

	org.teamapps.projector.component.timegraph.datapoints.IncidentGraphDataPoint getDataPoint(int index);

	default Stream<org.teamapps.projector.component.timegraph.datapoints.IncidentGraphDataPoint> streamDataPoints() {
		int[] i = {0};
		return Stream.generate(() -> getDataPoint(i[0]++))
				.limit(size());
	}

	@Override
	default DtoGraphData toUiGraphData() {
		return new DtoIncidentGraphData(getInterval().toUiLongInterval(), streamDataPoints()
				.map(d -> new DtoIncidentGraphDataPoint(d.getX1(), d.getX2(), d.getY(), d.getColor().toHtmlColorString(), d.getTooltipHtml()))
				.collect(Collectors.toList()));
	}
}
