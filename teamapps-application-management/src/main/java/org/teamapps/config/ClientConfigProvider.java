package org.teamapps.config;

import org.teamapps.theme.Theme;
import org.teamapps.ux.session.SessionConfiguration;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.ux.session.StylingTheme;

import java.time.ZoneId;
import java.util.Locale;

public interface ClientConfigProvider<USER> extends LocaleProvider<USER>, TimeZoneProvider<USER>, ThemeProvider<USER> {

	static <USER> ClientConfigProvider create(LocaleProvider<USER> localeProvider, TimeZoneProvider<USER> timeZoneProvider, ThemeProvider<USER> themeProvider) {
		return new StandardClientConfigProvider<USER>(localeProvider, timeZoneProvider, themeProvider);
	}

	static <USER> ClientConfigProvider create(LocaleProvider<USER> localeProvider) {
		return new StandardClientConfigProvider<USER>(localeProvider, null, null);
	}

	static <USER> ClientConfigProvider create(TimeZoneProvider<USER> timeZoneProvider) {
		return new StandardClientConfigProvider<USER>(null, timeZoneProvider, null);
	}

	static <USER> ClientConfigProvider create(ThemeProvider<USER> themeProvider) {
		return new StandardClientConfigProvider<USER>(null, null, themeProvider);
	}

	static <USER> ClientConfigProvider create() {
		return new StandardClientConfigProvider<USER>();
	}

	void setUserTheme(USER user, Theme theme);

	void updateClientConfiguration(USER user);

	void setClientSessionConfiguration(Locale locale, ZoneId timeZone, Theme theme);

	void setLocaleProvider(LocaleProvider<USER> localeProvider);

	void setTimeZoneProvider(TimeZoneProvider<USER> timeZoneProvider);

	void setThemeProvider(ThemeProvider<USER> themeProvider);

	static SessionConfiguration createUserAgentSessionConfiguration(boolean darkTheme, SessionContext context) {
		boolean optimizedForTouch = false;
		StylingTheme theme = StylingTheme.DEFAULT;
		if (context.getClientInfo().isMobileDevice()) {
			optimizedForTouch = true;
			theme = StylingTheme.MODERN;
		}
		if (darkTheme) {
			theme = StylingTheme.DARK;
		}

		Locale locale = Locale.forLanguageTag(context.getClientInfo().getPreferredLanguageIso());
		return SessionConfiguration.create(
				locale,
				ZoneId.of(context.getClientInfo().getTimeZone()),
				theme,
				optimizedForTouch
		);
	}


}
