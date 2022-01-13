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

import org.teamapps.ux.session.SessionContext;

import java.util.Locale;
import java.util.ResourceBundle;

public interface LocalizationProvider {

	default boolean matchesLocalization(String key, String query) {
		if (query == null || query.isEmpty()) {
			return true;
		}
		return getLocalizedOrEmptyString(key).toLowerCase().contains(query.toLowerCase());
	}

	default String getLocalizedOrEmptyString(String key) {
		String localized = getLocalized(key);
		return localized != null ? localized : "";
	}

	default String getLocalized(String key) {
		return getLocalized(SessionContext.current().getLocale(), key);
	}

	default String getLocalized(String key, Object... parameters) {
		return getLocalized(SessionContext.current().getLocale(), key, parameters);
	}

	String getLocalized(Locale locale, String key);

	String getLocalized(Locale locale, String key, Object... parameters);

	default ResourceBundle getResourceBundle() {
		return getResourceBundle(SessionContext.current().getLocale());
	}

	ResourceBundle getResourceBundle(Locale locale);

}
