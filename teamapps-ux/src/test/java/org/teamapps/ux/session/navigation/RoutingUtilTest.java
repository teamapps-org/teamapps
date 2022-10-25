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