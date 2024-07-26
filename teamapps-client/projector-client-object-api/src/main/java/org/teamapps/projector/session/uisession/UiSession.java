package org.teamapps.projector.session.uisession;

import org.teamapps.dto.protocol.server.AbstractReliableServerMessage;
import org.teamapps.dto.protocol.server.SessionClosingReason;
import org.teamapps.projector.session.uisession.stats.UiSessionStatistics;

public interface UiSession {
	String getSessionId();

	void setName(String name);

	String getName();

	void sendCommand(CommandWithResultCallback commandWithCallback);

	void sendReliableServerMessage(AbstractReliableServerMessage serverMessage);

	ClientBackPressureInfo getClientBackPressureInfo();

	void close(SessionClosingReason reason);

	UiSessionState getState();

	UiSessionStatistics getStatistics();
}
