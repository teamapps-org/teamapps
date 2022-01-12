package org.teamapps.uisession.statistics.app;

import org.teamapps.uisession.ClientBackPressureInfo;
import org.teamapps.uisession.UiSession;
import org.teamapps.uisession.statistics.UiSessionStats;

public class UiSessionStatsStatsTableRecord implements SessionStatsTableRecord {

	private final UiSessionStats uiSessionStats;

	public UiSessionStatsStatsTableRecord(UiSessionStats uiSessionStats) {
		this.uiSessionStats = uiSessionStats;
	}

	@Override
	public UiSession getUiSession() {
		return null;
	}

	@Override
	public UiSessionStats getStatistics() {
		return uiSessionStats;
	}

	@Override
	public ClientBackPressureInfo getClientBackPressureInfo() {
		return null;
	}
}