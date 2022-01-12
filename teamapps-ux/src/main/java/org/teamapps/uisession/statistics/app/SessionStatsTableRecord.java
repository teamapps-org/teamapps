package org.teamapps.uisession.statistics.app;

import org.teamapps.uisession.ClientBackPressureInfo;
import org.teamapps.uisession.UiSession;
import org.teamapps.uisession.statistics.UiSessionStats;

public interface SessionStatsTableRecord {

	UiSession getUiSession();

	UiSessionStats getStatistics();

	ClientBackPressureInfo getClientBackPressureInfo();

}