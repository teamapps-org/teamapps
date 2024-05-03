package org.teamapps.ux.component.field.textcolormarker;

import org.teamapps.dto.UiTextColorMarkerFieldMarker;
import org.teamapps.dto.UiTextColorMarkerFieldValue;

import java.util.List;

public record TextColorMarkerFieldValue(String text,
										List<TextMarker> markers) {

	public UiTextColorMarkerFieldValue toUiTextColorMarkerFieldValue() {
		List<UiTextColorMarkerFieldMarker> markers = this.markers.stream()
				.map(TextMarker::toUiTextColorMarkerFieldMarker)
				.toList();
		return new UiTextColorMarkerFieldValue(text, markers);
	}
}