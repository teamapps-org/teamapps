package org.teamapps.projector.components.infinitescroll.table;

import org.teamapps.projector.clientobject.ComponentLibrary;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;
import org.teamapps.ux.servlet.resourceprovider.ClassPathResourceProvider;

public class InfiniteScrollingComponentLibrary implements ComponentLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public InfiniteScrollingComponentLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/components/infinitescroll/resources/js");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/components/infinitescroll/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/projector/components/infinitescroll/resources/js/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}
}