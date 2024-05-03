package org.teamapps.ux.component.field.textcolormarker;

import org.teamapps.dto.UiTextColorMarkerFieldMarker;

public record TextMarker(int id, int start, int end) {

	public UiTextColorMarkerFieldMarker toUiTextColorMarkerFieldMarker() {
		UiTextColorMarkerFieldMarker ui = new UiTextColorMarkerFieldMarker();
		ui.setId(id);
		ui.setStart(start);
		ui.setEnd(end);
		return ui;
	}

}