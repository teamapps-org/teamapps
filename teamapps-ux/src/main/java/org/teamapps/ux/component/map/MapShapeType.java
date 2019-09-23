package org.teamapps.ux.component.map;

import org.teamapps.dto.UiMapShapeType;

public enum MapShapeType {

	CIRCLE,
	// MARKER,
	POLYGON,
	POLYLINE,
	RECTANGLE;

	public UiMapShapeType toUiMapShapeType() {
		return UiMapShapeType.valueOf(name());
	}

}
