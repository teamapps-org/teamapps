package org.teamapps.uisession.statistics.app;

import org.teamapps.event.Event;
import org.teamapps.uisession.TeamAppsSessionManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SessionStatsSharedBaseTableModel {

	public final Event<Void> onUpdated = new Event<>();

	private List<SessionStatsTableRecord> records = List.of();

	public SessionStatsSharedBaseTableModel(TeamAppsSessionManager sessionManager) {
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
