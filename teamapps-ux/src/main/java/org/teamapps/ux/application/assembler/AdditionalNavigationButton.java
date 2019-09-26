package org.teamapps.ux.application.assembler;

import org.teamapps.icons.api.Icon;

public class AdditionalNavigationButton {

	private final Icon icon;
	private final String caption;
	private final Runnable handler;

	public AdditionalNavigationButton(Icon icon, String caption, Runnable handler) {
		this.icon = icon;
		this.caption = caption;
		this.handler = handler;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getCaption() {
		return caption;
	}

	public Runnable getHandler() {
		return handler;
	}
}
