package org.teamapps.projector.components.common;

import org.teamapps.ux.component.ComponentLibrary;
import org.teamapps.ux.resource.ClassPathResource;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.servlet.resourceprovider.ClassPathResourceProvider;

public class CommonComponentLibrary implements ComponentLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public CommonComponentLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/components/common/resources/js");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/components/common/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/projector/components/common/resources/js/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}
}
