package org.teamapps.app.multi;

import org.teamapps.app.ComponentBuilder;
import org.teamapps.app.ComponentUpdateHandler;

public interface MultiApplicationHandler<USER> extends ComponentBuilder {

	void addApplication(ComponentBuilder componentBuilder);

	void addLogoutHandler(LogoutHandler<USER> logoutHandler);

	ApplicationLauncherComponentProvider getApplicationLauncherComponentProvider(ComponentUpdateHandler updateHandler);
}
