package org.teamapps.ux.application.assembler;

import org.teamapps.icons.Icon;

public class ButtonData {

	private final Icon icon;
	private final String title;
	private final String description;

	public ButtonData(Icon icon, String title, String description) {
		this.icon = icon;
		this.title = title;
		this.description = description;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
}
