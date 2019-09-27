package org.teamapps.app;

import org.teamapps.ux.component.Component;

public interface ComponentBuilder {

	Component buildComponent(ComponentUpdateHandler updateHandler);
}
