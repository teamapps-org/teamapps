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
package org.teamapps.localize;

import org.teamapps.localize.store.LocalizationStore;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class StandardResourceBundle extends ResourceBundle {

	private final String language;
	private final String prefix;
	private final LocalizationStore localizationStore;

	public StandardResourceBundle(String language, String prefix, LocalizationStore localizationStore) {
		this.language = language;
		this.prefix = prefix;
		this.localizationStore = localizationStore;
	}

	@Override
	protected Object handleGetObject(String key) {
		String value = localizationStore.getLocalization(language, prefix, key);
		if (value != null) {
			return value;
		} else {
			return key;
		}
	}

	@Override
	public Enumeration<String> getKeys() {
		return null;
	}
}
