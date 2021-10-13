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
package org.teamapps.ux.i18n;

import java.text.MessageFormat;
import java.util.Locale;

public interface TranslationProvider {

	String getTranslation(String key, Locale locale);

	default String getLocalized(Locale locale, String key, Object... parameters) {
		String value = getTranslation(key, locale);
		if (value != null) {
			return MessageFormat.format(value, parameters); // always use format, even without parameters, for consistent escaping!
		}
		return key;
	}

}
