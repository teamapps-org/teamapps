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
package org.teamapps.localize.dictionary;

import org.teamapps.ux.session.CurrentSessionContext;

import java.util.Locale;

public interface EnumDictionary extends DictionaryEntry {

	Dictionary getDictionary();

	default String localized() {
		return getLocalized();
	}

	default String getLocalized() {
		return getLocalized(CurrentSessionContext.get().getLocale());
	}

	default String getLocalized(Object... parameters) {
		return getLocalized(CurrentSessionContext.get().getLocale(), parameters);
	}

	default String getLocalized(Locale locale) {
		return getLocalized(locale, null);
	}

	default String getLocalized(Locale locale, Object... parameters) {
		if (getDictionary().getLocalizationProvider() == null) {
			return getValue();
		} else {
			return getDictionary().getLocalizationProvider().getLocalized(locale, getKey(), parameters);
		}
	}
}
