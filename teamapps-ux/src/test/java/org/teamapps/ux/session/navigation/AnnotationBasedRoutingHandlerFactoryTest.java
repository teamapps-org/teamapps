package org.teamapps.ux.session.navigation;

import org.junit.Test;
import org.teamapps.ux.session.navigation.annotation.PathParameter;
import org.teamapps.ux.session.navigation.annotation.QueryParameter;
import org.teamapps.ux.session.navigation.annotation.RoutingPath;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationBasedRoutingHandlerFactoryTest {

	@Test
	public void annotatedMethod() {
		AtomicBoolean wasInvoked = new AtomicBoolean();
		AnnotationBasedRoutingHandlerFactory factory = new AnnotationBasedRoutingHandlerFactory(new ParameterConverterProvider());

		List<AnnotationBasedRoutingHandlerFactory.AnnotationBasedRoutingHandler> routers = factory.createRouters(new Object() {

			@RoutingPath("/apps/{appName}/item/{itemId}")
			public void myMethod(
					@PathParameter("appName") String appName,
					@PathParameter("itemId") Integer id,
					@QueryParameter("flag") Boolean flag) {
				wasInvoked.set(true);
				assertThat(appName).isEqualTo("myApp");
				assertThat(id).isEqualTo(123);
				assertThat(flag).isTrue();
			}
		});

		assertThat(routers).extracting(r -> r.getPathTemplate()).containsExactlyInAnyOrder("/apps/{appName}/item/{itemId}");

		routers.get(0).handle("/apps/myApp/item/123", Map.of("appName", "myApp", "itemId", "123"), Map.of("flag", "true"));

		assertThat(wasInvoked).isTrue();
	}

	@Test
	public void classLevelPathAnnotation() {
		AnnotationBasedRoutingHandlerFactory factory = new AnnotationBasedRoutingHandlerFactory(new ParameterConverterProvider());

		MyRouter router = new MyRouter();
		List<AnnotationBasedRoutingHandlerFactory.AnnotationBasedRoutingHandler> routers = factory.createRouters(router);

		assertThat(routers).extracting(r -> r.getPathTemplate()).containsExactlyInAnyOrder("/foo/{x}/bar/{y}");

		routers.get(0).handle("/foo/111/bar/yyy", Map.of("x", "111", "y", "yyy"), Map.of());

		assertThat(router.wasInvoked).isTrue();
	}

	@RoutingPath("foo/{x}")
	public static class MyRouter {
		volatile boolean wasInvoked = false;

		@RoutingPath("bar/{y}")
		public void myMethod(@PathParameter("x") Integer x, @PathParameter("y") String y) {
			wasInvoked = true;
			assertThat(x).isEqualTo(111);
			assertThat(y).isEqualTo("yyy");
		}
	}
}