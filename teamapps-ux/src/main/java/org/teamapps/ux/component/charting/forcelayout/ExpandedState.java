package org.teamapps.ux.component.charting.forcelayout;

import org.teamapps.dto.UiNetworkNode;

public enum ExpandedState {

	NOT_EXPANDABLE, EXPANDED, COLLAPSED;

	public UiNetworkNode.ExpandState toExpandState() {
		return UiNetworkNode.ExpandState.valueOf(this.name());
	}
}
