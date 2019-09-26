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
