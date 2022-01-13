/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.localize;

import org.teamapps.localize.store.LocalizationStore;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public abstract class AbstractLocalizationProviderFactory implements LocalizationProviderFactory {

	public abstract LocalizationStore getLocalizationStore();

	@Override
	public synchronized LocalizationProvider createLocalizationProvider(ExistingLocalizationsInfo existingLocalizationsInfo) {
		String applicationNamespace = existingLocalizationsInfo.getApplicationNamespace();
		LocalizationStore handler = getLocalizationStore();
		handler.startImportingApplicationNamespace(applicationNamespace);
		existingLocalizationsInfo.getResourceBundleInfos().forEach(resourceBundleInfo -> {
			for (Locale translation : resourceBundleInfo.getTranslations()) {
				ResourceBundle bundle = resourceBundleInfo.getResourceBundleByLocaleFunction().apply(translation);
				if (bundle instanceof PropertyResourceBundle) {
					PropertyResourceBundle propertyResourceBundle = (PropertyResourceBundle) bundle;
					for (String key : bundle.keySet()) {
						Object result = propertyResourceBundle.handleGetObject(key);
						if (result != null) {
							String value = bundle.getString(key);
							if (checkNotEmpty(applicationNamespace, translation.getLanguage(), key, value)) {
								handler.addExistingLocalizationEntry(applicationNamespace, translation.getLanguage(), createLookupKey(applicationNamespace, key), value);
							}
						}
					}
				} else {
					for (String key : bundle.keySet()) {
						String value = bundle.getString(key);
						if (checkNotEmpty(applicationNamespace, translation.getLanguage(), key, value)) {
							handler.addExistingLocalizationEntry(applicationNamespace, translation.getLanguage(), createLookupKey(applicationNamespace, key), value);
						}
					}
				}

			}
		});
		existingLocalizationsInfo.getDictionaries().forEach(dictionary -> {
			handler.addDictionary(applicationNamespace, dictionary.getId());
			String prefix = applicationNamespace + "." + dictionary.getId();
			dictionary.getEntries().forEach(entry -> {
				if (checkNotEmpty(applicationNamespace, dictionary.getId(), dictionary.getLanguage(), entry.getKey(), entry.getValue())) {
					handler.addDictionaryEntry(applicationNamespace, dictionary.getId(), dictionary.getLanguage(), createLookupKey(prefix, entry.getKey()), entry.getValue());
				}
			});
			dictionary.setLocalizationProvider(new StandardLocalizationProvider(prefix, handler));
		});
		handler.finishImportingApplicationNamespace(applicationNamespace);
		return new StandardLocalizationProvider(applicationNamespace, handler);
	}

	public static String createLookupKey(String prefix, String key) {
		return prefix + "." + key;
	}

	private boolean checkNotEmpty(String... values) {
		for (String value : values) {
			boolean result = checkNotEmpty(value);
			if (!result) {
				return false;
			}
		}
		return true;
	}

	private boolean checkNotEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}
