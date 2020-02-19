/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
