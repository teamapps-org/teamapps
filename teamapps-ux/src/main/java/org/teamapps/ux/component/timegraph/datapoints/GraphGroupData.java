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
package org.teamapps.ux.component.timegraph.datapoints;

import org.teamapps.dto.DtoGraphData;
import org.teamapps.dto.DtoGraphGroupData;
import org.teamapps.ux.component.timegraph.Interval;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface GraphGroupData extends GraphData {

	Map<String, GraphData> getGraphData();

	default Interval getInterval() {
		return getGraphData().values().stream()
				.map(GraphData::getInterval)
				.reduce(Interval::intersection)
				.orElse(Interval.empty());
	}

	@Override
	default DtoGraphGroupData toUiGraphData() {
		final Map<String, DtoGraphData> uiGraphDataMap = getGraphData().entrySet().stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toUiGraphData()));
		return new DtoGraphGroupData(getInterval().toUiLongInterval(), uiGraphDataMap);
	}
}
