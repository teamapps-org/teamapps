package org.teamapps.projector.template.grid;

import org.teamapps.projector.clientobject.ClientObjectLibrary;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;

public class GridTemplateLibrary implements ClientObjectLibrary {

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/template/grid/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/projector/template/grid/resources/js/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return null;
	}
}
