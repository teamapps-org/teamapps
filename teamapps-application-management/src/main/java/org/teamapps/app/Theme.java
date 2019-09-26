package org.teamapps.app;

import org.teamapps.app.background.Background;

public class Theme {

	private final Background background;
	private final boolean darkTheme;

	public Theme(Background background, boolean darkTheme) {
		this.background = background;
		this.darkTheme = darkTheme;
	}

	public Background getBackground() {
		return background;
	}

	public boolean isDarkTheme() {
		return darkTheme;
	}
}
