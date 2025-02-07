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

import org.teamapps.projector.session.uisession.stats.UiSessionStatistics;
import org.teamapps.projector.server.uisession.SessionPair;

import java.util.List;

public class SessionStatsUpdatedEventData {
	private final List<SessionPair> allSessions;
	private final List<UiSessionStatistics> closedSessionsStatistics;

	public SessionStatsUpdatedEventData(List<SessionPair> allSessions, List<UiSessionStatistics> closedSessionsStatistics) {
		this.allSessions = allSessions;
		this.closedSessionsStatistics = closedSessionsStatistics;
	}

	public List<SessionPair> getAllSessions() {
		return allSessions;
	}

	public List<UiSessionStatistics> getClosedSessionsStatistics() {
		return closedSessionsStatistics;
	}
}
