package org.teamapps.auth;

import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

public interface SessionAuthenticatedUserResolver<USER> {

	default USER getUser() {
		return getUser(CurrentSessionContext.get());
	}

	USER getUser(SessionContext context);
}
