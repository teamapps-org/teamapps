package org.teamapps.ux.component.field.textcolormarker;

import org.teamapps.dto.UiTextColorMarkerFieldMarker;

public record TextColorMarker(int markerDefinitionId, int start, int end) {

	public UiTextColorMarkerFieldMarker toUiTextColorMarkerFieldMarker() {
		UiTextColorMarkerFieldMarker ui = new UiTextColorMarkerFieldMarker();
		ui.setMarkerDefinitionId(markerDefinitionId);
		ui.setStart(start);
		ui.setEnd(end);
		return ui;
	}

}