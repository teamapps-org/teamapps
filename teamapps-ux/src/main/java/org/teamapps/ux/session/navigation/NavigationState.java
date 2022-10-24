package org.teamapps.ux.session.navigation;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NavigationState {

	private static final Pattern RELATIVE_URL_PATTERN = Pattern.compile("(.*?)?(\\?.*?)?(#.*)?");

	private final String path;
	private final Map<String, String> queryParams;

	public static NavigationState parse(String relativeUrl) {
		if (StringUtils.isBlank(relativeUrl)) {
			return new NavigationState("/", Map.of());
		}
		Matcher matcher = RELATIVE_URL_PATTERN.matcher(relativeUrl);
		if (matcher.matches()) {
			String path = matcher.group(1);
			path = RoutingUtil.withSingleLeadingSlash(path);
			Map<String, String> queryParams = RoutingUtil.parseQueryParams(matcher.group(2));
			return new NavigationState(path, queryParams);
		} else {
			throw new IllegalArgumentException("Unparsable relative URL: " + relativeUrl);
		}
	}

	public NavigationState(String path, Map<String, String> queryParams) {
		this.path = path;
		this.queryParams = queryParams;
	}

	public NavigationState withPath(String path) {
		return new NavigationState(path, queryParams);
	}

	public NavigationState setQueryParam(String key, String value) {
		HashMap<String, String> newQueryParams = new HashMap<>(this.queryParams);
		newQueryParams.put(key, value);
		return new NavigationState(path, newQueryParams);
	}

	public NavigationState withoutQueryParams() {
		return new NavigationState(path, Map.of());
	}

	@Override
	public String toString() {
		return path + (!queryParams.isEmpty() ? "?" + queryParams.entrySet().stream()
				.map(e -> e.getKey()  + "=" + e.getValue())
				.collect(Collectors.joining("&")): "");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NavigationState that = (NavigationState) o;
		return Objects.equals(path, that.path) && Objects.equals(queryParams, that.queryParams);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, queryParams);
	}
}
