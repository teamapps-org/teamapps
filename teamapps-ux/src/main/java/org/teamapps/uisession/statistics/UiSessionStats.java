package org.teamapps.uisession.statistics;

import org.teamapps.uisession.QualifiedUiSessionId;

public interface UiSessionStats {

	long getStartTime();
	long getEndTime();
	QualifiedUiSessionId getSessionId();
	String getName();
	SessionState getState();

	CountStats getCommandStats();
	CountStats getCommandResultStats();
	CountStats getEventStats();
	CountStats getQueryStats();
	CountStats getQueryResultStats();

	SumStats getSentDataStats();
	SumStats getReceivedDataStats();

}
