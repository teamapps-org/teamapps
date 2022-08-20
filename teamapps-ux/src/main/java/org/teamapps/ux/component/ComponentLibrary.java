package org.teamapps.ux.component;

import org.teamapps.ux.resource.Resource;

public interface ComponentLibrary {

	Resource getMainJsResource();

	Resource getResource(String pathInfo);

}
