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
		assertThat(NavigationState.parse("/foo/")).isEqualTo(new NavigationState("/foo/", Map.of()));
		assertThat(NavigationState.parse("foo/bar")).isEqualTo(new NavigationState("/foo/bar", Map.of()));
		assertThat(NavigationState.parse("/foo/bar")).isEqualTo(new NavigationState("/foo/bar", Map.of()));
		assertThat(NavigationState.parse("/foo/bar/")).isEqualTo(new NavigationState("/foo/bar/", Map.of()));
		assertThat(NavigationState.parse("/foo/bar?")).isEqualTo(new NavigationState("/foo/bar", Map.of()));
		assertThat(NavigationState.parse("/foo/bar?x=1")).isEqualTo(new NavigationState("/foo/bar", Map.of("x", "1")));
		assertThat(NavigationState.parse("/foo/bar/?x=1&y=2")).isEqualTo(new NavigationState("/foo/bar/", Map.of("x", "1", "y", "2")));
	}

	@Test
	public void toUrlEncodedString() {
		assertThat(NavigationState.parse("föö/?þar=ba←").toString()).isEqualTo("/föö/?þar=ba←");
	}
}