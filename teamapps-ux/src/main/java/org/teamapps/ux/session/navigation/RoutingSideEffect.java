package org.teamapps.ux.session.navigation;

import java.util.Map;

public interface RoutingSideEffect {

	void apply(String path, Map<String, String> pathParams, Map<String, String> queryParams);

}
