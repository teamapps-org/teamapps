package org.teamapps.auth;

import org.teamapps.auth.hash.SecurePasswordHash;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.util.*;

public class StandardAuthenticationProvider<USER> implements AuthenticationProvider<USER> {

	private final LoginResolver<USER> loginResolver;
	private final SecurePasswordHash securePasswordHash;
	private final Map<SessionContext, USER> userBySessionMap;
	private final List<AuthenticationHandler<USER>> authenticationHandlers;

	public StandardAuthenticationProvider(LoginResolver<USER> loginResolver, SecurePasswordHash securePasswordHash) {
		this.loginResolver = loginResolver;
		this.securePasswordHash = securePasswordHash;
		userBySessionMap = Collections.synchronizedMap(new WeakHashMap<>());
		authenticationHandlers = new ArrayList<>();
	}

	@Override
	public AuthenticationResult<USER> authenticate(String login, String password) {
		String passwordHash = loginResolver.resolvePasswordHash(login);
		if (passwordHash != null) {
			if (securePasswordHash.verifyPassword(password, passwordHash)) {
				SessionContext context = CurrentSessionContext.get();
				USER user = loginResolver.resolveLogin(login);
				addAuthenticatedUser(user, context);
				authenticationHandlers.forEach(handler -> handler.handleAuthenticatedUser(user, context));
				return AuthenticationResult.createSuccessResult(user);
			}
		}
		return AuthenticationResult.createError();
	}

	@Override
	public void addAuthenticationHandler(AuthenticationHandler<USER> authenticationHandler) {
		authenticationHandlers.add(authenticationHandler);
	}

	@Override
	public SessionAuthenticatedUserResolver<USER> getSessionAuthenticatedUserResolver() {
		return (context) -> userBySessionMap.get(context);
	}

	protected void addAuthenticatedUser(USER user, SessionContext context) {
		userBySessionMap.put(context, user);
	}


}
