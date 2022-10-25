package org.teamapps.ux.session.navigation;

import org.teamapps.dto.UiNavigationStateChangeType;

public enum NavigationStateChangeType {

	PUSH, REPLACE;

	public UiNavigationStateChangeType toUiNavigationStateChangeType() {
		return UiNavigationStateChangeType.valueOf(this.name());
	}
}