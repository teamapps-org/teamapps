package org.teamapps.ux.session.navigation;

import org.teamapps.ux.session.SessionContext;

import static org.teamapps.ux.session.navigation.RoutingUtil.concatenatePaths;
import static org.teamapps.ux.session.navigation.RoutingUtil.normalizePath;

public class SubRouter implements Router {

	private final String pathPrefix;
	private final SessionContext sessionContext;

	public SubRouter(String pathPrefix) {
		this(pathPrefix, SessionContext.current());
	}

	public SubRouter(String pathPrefix, SessionContext sessionContext) {
		this.pathPrefix = normalizePath(pathPrefix);
		this.sessionContext = sessionContext;
	}

	@Override
	public RoutingHandlerRegistration registerRoutingHandler(String pathTemplate, boolean exact, RoutingHandler handler, boolean applyImmediately) {
		return sessionContext.registerRoutingHandler(concatenatePaths(pathPrefix, pathTemplate), false, handler, applyImmediately);
	}

	@Override
	public Router createSubRouter(String relativePath) {
		return new SubRouter(concatenatePaths(pathPrefix, relativePath));
	}
}
