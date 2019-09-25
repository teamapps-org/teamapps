package org.teamapps.localize;

import org.teamapps.ux.session.CurrentSessionContext;

import java.util.Locale;
import java.util.ResourceBundle;

public interface LocalizationProvider {

	default String getLocalized(String key) {
		return getLocalized(CurrentSessionContext.get().getLocale(), key);
	}

	default String getLocalized(String key, Object... parameters) {
		return getLocalized(CurrentSessionContext.get().getLocale(), key, parameters);
	}

	String getLocalized(Locale locale, String key);

	String getLocalized(Locale locale, String key, Object... parameters);

	default ResourceBundle getResourceBundle() {
		return getResourceBundle(CurrentSessionContext.get().getLocale());
	}

	ResourceBundle getResourceBundle(Locale locale);

}
