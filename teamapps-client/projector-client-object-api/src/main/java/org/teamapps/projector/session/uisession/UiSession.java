package org.teamapps.projector.session.uisession;

import org.teamapps.dto.protocol.server.AbstractReliableServerMessage;
import org.teamapps.dto.protocol.server.SessionClosingReason;

public interface UiSession {
	String getSessionId();

	void setName(String name);

	String getName();

	void sendCommand(CommandWithResultCallback commandWithCallback);

	void sendReliableServerMessage(AbstractReliableServerMessage serverMessage);

	ClientBackPressureInfo getClientBackPressureInfo();

	void close(SessionClosingReason reason);

	UiSessionState getState();

	UiSessionStats getStatistics();
}
