package org.teamapps.ux.component;

import org.teamapps.ux.resource.ByteArrayResource;
import org.teamapps.ux.resource.ClassPathResource;
import org.teamapps.ux.resource.Resource;

import java.nio.charset.StandardCharsets;

public class CoreComponentLibrary implements ComponentLibrary{
	@Override
	public Resource getMainJsResource() {
		return new ClassPathResource("org/teamapps/client/commoncomponents/resources/dist/index.js", "text/javascript");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return new ByteArrayResource("console.log('hello from some other library resource!!!');  export var y = 'slkfdjal';".getBytes(StandardCharsets.UTF_8), "asdf.js");
	}
}
