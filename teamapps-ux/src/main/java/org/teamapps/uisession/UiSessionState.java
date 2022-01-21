package org.teamapps.uisession;

public enum UiSessionState {
	ACTIVE(true), NEARLY_INACTIVE(true), INACTIVE(false), CLOSED(false);

	private final boolean active;

	UiSessionState(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
}