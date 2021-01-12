package org.teamapps.ux.component.map;

import org.teamapps.dto.UiMapMarkerAnchor;

public enum MarkerAnchor {

	CENTER,
	TOP,
	BOTTOM,
	LEFT,
	RIGHT,
	TOP_LEFT,
	TOP_RIGHT,
	BOTTOM_LEFT,
	BOTTOM_RIGHT;

	public UiMapMarkerAnchor toUiMapMarkerAnchor() {
		return UiMapMarkerAnchor.valueOf(name());
	}

}
