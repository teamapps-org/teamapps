package org.teamapps.theme;

public interface ThemeProvider<USER> {

	Theme getUserTheme(USER user);
}
