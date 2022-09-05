package org.teamapps.ux.component;

import org.teamapps.ux.resource.ByteArrayResource;
import org.teamapps.ux.resource.Resource;

import java.nio.charset.StandardCharsets;

public class CoreComponentLibrary implements ComponentLibrary{
	@Override
	public Resource getMainJsResource() {
		return new ByteArrayResource("console.log('hello from the library index.js!!!'); export var x = 123;".getBytes(StandardCharsets.UTF_8), "index.js");
	}

	@Override
	public Resource getResource(String pathInfo) {
		return new ByteArrayResource("console.log('hello from some other library resource!!!');  export var y = 'slkfdjal';".getBytes(StandardCharsets.UTF_8), "asdf.js");
	}
}
