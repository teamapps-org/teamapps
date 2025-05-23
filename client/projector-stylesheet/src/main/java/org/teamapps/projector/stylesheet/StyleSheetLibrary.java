package org.teamapps.projector.stylesheet;

import org.teamapps.projector.clientobject.ClientObjectLibrary;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;
import org.teamapps.projector.resource.provider.ClassPathResourceProvider;

public class StyleSheetLibrary implements ClientObjectLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public StyleSheetLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/stylesheet/resources/js");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/stylesheet/resources/js/index.js", "text/javastylesheet");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}

}
