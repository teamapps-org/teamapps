/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
