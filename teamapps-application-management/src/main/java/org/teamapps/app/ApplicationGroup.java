package org.teamapps.app;

import org.teamapps.icons.api.Icon;

public class ApplicationGroup {

	private final Icon icon;
	private final String title;

	public ApplicationGroup(Icon icon, String title) {
		this.icon = icon;
		this.title = title;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}
}
