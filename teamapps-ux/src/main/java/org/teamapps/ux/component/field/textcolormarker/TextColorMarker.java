package org.teamapps.ux.component.field.textcolormarker;

import org.teamapps.dto.UiTextColorMarkerFieldMarker;

public record TextColorMarker(int markerDefinitionId, int start, int end) {

	public UiTextColorMarkerFieldMarker toUiTextColorMarkerFieldMarker() {
        return new UiTextColorMarkerFieldMarker(markerDefinitionId, start, end);
	}

}