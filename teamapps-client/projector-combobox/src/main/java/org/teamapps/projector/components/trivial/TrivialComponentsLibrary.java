package org.teamapps.projector.components.trivial;

import org.teamapps.ux.component.ComponentLibrary;
import org.teamapps.ux.resource.ClassPathResource;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.servlet.resourceprovider.ClassPathResourceProvider;

public class TrivialComponentsLibrary implements ComponentLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public TrivialComponentsLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/components/core/resources/js");
	}


	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/components/trivial/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/projector/components/trivial/resources/js/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}
}
