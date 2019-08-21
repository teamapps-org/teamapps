package org.teamapps.ux.component.floating;

import org.teamapps.dto.UiFloatingComponentPosition;

public enum FloatingPosition {

	TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

	public UiFloatingComponentPosition toUiPosition() {
		return UiFloatingComponentPosition.valueOf(name());
	}

}
