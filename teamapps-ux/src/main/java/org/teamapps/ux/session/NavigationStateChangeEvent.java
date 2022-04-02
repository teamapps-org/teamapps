package org.teamapps.ux.session;

public class NavigationStateChangeEvent {

	private final Location location;
	private final boolean triggeredByUser;

	public NavigationStateChangeEvent(Location location, boolean triggeredByUser) {
		this.location = location;
		this.triggeredByUser = triggeredByUser;
	}

	public Location getLocation() {
		return location;
	}

	public boolean isTriggeredByUser() {
		return triggeredByUser;
	}
}
