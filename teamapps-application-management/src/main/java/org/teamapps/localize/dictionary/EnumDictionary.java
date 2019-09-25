package org.teamapps.localize.dictionary;

import org.teamapps.ux.session.CurrentSessionContext;

import java.util.Locale;

public interface EnumDictionary extends DictionaryEntry {

	Dictionary getDictionary();

	default String localized() {
		return getLocalized();
	}

	default String getLocalized() {
		return getLocalized(CurrentSessionContext.get().getLocale());
	}

	default String getLocalized(Object... parameters) {
		return getLocalized(CurrentSessionContext.get().getLocale(), parameters);
	}

	default String getLocalized(Locale locale) {
		return getLocalized(locale, null);
	}

	default String getLocalized(Locale locale, Object... parameters) {
		if (getDictionary().getLocalizationProvider() == null) {
			return getValue();
		} else {
			return getDictionary().getLocalizationProvider().getLocalized(locale, getKey(), parameters);
		}
	}
}
