package org.teamapps.projector.resource.provider;

public class ResourceProviderUtil {

	public static String concatPaths(String... pathParts) {
		StringBuilder result = new StringBuilder(pathParts[0]);
		for (int i = 1; i < pathParts.length; i++) {
			if (!result.toString().endsWith("/")) {
				result.append("/");
			}
			result.append(pathParts[i]);
		}
		return result.toString();
	}

}
