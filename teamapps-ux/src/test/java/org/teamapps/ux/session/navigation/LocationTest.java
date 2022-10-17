package org.teamapps.ux.session.navigation;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class LocationTest {

	@Test
	public void parse() throws MalformedURLException {
		Location location = Location.parse("http://teamapps.org:8080/my/path?q=123#ref");

		Assertions.assertThat(location.getHref()).isEqualTo("http://teamapps.org:8080/my/path?q=123#ref");
		Assertions.assertThat(location.getOrigin()).isEqualTo("http://teamapps.org:8080");
		Assertions.assertThat(location.getProtocol()).isEqualTo("http");
		Assertions.assertThat(location.getHost()).isEqualTo("teamapps.org:8080");
		Assertions.assertThat(location.getHostname()).isEqualTo("teamapps.org");
		Assertions.assertThat(location.getPort()).isEqualTo(8080);
		Assertions.assertThat(location.getPathname()).isEqualTo("/my/path");
		Assertions.assertThat(location.getSearch()).isEqualTo("?q=123");
		Assertions.assertThat(location.getHash()).isEqualTo("#ref");
	}

	@Test
	public void parseMinimal() throws MalformedURLException {
		Location location = Location.parse("http://teamapps.org");

		Assertions.assertThat(location.getHref()).isEqualTo("http://teamapps.org");
		Assertions.assertThat(location.getOrigin()).isEqualTo("http://teamapps.org");
		Assertions.assertThat(location.getProtocol()).isEqualTo("http");
		Assertions.assertThat(location.getHost()).isEqualTo("teamapps.org");
		Assertions.assertThat(location.getHostname()).isEqualTo("teamapps.org");
		Assertions.assertThat(location.getPort()).isNull();
		Assertions.assertThat(location.getPathname()).isEqualTo("/");
		Assertions.assertThat(location.getSearch()).isEqualTo("");
		Assertions.assertThat(location.getHash()).isEqualTo("");
	}
}