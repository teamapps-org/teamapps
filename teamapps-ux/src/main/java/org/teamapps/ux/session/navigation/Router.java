package org.teamapps.ux.session.navigation;

import org.teamapps.ux.session.SessionContext;

import java.util.Map;
import java.util.stream.Collectors;

public interface Router {

	default RoutingHandlerRegistration registerRoutingHandler(String pathTemplate, RoutingHandler handler) {
		return registerRoutingHandler(pathTemplate, false, handler, true);
	}

	RoutingHandlerRegistration registerRoutingHandler(String pathTemplate, boolean exact, RoutingHandler handler, boolean applyImmediately);

	default Map<String, RoutingHandlerRegistration> registerRoutingHandlers(Object annotatedClassInstance, boolean applyImmediately) {
		return new AnnotationBasedRoutingHandlerFactory(SessionContext.current().getRoutingParamConverterProvider())
				.createRouters(annotatedClassInstance).stream()
				.collect(Collectors.toMap(
						handler -> handler.getPathTemplate(),
						handler -> registerRoutingHandler(handler.getPathTemplate(), handler.isExact(), handler, applyImmediately))
				);
	}

	Router createSubRouter(String pathPrefix);

}