package org.teamapps.ux.component.field.textcolormarker;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiTextColorMarkerFieldMarkerDefinition;

public record TextColorMarkerFieldMarkerDefinition(
		int id,
		Color borderColor,
		Color backgroundColor,
		String hint
) {

	public UiTextColorMarkerFieldMarkerDefinition toUiTextColorMarkerFieldMarkerDefinition() {
		UiTextColorMarkerFieldMarkerDefinition ui = new UiTextColorMarkerFieldMarkerDefinition();
		ui.setId(id);
		ui.setBorderColor(borderColor.toHtmlColorString());
		ui.setBackgroundColor(backgroundColor.toHtmlColorString());
		ui.setHint(hint);
		return ui;
	}

}