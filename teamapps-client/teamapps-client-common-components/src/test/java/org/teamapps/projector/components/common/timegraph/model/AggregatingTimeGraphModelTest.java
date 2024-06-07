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
package org.teamapps.projector.components.timegraph.model;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.teamapps.projector.components.timegraph.Interval;
import org.teamapps.projector.components.timegraph.TimePartitioningUnit;
import org.teamapps.projector.components.timegraph.datapoints.LineGraphDataPoint;
import org.teamapps.projector.components.timegraph.datapoints.ListLineGraphData;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AggregatingTimeGraphModelTest {

	private AggregatingLineGraphModel model;

	@Before
	public void setUp() throws Exception {
		model = new AggregatingLineGraphModel();
		model.setGraphData(new ListLineGraphData(Arrays.asList(
				new LineGraphDataPoint(100, 1),
				new LineGraphDataPoint(200, 11),
				new LineGraphDataPoint(300, 2),
				new LineGraphDataPoint(405, 0),
				new LineGraphDataPoint(500, 13),
				new LineGraphDataPoint(600, 12),
				new LineGraphDataPoint(700, 3)
		), new Interval(100, 800)));
	}

	@Test
	public void testFirstValue() throws Exception {
		model.setAggregationPolicy(AggregationType.FIRST_VALUE);
		model.setAddDataPointBeforeAndAfterQueryResult(false); // for testing

		List<LineGraphDataPoint> dataPoints = model.getData(TimePartitioningUnit.YEAR, ZoneOffset.UTC, new Interval(0, 1000), new Interval(0, 1000)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(0, 1));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(200, 350), new Interval(200, 350)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(200, 11));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(300, 401), new Interval(300, 401)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(200, 11), new LineGraphDataPoint(400, 0));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(500, 601), new Interval(500, 601)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(400, 0), new LineGraphDataPoint(600, 12));
	}

	@Test
	public void testMin() throws Exception {
		model.setAggregationPolicy(AggregationType.MIN);
		model.setAddDataPointBeforeAndAfterQueryResult(false); // for testing

		List<LineGraphDataPoint> dataPoints = model.getData(TimePartitioningUnit.YEAR, ZoneOffset.UTC, new Interval(0, 1000), new Interval(0, 1000)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(0, 0));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(200, 350), new Interval(200, 350)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(200, 2));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(300, 401), new Interval(300, 401)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(200, 2), new LineGraphDataPoint(400, 0));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(500, 601), new Interval(500, 601)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(400, 0), new LineGraphDataPoint(600, 3));
	}

	@Test
	public void testMax() throws Exception {
		model.setAggregationPolicy(AggregationType.MAX);
		model.setAddDataPointBeforeAndAfterQueryResult(false); // for testing

		List<LineGraphDataPoint> dataPoints = model.getData(TimePartitioningUnit.YEAR, ZoneOffset.UTC, new Interval(0, 1000), new Interval(0, 1000)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(0, 13));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(400, 550), new Interval(400, 550)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(400, 13));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(300, 500), new Interval(300, 500)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(200, 11), new LineGraphDataPoint(400, 13));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(500, 700), new Interval(500, 700)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(400, 13), new LineGraphDataPoint(600, 12));
	}

	@Test
	public void testAverage() throws Exception {
		model.setAggregationPolicy(AggregationType.AVERAGE);
		model.setAddDataPointBeforeAndAfterQueryResult(false); // for testing

		List<LineGraphDataPoint> dataPoints = model.getData(TimePartitioningUnit.YEAR, ZoneOffset.UTC, new Interval(0, 1000), new Interval(0, 1000)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(0, 6));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(400, 550), new Interval(400, 550)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(400, 6.5));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(300, 401), new Interval(300, 401)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(200, 6.5), new LineGraphDataPoint(400, 6.5));

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(500, 601), new Interval(500, 601)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(new LineGraphDataPoint(400, 6.5), new LineGraphDataPoint(600, 7.5));
	}

	@Test
	public void testAddDataPointBeforeAndAfterQueryResult() throws Exception {
		model.setAggregationPolicy(AggregationType.FIRST_VALUE);
		model.setAddDataPointBeforeAndAfterQueryResult(true);

		List<LineGraphDataPoint> dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(450, 550), new Interval(450, 550)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(
				new LineGraphDataPoint(200, 11),
				new LineGraphDataPoint(400, 0),
				new LineGraphDataPoint(600, 12)
		);

		dataPoints = model.getData(TimePartitioningUnit.MILLISECOND_200, ZoneOffset.UTC, new Interval(400, 600), new Interval(400, 600)).streamDataPoints().collect(Collectors.toList());
		Assertions.assertThat(dataPoints).containsExactly(
				new LineGraphDataPoint(200, 11),
				new LineGraphDataPoint(400, 0),
				new LineGraphDataPoint(600, 12)
		);
	}
}
