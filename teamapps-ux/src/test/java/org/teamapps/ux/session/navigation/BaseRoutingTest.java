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

import org.assertj.core.data.MapEntry;
import org.glassfish.jersey.uri.UriTemplate;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.teamapps.ux.session.navigation.BaseRouting.PATH_REMAINDER_SUFFIX;
import static org.teamapps.ux.session.navigation.BaseRouting.PATH_REMAINDER_VARNAME;

public class BaseRoutingTest {

	@Test
	public void pathRemainderSuffix() {
		UriTemplate uriTemplate = new UriTemplate("/asdf/x" + PATH_REMAINDER_SUFFIX);
		testUriPatternMatches(uriTemplate, "/asdf/x", entry(PATH_REMAINDER_VARNAME, ""));
		testUriPatternMatches(uriTemplate, "/asdf/x/foo", entry(PATH_REMAINDER_VARNAME, "/foo"));
		assertThat(uriTemplate.match("/asdf/xyz", new HashMap<>())).isFalse();
	}

	@SafeVarargs
	private static void testUriPatternMatches(UriTemplate uriTemplate, String path, MapEntry<String, String>... expectedEntries) {
		HashMap<String, String> map = new HashMap<>();
		assertThat(uriTemplate.match(path, map)).isTrue();
		assertThat(map).containsOnly(expectedEntries);
	}
}
