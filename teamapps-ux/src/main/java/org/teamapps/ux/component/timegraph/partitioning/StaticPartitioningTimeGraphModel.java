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
package org.teamapps.ux.component.timegraph.partitioning;

import org.teamapps.ux.component.timegraph.Interval;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

public class StaticPartitioningTimeGraphModel extends PartitioningTimeGraphModel {
	
	private StaticRawTimedDataModel delegateModel;

	private StaticPartitioningTimeGraphModel(ZoneId timeZone, StaticRawTimedDataModel delegateModel) {
		super(timeZone, delegateModel);
		this.delegateModel = delegateModel;
	}
	
	public static StaticPartitioningTimeGraphModel create(ZoneId zoneId) {
		return new StaticPartitioningTimeGraphModel(zoneId, new StaticRawTimedDataModel());
	}

	public void setEventTimestampsByDataSeriesId(Map<String, long[]> eventTimestampsByDataSeriesIds) {
		delegateModel.setEventTimestampsByDataSeriesId(eventTimestampsByDataSeriesIds);
	}

	public void setEventTimestampsForDataSeriesId(String dataSeriesId, long[] eventTimestamps) {
		delegateModel.setEventTimestampsForDataSeriesIds(dataSeriesId, eventTimestamps);
	}

	public Map<String, long[]> getRawEventTimes(Collection<String> dataSeriesIds, Interval neededIntervalX) {
		return delegateModel.getRawEventTimes(dataSeriesIds, neededIntervalX);
	}
}
