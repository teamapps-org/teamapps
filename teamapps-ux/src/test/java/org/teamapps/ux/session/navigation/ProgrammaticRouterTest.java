package org.teamapps.ux.session.navigation;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProgrammaticRouterTest {

	@Test
	public void shouldExecuteSideEffectWithCorrectParameters() {
		RoutingSideEffect sideEffectMock = Mockito.mock(RoutingSideEffect.class);

		ProgrammaticRouter router = new ProgrammaticRouter("/app/{appName}/perspective/{perspectiveName}", sideEffectMock);

		boolean match = router.route("/app/myApp/perspective/items", Map.of("q", "qqq"));

		assertThat(match).isTrue();
		verify(sideEffectMock, times(1)).apply(
				"/app/myApp/perspective/items",
				Map.of("appName", "myApp", "perspectiveName", "items"),
				Map.of("q", "qqq")
		);
	}

	@Test
	public void shouldNotExecuteSideEffectIfNotMatching() {
		RoutingSideEffect sideEffectMock = Mockito.mock(RoutingSideEffect.class);
		ProgrammaticRouter router = new ProgrammaticRouter("/a/b/", sideEffectMock);

		boolean match = router.route("/x/y/z", Map.of());

		assertThat(match).isFalse();
		verify(sideEffectMock, never()).apply(any(), any(), any());
	}

	@Test
	public void shouldForward() {
		RoutingSideEffect sideEffectMock = Mockito.mock(RoutingSideEffect.class);
		ProgrammaticRouter hierarchicalRouter = new ProgrammaticRouter("/apps/{x}/", sideEffectMock);

		Router subRouterMock = Mockito.mock(Router.class);
		hierarchicalRouter.addSubRouter(subRouterMock);

		boolean matches = hierarchicalRouter.route("/apps/myApp/perspective/myPerspective/items/123", Map.of("filter", "abc"));

		assertThat(matches).isTrue();
		verify(sideEffectMock, times(1)).apply(
				"/apps/myApp/perspective/myPerspective/items/123",
				Map.of("x", "myApp"),
				Map.of("filter", "abc")
		);
		verify(subRouterMock, times(1)).route(
				"/perspective/myPerspective/items/123",
				Map.of("filter", "abc")
		);
	}
}