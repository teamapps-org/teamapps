package org.teamapps.ux.component.field.textcolormarker;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiTextColorMarkerFieldMarkerDefinition;

public record TextColorMarkerDefinition(
		int id,
		Color borderColor,
		Color backgroundColor,
		String hint
) {

	public UiTextColorMarkerFieldMarkerDefinition toUiTextColorMarkerFieldMarkerDefinition() {
		UiTextColorMarkerFieldMarkerDefinition ui = new UiTextColorMarkerFieldMarkerDefinition(id);
		ui.setBorderColor(borderColor == null ? null : borderColor.toHtmlColorString());
		ui.setBackgroundColor(backgroundColor == null ? null : backgroundColor.toHtmlColorString());
		ui.setHint(hint);
		return ui;
	}

}