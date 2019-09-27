package org.teamapps.login;

import org.teamapps.app.*;
import org.teamapps.auth.AuthenticationResult;
import org.teamapps.theme.Theme;
import org.teamapps.app.background.Background;
import org.teamapps.auth.AuthenticationProvider;
import org.teamapps.theme.UserThemeProvider;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.login.LoginWindow;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.time.ZoneId;
import java.util.Locale;

public class LoginHandler<USER> implements ComponentBuilder {

	private final AuthenticationProvider<USER> authenticationProvider;
	private final ComponentBuilder componentBuilder;

	private Theme loginTheme = Theme.create(Background.createDefaultLoginBackground(), false);
	private Theme defaultTheme = Theme.create(Background.createDefaultBackground(), false);
	private UserLocaleProvider<USER> userLocaleProvider;
	private UserTimeZoneProvider<USER> userTimeZoneProvider;
	private UserThemeProvider<USER> userThemeProvider;


	public LoginHandler(AuthenticationProvider<USER> authenticationProvider, ComponentBuilder componentBuilder) {
		this.authenticationProvider = authenticationProvider;
		this.componentBuilder = componentBuilder;
	}

	public LoginHandler(AuthenticationProvider<USER> authenticationProvider, ComponentBuilder componentBuilder, Theme loginTheme, Theme defaultTheme) {
		this.authenticationProvider = authenticationProvider;
		this.componentBuilder = componentBuilder;
		this.loginTheme = loginTheme;
		this.defaultTheme = defaultTheme;
	}

	public LoginHandler(AuthenticationProvider<USER> authenticationProvider, ComponentBuilder componentBuilder, Theme loginTheme, UserLocaleProvider<USER> userLocaleProvider, UserTimeZoneProvider<USER> userTimeZoneProvider, UserThemeProvider<USER> userThemeProvider) {
		this.authenticationProvider = authenticationProvider;
		this.componentBuilder = componentBuilder;
		this.loginTheme = loginTheme;
		this.userLocaleProvider = userLocaleProvider;
		this.userTimeZoneProvider = userTimeZoneProvider;
		this.userThemeProvider = userThemeProvider;
	}

	@Override
	public Component buildComponent(ComponentUpdateHandler updateHandler) {
		SessionContext context = CurrentSessionContext.get();
		loginTheme.getBackground().registerAndApply(context);
		defaultTheme.getBackground().registerBackground(context);
		LoginWindow loginWindow = new LoginWindow();
		loginWindow.onLogin.addListener(loginData -> {
			AuthenticationResult<USER> authenticationResult = authenticationProvider.authenticate(loginData.login, loginData.password);
			if (authenticationResult.isSuccess()) {
				USER authenticatedUser = authenticationResult.getAuthenticatedUser();
				Theme theme = userThemeProvider != null ? userThemeProvider.getUserTheme(authenticatedUser) : defaultTheme;
				updateUserConfiguration(authenticatedUser, theme, context);
				theme.getBackground().registerAndApply(context);
				updateHandler.updateComponent(componentBuilder.buildComponent(updateHandler));
			} else {
				loginWindow.setError();
			}
		});
		return loginWindow.getElegantPanel();
	}

	private void updateUserConfiguration(USER user, Theme theme, SessionContext context) {
		Locale userAgentLocale = Locale.forLanguageTag(context.getClientInfo().getPreferredLanguageIso());
		ZoneId userAgentTimeZone = ZoneId.of(context.getClientInfo().getTimeZone());

		Locale locale = userLocaleProvider != null ? userLocaleProvider.getUserLocale(user, userAgentLocale) : userAgentLocale;
		ZoneId timeZone = userTimeZoneProvider != null ? userTimeZoneProvider.getUserTimeZone(user, userAgentTimeZone) : userAgentTimeZone;
		ApplicationWebController.updateSessionConfiguration(locale, timeZone, theme, context);
	}

	public void setLoginTheme(Theme loginTheme) {
		this.loginTheme = loginTheme;
	}

	public void setDefaultTheme(Theme defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

	public void setUserLocaleProvider(UserLocaleProvider<USER> userLocaleProvider) {
		this.userLocaleProvider = userLocaleProvider;
	}

	public void setUserTimeZoneProvider(UserTimeZoneProvider<USER> userTimeZoneProvider) {
		this.userTimeZoneProvider = userTimeZoneProvider;
	}

	public void setUserThemeProvider(UserThemeProvider<USER> userThemeProvider) {
		this.userThemeProvider = userThemeProvider;
	}
}
