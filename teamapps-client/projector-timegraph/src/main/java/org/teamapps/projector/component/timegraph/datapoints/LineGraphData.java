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

import org.teamapps.projector.component.timegraph.DtoLineGraphData;
import org.teamapps.projector.component.timegraph.DtoLineGraphDataPoint;

import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public interface LineGraphData extends GraphData {

	int size();

	double getX(int index);

	double getY(int index);

	LineGraphDataPoint getDataPoint(int index);

	default DoubleStream streamX() {
		int[] i = {0};
		return DoubleStream.generate(() -> getX(i[0]++))
				.limit(size());
	}

	default DoubleStream streamY() {
		int[] i = {0};
		return DoubleStream.generate(() -> getX(i[0]++))
				.limit(size());
	}

	default Stream<LineGraphDataPoint> streamDataPoints() {
		int[] i = {0};
		return Stream.generate(() -> getDataPoint(i[0]++))
				.limit(size());
	}

	@Override
	default DtoLineGraphData toUiGraphData() {
		return new DtoLineGraphData(getInterval().toUiLongInterval(), streamDataPoints()
				.map(dp -> new DtoLineGraphDataPoint(dp.getX(), dp.getY()))
				.collect(Collectors.toList()));
	}
}
