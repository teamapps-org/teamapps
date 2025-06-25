package org.teamapps.ux.component.field.textcolormarker;

import org.teamapps.dto.UiTextColorMarkerFieldMarker;
import org.teamapps.dto.UiTextColorMarkerFieldValue;

import java.util.List;

public record TextColorMarkerFieldValue(String text,
										List<TextColorMarker> markers) {
	public TextColorMarkerFieldValue(String text) {
		this(text, List.of());
	}

	public UiTextColorMarkerFieldValue toUiTextColorMarkerFieldValue() {
		List<UiTextColorMarkerFieldMarker> markers = this.markers == null ? null : this.markers.stream()
				.map(TextColorMarker::toUiTextColorMarkerFieldMarker)
				.toList();
		return new UiTextColorMarkerFieldValue(text, markers);
	}
}