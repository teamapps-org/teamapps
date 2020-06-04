package org.teamapps.ux.i18n;

import java.util.*;

public class MapTranslationProvider implements TranslationProvider{

	private Set<String> allKeys;
	private Map<Locale, Map<String, String>> translationsMap;

	public MapTranslationProvider() {
		translationsMap = new HashMap<>();
		allKeys = new HashSet<>();
	}

	public void addTranslation(Locale locale, String key, String value) {
		if (value == null) {
			return;
		}
		allKeys.add(key);
		translationsMap.computeIfAbsent(locale, loc -> new HashMap<>()).put(key, value);
	}


	@Override
	public List<Locale> getLanguages() {
		return new ArrayList<>(translationsMap.keySet());
	}

	@Override
	public List<String> getKeys() {
		return new ArrayList<>(allKeys);
	}

	@Override
	public String getTranslation(String key, Locale locale) {
		if (translationsMap.containsKey(locale)) {
			return translationsMap.get(locale).get(key);
		} else {
			return null;
		}
	}
}
