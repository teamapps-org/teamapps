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
import org.teamapps.commons.util.ExceptionUtil;

import java.net.*;
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

	public static String normalizePath(String prefix) {
		if (isEmptyPath(prefix)) {
			return "/";
		}
		prefix = withSingleLeadingSlash(prefix);
		while (prefix.length() > 1 && prefix.endsWith("/")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}
		return prefix;
	}

	public static boolean isEmptyPath(String prefix) {
		return StringUtils.isBlank(prefix) || prefix.equals("/");
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
		suffix = withSingleLeadingSlash(suffix);
		return normalizePath(prefix + suffix);
	}

	public static String removePrefix(String path, String prefix) {
		path = normalizePath(path);
		prefix = normalizePath(prefix);
		if (prefix.equals("/")) {
			return path;
		} else if (path.startsWith(prefix)) {
			return normalizePath(path.substring(prefix.length()));
		} else {
			throw new IllegalArgumentException("Cannot remove prefix " + prefix + " from path " + path);
		}
	}

	public static URL withPathNameAndQueryParams(URL baseUrl, String pathNameAndQueryParams) {
		int questionMarkIndex = pathNameAndQueryParams.indexOf("?");
		String pathName;
		String search;
		if (questionMarkIndex >= 0) {
			pathName = pathNameAndQueryParams.substring(0, questionMarkIndex);
			search = pathNameAndQueryParams.substring(questionMarkIndex);
		} else {
			pathName = pathNameAndQueryParams;
			search = null;
		}
		pathName = normalizePath(pathName);
		if (StringUtils.startsWith(search, "?")) {
			search = search.substring(1);
		}
		if (StringUtils.isEmpty(search)) {
			search = null;
		}
		return createUrl(baseUrl, pathName, search);
	}

	private static URL createUrl(URL baseUrl, String pathName, String search) {
		return ExceptionUtil.runWithSoftenedExceptions(() -> new URI(baseUrl.getProtocol(), null, baseUrl.getHost(), baseUrl.getPort(), pathName, search, baseUrl.getRef()).toURL());
	}


}
