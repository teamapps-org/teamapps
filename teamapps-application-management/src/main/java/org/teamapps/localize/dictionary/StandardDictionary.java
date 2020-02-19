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
package org.teamapps.localize.dictionary;

import org.teamapps.localize.LocalizationProvider;

import java.util.Arrays;
import java.util.List;

public class StandardDictionary implements Dictionary {

	private final String id;
	private final String language;
	private final List<DictionaryEntry> entries;
	private LocalizationProvider localizationProvider;

	public StandardDictionary(String id, String language, DictionaryEntry... entries) {
		this(id, language, Arrays.asList(entries));
	}

	public StandardDictionary(String id, String language, List<DictionaryEntry> entries) {
		this.id = id;
		this.language = language;
		this.entries = entries;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public List<DictionaryEntry> getEntries() {
		return entries;
	}

	@Override
	public void setLocalizationProvider(LocalizationProvider localizationProvider) {
		this.localizationProvider = localizationProvider;
	}

	public LocalizationProvider getLocalizationProvider() {
		return localizationProvider;
	}
}
