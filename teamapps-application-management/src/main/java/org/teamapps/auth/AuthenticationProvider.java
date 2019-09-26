package org.teamapps.auth;

import org.teamapps.auth.hash.SecurePasswordHash;

public interface AuthenticationProvider<USER>  {

	static <USER> AuthenticationProvider<USER> createAuthenticationProvider(LoginResolver<USER> loginResolver, SecurePasswordHash securePasswordHash) {
		return new StandardAuthenticationProvider<>(loginResolver, securePasswordHash);
	}

	static <USER> AuthenticationProvider<USER> createAuthenticationProvider(LoginResolver<USER> loginResolver) {
		return new StandardAuthenticationProvider<>(loginResolver, SecurePasswordHash.createDefault());
	}

	AuthenticationResult<USER> authenticate(String login, String password);

	SessionAuthenticatedUserResolver<USER> getSessionAuthenticatedUserResolver();
}
