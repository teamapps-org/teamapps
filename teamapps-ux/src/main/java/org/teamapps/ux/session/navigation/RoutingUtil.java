package org.teamapps.ux.session.navigation;

import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoutingUtil {

	public static Map<String, String> parseQueryParams(String query) {
		if (StringUtils.isBlank(query)) {
			return Map.of();
		}
		if (query.startsWith("?")) {
			query = query.substring(1);
		}
		if (StringUtils.isBlank(query)) {
			return Map.of();
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

	public static String normalizePathPrefix(String prefix) {
		if (StringUtils.isBlank(prefix)) {
			return "/";
		}
		prefix = withSingleLeadingSlash(prefix);
		if (prefix.length() > 1 && prefix.endsWith("/")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}
		return prefix;
	}

	public static String withSingleLeadingSlash(String path) {
		if (StringUtils.isBlank(path)) {
			return "/";
		}
		while (path.startsWith("//")) {
			path = path.substring(1);
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return path;
	}

	public static String concatenatePaths(String prefix, String suffix) {
		prefix = normalizePathPrefix(prefix);
		suffix = withSingleLeadingSlash(suffix);
		return withSingleLeadingSlash(prefix + suffix);
	}

}
