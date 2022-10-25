package org.teamapps.ux.session.navigation;

import org.teamapps.ux.session.SessionContext;

import java.util.Map;
import java.util.stream.Collectors;

public interface Router {
	
	RoutingHandlerRegistration registerRoutingHandler(String pathTemplate, RoutingHandler handler, boolean applyImmediately);

	default Map<String, RoutingHandlerRegistration> registerRoutingHandlers(Object annotatedClassInstance, boolean applyImmediately) {
		return new AnnotationBasedRoutingHandlerFactory(SessionContext.current().getRoutingParamConverterProvider())
				.createRouters(annotatedClassInstance).stream()
				.collect(Collectors.toMap(
						r -> r.getPathTemplate(),
						r -> registerRoutingHandler(r.getPathTemplate(), r, applyImmediately))
				);
	}

	Router createSubRouter(String relativePath);

}