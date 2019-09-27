package org.teamapps.login;

import org.teamapps.app.*;
import org.teamapps.auth.AuthenticationResult;
import org.teamapps.localize.LocaleProvider;
import org.teamapps.localize.TimeZoneProvider;
import org.teamapps.theme.Theme;
import org.teamapps.theme.background.Background;
import org.teamapps.auth.AuthenticationProvider;
import org.teamapps.theme.ThemeProvider;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.login.LoginWindow;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.ux.session.StylingTheme;

import java.time.ZoneId;
import java.util.Locale;

public class LoginHandler<USER> implements ComponentBuilder {

	private final AuthenticationProvider<USER> authenticationProvider;
	private final ComponentBuilder componentBuilder;

	private Theme loginTheme = Theme.create(Background.createDefaultLoginBackground(), false);
	private Theme defaultTheme = Theme.create(Background.createDefaultBackground(), false);
	private LocaleProvider<USER> localeProvider;
	private TimeZoneProvider<USER> timeZoneProvider;
	private ThemeProvider<USER> themeProvider;


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

	public LoginHandler(AuthenticationProvider<USER> authenticationProvider, ComponentBuilder componentBuilder, Theme loginTheme, LocaleProvider<USER> localeProvider, TimeZoneProvider<USER> timeZoneProvider, ThemeProvider<USER> themeProvider) {
		this.authenticationProvider = authenticationProvider;
		this.componentBuilder = componentBuilder;
		this.loginTheme = loginTheme;
		this.localeProvider = localeProvider;
		this.timeZoneProvider = timeZoneProvider;
		this.themeProvider = themeProvider;
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
				Theme theme = themeProvider != null ? themeProvider.getUserTheme(authenticatedUser) : defaultTheme;
				updateUserConfiguration(authenticatedUser, theme, context);
				theme.getBackground().registerAndApply(context);
				updateHandler.updateComponent(componentBuilder.buildComponent(updateHandler));
			} else {
				loginWindow.setError();
			}
		});
		return loginWindow.getElegantPanel();
	}

	public void updateUserTheme(Theme theme) {
		SessionContext context = CurrentSessionContext.get();
		StylingTheme stylingTheme = context.getConfiguration().getTheme();
		if ((theme.isDarkTheme() && stylingTheme != StylingTheme.DARK) ||(!theme.isDarkTheme() && stylingTheme == StylingTheme.DARK)) {
			USER user = getAuthenticatedUser(context);
			updateUserConfiguration(user, theme, context);
		}
		theme.getBackground().registerAndApply(context);
	}

	public USER getAuthenticatedUser(SessionContext context) {
		return authenticationProvider.getSessionAuthenticatedUserResolver().getUser(context);
	}

	private void updateUserConfiguration(USER user, Theme theme, SessionContext context) {
		Locale userAgentLocale = Locale.forLanguageTag(context.getClientInfo().getPreferredLanguageIso());
		ZoneId userAgentTimeZone = ZoneId.of(context.getClientInfo().getTimeZone());

		Locale locale = localeProvider != null ? localeProvider.getUserLocale(user, userAgentLocale) : userAgentLocale;
		ZoneId timeZone = timeZoneProvider != null ? timeZoneProvider.getUserTimeZone(user, userAgentTimeZone) : userAgentTimeZone;
		ApplicationWebController.updateSessionConfiguration(locale, timeZone, theme, context);
	}

	public void setLoginTheme(Theme loginTheme) {
		this.loginTheme = loginTheme;
	}

	public void setDefaultTheme(Theme defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

	public void setLocaleProvider(LocaleProvider<USER> localeProvider) {
		this.localeProvider = localeProvider;
	}

	public void setTimeZoneProvider(TimeZoneProvider<USER> timeZoneProvider) {
		this.timeZoneProvider = timeZoneProvider;
	}

	public void setThemeProvider(ThemeProvider<USER> themeProvider) {
		this.themeProvider = themeProvider;
	}
}
