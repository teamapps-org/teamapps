/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.projector.session.navigation;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Route {

	public static final Route EMPTY = new Route("/", Map.of());

	private static final Pattern RELATIVE_URL_PATTERN = Pattern.compile("(.*?)?(\\?.*?)?(#.*)?");

	private final String relativePath;
	private final Map<String, String> queryParams;

	public static Route create() {
		return EMPTY;
	}

	public static Route parse(String relativeUrl) {
		if (StringUtils.isBlank(relativeUrl)) {
			return new Route("/", Map.of());
		}
		Matcher matcher = RELATIVE_URL_PATTERN.matcher(relativeUrl);
		if (matcher.matches()) {
			String path = matcher.group(1);
			path = RoutingUtil.normalizePath(path);
			Map<String, String> queryParams = RoutingUtil.parseQueryParams(matcher.group(2));
			return new Route(path, queryParams);
		} else {
			throw new IllegalArgumentException("Unparsable relative URL: " + relativeUrl);
		}
	}

	public Route(String relativePath, Map<String, String> queryParams) {
		this.relativePath = RoutingUtil.normalizePath(relativePath);
		this.queryParams = queryParams;
	}

	public static Route fromLocation(URL location) {
		return parse(location.getPath() + location.getQuery());
	}

	public Route subRoute(String pathPrefixToRemove) {
		return new Route(RoutingUtil.removePrefix(relativePath, pathPrefixToRemove), queryParams);
	}

	public Route withPathSuffix(String pathSuffix) {
		return new Route(RoutingUtil.concatenatePaths(relativePath, pathSuffix), queryParams);
	}

	public Route withPathPrefix(String pathPrefix) {
		return new Route(RoutingUtil.concatenatePaths(pathPrefix, relativePath), queryParams);
	}

	public Route withPath(String path) {
		return new Route(path, queryParams);
	}

	public Route withQueryParam(String key, String value) {
		if (StringUtils.isBlank(key)) {
			return this;
		}
		HashMap<String, String> newQueryParams = new HashMap<>(this.queryParams);
		if (StringUtils.isNotBlank(value)) {
			newQueryParams.put(key, value);
		} else {
			newQueryParams.remove(key);
		}
		return new Route(relativePath, newQueryParams);
	}

	public Route withQueryParams(Map<String, String> queryParams) {
		if (queryParams == null || queryParams.isEmpty()) {
			return new Route(relativePath, Map.of());
		}

		HashMap<String, String> newQueryParams = new HashMap<>(this.queryParams);
		queryParams.forEach((key, value) -> {
			if (StringUtils.isNotBlank(value)) {
				newQueryParams.put(key, value);
			} else {
				newQueryParams.remove(key);
			}
		});

		return new Route(relativePath, newQueryParams);
	}

	public String getPath() {
		return relativePath;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public String getQueryParam(String name) {
		return queryParams.get(name);
	}

	@Override
	public String toString() {
		return relativePath + (!queryParams.isEmpty() ? "?" + queryParams.entrySet().stream()
				.map(e -> e.getKey() + "=" + e.getValue())
				.collect(Collectors.joining("&")) : "");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Route that = (Route) o;
		return Objects.equals(relativePath, that.relativePath) && Objects.equals(queryParams, that.queryParams);
	}

	@Override
	public int hashCode() {
		return Objects.hash(relativePath, queryParams);
	}
}
