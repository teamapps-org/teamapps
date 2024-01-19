/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.ux.component.timegraph.partitioning;

import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.teamapps.ux.component.timegraph.TimePartitioningUnit.*;

public class TimePartitioningUnitTest {

	private static final ZonedDateTime TEST_TIME_UNALIGNED = ZonedDateTime.of(2020, 7, 17, 17, 47, 47, 777777777, ZoneOffset.UTC);
	private static final ZonedDateTime TEST_TIME_INCREMENT = ZonedDateTime.of(2020, 12, 31, 23, 59, 59, 999000000, ZoneOffset.UTC);
	private static final ZonedDateTime TEST_TIME_DECREMENT = ZonedDateTime.of(2020, 01, 01, 00, 00, 00, 0, ZoneOffset.UTC);

	@Test public void testGetPartition_MILLISECOND() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.777Z"), MILLISECOND.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MILLISECOND_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.776Z"), MILLISECOND_2.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MILLISECOND_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.775Z"), MILLISECOND_5.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MILLISECOND_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.770Z"), MILLISECOND_10.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MILLISECOND_20() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.760Z"), MILLISECOND_20.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MILLISECOND_50() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.750Z"), MILLISECOND_50.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MILLISECOND_100() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.700Z"), MILLISECOND_100.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MILLISECOND_200() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.600Z"), MILLISECOND_200.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MILLISECOND_500() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.500Z"), MILLISECOND_500.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_SECOND() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:47.000Z"), SECOND.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_SECONDS_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:46.000Z"), SECONDS_2.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_SECONDS_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:45.000Z"), SECONDS_5.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_SECONDS_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:40.000Z"), SECONDS_10.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_SECONDS_15() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:45.000Z"), SECONDS_15.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_SECONDS_30() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:30.000Z"), SECONDS_30.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MINUTE() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:47:00.000Z"), MINUTE.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MINUTES_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:46:00.000Z"), MINUTES_2.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MINUTES_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:45:00.000Z"), MINUTES_5.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MINUTES_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:40:00.000Z"), MINUTES_10.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MINUTES_15() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:45:00.000Z"), MINUTES_15.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MINUTES_30() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:30:00.000Z"), MINUTES_30.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_HOUR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T17:00:00.000Z"), HOUR.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_HOURS_6() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T12:00:00.000Z"), HOURS_6.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_HOURS_12() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T12:00:00.000Z"), HOURS_12.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_DAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-17T00:00:00.000Z"), DAY.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_WEEK_SUNDAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-12T00:00:00.000Z"), WEEK_SUNDAY.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_WEEK_MONDAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-13T00:00:00.000Z"), WEEK_MONDAY.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_MONTH() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-01T00:00:00.000Z"), MONTH.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_QUARTER() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-01T00:00:00.000Z"), QUARTER.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_HALF_YEAR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-07-01T00:00:00.000Z"), HALF_YEAR.getPartitionStart(TEST_TIME_UNALIGNED));  }
	@Test public void testGetPartition_YEAR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2020-01-01T00:00:00.000Z"), YEAR.getPartitionStart(TEST_TIME_UNALIGNED));  }

	@Test public void testIncrement_MILLISECOND() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.000Z"), MILLISECOND.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MILLISECOND_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.001Z"), MILLISECOND_2.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MILLISECOND_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.004Z"), MILLISECOND_5.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MILLISECOND_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.009Z"), MILLISECOND_10.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MILLISECOND_20() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.019Z"), MILLISECOND_20.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MILLISECOND_50() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.049Z"), MILLISECOND_50.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MILLISECOND_100() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.099Z"), MILLISECOND_100.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MILLISECOND_200() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.199Z"), MILLISECOND_200.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MILLISECOND_500() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.499Z"), MILLISECOND_500.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_SECOND() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:00.999Z"), SECOND.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_SECONDS_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:01.999Z"), SECONDS_2.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_SECONDS_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:04.999Z"), SECONDS_5.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_SECONDS_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:09.999Z"), SECONDS_10.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_SECONDS_15() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:14.999Z"), SECONDS_15.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_SECONDS_30() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:29.999Z"), SECONDS_30.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MINUTE() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:00:59.999Z"), MINUTE.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MINUTES_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:01:59.999Z"), MINUTES_2.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MINUTES_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:04:59.999Z"), MINUTES_5.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MINUTES_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:09:59.999Z"), MINUTES_10.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MINUTES_15() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:14:59.999Z"), MINUTES_15.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MINUTES_30() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:29:59.999Z"), MINUTES_30.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_HOUR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T00:59:59.999Z"), HOUR.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_HOURS_6() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T05:59:59.999Z"), HOURS_6.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_HOURS_12() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T11:59:59.999Z"), HOURS_12.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_DAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-01T23:59:59.999Z"), DAY.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_WEEK_SUNDAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-07T23:59:59.999Z"), WEEK_SUNDAY.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_WEEK_MONDAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-07T23:59:59.999Z"), WEEK_MONDAY.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_MONTH() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-01-31T23:59:59.999Z"), MONTH.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_QUARTER() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-03-31T23:59:59.999Z"), QUARTER.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_HALF_YEAR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-06-30T23:59:59.999Z"), HALF_YEAR.increment(TEST_TIME_INCREMENT));  }
	@Test public void testIncrement_YEAR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2021-12-31T23:59:59.999Z"), YEAR.increment(TEST_TIME_INCREMENT));  }

	@Test public void testDecrement_MILLISECOND() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.999Z"), MILLISECOND.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MILLISECOND_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.998Z"), MILLISECOND_2.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MILLISECOND_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.995Z"), MILLISECOND_5.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MILLISECOND_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.990Z"), MILLISECOND_10.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MILLISECOND_20() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.980Z"), MILLISECOND_20.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MILLISECOND_50() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.950Z"), MILLISECOND_50.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MILLISECOND_100() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.900Z"), MILLISECOND_100.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MILLISECOND_200() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.800Z"), MILLISECOND_200.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MILLISECOND_500() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.500Z"), MILLISECOND_500.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_SECOND() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:59.000Z"), SECOND.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_SECONDS_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:58.000Z"), SECONDS_2.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_SECONDS_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:55.000Z"), SECONDS_5.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_SECONDS_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:50.000Z"), SECONDS_10.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_SECONDS_15() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:45.000Z"), SECONDS_15.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_SECONDS_30() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:30.000Z"), SECONDS_30.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MINUTE() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:59:00.000Z"), MINUTE.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MINUTES_2() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:58:00.000Z"), MINUTES_2.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MINUTES_5() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:55:00.000Z"), MINUTES_5.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MINUTES_10() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:50:00.000Z"), MINUTES_10.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MINUTES_15() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:45:00.000Z"), MINUTES_15.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MINUTES_30() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:30:00.000Z"), MINUTES_30.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_HOUR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T23:00:00.000Z"), HOUR.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_HOURS_6() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T18:00:00.000Z"), HOURS_6.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_HOURS_12() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T12:00:00.000Z"), HOURS_12.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_DAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-31T00:00:00.000Z"), DAY.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_WEEK_SUNDAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-25T00:00:00.000Z"), WEEK_SUNDAY.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_WEEK_MONDAY() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-25T00:00:00.000Z"), WEEK_MONDAY.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_MONTH() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-12-01T00:00:00.000Z"), MONTH.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_QUARTER() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-10-01T00:00:00.000Z"), QUARTER.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_HALF_YEAR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-07-01T00:00:00.000Z"), HALF_YEAR.decrement(TEST_TIME_DECREMENT));  }
	@Test public void testDecrement_YEAR() throws Exception { Assert.assertEquals(ZonedDateTime.parse("2019-01-01T00:00:00.000Z"), YEAR.decrement(TEST_TIME_DECREMENT));  }
}
