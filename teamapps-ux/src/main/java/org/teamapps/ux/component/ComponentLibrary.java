package org.teamapps.ux.component;

import org.teamapps.ux.resource.Resource;

public interface ComponentLibrary {

	Resource getMainJsResource();

	default Resource getMainCssResource() {
		return null;
	}

	Resource getResource(String pathInfo);

}
