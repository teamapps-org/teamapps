package org.teamapps.ux.session.navigation;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AnnotationBasedRouterTest {

	@Test
	public void shoudMatchCorrectMethod() {
		AtomicBoolean wasInvoked = new AtomicBoolean();
		AnnotationBasedRouter router = new AnnotationBasedRouter(new ParameterConverterProvider(), true) {

			@Path("/apps2/{appName2}/item2/{itemId2}")
			public void myOtherMethod(@PathParam("appName2") String appName, @PathParam("itemId2") Integer id, @QueryParam("flag") Boolean flag) {
				fail("Should not invoke this method!");
			}

			@Path("/apps/{appName}/item/{itemId}")
			public void myMethod(
					@PathParam("appName") String appName,
					@PathParam("itemId") Integer id,
					@QueryParam("flag") Boolean flag) {
				wasInvoked.set(true);
				assertThat(appName).isEqualTo("myApp");
				assertThat(id).isEqualTo(123);
				assertThat(flag).isTrue();
			}

		};

		boolean match = router.route("/apps/myApp/item/123", Map.of("flag", "true"));

		assertThat(match).isTrue();
		assertThat(wasInvoked).isTrue();
	}

	@Test
	public void shoudReturnFalseIfNoMethodMatches() {
		AtomicBoolean wasInvoked = new AtomicBoolean();
		AnnotationBasedRouter router = new AnnotationBasedRouter() {
			@Path("/apps/{appName}/item/{itemId}")
			public void myNonMatching(@PathParam("appName") String appName, @PathParam("itemId") Integer id) {
				fail("Should not invoke this method!");
			}
		};

		boolean match = router.route("/foo/bar", Map.of("flag", "true"));

		assertThat(match).isFalse();
		assertThat(wasInvoked).isFalse();
	}

	@Test
	public void shouldForwardRemainingPathToSubRouter() {
		AtomicBoolean mainRouterWasInvoked = new AtomicBoolean();
		AnnotationBasedRouter hierarchicalRouter = new AnnotationBasedRouter() {
			@Path("/apps/{appName}")
			public void myMethod(@PathParam("appName") String appName) {
				mainRouterWasInvoked.set(true);
				assertThat(appName).isEqualTo("myApp");
			}
		};

		Router subRouterMock = Mockito.mock(Router.class);
		hierarchicalRouter.addSubRouter(subRouterMock);

		boolean matches = hierarchicalRouter.route("/apps/myApp/perspective/myPerspective", Map.of("filter", "abc"));

		assertThat(matches).isTrue();
		assertThat(mainRouterWasInvoked).isTrue();
		verify(subRouterMock, times(1)).route(
				"/perspective/myPerspective",
				Map.of("filter", "abc")
		);
	}

	@Test
	public void shouldNotWildcardIfSetToFalse() {
		AtomicBoolean mainRouterWasInvoked = new AtomicBoolean();
		AnnotationBasedRouter hierarchicalRouter = new AnnotationBasedRouter(new ParameterConverterProvider(), false) {
			@Path("/apps/{appName}")
			public void myMethod(@PathParam("appName") String appName) {
				mainRouterWasInvoked.set(true);
			}
		};

		boolean matches = hierarchicalRouter.route("/apps/myApp/perspective/myPerspective", Map.of("filter", "abc"));

		assertThat(matches).isFalse();
		assertThat(mainRouterWasInvoked).isFalse();
	}

}