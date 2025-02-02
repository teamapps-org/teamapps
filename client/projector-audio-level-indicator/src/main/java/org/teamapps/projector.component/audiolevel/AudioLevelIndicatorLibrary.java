package org.teamapps.projector.component.audioLevelIndicator;

import org.teamapps.projector.clientobject.ClientObjectLibrary;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;
import org.teamapps.projector.resourceprovider.ClassPathResourceProvider;

public class AudioLevelIndicatorLibrary implements ClientObjectLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public AudioLevelIndicatorLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/component/audioLevelIndicator/resources/js");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/component/audioLevelIndicator/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/projector/component/audioLevelIndicator/resources/js/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}

}
