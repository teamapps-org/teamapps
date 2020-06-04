package org.teamapps.ux.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public interface TranslationProvider {

	static TranslationProvider createFromResourceBundle(String baseName, Locale... languages) {
		return createFromResourceBundle(baseName, "properties", languages);
	}

	static TranslationProvider createFromResourceBundle(String baseName, String resourceFileSuffix, Locale... languages) {
		return new ResourceBundleTranslationProvider(baseName, resourceFileSuffix, languages);
	}

	List<Locale> getLanguages();

	List<String> getKeys();

	default List<String> getKeys(Locale locale) {
		return getKeys().stream()
				.filter(key -> getTranslation(key, locale) != null)
				.collect(Collectors.toList());
	}

	String getTranslation(String key, Locale locale);

	default String getTranslation(String key, List<Locale> acceptedLanguages) {
		for (Locale locale : acceptedLanguages) {
			String translation = getTranslation(key, locale);
			if (translation != null) {
				return translation;
			}
		}
		return null;
	}

	default String getTranslation(String key, Locale... acceptedLanguages) {
		return getTranslation(key, Arrays.asList(acceptedLanguages));
	}
}
