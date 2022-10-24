package org.teamapps.ux.session.navigation;

import org.teamapps.ux.session.SessionContext;

import static org.teamapps.ux.session.navigation.RoutingUtil.concatenatePaths;
import static org.teamapps.ux.session.navigation.RoutingUtil.normalizePathPrefix;

public class RelativeRouterRegistrationPoint implements RouterRegistrationPoint {

	private final String pathPrefix;
	private final SessionContext sessionContext;

	public RelativeRouterRegistrationPoint(String pathPrefix) {
		this(pathPrefix, SessionContext.current());
	}

	public RelativeRouterRegistrationPoint(String pathPrefix, SessionContext sessionContext) {
		this.pathPrefix = normalizePathPrefix(pathPrefix);
		this.sessionContext = sessionContext;
	}

	@Override
	public RouterRegistration registerRouter(String pathTemplate, Router router, boolean applyImmediately) {
		return sessionContext.registerRouter(concatenatePaths(pathPrefix, pathTemplate), router, applyImmediately);
	}

	@Override
	public RouterRegistrationPoint createSubRouterRegistrationPoint(String relativePath) {
		return new RelativeRouterRegistrationPoint(concatenatePaths(pathPrefix, relativePath));
	}
}
