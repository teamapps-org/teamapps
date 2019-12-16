package org.teamapps.localize;

import org.teamapps.ux.session.SessionContext;

import java.util.Locale;
import java.util.ResourceBundle;

public interface LocalizationProvider {

	default boolean matchesLocalization(String key, String query) {
		if (query == null || query.isEmpty()) {
			return true;
		}
		return getLocalizedOrEmptyString(key).toLowerCase().contains(query.toLowerCase());
	}

	default String getLocalizedOrEmptyString(String key) {
		String localized = getLocalized(key);
		return localized != null ? localized : "";
	}

	default String getLocalized(String key) {
		return getLocalized(SessionContext.current().getLocale(), key);
	}

	default String getLocalized(String key, Object... parameters) {
		return getLocalized(SessionContext.current().getLocale(), key, parameters);
	}

	String getLocalized(Locale locale, String key);

	String getLocalized(Locale locale, String key, Object... parameters);

	default ResourceBundle getResourceBundle() {
		return getResourceBundle(SessionContext.current().getLocale());
	}

	ResourceBundle getResourceBundle(Locale locale);

}
