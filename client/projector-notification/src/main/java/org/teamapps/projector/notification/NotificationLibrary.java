package org.teamapps.projector.notification;

import org.teamapps.projector.clientobject.ClientObjectLibrary;
import org.teamapps.projector.resource.ClassPathResource;
import org.teamapps.projector.resource.Resource;
import org.teamapps.projector.resource.provider.ClassPathResourceProvider;

public class NotificationLibrary implements ClientObjectLibrary {

	private final ClassPathResourceProvider resourceProvider;

	public NotificationLibrary() {
		this.resourceProvider = new ClassPathResourceProvider("org/teamapps/projector/component/notification/resources/js");
	}

	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/projector/component/notification/resources/js/index.js", "text/javascript");
	}

	@Override
	public Resource getMainCssResource() {
		return new ClassPathResource("org/teamapps/projector/component/notification/resources/js/index.css", "text/css");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return resourceProvider.getResource(null, pathInfo, null);
	}

}
