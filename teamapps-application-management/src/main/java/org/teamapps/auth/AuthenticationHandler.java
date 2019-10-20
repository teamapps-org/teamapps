package org.teamapps.auth;

import org.teamapps.ux.session.SessionContext;

public interface AuthenticationHandler<USER> {

	void handleAuthenticatedUser(USER user, SessionContext context);
}
