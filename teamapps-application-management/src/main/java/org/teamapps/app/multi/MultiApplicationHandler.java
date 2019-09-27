package org.teamapps.app.multi;

import org.teamapps.app.ComponentBuilder;
import org.teamapps.icons.api.Icon;
import org.teamapps.theme.Theme;

public interface MultiApplicationHandler<USER> extends ComponentBuilder {

	default void addApplication(ComponentBuilder componentBuilder, Icon icon, String title, String description) {
		addApplication(componentBuilder, null, icon, title, description, null);
	}

	default void addApplication(ComponentBuilder componentBuilder, ApplicationGroup applicationGroup, Icon icon, String title, String description) {
		addApplication(componentBuilder, applicationGroup, icon, title, description, null);
	}

	void addApplication(ComponentBuilder componentBuilder, ApplicationGroup applicationGroup, Icon icon, String title, String description, Theme theme);

	void setAppLaunchInfoProvider(ApplicationLaunchInfoProvider<USER> appLaunchInfoProvider);

	void addLogoutHandler(LogoutHandler<USER> logoutHandler);

	ApplicationLauncherComponentProvider getApplicationLauncherComponentProvider();
}
