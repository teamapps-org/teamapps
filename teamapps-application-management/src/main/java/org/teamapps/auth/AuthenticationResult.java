/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.auth;

public class AuthenticationResult<USER>  {

	public static <USER> AuthenticationResult<USER> createSuccessResult(USER authenticatedUser) {
		return new AuthenticationResult<>(true, authenticatedUser);
	}

	public static <USER> AuthenticationResult<USER> createError() {
		return new AuthenticationResult<>(false, null);
	}

	private final boolean success;
	private final USER authenticatedUser;


	private AuthenticationResult(boolean success, USER authenticatedUser) {
		this.success = success;
		this.authenticatedUser = authenticatedUser;
	}

	public boolean isSuccess() {
		return success;
	}

	public USER getAuthenticatedUser() {
		return authenticatedUser;
	}
}
