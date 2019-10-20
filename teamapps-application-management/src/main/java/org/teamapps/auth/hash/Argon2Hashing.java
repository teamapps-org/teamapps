package org.teamapps.auth.hash;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Argon2Hashing implements SecurePasswordHash {

	private final Argon2 argon2;
	private final int iterations;
	private final int memory;
	private final int parallelism;

	public Argon2Hashing(int iterations, int memory, int parallelism) {
		this.iterations = iterations;
		this.memory = memory;
		this.parallelism = parallelism;
		this.argon2 = Argon2Factory.create();
	}

	@Override
	public String createSecureHash(String password) {
		if (password == null || password.isEmpty()) {
			return null;
		} else {
			return argon2.hash(iterations, memory, parallelism, password);
		}
	}

	@Override
	public boolean verifyPassword(String password, String hash) {
		if (hash == null || hash.isEmpty() || password == null || password.isEmpty()) {
			return false;
		} else {
			return argon2.verify(hash, password);
		}
	}

}

