package org.teamapps.uisession;

import org.teamapps.ux.session.SessionContext;

public class SessionPair {

	private final UiSession uiSession;
	private final SessionContext sessionContext;

	public SessionPair(UiSession uiSession, SessionContext sessionContext) {
		this.uiSession = uiSession;
		this.sessionContext = sessionContext;
	}

	public UiSession getUiSession() {
		return uiSession;
	}

	public SessionContext getSessionContext() {
		return sessionContext;
	}
}
