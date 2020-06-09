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

import java.util.*;

public class ResourceBundleTranslationProvider implements TranslationProvider {

	private List<Locale> languages;
	private List<String> keys;
	private Map<Locale, PropertyResourceBundle> resourceBundleByLocale = new HashMap<>();

	public ResourceBundleTranslationProvider(String baseName, Locale ... languages) {
		this(baseName, "properties", languages);
	}

	public ResourceBundleTranslationProvider(String baseName, String resourceFileSuffix, Locale ... languages) {
		this.languages = Arrays.asList(languages);
		Set<String> allKeys = new HashSet<>();
		for (Locale language : languages) {
			ResourceBundle bundle = ResourceBundle.getBundle(baseName, language, new UTF8Control(resourceFileSuffix));
			if (bundle instanceof PropertyResourceBundle) {
				PropertyResourceBundle propertyResourceBundle = (PropertyResourceBundle) bundle;
				resourceBundleByLocale.put(language, propertyResourceBundle);
				allKeys.addAll(propertyResourceBundle.keySet());
			}
		}
		keys = new ArrayList<>(allKeys);
	}

	@Override
	public List<Locale> getLanguages() {
		return languages;
	}

	@Override
	public List<String> getKeys() {
		return keys;
	}

	@Override
	public String getTranslation(String key, Locale locale) {
		PropertyResourceBundle propertyResourceBundle = resourceBundleByLocale.get(locale);
		if (propertyResourceBundle == null) {
			return null;
		}
		Object value = propertyResourceBundle.handleGetObject(key);
		if (value != null) {
			return (String) value;
		} else {
			return null;
		}
	}
}
