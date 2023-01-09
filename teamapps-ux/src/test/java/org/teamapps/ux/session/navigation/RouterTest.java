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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.teamapps.ux.session.navigation.NavigationHistoryOperation.PUSH;
import static org.teamapps.ux.session.navigation.NavigationHistoryOperation.REPLACE;
import static org.teamapps.ux.session.navigation.Router.PATH_REMAINDER_SUFFIX;
import static org.teamapps.ux.session.navigation.Router.PATH_REMAINDER_VARNAME;

public class RouterTest {

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

	@Test
	public void setPath() {
		Router router = new Router("/");
		router.setPath("x", PUSH);

		router.setPathSupplier(() -> "fromSupplier", REPLACE);
		router.setRouteSupplier(() -> new Route("fromRouteSupplier", Map.of()), REPLACE);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getPath()).isEqualTo("/x");
		assertThat(routeInfo.isPathChangeWorthStatePush()).isTrue();
	}

	@Test
	public void setPath2() {
		Router router = new Router("/");
		router.setPath("x", REPLACE);

		router.setPathSupplier(() -> "fromSupplier", PUSH);
		router.setRouteSupplier(() -> new Route("fromRouteSupplier", Map.of()), PUSH);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getPath()).isEqualTo("/x");
		assertThat(routeInfo.isPathChangeWorthStatePush()).isFalse();
	}

	@Test
	public void setQueryParameter() {
		Router router = new Router("/");
		router.setQueryParameter("x", "xValue", PUSH);

		router.setQueryParameterSupplier("x", () -> "fromSupplier", REPLACE);
		router.addQueryParametersSupplier(() -> Map.of("x", "fromSupplier2"), REPLACE);
		router.setRouteSupplier(() -> new Route("/", Map.of("x", "fromRouteSupplier")), REPLACE);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getQueryParam("x")).isEqualTo("xValue");
		assertThat(routeInfo.getQueryParamNamesWorthStatePush()).contains("x");
	}
	@Test
	public void setQueryParameter2() {
		Router router = new Router("/");
		router.setQueryParameter("x", "xValue", REPLACE);

		router.setQueryParameterSupplier("x", () -> "fromSupplier", PUSH);
		router.addQueryParametersSupplier(() -> Map.of("x", "fromSupplier2"), PUSH);
		router.setRouteSupplier(() -> new Route("/", Map.of("x", "fromRouteSupplier")), PUSH);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getQueryParam("x")).isEqualTo("xValue");
		assertThat(routeInfo.getQueryParamNamesWorthStatePush()).doesNotContain("x");
	}

	@Test
	public void setPathSupplier() {
		Router router = new Router("/");
		router.setPathSupplier(() -> "fromPathSupplier", PUSH);

		router.setRouteSupplier(() -> new Route("fromRouteSupplier", Map.of()), REPLACE);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getPath()).isEqualTo("/fromPathSupplier");
		assertThat(routeInfo.isPathChangeWorthStatePush()).isTrue();
	}

	@Test
	public void setPathSupplier2() {
		Router router = new Router("/");
		router.setPathSupplier(() -> "fromPathSupplier", REPLACE);

		router.setRouteSupplier(() -> new Route("fromRouteSupplier", Map.of()), PUSH);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getPath()).isEqualTo("/fromPathSupplier");
		assertThat(routeInfo.isPathChangeWorthStatePush()).isFalse();
	}

	@Test
	public void setQueryParameterSupplier() {
		Router router = new Router("/");
		router.setQueryParameterSupplier("x", () -> "fromSupplier", PUSH);

		router.addQueryParametersSupplier(() -> Map.of("x", "fromSupplier2"), REPLACE);
		router.setRouteSupplier(() -> new Route("/", Map.of("x", "fromRouteSupplier")), REPLACE);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getQueryParam("x")).isEqualTo("fromSupplier");
		assertThat(routeInfo.getQueryParamNamesWorthStatePush()).contains("x");
	}
	@Test
	public void setQueryParameterSupplier2() {
		Router router = new Router("/");
		router.setQueryParameterSupplier("x", () -> "fromSupplier", REPLACE);

		router.addQueryParametersSupplier(() -> Map.of("x", "fromSupplier2"), PUSH);
		router.setRouteSupplier(() -> new Route("/", Map.of("x", "fromRouteSupplier")), PUSH);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getQueryParam("x")).isEqualTo("fromSupplier");
		assertThat(routeInfo.getQueryParamNamesWorthStatePush()).doesNotContain("x");
	}

	@Test
	public void setQueryParametersSupplier() {
		Router router = new Router("/");
		router.addQueryParametersSupplier(() -> Map.of("x", "fromParametersSupplier"), PUSH);
		router.setRouteSupplier(() -> new Route("/", Map.of("x", "fromRouteSupplier")), REPLACE);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getQueryParam("x")).isEqualTo("fromParametersSupplier");
		assertThat(routeInfo.getQueryParamNamesWorthStatePush()).contains("x");
	}

	@Test
	public void setQueryParametersSupplier2() {
		Router router = new Router("/");
		router.addQueryParametersSupplier(() -> Map.of("x", "fromParametersSupplier"), REPLACE);
		router.setRouteSupplier(() -> new Route("/", Map.of("x", "fromRouteSupplier")), PUSH);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getQueryParam("x")).isEqualTo("fromParametersSupplier");
		assertThat(routeInfo.getQueryParamNamesWorthStatePush()).doesNotContain("x");
	}

	@Test
	public void setRouteSupplier() {
		Router router = new Router("/");
		router.setRouteSupplier(() -> new Route("fromRouteSupplier", Map.of()), PUSH);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getPath()).isEqualTo("/fromRouteSupplier");
		assertThat(routeInfo.isPathChangeWorthStatePush()).isTrue();
	}

	@Test
	public void setRouteSupplier2() {
		Router router = new Router("/");
		router.setRouteSupplier(() -> new Route("fromRouteSupplier", Map.of()), REPLACE);

		RouteInfo routeInfo = router.calculateRouteInfo();
		assertThat(routeInfo.getRoute().getPath()).isEqualTo("/fromRouteSupplier");
		assertThat(routeInfo.isPathChangeWorthStatePush()).isFalse();
	}

	@Test
	public void matchesPath() throws Exception {
		assertThat(new Router("").matchesPath("/a/b/c")).isTrue();
		assertThat(new Router("/").matchesPath("/a/b/c")).isTrue();
		assertThat(new Router("a").matchesPath("/a/b/c")).isTrue();
		assertThat(new Router("/a").matchesPath("/a/b/c")).isTrue();
		assertThat(new Router("a/b").matchesPath("/a/b/c")).isTrue();
		assertThat(new Router("/a/b").matchesPath("/a/b/c")).isTrue();
		assertThat(new Router("/a/{v}/b").matchesPath("/a/xxx/b")).isTrue();
		assertThat(new Router("/a/{v}/b").matchesPath("/a/xxx/b/c")).isTrue();

		assertThat(new Router("x").matchesPath("/a/b/c")).isFalse();
		assertThat(new Router("/x").matchesPath("/a/b/c")).isFalse();
		assertThat(new Router("a/x").matchesPath("/a/b/c")).isFalse();
		assertThat(new Router("/a/x").matchesPath("/a/b/c")).isFalse();
		assertThat(new Router("/a/b/c/d").matchesPath("/a/b/c")).isFalse();
		assertThat(new Router("/a/{v}/b").matchesPath("/a/xxx/c")).isFalse();
		assertThat(new Router("/a/{v}/b").matchesPath("/a/xxx")).isFalse();
	}

	@Test
	public void matchesPathPrefix() {
		assertThat(new Router("").matchesPathPrefix("/a/b")).isFalse();
		assertThat(new Router("/").matchesPathPrefix("/a/b")).isFalse();
		assertThat(new Router("a").matchesPathPrefix("/a/b")).isFalse();
		assertThat(new Router("/a").matchesPathPrefix("/a/b")).isFalse();
		assertThat(new Router("x").matchesPathPrefix("/a/b")).isFalse();
		assertThat(new Router("/a/x").matchesPathPrefix("/a/b")).isFalse();
		assertThat(new Router("/a/{v}/b").matchesPathPrefix("/a")).isFalse();
		assertThat(new Router("/a/{v}/b").matchesPathPrefix("/a/b")).isFalse();
		assertThat(new Router("/a/{v}/b").matchesPathPrefix("/a/b/c")).isFalse();

		assertThat(new Router("a/b").matchesPathPrefix("/a/b")).isTrue();
		assertThat(new Router("/a/b").matchesPathPrefix("/a/b")).isTrue();
		assertThat(new Router("/a/{v}/b").matchesPathPrefix("/a/xxx/b")).isTrue();
	}
}
