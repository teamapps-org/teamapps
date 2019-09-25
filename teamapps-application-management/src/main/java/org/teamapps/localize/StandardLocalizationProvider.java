package org.teamapps.localize;

import org.teamapps.localize.store.LocalizationStore;

import java.util.Locale;
import java.util.ResourceBundle;

public class StandardLocalizationProvider implements LocalizationProvider {

	private final String prefix;
	private final LocalizationStore localizationStore;

	public StandardLocalizationProvider(String prefix, LocalizationStore localizationStore) {
		this.prefix = prefix;
		this.localizationStore = localizationStore;
	}


	@Override
	public String getLocalized(Locale locale, String key) {
		return localizationStore.getLocalization(locale.getLanguage(), prefix, key);
	}

	@Override
	public String getLocalized(Locale locale, String key, Object... parameters) {
		return localizationStore.getLocalization(locale.getLanguage(), prefix, key, parameters);
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return new StandardResourceBundle(locale.getLanguage(), prefix, localizationStore);
	}
}
