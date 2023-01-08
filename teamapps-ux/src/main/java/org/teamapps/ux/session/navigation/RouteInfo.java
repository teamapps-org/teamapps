package org.teamapps.ux.session.navigation;

import java.util.Set;

public class RouteInfo {

	private final Route route;
	private final boolean pathChangeWorthStatePush;
	private final Set<String> queryParamNamesWorthStatePush;

	public RouteInfo(Route route, boolean pathChangeWorthStatePush, Set<String> queryParamNamesWorthStatePush) {
		this.route = route;
		this.pathChangeWorthStatePush = pathChangeWorthStatePush;
		this.queryParamNamesWorthStatePush = queryParamNamesWorthStatePush;
	}

	public Route getRoute() {
		return route;
	}

	public boolean isPathChangeWorthStatePush() {
		return pathChangeWorthStatePush;
	}

	public Set<String> getQueryParamNamesWorthStatePush() {
		return queryParamNamesWorthStatePush;
	}
}
