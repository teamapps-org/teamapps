package org.teamapps.auth.hash;

public interface SecurePasswordHash {

	static SecurePasswordHash createDefault() {
		return new Argon2Hashing(1, 65536, 1);
	}

	static SecurePasswordHash createHighEffort() {
		return new Argon2Hashing(10, 65536, 1);
	}

	static SecurePasswordHash createLowEffort() {
		return new Argon2Hashing(1, 8192, 1);
	}

	String createSecureHash(String password);

	boolean verifyPassword(String password, String hash);
}
