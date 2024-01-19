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
package org.teamapps.util.threading;

/**
 * Caution: You should use {@link java.util.LongSummaryStatistics} whenever you can.
 * The only advantage of this class is it being immutable.
 */
public class MinMaxAverageStats {
	private final long min;
	private final long max;
	private final long total;
	private final long count;

	public MinMaxAverageStats() {
		this.min = Long.MAX_VALUE;
		this.max = 0;
		this.total = 0;
		this.count = 0;
	}

	public MinMaxAverageStats(long min, long max, long total, long count) {
		this.min = min;
		this.max = max;
		this.total = total;
		this.count = count;
	}

	public MinMaxAverageStats push(long time) {
		return new MinMaxAverageStats(Math.min(time, min), Math.max(time, max), this.total + time, this.count + 1);
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public long getAvg() {
		return total / count;
	}

	public long getCount() {
		return count;
	}
}
