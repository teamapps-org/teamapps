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
import org.teamapps.projector.component.timegraph.DtoGraphGroupData;
import org.teamapps.projector.component.timegraph.Interval;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface GraphGroupData extends org.teamapps.projector.component.timegraph.datapoints.GraphData {

	Map<String, org.teamapps.projector.component.timegraph.datapoints.GraphData> getGraphData();

	default Interval getInterval() {
		return getGraphData().values().stream()
				.map(org.teamapps.projector.component.timegraph.datapoints.GraphData::getInterval)
				.reduce(Interval::intersection)
				.orElse(Interval.empty());
	}

	@Override
	default DtoGraphGroupData toDtoGraphData() {
		final Map<String, DtoGraphData> uiGraphDataMap = getGraphData().entrySet().stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toDtoGraphData()));
		return new DtoGraphGroupData(getInterval().toDtoLongInterval(), uiGraphDataMap);
	}
}
