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

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class NavigationStateTest {

	@Test
	public void parse() {
		assertThat(NavigationState.parse(null)).isEqualTo(new NavigationState("/", Map.of()));
		assertThat(NavigationState.parse("")).isEqualTo(new NavigationState("/", Map.of()));
		assertThat(NavigationState.parse("/")).isEqualTo(new NavigationState("/", Map.of()));
		assertThat(NavigationState.parse("foo")).isEqualTo(new NavigationState("/foo", Map.of()));
		assertThat(NavigationState.parse("/foo")).isEqualTo(new NavigationState("/foo", Map.of()));
		assertThat(NavigationState.parse("/foo/")).isEqualTo(new NavigationState("/foo", Map.of()));
		assertThat(NavigationState.parse("foo/bar")).isEqualTo(new NavigationState("/foo/bar", Map.of()));
		assertThat(NavigationState.parse("/foo/bar")).isEqualTo(new NavigationState("/foo/bar", Map.of()));
		assertThat(NavigationState.parse("/foo/bar/")).isEqualTo(new NavigationState("/foo/bar", Map.of()));
		assertThat(NavigationState.parse("/foo/bar?")).isEqualTo(new NavigationState("/foo/bar", Map.of()));
		assertThat(NavigationState.parse("/foo/bar?x=1")).isEqualTo(new NavigationState("/foo/bar", Map.of("x", "1")));
		assertThat(NavigationState.parse("/foo/bar/?x=1&y=2")).isEqualTo(new NavigationState("/foo/bar", Map.of("x", "1", "y", "2")));
	}

	@Test
	public void toUrlEncodedString() {
		assertThat(NavigationState.parse("föö/?þar=ba←").toString()).isEqualTo("/föö?þar=ba←");
	}
}
