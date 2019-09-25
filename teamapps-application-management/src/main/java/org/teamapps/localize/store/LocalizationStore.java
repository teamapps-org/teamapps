package org.teamapps.localize.store;

import java.text.MessageFormat;
import java.util.List;

public interface LocalizationStore {

	default String getLocalization(String language, String lookupKey, Object... parameters) {
		String value = getLocalization(language, lookupKey);
		return parameters != null ? MessageFormat.format(value, parameters) : value;
	}

	String getLocalization(String language, String lookupKey);

	void addTranslationResult(String language, String lookupKey, String value);

	void startImportingApplicationNamespace(String applicationNamespace);

	void addExistingLocalizationEntry(String applicationNamespace, String language, String lookupKey, String value);

	void addDictionary(String applicationNamespace, String dictionaryId);

	void addDictionaryEntry(String applicationNamespace, String dictionaryId, String language, String lookupKey, String value);

	void finishImportingApplicationNamespace(String applicationNamespace);

	void finishStoreUpdates();
	
	List<String> getAllUsedStoreKeys();

	List<String> getAllUsedLanguages();
}
