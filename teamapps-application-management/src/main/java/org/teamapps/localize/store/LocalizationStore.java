/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
