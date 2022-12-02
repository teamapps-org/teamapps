package org.teamapps.ux.component;

import org.teamapps.ux.resource.ClassPathResource;
import org.teamapps.ux.resource.Resource;
import org.teamapps.ux.servlet.resourceprovider.ClassPathResourceProvider;

public class CommonComponentLibrary implements ComponentLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public CommonComponentLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/client/commoncomponents/resources/dist");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/client/commoncomponents/resources/dist/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/client/commoncomponents/resources/dist/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}
}
