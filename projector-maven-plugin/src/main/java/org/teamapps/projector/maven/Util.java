package org.teamapps.projector.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class Util {

	public static void checkMavenVersion(String versionString, Log log) throws MojoExecutionException {
		String[] parts = versionString.split("\\.");
		int[] versionParts = new int[3];
		for (int i = 0; i < Math.min(parts.length, versionParts.length); i++) {
			versionParts[i] = Integer.parseInt(parts[i]);
		}
		if (versionParts[0] * 1_000_000 + versionParts[1] * 1000 + versionParts[2] < 3_003_009) {
			String message = "Maven version needs to be at least 3.3.9 for projector-maven-plugin to run! Your version is " + versionString;
			log.error(message);
			throw new MojoExecutionException(message);
		}
	}

}
