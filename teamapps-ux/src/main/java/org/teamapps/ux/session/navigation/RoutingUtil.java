package org.teamapps.ux.session.navigation;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.uri.UriTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoutingUtil {

	public static MatchingResult match(UriTemplate uriTemplate, String path) {
		HashMap<String, String> pathParams = new HashMap<>();
		boolean matches = uriTemplate.match(path, pathParams);
		return new MatchingResult(matches, pathParams);
	}

	public static class MatchingResult {
		private final boolean match;
		private final HashMap<String, String> pathParams;

		public MatchingResult(boolean match, HashMap<String, String> pathParams) {
			this.match = match;
			this.pathParams = pathParams;
		}

		public boolean isMatch() {
			return match;
		}

		public HashMap<String, String> getPathParams() {
			return pathParams;
		}
	}

	public static Map<String, String> parseQueryParams(String query) {
		if (StringUtils.isBlank(query)) {
			return Map.of();
		}
		if (query.startsWith("?")) {
			query = query.substring(1);
		}
		Map<String, String> queryParams = new LinkedHashMap<>();
		String[] assignments = query.split("&");
		for (String assignment : assignments) {
			int equalsSignIndex = assignment.indexOf("=");
			queryParams.put(
					URLDecoder.decode(assignment.substring(0, equalsSignIndex), StandardCharsets.UTF_8),
					URLDecoder.decode(assignment.substring(equalsSignIndex + 1), StandardCharsets.UTF_8)
			);
		}
		return queryParams;
	}

}
