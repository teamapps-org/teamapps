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
package org.teamapps.uisession.statistics.app;

import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.server.uisession.SessionManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SessionStatsSharedBaseTableModel {

	public final ProjectorEvent<Void> onUpdated = new ProjectorEvent<>();

	private List<SessionStatsTableRecord> records = List.of();

	public SessionStatsSharedBaseTableModel(SessionManager sessionManager) {
		sessionManager.onStatsUpdated.addListener(eventData -> {
			this.records = Stream.concat(
					eventData.getAllSessions().stream()
							.map(sessionPair -> new UiSessionSessionStatsTableRecord(sessionPair.getUiSession())),
					eventData.getClosedSessionsStatistics().stream()
							.map(UiSessionStatsStatsTableRecord::new)
			).collect(Collectors.toList());
			onUpdated.fire();
		});
	}

	public List<SessionStatsTableRecord> getRecords() {
		return records;
	}

}
