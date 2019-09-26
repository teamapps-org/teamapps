package org.teamapps.app;

import org.teamapps.ux.session.SessionContext;

public interface ThemeHandler<USER> {

	Theme getUserTheme(USER user, boolean mobileDevice, SessionContext context);
}
