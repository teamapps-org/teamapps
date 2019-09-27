package org.teamapps.app;

import org.teamapps.app.ComponentBuilder;
import org.teamapps.icons.api.Icon;

public interface MultiApplicationHandler extends ComponentBuilder {

	void addApplication(ComponentBuilder componentBuilder, ApplicationGroup applicationGroup, Icon icon, String title, String description);

}
