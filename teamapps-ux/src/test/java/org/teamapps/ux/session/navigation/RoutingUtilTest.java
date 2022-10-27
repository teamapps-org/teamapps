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

import static org.assertj.core.api.Assertions.assertThat;


public class RoutingUtilTest {

	@Test
	public void normalizePathPrefix() {
		assertThat(RoutingUtil.normalizePath(null)).isEqualTo("/");
		assertThat(RoutingUtil.normalizePath("")).isEqualTo("/");
		assertThat(RoutingUtil.normalizePath("/")).isEqualTo("/");
		assertThat(RoutingUtil.normalizePath("foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePath("/foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePath("foo/bar")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePath("/foo/bar")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePath("foo/bar/")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePath("/foo/bar/")).isEqualTo("/foo/bar");
	}

	@Test
	public void withSingleLeadingSlash() {
		assertThat(RoutingUtil.normalizePath(null)).isEqualTo("/");
		assertThat(RoutingUtil.normalizePath("/")).isEqualTo("/");
		assertThat(RoutingUtil.normalizePath("foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePath("/foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePath("//foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePath("foo/bar")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePath("/foo/bar")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePath("//foo/bar/")).isEqualTo("/foo/bar");
	}

	@Test
	public void concatenatePaths() {
		assertThat(RoutingUtil.concatenatePaths(null, "asdf")).isEqualTo("/asdf");
		assertThat(RoutingUtil.concatenatePaths("", "asdf")).isEqualTo("/asdf");
		assertThat(RoutingUtil.concatenatePaths("/", "asdf")).isEqualTo("/asdf");
		assertThat(RoutingUtil.concatenatePaths("/", "/asdf")).isEqualTo("/asdf");
		assertThat(RoutingUtil.concatenatePaths("prefix", "suffix")).isEqualTo("/prefix/suffix");
		assertThat(RoutingUtil.concatenatePaths("/prefix", "suffix")).isEqualTo("/prefix/suffix");
		assertThat(RoutingUtil.concatenatePaths("prefix", "/suffix")).isEqualTo("/prefix/suffix");
		assertThat(RoutingUtil.concatenatePaths("/prefix", "/suffix")).isEqualTo("/prefix/suffix");
	}
}
