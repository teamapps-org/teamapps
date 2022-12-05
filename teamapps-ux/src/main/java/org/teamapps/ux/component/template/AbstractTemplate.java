package org.teamapps.ux.component.template;

import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

import java.util.UUID;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public abstract class AbstractTemplate implements Template {

	private final String id = getClass().getSimpleName() + "-" + UUID.randomUUID();

	@Override
	public String getId() {
		return id;
	}
}
