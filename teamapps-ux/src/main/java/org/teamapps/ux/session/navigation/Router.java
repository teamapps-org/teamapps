package org.teamapps.ux.session.navigation;

import java.util.Map;

public interface Router {

	boolean route(String path, Map<String, String> queryParams);

	default boolean route(Location location) {
		return route(location.getPathname(), RoutingUtil.parseQueryParams(location.getSearch()));
	}

}
