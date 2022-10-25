package org.teamapps.ux.session.navigation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoutingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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

	public static String normalizePath(String prefix) {
		if (StringUtils.isBlank(prefix) || prefix.equals("/")) {
			return "/";
		}
		prefix = withSingleLeadingSlash(prefix);
		while (prefix.length() > 1 && prefix.endsWith("/")) {
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
		prefix = normalizePath(prefix);
		suffix = normalizePath(suffix);
		return withSingleLeadingSlash(prefix + suffix);
	}

	private static String concatenatePathAndParams(String absolutePath, Map<String, String> queryParams) {
		absolutePath = withSingleLeadingSlash(absolutePath);
		return absolutePath + (queryParams.isEmpty() ? "" : "?" + queryParams.entrySet().stream()
				.map(e -> e.getKey() + "=" + e.getValue())
				.collect(Collectors.joining("&")));
	}

	public static Optional<NavigationState> locationToNavigationState(Location location, String pathPrefix) {
		String path = location.getPathname();
		if (!path.startsWith(pathPrefix)) {
			LOGGER.warn("path does not start with prefix '{}'. Will return null!", pathPrefix);
			return Optional.empty();
		}
		String relativePath = pathPrefix.equals("/") ? path : path.substring(pathPrefix.length());

		Map<String, String> queryParams = RoutingUtil.parseQueryParams(location.getSearch());
		return Optional.of(new NavigationState(relativePath, queryParams));
	}

}
