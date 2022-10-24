package org.teamapps.ux.session.navigation;

import org.teamapps.ux.session.SessionContext;

import java.util.Map;
import java.util.stream.Collectors;

public interface RouterRegistrationPoint {
	/**
	 * Just like in SessionContext, but with relative path templates!
	 * Note that the returned RouterRegistration can be the one returned by the SessionContext. 
	 */
	RouterRegistration registerRouter(String pathTemplate, Router router, boolean applyImmediately);

	/**
	 * Register an instance of an annotated class as router(s).
	 *
	 * @param annotatedClassInstance instance of an annotated class
	 * @return a {@link Map} from path template to RouterRegistration
	 * @see AnnotationBasedRouterFactory
	 */
	default Map<String, RouterRegistration> registerRouters(Object annotatedClassInstance, boolean applyImmediately) {
		return new AnnotationBasedRouterFactory(SessionContext.current().getRoutingParamConverterProvider())
				.createRouters(annotatedClassInstance).stream()
				.collect(Collectors.toMap(
						r -> r.getPathTemplate(),
						r -> registerRouter(r.getPathTemplate(), r, applyImmediately))
				);
	}

	RouterRegistrationPoint createSubRouterRegistrationPoint(String relativePath);


}