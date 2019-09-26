package org.teamapps.localize;

import org.teamapps.localize.store.LocalizationStore;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class StandardResourceBundle extends ResourceBundle {

	private final String language;
	private final String prefix;
	private final LocalizationStore localizationStore;

	public StandardResourceBundle(String language, String prefix, LocalizationStore localizationStore) {
		this.language = language;
		this.prefix = prefix;
		this.localizationStore = localizationStore;
	}

	@Override
	protected Object handleGetObject(String key) {
		String value = localizationStore.getLocalization(language, prefix, key);
		if (value != null) {
			return value;
		} else {
			return key;
		}
	}

	@Override
	public Enumeration<String> getKeys() {
		return null;
	}
}
