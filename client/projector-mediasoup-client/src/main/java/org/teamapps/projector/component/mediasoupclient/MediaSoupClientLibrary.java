package org.teamapps.projector.component.mediasoupclient;

import org.teamapps.projector.clientobject.ClientObjectLibrary;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;
import org.teamapps.projector.resource.provider.ClassPathResourceProvider;

public class MediaSoupClientLibrary implements ClientObjectLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public MediaSoupClientLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/component/mediasoupclient/resources/js");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/component/mediasoupclient/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/projector/component/mediasoupclient/resources/js/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}

}
