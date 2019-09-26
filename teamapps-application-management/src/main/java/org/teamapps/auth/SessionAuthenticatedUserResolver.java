package org.teamapps.auth;

import org.teamapps.ux.session.SessionContext;

public interface SessionAuthenticatedUserResolver<USER> {

	USER getUser(SessionContext context);
}
