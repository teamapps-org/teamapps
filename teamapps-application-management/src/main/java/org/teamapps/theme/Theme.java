package org.teamapps.theme;

import org.teamapps.theme.background.Background;

public class Theme {

	public static Theme create(Background background, boolean darkTheme) {
		return new Theme(background, darkTheme);
	}

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
