/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

import org.junit.Test;

import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationTest {

	@Test
	public void parse() throws MalformedURLException {
		Location location = Location.parse("http://teamapps.org:8080/my/path?q=123#ref");

		assertThat(location.getHref()).isEqualTo("http://teamapps.org:8080/my/path?q=123#ref");
		assertThat(location.getOrigin()).isEqualTo("http://teamapps.org:8080");
		assertThat(location.getProtocol()).isEqualTo("http");
		assertThat(location.getHost()).isEqualTo("teamapps.org:8080");
		assertThat(location.getHostname()).isEqualTo("teamapps.org");
		assertThat(location.getPort()).isEqualTo(8080);
		assertThat(location.getPathname()).isEqualTo("/my/path");
		assertThat(location.getSearch()).isEqualTo("?q=123");
		assertThat(location.getHash()).isEqualTo("#ref");
	}

	@Test
	public void parseMinimal() throws MalformedURLException {
		Location location = Location.parse("http://teamapps.org");

		assertThat(location.getHref()).isEqualTo("http://teamapps.org");
		assertThat(location.getOrigin()).isEqualTo("http://teamapps.org");
		assertThat(location.getProtocol()).isEqualTo("http");
		assertThat(location.getHost()).isEqualTo("teamapps.org");
		assertThat(location.getHostname()).isEqualTo("teamapps.org");
		assertThat(location.getPort()).isNull();
		assertThat(location.getPathname()).isEqualTo("/");
		assertThat(location.getSearch()).isEqualTo("");
		assertThat(location.getHash()).isEqualTo("");
	}

}
