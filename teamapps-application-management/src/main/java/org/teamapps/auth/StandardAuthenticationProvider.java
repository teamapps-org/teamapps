/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
		authenticationHandlers.forEach(handler -> handler.handleAuthenticatedUser(user, context));
	}


}
