/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.ULocale;
import org.teamapps.dto.ClosedSessionHandlingType;
import org.teamapps.dto.UiConfiguration;
import org.teamapps.ux.session.DateTimeFormatDescriptor.FullLongMediumShortType;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.Locale;

public class SessionConfiguration {

	private ULocale locale = ULocale.US;
	private DateTimeFormatDescriptor dateFormat = DateTimeFormatDescriptor.forDate(FullLongMediumShortType.SHORT);
	private DateTimeFormatDescriptor timeFormat = DateTimeFormatDescriptor.forTime(FullLongMediumShortType.SHORT);
	private ZoneId timeZone = ZoneId.of("Europe/Berlin");
	private DayOfWeek firstDayOfWeek; // null == determine by locale

	private boolean optimizedForTouch = false;
	private String iconPath = "/icons";
	private StylingTheme theme = StylingTheme.DEFAULT;
	private ClosedSessionHandlingType closedSessionHandling = ClosedSessionHandlingType.MESSAGE_WINDOW;

	public static SessionConfiguration createForClientInfo(ClientInfo clientInfo) {
		boolean optimizedForTouch = false;
		StylingTheme theme = StylingTheme.DEFAULT;
		if (clientInfo.isMobileDevice()) {
			optimizedForTouch = true;
			theme = StylingTheme.MODERN;
		}
		return SessionConfiguration.create(
				ULocale.forLanguageTag(clientInfo.getPreferredLanguageIso()),
				ZoneId.of(clientInfo.getTimeZone()),
				theme,
				optimizedForTouch
		);
	}

	public static SessionConfiguration create(ULocale locale, ZoneId timeZone, StylingTheme theme, boolean optimizedForTouch) {
		SessionConfiguration config = new SessionConfiguration();
		config.setULocale(locale);
		config.setTimeZone(timeZone);
		config.setTheme(theme);
		config.setOptimizedForTouch(optimizedForTouch);
		return config;
	}

	private static DayOfWeek determineFirstDayOfWeek(ULocale locale) {
		return DayOfWeek.of(GregorianCalendar.getInstance(locale).getFirstDayOfWeek()).minus(1);
	}

	public UiConfiguration createUiConfiguration() {
		UiConfiguration config = new UiConfiguration();
		config.setLocale(locale.toLanguageTag());
		config.setOptimizedForTouch(optimizedForTouch);
		config.setThemeClassName(theme.getCssClass());
		return config;
	}

	public Locale getLocale() {
		return locale.toLocale();
	}

	public ULocale getULocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		setULocale(ULocale.forLocale(locale));
	}

	public void setULocale(ULocale locale) {
		this.locale = locale;
	}

	public DateTimeFormatDescriptor getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateTimeFormatDescriptor dateFormat) {
		this.dateFormat = dateFormat;
	}

	public DateTimeFormatDescriptor getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(DateTimeFormatDescriptor timeFormat) {
		this.timeFormat = timeFormat;
	}

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}

	public DayOfWeek getFirstDayOfWeek() {
		return firstDayOfWeek != null ? firstDayOfWeek : determineFirstDayOfWeek(locale);
	}

	public void setFirstDayOfWeek(DayOfWeek firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
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

	public ClosedSessionHandlingType getClosedSessionHandling() {
		return closedSessionHandling;
	}

	public void setClosedSessionHandling(ClosedSessionHandlingType closedSessionHandling) {
		this.closedSessionHandling = closedSessionHandling;
	}
}
