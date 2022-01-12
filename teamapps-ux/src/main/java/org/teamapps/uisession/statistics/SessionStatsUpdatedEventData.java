package org.teamapps.uisession.statistics;

import org.teamapps.uisession.SessionPair;

import java.util.List;

public class SessionStatsUpdatedEventData {
	private final List<SessionPair> allSessions;
	private final List<UiSessionStats> closedSessionsStatistics;

	public SessionStatsUpdatedEventData(List<SessionPair> allSessions, List<UiSessionStats> closedSessionsStatistics) {
		this.allSessions = allSessions;
		this.closedSessionsStatistics = closedSessionsStatistics;
	}

	public List<SessionPair> getAllSessions() {
		return allSessions;
	}

	public List<UiSessionStats> getClosedSessionsStatistics() {
		return closedSessionsStatistics;
	}
}
