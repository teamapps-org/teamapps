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
