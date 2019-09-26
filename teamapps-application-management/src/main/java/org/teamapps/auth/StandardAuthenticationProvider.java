package org.teamapps.auth;

import org.teamapps.auth.hash.SecurePasswordHash;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class StandardAuthenticationProvider<USER> implements AuthenticationProvider<USER> {

	private final LoginResolver<USER> loginResolver;
	private final SecurePasswordHash securePasswordHash;
	private final Map<SessionContext, USER> userBySessionMap;

	public StandardAuthenticationProvider(LoginResolver<USER> loginResolver, SecurePasswordHash securePasswordHash) {
		this.loginResolver = loginResolver;
		this.securePasswordHash = securePasswordHash;
		userBySessionMap = Collections.synchronizedMap(new WeakHashMap<>());
	}

	@Override
	public AuthenticationResult<USER> authenticate(String login, String password) {
		String passwordHash = loginResolver.resolvePasswordHash(login);
		if (passwordHash != null) {
			if (securePasswordHash.verifyPassword(password, passwordHash)) {
				USER user = loginResolver.resolveLogin(login);
				userBySessionMap.put(CurrentSessionContext.get(), user);
				return AuthenticationResult.createSuccessResult(user);
			}
		}
		return AuthenticationResult.createError();
	}

	@Override
	public SessionAuthenticatedUserResolver<USER> getSessionAuthenticatedUserResolver() {
		return (context) -> userBySessionMap.get(context);
	}


}
