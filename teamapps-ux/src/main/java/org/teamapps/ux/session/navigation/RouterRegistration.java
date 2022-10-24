package org.teamapps.ux.session.navigation;

import java.util.Map;

public interface RouterRegistration {

	String createPath(Map<String, String> params); // path and query parameters
	void dispose();

}