/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import com.ibm.icu.util.ULocale;
import org.teamapps.theme.Theme;
import org.teamapps.theme.background.Background;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionConfiguration;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.ux.session.StylingTheme;

import java.time.ZoneId;
import java.util.Locale;

public class StandardClientConfigProvider<USER> implements ClientConfigProvider<USER>{

	private LocaleProvider<USER> localeProvider;
	private TimeZoneProvider<USER> timeZoneProvider;
	private ThemeProvider<USER> themeProvider;
	private Theme defaultTheme = Theme.create(Background.createDefaultBackground(), false);

	public StandardClientConfigProvider() {

	}

	public StandardClientConfigProvider(LocaleProvider<USER> localeProvider, TimeZoneProvider<USER> timeZoneProvider, ThemeProvider<USER> themeProvider) {
		this.localeProvider = localeProvider;
		this.timeZoneProvider = timeZoneProvider;
		this.themeProvider = themeProvider;
	}

	@Override
	public void setUserTheme(USER user, Theme theme) {
		SessionContext context = CurrentSessionContext.get();
		StylingTheme stylingTheme = context.getConfiguration().getTheme();
		if ((theme.isDarkTheme() && stylingTheme != StylingTheme.DARK) ||(!theme.isDarkTheme() && stylingTheme == StylingTheme.DARK)) {
			updateUserThemeConfiguration(user, theme); //todo session context must provide setTheme()
		}
		theme.getBackground().registerAndApply(context);
	}

	private void updateUserThemeConfiguration(USER user, Theme theme) {
		SessionContext context = CurrentSessionContext.get();
		Locale userAgentLocale = Locale.forLanguageTag(context.getClientInfo().getPreferredLanguageIso());
		ZoneId userAgentTimeZone = ZoneId.of(context.getClientInfo().getTimeZone());
		Locale locale = getUserLocale(user, userAgentLocale);
		ZoneId timeZone = getUserTimeZone(user, userAgentTimeZone);
		setClientSessionConfiguration(locale, timeZone, theme);
	}

	@Override
	public void updateClientConfiguration(USER user) {
		SessionContext context = CurrentSessionContext.get();
		Locale userAgentLocale = Locale.forLanguageTag(context.getClientInfo().getPreferredLanguageIso());
		ZoneId userAgentTimeZone = ZoneId.of(context.getClientInfo().getTimeZone());

		Locale locale = getUserLocale(user, userAgentLocale);
		ZoneId timeZone = getUserTimeZone(user, userAgentTimeZone);
		Theme theme = getUserTheme(user);

		setClientSessionConfiguration(locale, timeZone, theme);
	}


	@Override
	public void setClientSessionConfiguration(Locale locale, ZoneId timeZone, Theme theme) {
		SessionContext context = CurrentSessionContext.get();
		boolean darkTheme = false;
		if (theme != null) {
			darkTheme = theme.isDarkTheme();
		}
		boolean optimizedForTouch = false;
		StylingTheme stylingTheme = StylingTheme.DEFAULT;
		if (context.getClientInfo().isMobileDevice()) {
			optimizedForTouch = true;
			stylingTheme = StylingTheme.MODERN;
		}
		if (darkTheme) {
			stylingTheme = StylingTheme.DARK;
		}
		context.setConfiguration(SessionConfiguration.create(
				ULocale.forLocale(locale),
				timeZone,
				stylingTheme,
				optimizedForTouch
		));
	}

	public void setDefaultTheme(Theme defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

	@Override
	public Locale getUserLocale(USER user, Locale userAgentLocale) {
		return localeProvider != null ? localeProvider.getUserLocale(user, userAgentLocale) : userAgentLocale;
	}

	@Override
	public Theme getUserTheme(USER user) {
		return themeProvider != null ? themeProvider.getUserTheme(user) : defaultTheme;
	}

	@Override
	public ZoneId getUserTimeZone(USER user, ZoneId userAgentTimeZone) {
		return timeZoneProvider != null ? timeZoneProvider.getUserTimeZone(user, userAgentTimeZone) : userAgentTimeZone;
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
