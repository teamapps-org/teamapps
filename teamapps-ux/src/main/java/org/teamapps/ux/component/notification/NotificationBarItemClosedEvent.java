package org.teamapps.ux.component.notification;

public class NotificationBarItemClosedEvent {

	enum ClosingReason {
		USER,
		TIMEOUT
	}

	private final NotificationBarItem item;
	private final ClosingReason closingReason;

	public NotificationBarItemClosedEvent(NotificationBarItem item, ClosingReason closingReason) {
		this.item = item;
		this.closingReason = closingReason;
	}

	public NotificationBarItem getItem() {
		return item;
	}

	public ClosingReason getClosingReason() {
		return closingReason;
	}
}
