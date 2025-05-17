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
package org.teamapps.projector.component.timegraph;

public record Interval(long min, long max) {

	public static Interval union(Interval interval, Interval intervalB) {
		return new Interval(Math.min(interval.min(), intervalB.min()), Math.max(interval.max(), intervalB.max()));
	}

	public static Interval intersection(Interval a, Interval b) {
		if (a.min < b.max && a.max > b.min) {
			return new Interval(
					Math.max(a.min, b.min),
					Math.min(a.max, b.max)
			);
		} else {
			return Interval.empty();
		}
	}

	public static Interval empty() {
		return new Interval(0, 0);
	}

	public DtoLongInterval toDtoLongInterval() {
		DtoLongInterval interval = new DtoLongInterval();
		interval.setMin(min);
		interval.setMax(max);
		return interval;
	}

}
