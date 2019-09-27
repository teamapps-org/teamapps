package org.teamapps.app;

import org.teamapps.icons.api.Icon;
import org.teamapps.login.LoginHandler;
import org.teamapps.ux.component.Component;

public class StandardMultiApplicationHandler implements MultiApplicationHandler {

	private final LoginHandler loginHandler;

	public StandardMultiApplicationHandler(LoginHandler loginHandler) {
		this.loginHandler = loginHandler;
	}

	@Override
	public void addApplication(ComponentBuilder componentBuilder, ApplicationGroup applicationGroup, Icon icon, String title, String description) {

	}

	@Override
	public Component buildComponent(ComponentUpdateHandler updateHandler) {
		return null;
	}
}
