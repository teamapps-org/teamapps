/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.ux.session.navigation;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NavigationState {

	private static final NavigationState EMPTY = new NavigationState("/", Map.of());

	private static final Pattern RELATIVE_URL_PATTERN = Pattern.compile("(.*?)?(\\?.*?)?(#.*)?");

	private final String relativePath;
	private final Map<String, String> queryParams;

	public static NavigationState create() {
		return EMPTY;
	}

	public static NavigationState parse(String relativeUrl) {
		if (StringUtils.isBlank(relativeUrl)) {
			return new NavigationState("/", Map.of());
		}
		Matcher matcher = RELATIVE_URL_PATTERN.matcher(relativeUrl);
		if (matcher.matches()) {
			String path = matcher.group(1);
			path = RoutingUtil.normalizePath(path);
			Map<String, String> queryParams = RoutingUtil.parseQueryParams(matcher.group(2));
			return new NavigationState(path, queryParams);
		} else {
			throw new IllegalArgumentException("Unparsable relative URL: " + relativeUrl);
		}
	}

	public NavigationState(String relativePath, Map<String, String> queryParams) {
		this.relativePath = relativePath;
		this.queryParams = queryParams;
	}

	public NavigationState withPrefix(String pathPrefix) {
		return new NavigationState(RoutingUtil.concatenatePaths(pathPrefix, relativePath), queryParams);
	}

	public NavigationState withPath(String path) {
		return new NavigationState(path, queryParams);
	}

	public NavigationState withQueryParam(String key, String value) {
		if (StringUtils.isBlank(key)) {
			return this;
		}
		HashMap<String, String> newQueryParams = new HashMap<>(this.queryParams);
		if (StringUtils.isNotBlank(value)) {
			newQueryParams.put(key, value);
		} else {
			newQueryParams.remove(key);
		}
		return new NavigationState(relativePath, newQueryParams);
	}

	public String getRelativePath() {
		return relativePath;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
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
		NavigationState that = (NavigationState) o;
		return Objects.equals(relativePath, that.relativePath) && Objects.equals(queryParams, that.queryParams);
	}

	@Override
	public int hashCode() {
		return Objects.hash(relativePath, queryParams);
	}
}
