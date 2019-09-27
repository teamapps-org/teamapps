package org.teamapps.app.multi;

import org.teamapps.ux.session.SessionContext;

public interface LogoutHandler<USER> {

	void handleUserLogout(USER user);
}
