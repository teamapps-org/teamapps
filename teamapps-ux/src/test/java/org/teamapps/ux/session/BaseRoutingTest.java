package org.teamapps.ux.session;

import org.assertj.core.data.MapEntry;
import org.glassfish.jersey.uri.UriTemplate;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.teamapps.ux.session.BaseRouting.PATH_REMAINDER_SUFFIX;
import static org.teamapps.ux.session.BaseRouting.PATH_REMAINDER_VARNAME;

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