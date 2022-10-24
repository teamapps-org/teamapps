package org.teamapps.ux.session.navigation;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationBasedRouterFactoryTest {

	@Test
	public void annotatedMethod() {
		AtomicBoolean wasInvoked = new AtomicBoolean();
		AnnotationBasedRouterFactory factory = new AnnotationBasedRouterFactory(new ParameterConverterProvider());

		List<AnnotationBasedRouterFactory.AnnotationBasedRouter> routers = factory.createRouters(new Object() {

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
		});

		assertThat(routers).extracting(r -> r.getPathTemplate()).containsExactlyInAnyOrder("/apps/{appName}/item/{itemId}");

		routers.get(0).route("/apps/myApp/item/123", Map.of("appName", "myApp", "itemId", "123"), Map.of("flag", "true"));

		assertThat(wasInvoked).isTrue();
	}

	@Test
	public void classLevelPathAnnotation() {
		AnnotationBasedRouterFactory factory = new AnnotationBasedRouterFactory(new ParameterConverterProvider());

		MyRouter router = new MyRouter();
		List<AnnotationBasedRouterFactory.AnnotationBasedRouter> routers = factory.createRouters(router);

		assertThat(routers).extracting(r -> r.getPathTemplate()).containsExactlyInAnyOrder("/foo/{x}/bar/{y}");

		routers.get(0).route("/foo/111/bar/yyy", Map.of("x", "111", "y", "yyy"), Map.of());

		assertThat(router.wasInvoked).isTrue();
	}

	@Path("foo/{x}")
	public static class MyRouter {
		volatile boolean wasInvoked = false;

		@Path("bar/{y}")
		public void myMethod(@PathParam("x") Integer x, @PathParam("y") String y) {
			wasInvoked = true;
			assertThat(x).isEqualTo(111);
			assertThat(y).isEqualTo("yyy");
		}
	}
}