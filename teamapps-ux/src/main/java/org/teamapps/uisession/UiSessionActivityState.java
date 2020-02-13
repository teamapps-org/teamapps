package org.teamapps.uisession;

public class UiSessionActivityState {

	/**
	 * If true, the client has sent any ui protocol message (at least a KEEPALIVE) within
	 * the timeout configured as {@link org.teamapps.config.TeamAppsConfiguration#uiSessionInactivityTimeoutMillis}.
	 * Otherwise (false), the client can be regarded as temporarily disconnected.
	 */
	private final boolean active;

	public UiSessionActivityState(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

}
