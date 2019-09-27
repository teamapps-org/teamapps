package org.teamapps.app.multi;

import org.teamapps.app.ComponentBuilder;

public interface ApplicationAccessProvider<USER> {

	boolean isAccessible(USER user, ComponentBuilder application);
}
