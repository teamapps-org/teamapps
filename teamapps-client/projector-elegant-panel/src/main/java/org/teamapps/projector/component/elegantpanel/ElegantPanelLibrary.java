package org.teamapps.projector.component.elegantpanel;

import org.teamapps.projector.clientobject.ClientObjectLibrary;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;
import org.teamapps.projector.resourceprovider.ClassPathResourceProvider;

public class ElegantPanelLibrary implements ClientObjectLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public ElegantPanelLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/components/elegantpanel/resources/js");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/components/elegantpanel/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/projector/components/elegantpanel/resources/js/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}
}
