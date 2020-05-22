/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.teamapps.ux.component.timegraph.partitioning.TimePartitionUnit;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AggregatingTimeGraphModelTest {

	private AggregatingTimeGraphModel model;

	@Before
	public void setUp() throws Exception {
		model = new AggregatingTimeGraphModel(ZoneOffset.UTC);
		model.setDataPoints("line1", new ListLineChartDataPoints(Arrays.asList(
				new LineChartDataPoint(100, 1),
				new LineChartDataPoint(200, 11),
				new LineChartDataPoint(300, 2),
				new LineChartDataPoint(405, 0),
				new LineChartDataPoint(500, 13),
				new LineChartDataPoint(600, 12),
				new LineChartDataPoint(700, 3)
		)));
		model.setAddDataPointBeforeAndAfterQueryResult(false); // for testing
	}

	@Test
	public void testFirstValue() throws Exception {
		model.setAggregationPolicy("line1", AggregatingTimeGraphModel.AggregationPolicy.FIRST_VALUE);

		List<LineChartDataPoint> dataPoints = model.getDataPoints("line1", TimePartitionUnit.YEAR, new Interval(0, 1000)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(0, 1));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(200, 350)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(200, 11));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(300, 400)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(200, 11), new LineChartDataPoint(400, 0));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(500, 600)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(400, 0), new LineChartDataPoint(600, 12));
	}

	@Test
	public void testMin() throws Exception {
		model.setAggregationPolicy("line1", AggregatingTimeGraphModel.AggregationPolicy.MIN);

		List<LineChartDataPoint> dataPoints = model.getDataPoints("line1", TimePartitionUnit.YEAR, new Interval(0, 1000)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(0, 0));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(200, 350)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(200, 2));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(300, 400)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(200, 2), new LineChartDataPoint(400, 0));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(500, 600)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(400, 0), new LineChartDataPoint(600, 3));
	}

	@Test
	public void testMax() throws Exception {
		model.setAggregationPolicy("line1", AggregatingTimeGraphModel.AggregationPolicy.MAX);

		List<LineChartDataPoint> dataPoints = model.getDataPoints("line1", TimePartitionUnit.YEAR, new Interval(0, 1000)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(0, 13));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(400, 550)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(400, 13));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(300, 400)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(200, 11), new LineChartDataPoint(400, 13));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(500, 600)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(400, 13), new LineChartDataPoint(600, 12));
	}

	@Test
	public void testAverage() throws Exception {
		model.setAggregationPolicy("line1", AggregatingTimeGraphModel.AggregationPolicy.AVERAGE);

		List<LineChartDataPoint> dataPoints = model.getDataPoints("line1", TimePartitionUnit.YEAR, new Interval(0, 1000)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(0, 6));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(400, 550)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(400, 6.5));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(300, 400)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(200, 6.5), new LineChartDataPoint(400, 6.5));

		dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(500, 600)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(new LineChartDataPoint(400, 6.5), new LineChartDataPoint(600, 7.5));
	}

	@Test
	public void testAddDataPointBeforeAndAfterQueryResult() throws Exception {
		model.setAggregationPolicy("line1", AggregatingTimeGraphModel.AggregationPolicy.FIRST_VALUE);
		model.setAddDataPointBeforeAndAfterQueryResult(true);

		List<LineChartDataPoint> dataPoints = model.getDataPoints("line1", TimePartitionUnit.MILLISECOND_200, new Interval(400, 550)).streamDataPoints().collect(Collectors.toList());;
		Assertions.assertThat(dataPoints).containsExactly(
				new LineChartDataPoint(200, 11),
				new LineChartDataPoint(400, 0),
				new LineChartDataPoint(600, 12)
		);

	}
}
