package org.teamapps.auth;

public interface LoginResolver<USER> {

	USER resolveLogin(String login);

	String resolvePasswordHash(String login);
}
