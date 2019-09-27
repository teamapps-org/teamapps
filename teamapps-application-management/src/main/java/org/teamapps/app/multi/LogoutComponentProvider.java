package org.teamapps.app.multi;

import org.teamapps.app.ComponentBuilder;
import org.teamapps.ux.session.SessionContext;

public interface LogoutComponentProvider<USER> {

	ComponentBuilder getLoggedOutUserComponent(USER user);
}
