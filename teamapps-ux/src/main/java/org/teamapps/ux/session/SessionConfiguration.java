/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.session;

import org.teamapps.dto.UiConfiguration;
import org.teamapps.dto.UiWeekDay;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;

public class SessionConfiguration {

	private Locale languageLocale = Locale.ENGLISH;
	private ZoneId timeZone = ZoneId.of("Europe/Berlin");
	private String dateFormat = "yyyy-MM-dd";
	private String timeFormat = "HH:mm";
	private UiWeekDay firstDayOfWeek = UiWeekDay.MONDAY;
	private String decimalSeparator = ".";
	private String thousandsSeparator = "";

	private boolean optimizedForTouch = false;
	private String iconPath = "icons";
	private StylingTheme theme = StylingTheme.DEFAULT;

	public static SessionConfiguration createDefault() {
		return new SessionConfiguration();
	}

	public static SessionConfiguration create(Locale userLocale, ZoneId timeZone, StylingTheme theme, boolean optimizedForTouch) {
		SessionConfiguration config = new SessionConfiguration();
		config.setLanguageLocale(userLocale);
		config.setIconPath("icons");
		config.setTimeZone(timeZone);

		String dateFormat = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, userLocale)).toPattern();
		config.setDateFormat(dateFormat);

		String timeFormat = ((SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT, userLocale)).toPattern();
		config.setTimeFormat(timeFormat);

		DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) DecimalFormat.getInstance(userLocale)).getDecimalFormatSymbols();
		config.setThousandsSeparator("" + decimalFormatSymbols.getGroupingSeparator());
		config.setDecimalSeparator("" + decimalFormatSymbols.getDecimalSeparator());

		config.setTheme(theme);
		config.setOptimizedForTouch(optimizedForTouch);
		return config;
	}

	public UiConfiguration createUiConfiguration() {
		UiConfiguration config = new UiConfiguration();
		config.setIsoLanguage(languageLocale.getLanguage());
		config.setTimeZoneId(timeZone.getId());
		config.setDateFormat(dateFormat);
		config.setTimeFormat(timeFormat);
		config.setFirstDayOfWeek(firstDayOfWeek);
		config.setDecimalSeparator(decimalSeparator);
		config.setThousandsSeparator(thousandsSeparator);
		config.setOptimizedForTouch(optimizedForTouch);
		config.setIconPath(iconPath);
		config.setThemeClassName(theme.getCssClass());
		return config;
	}

	public Locale getLanguageLocale() {
		return languageLocale;
	}

	public void setLanguageLocale(Locale languageLocale) {
		this.languageLocale = languageLocale;
	}

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public UiWeekDay getFirstDayOfWeek() {
		return firstDayOfWeek;
	}

	public void setFirstDayOfWeek(UiWeekDay firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public String getThousandsSeparator() {
		return thousandsSeparator;
	}

	public void setThousandsSeparator(String thousandsSeparator) {
		this.thousandsSeparator = thousandsSeparator;
	}

	public boolean isOptimizedForTouch() {
		return optimizedForTouch;
	}

	public void setOptimizedForTouch(boolean optimizedForTouch) {
		this.optimizedForTouch = optimizedForTouch;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public StylingTheme getTheme() {
		return theme;
	}

	public void setTheme(StylingTheme theme) {
		this.theme = theme;
	}
}
