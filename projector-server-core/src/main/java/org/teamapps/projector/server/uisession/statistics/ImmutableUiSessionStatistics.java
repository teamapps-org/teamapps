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
package org.teamapps.projector.server.uisession.statistics;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.teamapps.projector.session.uisession.UiSessionState;
import org.teamapps.projector.session.uisession.stats.CountStats;
import org.teamapps.projector.session.uisession.stats.SumStats;
import org.teamapps.projector.session.uisession.stats.UiSessionStatistics;

public class ImmutableUiSessionStatistics implements UiSessionStatistics {

	private final long startTime;
	private final long endTime;
	private final String sessionId;
	private final String name;
	private final UiSessionState state;
	private final ImmutableCountStats commandStats;
	private final ImmutableCountStats commandResultStats;
	private final ImmutableCountStats eventStats;
	private final ImmutableCountStats queryStats;
	private final ImmutableCountStats queryResultStats;
	private final ImmutableSumStats sentDataStats;
	private final ImmutableSumStats receivedDataStats;

	public static class ImmutableCountStats implements CountStats {
		private final long count;
		private final long countLastMinute;
		private final long countLast10Seconds;
		private final Object2LongMap<String> countByClass;

		public ImmutableCountStats(long count, long countLastMinute, long countLast10Seconds, Object2LongMap<String> countByTypeId) {
			this.count = count;
			this.countLastMinute = countLastMinute;
			this.countLast10Seconds = countLast10Seconds;
			this.countByClass = countByTypeId;
		}

		@Override
		public long getCount() {
			return count;
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
		public Object2LongMap<String> getCountByTypeId() {
			return countByClass;
		}
	}

	public static class ImmutableSumStats implements SumStats {
		private final long total;
		private final long sumLastMinute;
		private final long sumLast10Seconds;

		public ImmutableSumStats(long total, long sumLastMinute, long sumLast10Seconds) {
			this.total = total;
			this.sumLastMinute = sumLastMinute;
			this.sumLast10Seconds = sumLast10Seconds;
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
	}

	public ImmutableUiSessionStatistics(long startTime, long endTime,
										String sessionId, String name, UiSessionState state,
										ImmutableCountStats commandStats         ,
										ImmutableCountStats commandResultStats   ,
										ImmutableCountStats eventStats           ,
										ImmutableCountStats queryStats           ,
										ImmutableCountStats queryResultStats     ,
										ImmutableSumStats sentDataStats,
										ImmutableSumStats receivedDataStats
	) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.sessionId = sessionId;
		this.name = name;
		this.state = state;
		this.commandStats = commandStats;
		this.commandResultStats = commandResultStats;
		this.eventStats = eventStats;
		this.queryStats = queryStats;
		this.queryResultStats = queryResultStats;
		this.sentDataStats = sentDataStats;
		this.receivedDataStats = receivedDataStats;
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

	@Override
	public UiSessionStatistics immutable() {
		return this;
	}
}
