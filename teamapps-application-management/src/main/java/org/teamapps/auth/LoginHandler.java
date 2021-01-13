/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.auth;

import org.teamapps.app.ComponentBuilder;
import org.teamapps.app.ComponentUpdateHandler;
import org.teamapps.config.ClientConfigProvider;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.theme.Theme;
import org.teamapps.theme.background.Background;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.login.LoginWindow;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

public class LoginHandler<USER> implements ComponentBuilder {

	private final AuthenticationProvider<USER> authenticationProvider;
	private ComponentBuilder componentBuilder;
	private ClientConfigProvider<USER> clientConfigProvider;

	private Theme loginTheme;

	public LoginHandler(AuthenticationProvider<USER> authenticationProvider) {
		this(authenticationProvider, null, null, null);
	}

	public LoginHandler(AuthenticationProvider<USER> authenticationProvider, ComponentBuilder componentBuilder) {
		this(authenticationProvider, componentBuilder, null, null);
	}

	public LoginHandler(AuthenticationProvider<USER> authenticationProvider, ComponentBuilder componentBuilder, Theme loginTheme) {
		this(authenticationProvider, componentBuilder, loginTheme, null);
	}

	public LoginHandler(AuthenticationProvider<USER> authenticationProvider, ComponentBuilder componentBuilder, Theme loginTheme, ClientConfigProvider<USER> clientConfigProvider) {
		this.authenticationProvider = authenticationProvider;
		this.componentBuilder = componentBuilder != null ? componentBuilder : updateHandler -> new Panel(MaterialIcon.ERROR, "No result component builder registered");
		this.loginTheme = loginTheme != null ? loginTheme : Theme.create(Background.createDefaultLoginBackground(), false);
		this.clientConfigProvider = clientConfigProvider != null ? clientConfigProvider : ClientConfigProvider.create();
	}

	@Override
	public Component buildComponent(ComponentUpdateHandler updateHandler) {
		SessionContext context = CurrentSessionContext.get();
		loginTheme.getBackground().registerAndApply(context);
		LoginWindow loginWindow = new LoginWindow();
		loginWindow.onLogin.addListener(loginData -> {
			AuthenticationResult<USER> authenticationResult = authenticationProvider.authenticate(loginData.login, loginData.password);
			if (authenticationResult.isSuccess()) {
				USER authenticatedUser = authenticationResult.getAuthenticatedUser();
				clientConfigProvider.updateClientConfiguration(authenticatedUser);
				Theme theme = clientConfigProvider.getUserTheme(authenticatedUser);
				clientConfigProvider.setUserTheme(authenticatedUser, theme);
				theme.getBackground().registerAndApply(context);
				updateHandler.updateComponent(componentBuilder.buildComponent(updateHandler));
			} else {
				loginWindow.setError();
			}
		});
		return loginWindow.getElegantPanel();
	}

	public USER getAuthenticatedUser(SessionContext context) {
		return authenticationProvider.getSessionAuthenticatedUserResolver().getUser(context);
	}

	public void setComponentBuilder(ComponentBuilder componentBuilder) {
		this.componentBuilder = componentBuilder;
	}

	public void setLoginTheme(Theme loginTheme) {
		this.loginTheme = loginTheme;
	}

	public void setClientConfigProvider(ClientConfigProvider<USER> clientConfigProvider) {
		this.clientConfigProvider = clientConfigProvider;
	}

	public AuthenticationProvider<USER> getAuthenticationProvider() {
		return authenticationProvider;
	}

	public ClientConfigProvider<USER> getClientConfigProvider() {
		return clientConfigProvider;
	}
}
