package org.teamapps.app.multi;

import org.teamapps.app.ComponentBuilder;
import org.teamapps.app.ComponentUpdateHandler;
import org.teamapps.auth.AuthenticationProvider;
import org.teamapps.auth.SessionAuthenticatedUserResolver;
import org.teamapps.icons.api.Icon;
import org.teamapps.theme.Theme;
import org.teamapps.ux.component.Component;

import java.util.ArrayList;
import java.util.List;

public class StandardMultiApplicationHandler<USER> implements MultiApplicationHandler<USER> {

	private final SessionAuthenticatedUserResolver<USER> sessionAuthenticatedUserResolver;
	private ApplicationAccessProvider<USER> applicationAccessProvider;
	private ComponentBuilder loggedOutComponentBuilder;
	private ApplicationLaunchInfoProvider<USER> appLaunchInfoProvider;
	private List<LogoutHandler<USER>> logoutHandlers;


	public StandardMultiApplicationHandler(SessionAuthenticatedUserResolver<USER> sessionAuthenticatedUserResolver) {
		this(sessionAuthenticatedUserResolver, null);
	}

	public StandardMultiApplicationHandler(AuthenticationProvider<USER> authenticationProvider, ApplicationAccessProvider<USER> applicationAccessProvider) {
		this(authenticationProvider.getSessionAuthenticatedUserResolver(), applicationAccessProvider);
	}

	public StandardMultiApplicationHandler(SessionAuthenticatedUserResolver<USER> sessionAuthenticatedUserResolver, ApplicationAccessProvider<USER> applicationAccessProvider) {
		this.sessionAuthenticatedUserResolver = sessionAuthenticatedUserResolver;
		this.applicationAccessProvider = applicationAccessProvider;
		this.logoutHandlers = new ArrayList<>();
	}

	public void setLoggedOutComponentBuilder(ComponentBuilder loggedOutComponentBuilder) {
		this.loggedOutComponentBuilder = loggedOutComponentBuilder;
	}

	@Override
	public void addApplication(ComponentBuilder componentBuilder, ApplicationGroup applicationGroup, Icon icon, String title, String description, Theme theme) {

	}

	@Override
	public void setAppLaunchInfoProvider(ApplicationLaunchInfoProvider<USER> appLaunchInfoProvider) {
		this.appLaunchInfoProvider = appLaunchInfoProvider;
	}

	@Override
	public void addLogoutHandler(LogoutHandler<USER> logoutHandler) {
		logoutHandlers.add(logoutHandler);
	}

	@Override
	public ApplicationLauncherComponentProvider getApplicationLauncherComponentProvider() {
		return () -> {
			USER user = sessionAuthenticatedUserResolver.getUser();
			return null;
		};
	}

	@Override
	public Component buildComponent(ComponentUpdateHandler updateHandler) {
		USER user = sessionAuthenticatedUserResolver.getUser();
		//applicationAccessProvider.isAccessible()
		return null;
	}

	private void onLogout(USER user, ComponentUpdateHandler updateHandler) {
		logoutHandlers.forEach(handler -> handler.handleUserLogout(user));
		if (loggedOutComponentBuilder != null) {
			updateHandler.updateComponent(loggedOutComponentBuilder.buildComponent(updateHandler));
		}
	}
}
