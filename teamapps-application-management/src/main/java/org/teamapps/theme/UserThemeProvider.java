package org.teamapps.theme;

public interface UserThemeProvider<USER> {

	Theme getUserTheme(USER user);
}
