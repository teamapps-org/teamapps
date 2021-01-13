/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
		return localizationStore.getLocalization(locale.getLanguage(), AbstractLocalizationProviderFactory.createLookupKey(prefix, key));
	}

	@Override
	public String getLocalized(Locale locale, String key, Object... parameters) {
		return localizationStore.getLocalization(locale.getLanguage(), AbstractLocalizationProviderFactory.createLookupKey(prefix, key), parameters);
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return new StandardResourceBundle(locale.getLanguage(), prefix, localizationStore);
	}
}
