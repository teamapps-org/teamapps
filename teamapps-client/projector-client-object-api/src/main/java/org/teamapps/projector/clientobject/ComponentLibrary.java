package org.teamapps.projector.clientobject;

import org.teamapps.projector.resource.Resource;

public interface ComponentLibrary {

	Resource getMainJsResource();

	default Resource getMainCssResource() {
		return null;
	}

	Resource getResource(String pathInfo);

}
