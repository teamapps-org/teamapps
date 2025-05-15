/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component.timegraph.model.timestamps;

import org.teamapps.ux.component.timegraph.Interval;

import java.util.Arrays;
import java.util.LongSummaryStatistics;

public class StaticTimestampsModel extends AbstractTimestampsModel {

	private long[] timestamps;
	private Interval staticDomainX;

	public void setTimestamps(long[] timestamps) {
		this.timestamps = timestamps;
		onDataChanged.fire(null);
	}

	public void setEventTimestamps(long[] timestamps) {
		this.timestamps = timestamps;
		onDataChanged.fire(null);
	}

	@Override
	public long[] getTimestamps(Interval neededIntervalX) {
		return timestamps;
	}

	@Override
	public Interval getDomainX() {
		if (staticDomainX != null) {
			return staticDomainX;
		} else {
			final LongSummaryStatistics minMax = Arrays.stream(timestamps).summaryStatistics();
			return new Interval(minMax.getMin(), minMax.getMax());
		}
	}

	public void setStaticDomainX(Interval staticDomainX) {
		this.staticDomainX = staticDomainX;
	}
}
