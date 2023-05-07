package org.teamapps.ux.component.template;

import java.util.UUID;

public abstract class AbstractTemplate implements Template {

	private final String id = getClass().getSimpleName() + "-" + UUID.randomUUID();

	@Override
	public String getId() {
		return id;
	}
}
