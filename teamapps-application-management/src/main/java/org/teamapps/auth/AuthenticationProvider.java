package org.teamapps.auth;

import org.teamapps.auth.hash.SecurePasswordHash;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

public interface AuthenticationProvider<USER>  {

	static <USER> AuthenticationProvider<USER> createAuthenticationProvider(LoginResolver<USER> loginResolver, SecurePasswordHash securePasswordHash) {
		return new StandardAuthenticationProvider<>(loginResolver, securePasswordHash);
	}

	static <USER> AuthenticationProvider<USER> createAuthenticationProvider(LoginResolver<USER> loginResolver) {
		return new StandardAuthenticationProvider<>(loginResolver, SecurePasswordHash.createDefault());
	}

	AuthenticationResult<USER> authenticate(String login, String password);

	default USER getAuthenticatedUser() {
		return getAuthenticatedUser(CurrentSessionContext.get());
	}

	default USER getAuthenticatedUser(SessionContext context) {
		return getSessionAuthenticatedUserResolver().getUser(context);
	}

	void addAuthenticationHandler(AuthenticationHandler<USER> authenticationHandler);

	SessionAuthenticatedUserResolver<USER> getSessionAuthenticatedUserResolver();
}
