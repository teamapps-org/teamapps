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
