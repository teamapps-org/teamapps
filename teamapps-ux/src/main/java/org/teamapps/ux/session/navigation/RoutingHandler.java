package org.teamapps.ux.session.navigation;

import java.util.Map;

public interface RoutingHandler {

	/**
	 * @param path full path
	 * @param pathParams all path parameters of the whole path template
	 * @param queryParams all query parameters
	 */
	void handle(String path, Map<String, String> pathParams, Map<String, String> queryParams);

}
