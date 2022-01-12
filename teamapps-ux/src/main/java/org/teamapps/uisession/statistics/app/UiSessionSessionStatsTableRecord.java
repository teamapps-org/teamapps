package org.teamapps.uisession.statistics.app;

import org.teamapps.uisession.ClientBackPressureInfo;
import org.teamapps.uisession.UiSession;
import org.teamapps.uisession.statistics.UiSessionStats;

public class UiSessionSessionStatsTableRecord implements SessionStatsTableRecord {

	private final UiSession uiSession;
	private final ClientBackPressureInfo clientBackPressureInfo;

	public UiSessionSessionStatsTableRecord(UiSession uiSession) {
		this.uiSession = uiSession;
		this.clientBackPressureInfo = uiSession.getClientBackPressureInfo(); // proactively extract, so not every column has to do it!
	}

	@Override
	public UiSession getUiSession() {
		return uiSession;
	}

	@Override
	public UiSessionStats getStatistics() {
		return uiSession.getStatistics();
	}

	@Override
	public ClientBackPressureInfo getClientBackPressureInfo() {
		return clientBackPressureInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UiSessionSessionStatsTableRecord that = (UiSessionSessionStatsTableRecord) o;
		return uiSession.getSessionId().equals(that.uiSession.getSessionId());
	}

	@Override
	public int hashCode() {
		return uiSession.getSessionId().hashCode();
	}
}