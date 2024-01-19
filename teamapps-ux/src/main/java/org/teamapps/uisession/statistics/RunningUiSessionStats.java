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

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.teamapps.dto.UiCommand;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiQuery;
import org.teamapps.uisession.UiSessionState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;

public class RunningUiSessionStats implements UiSessionStats {

	private static class RunningCountStats implements CountStats {
		private final AtomicLong count = new AtomicLong();
		private final AtomicLong currentChunkCount = new AtomicLong();
		private final Deque<Long> count10sChunks = new ArrayDeque<>(6); // 1 minute
		private long countLast10Seconds;
		private long countLastMinute;
		private final Object2LongMap<Class<?>> countByClass = new Object2LongOpenHashMap<>();

		public void add(Class<?> aClass) {
			count.incrementAndGet();
			currentChunkCount.incrementAndGet();
			countByClass.computeLong(aClass, (clazz, count) -> count != null ? count + 1 : 1);
		}

		public void flush() {
			while (count10sChunks.size() >= 6) {
				count10sChunks.removeFirst();
			}
			long currentChunkCount = this.currentChunkCount.getAndSet(0);
			count10sChunks.addLast(currentChunkCount);
			countLastMinute = count10sChunks.stream().mapToLong(value -> value).sum();
			countLast10Seconds = currentChunkCount;
		}

		@Override
		public long getCount() {
			return count.get();
		}

		@Override
		public long getCountLastMinute() {
			return countLastMinute;
		}

		@Override
		public long getCountLast10Seconds() {
			return countLast10Seconds;
		}

		@Override
		public Object2LongMap<Class<?>> getCountByClass() {
			return countByClass;
		}

		public ImmutableUiSessionStats.ImmutableCountStats toImmutable() {
			return new ImmutableUiSessionStats.ImmutableCountStats(count.get(), getCountLastMinute(), getCountLast10Seconds(), countByClass);
		}
	}

	private static class RunningSumStats implements SumStats {
		private long total;
		private final Deque<Long> sum10sChunks = new ArrayDeque<>(6); // 1 minute
		private long sumLast10Seconds;
		private long sumLastMinute;

		public void update(long total) {
			while (sum10sChunks.size() >= 6) {
				sum10sChunks.removeFirst();
			}
			long size10s = total - this.total;
			sum10sChunks.addLast(size10s);
			sumLastMinute = sum10sChunks.stream().mapToLong(value -> value).sum();
			sumLast10Seconds = size10s;
			this.total = total;
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

	private final long startTime;
	private long endTime = -1;
	private final String sessionId;
	private String name;
	private UiSessionState state = UiSessionState.ACTIVE;

	private final RunningCountStats commandStats = new RunningCountStats();
	private final RunningCountStats commandResultStats = new RunningCountStats();
	private final RunningCountStats eventStats = new RunningCountStats();
	private final RunningCountStats queryStats = new RunningCountStats();
	private final RunningCountStats queryResultStats = new RunningCountStats();

	private final RunningSumStats sentDataStats = new RunningSumStats();
	private final RunningSumStats receivedDataStats = new RunningSumStats();


	public RunningUiSessionStats(long startTime, String sessionId, String name) {
		this.startTime = startTime;
		this.sessionId = sessionId;
		this.name = name;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UiSessionState getState() {
		return state;
	}

	@Override
	public CountStats getCommandStats() {
		return commandStats;
	}

	@Override
	public CountStats getCommandResultStats() {
		return commandResultStats;
	}

	@Override
	public CountStats getEventStats() {
		return eventStats;
	}

	@Override
	public CountStats getQueryStats() {
		return queryStats;
	}

	@Override
	public CountStats getQueryResultStats() {
		return queryResultStats;
	}

	@Override
	public SumStats getSentDataStats() {
		return sentDataStats;
	}

	@Override
	public SumStats getReceivedDataStats() {
		return receivedDataStats;
	}

	public void nameChanged(String name) {
		this.name = name;
	}

	public void commandSent(UiCommand command) {
		commandStats.add(command.getClass());
	}

	public void commandResultReceivedFor(Class<?> commandClass) {
		commandResultStats.add(commandClass);
	}

	public void eventReceived(UiEvent event) {
		eventStats.add(event.getClass());
	}

	public void queryReceived(UiQuery query) {
		queryStats.add(query.getClass());
	}

	public void queryResultSentFor(UiQuery query) {
		queryResultStats.add(query.getClass());
	}


	public void stateChanged(UiSessionState sessionState) {
		state = sessionState;
		if (state == UiSessionState.CLOSED) {
			endTime = System.currentTimeMillis();
		}
	}

	public ImmutableUiSessionStats immutableCopy() {
		return new ImmutableUiSessionStats(startTime, endTime, sessionId, name, state,
				commandStats.toImmutable(),
				commandResultStats.toImmutable(),
				eventStats.toImmutable(),
				queryStats.toImmutable(),
				queryResultStats.toImmutable(),
				sentDataStats.toImmutable(),
				receivedDataStats.toImmutable());
	}

	public void update(long totalDataSent, long totalDataReceived) {
		commandStats.flush();
		commandResultStats.flush();
		eventStats.flush();
		queryStats.flush();
		queryResultStats.flush();
		sentDataStats.update(totalDataSent);
		receivedDataStats.update(totalDataReceived);
	}

}
