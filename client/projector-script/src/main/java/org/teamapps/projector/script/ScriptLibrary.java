package org.teamapps.projector.script;

import org.teamapps.projector.clientobject.ClientObjectLibrary;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;
import org.teamapps.projector.resourceprovider.ClassPathResourceProvider;

public class ScriptLibrary implements ClientObjectLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public ScriptLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/script/resources/js");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/script/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}

}
