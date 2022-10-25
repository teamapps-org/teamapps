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