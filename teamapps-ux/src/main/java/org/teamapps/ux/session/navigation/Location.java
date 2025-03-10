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
package org.teamapps.ux.session.navigation;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.dto.UiLocation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Location {

	/**
	 * Returns the complete URL.
	 * <p>
	 * Non-null.
	 */
	private final String href;

	/**
	 * Returns the URL's origin.
	 * <p>
	 * The origin is a String containing the Unicode serialization of the origin of the represented URL.
	 * <p>
	 * That is, for a URL using the http or https, the scheme followed by '://', followed by the domain, followed by ':',
	 * followed by the port, if explicitly specified.
	 * <p>
	 * Non-null.
	 */
	private final String origin;

	/**
	 * Returns the URL's protocol scheme.
	 * <p>
	 * Non-null.
	 */
	private final String protocol;

	/**
	 * Returns the URL's host and port (if different from the default port for the scheme).
	 * <p>
	 * Non-null.
	 */
	private final String host;

	/**
	 * Returns the URL's hostname. This may be the domain name (DNS), the IPv4 address (e.g. "93.184.216.34"),
	 * or IPv6 address in brackets (e.g. "[2606:2800:220:1:248:1893:25c8:1946]"), depending on the URL.
	 * <p>
	 * Non-null.
	 */
	private final String hostname;

	/**
	 * Returns the URL's port number.
	 * <p>
	 * Note that this can be null if the port number is not explicitly specified in the URL.
	 */
	private final Integer port;

	/**
	 * Returns the URL's path. This can (by the JavaScript spec) be an empty string (""), a single slash "/", or any path string.
	 * <p>
	 * Non-null.
	 */
	private final String pathname;

	/**
	 * Returns the URL's query (including the leading "?" if non-empty).
	 * <p>
	 * Non-null (empty string if empty).
	 */
	private final String search;

	/**
	 * Returns the URL's fragment (includes leading "#" if non-empty)
	 * <p>
	 * Non-null (empty string if empty).
	 */
	private final String hash;

	public Location(String protocol, String hostname, Integer port, String pathname, String search, String hash) {
		if (!protocol.endsWith(":")) {
			protocol = protocol + ":";
		}
		this.href = protocol + "//" + hostname + ":" + port + RoutingUtil.withSingleLeadingSlash(pathname) + search + hash;
		this.origin = protocol + "//" + hostname + ":" + port;
		this.protocol = protocol;
		this.host = hostname + ":" + port;
		this.hostname = hostname;
		this.port = port;
		this.pathname = pathname;
		this.search = StringUtils.isBlank(search) ? "" : search.startsWith("?") ? search : "?" + search;
		this.hash = hash;
	}

	public Location(String href, String origin, String protocol, String host, String hostname, Integer port, String pathname, String search, String hash) {
		this.href = href;
		this.origin = origin;
		this.protocol = protocol;
		this.host = host;
		this.hostname = hostname;
		this.port = port;
		this.pathname = pathname;
		this.search = StringUtils.isBlank(search) ? "" : search.startsWith("?") ? search : "?" + search;
		this.hash = hash;
	}

	public static Location parse(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		return new Location(
				url.toString(),
				url.getProtocol() + "://" + url.getHost() + (url.getPort() > 0 ? ":" + url.getPort() : ""),
				url.getProtocol(),
				url.getHost() + (url.getPort() > 0 ? ":" + url.getPort() : ""),
				url.getHost(),
				url.getPort() > 0 ? url.getPort() : null,
				StringUtils.isNotBlank(url.getPath()) ? url.getPath() : "/",
				StringUtils.isNotBlank(url.getQuery()) ? "?" + url.getQuery() : "",
				StringUtils.isNotBlank(url.getRef()) ? "#" + url.getRef() : ""
		);
	}

	public static Location fromUiLocation(UiLocation uiLocation) {
		if (uiLocation.getPathname().length() > 0 && !uiLocation.getPathname().startsWith("/")) {
			throw new IllegalArgumentException("Non-empty pathname must always start with a slash!");
		}
		return new Location(
				Objects.requireNonNull(uiLocation.getHref()),
				Objects.requireNonNull(uiLocation.getOrigin()),
				Objects.requireNonNull(uiLocation.getProtocol()),
				Objects.requireNonNull(uiLocation.getHost()),
				Objects.requireNonNull(uiLocation.getHostname()),
				uiLocation.getPort(),
				uiLocation.getPathname() != null ? uiLocation.getPathname() : "",
				uiLocation.getSearch() != null ? uiLocation.getSearch() : "",
				uiLocation.getHash() != null ? uiLocation.getHash() : ""
		);
	}

	public String getHref() {
		return href;
	}

	public String getOrigin() {
		return origin;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public String getHostname() {
		return hostname;
	}

	public Integer getPort() {
		return port;
	}

	public String getPathname() {
		return pathname;
	}

	public String getSearch() {
		return search;
	}

	public String getHash() {
		return hash;
	}

	public Location withPathNameAndQueryParams(String pathNameAndQueryParams) {
		int questionMarkIndex = pathNameAndQueryParams.indexOf("?");
		String pathName;
		String search;
		if (questionMarkIndex >= 0) {
			pathName = pathNameAndQueryParams.substring(0, questionMarkIndex);
			search = pathNameAndQueryParams.substring(questionMarkIndex);
		} else {
			pathName = pathNameAndQueryParams;
			search = "";
		}
		return new Location(protocol, hostname, port, pathName, search, hash);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Location location = (Location) o;
		return Objects.equals(href, location.href) && Objects.equals(origin, location.origin) && Objects.equals(protocol, location.protocol) && Objects.equals(host, location.host) && Objects.equals(hostname, location.hostname) && Objects.equals(port, location.port) && Objects.equals(pathname, location.pathname) && Objects.equals(search, location.search) && Objects.equals(hash, location.hash);
	}

	@Override
	public int hashCode() {
		return Objects.hash(href, origin, protocol, host, hostname, port, pathname, search, hash);
	}
}
