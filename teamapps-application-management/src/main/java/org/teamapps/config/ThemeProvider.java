package org.teamapps.config;

import org.teamapps.theme.Theme;

public interface ThemeProvider<USER> {

	Theme getUserTheme(USER user);
}
