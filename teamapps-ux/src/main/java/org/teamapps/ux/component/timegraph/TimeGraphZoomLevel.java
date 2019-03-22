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

import org.teamapps.dto.UiTimeChartZoomLevel;

public class TimeGraphZoomLevel {

	private final long approximateMillisecondsPerDataPoint;

	public TimeGraphZoomLevel(long approximateMillisecondsPerDataPoint) {
		this.approximateMillisecondsPerDataPoint = approximateMillisecondsPerDataPoint;
	}

	public long getApproximateMillisecondsPerDataPoint() {
		return approximateMillisecondsPerDataPoint;
	}

	public UiTimeChartZoomLevel createUiTimeChartZoomLevel() {
		return new UiTimeChartZoomLevel(approximateMillisecondsPerDataPoint);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TimeGraphZoomLevel that = (TimeGraphZoomLevel) o;

		return approximateMillisecondsPerDataPoint == that.approximateMillisecondsPerDataPoint;
	}

	@Override
	public int hashCode() {
		return (int) (approximateMillisecondsPerDataPoint ^ (approximateMillisecondsPerDataPoint >>> 32));
	}
}
