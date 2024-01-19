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
package org.teamapps.uisession.statistics;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;

public class TrafficStats implements SumStats {
	private long total;
	private final AtomicLong currentChunkSum = new AtomicLong();
	private final Deque<Long> sum10sChunks = new ArrayDeque<>(6); // 1 minute
	private long sumLast10Seconds;
	private long sumLastMinute;

	public void add(long size) {
		total += size;
		currentChunkSum.addAndGet(size);
	}

	public void flush() {
		while (sum10sChunks.size() >= 6) {
			sum10sChunks.removeFirst();
		}
		long currentChunkSum = this.currentChunkSum.getAndSet(0);
		sum10sChunks.addLast(currentChunkSum);
		sumLastMinute = sum10sChunks.stream().mapToLong(value -> value).sum();
		sumLast10Seconds = currentChunkSum;
	}

	@Override
	public long getSum() {
		return total;
	}

	@Override
	public long getSumLastMinute() {
		return sumLastMinute;
	}

	@Override
	public long getSumLast10Seconds() {
		return sumLast10Seconds;
	}

	public ImmutableUiSessionStats.ImmutableSumStats toImmutable() {
		return new ImmutableUiSessionStats.ImmutableSumStats(total, sumLastMinute, sumLast10Seconds);
	}
}
