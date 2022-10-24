package org.teamapps.ux.session.navigation;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RoutingUtilTest {

	@Test
	public void normalizePathPrefix() {
		assertThat(RoutingUtil.normalizePathPrefix(null)).isEqualTo("/");
		assertThat(RoutingUtil.normalizePathPrefix("")).isEqualTo("/");
		assertThat(RoutingUtil.normalizePathPrefix("/")).isEqualTo("/");
		assertThat(RoutingUtil.normalizePathPrefix("foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePathPrefix("/foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePathPrefix("foo/bar")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePathPrefix("/foo/bar")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePathPrefix("foo/bar/")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePathPrefix("/foo/bar/")).isEqualTo("/foo/bar");
	}

	@Test
	public void withSingleLeadingSlash() {
		assertThat(RoutingUtil.normalizePathPrefix(null)).isEqualTo("/");
		assertThat(RoutingUtil.normalizePathPrefix("/")).isEqualTo("/");
		assertThat(RoutingUtil.normalizePathPrefix("foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePathPrefix("/foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePathPrefix("//foo")).isEqualTo("/foo");
		assertThat(RoutingUtil.normalizePathPrefix("foo/bar")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePathPrefix("/foo/bar")).isEqualTo("/foo/bar");
		assertThat(RoutingUtil.normalizePathPrefix("//foo/bar/")).isEqualTo("/foo/bar");
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