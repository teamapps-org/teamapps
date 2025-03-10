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

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class NavigationStateTest {

	@Test
	public void parse() {
		assertThat(Route.parse(null)).isEqualTo(new Route("/", Map.of()));
		assertThat(Route.parse("")).isEqualTo(new Route("/", Map.of()));
		assertThat(Route.parse("/")).isEqualTo(new Route("/", Map.of()));
		assertThat(Route.parse("foo")).isEqualTo(new Route("/foo", Map.of()));
		assertThat(Route.parse("/foo")).isEqualTo(new Route("/foo", Map.of()));
		assertThat(Route.parse("/foo/")).isEqualTo(new Route("/foo", Map.of()));
		assertThat(Route.parse("foo/bar")).isEqualTo(new Route("/foo/bar", Map.of()));
		assertThat(Route.parse("/foo/bar")).isEqualTo(new Route("/foo/bar", Map.of()));
		assertThat(Route.parse("/foo/bar/")).isEqualTo(new Route("/foo/bar", Map.of()));
		assertThat(Route.parse("/foo/bar?")).isEqualTo(new Route("/foo/bar", Map.of()));
		assertThat(Route.parse("/foo/bar?x=1")).isEqualTo(new Route("/foo/bar", Map.of("x", "1")));
		assertThat(Route.parse("/foo/bar/?x=1&y=2")).isEqualTo(new Route("/foo/bar", Map.of("x", "1", "y", "2")));
	}

	@Test
	public void toUrlEncodedString() {
		assertThat(Route.parse("föö/?þar=ba←").toString()).isEqualTo("/föö?þar=ba←");
	}
}
