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
package org.teamapps.projector.i18n;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;

/**
 * Interface for providing translations based on keys and locales.
 * Implementations of this interface are responsible for retrieving translated strings
 * from various sources such as resource bundles, databases, or external services.
 * <p>
 * The interface provides methods for retrieving raw translation strings as well as
 * formatted translations with parameter substitution.
 */
public interface TranslationProvider {

	/**
	 * Returns the raw (not prefilled) translation string for the given key and locale.
	 * 
	 * @param key The translation key to look up
	 * @param locale The locale for which to retrieve the translation
	 * @return The raw translation string, or null if no translation is found
	 */
	String getRawTranslationString(String key, Locale locale);

	/**
	 * Returns all available translation keys for the specified locale.
	 * 
	 * @param locale The locale for which to retrieve the keys
	 * @return A collection of all available translation keys for the given locale
	 */
	Collection<String> getKeys(Locale locale);

	/**
	 * Returns a localized string with parameter substitution using MessageFormat.
	 * This method retrieves the raw translation string and then applies parameter
	 * substitution using {@link MessageFormat#format(String, Object[])}.
	 * <p>
	 * If no translation is found for the given key, the key itself is returned.
	 * 
	 * @param locale The locale for which to retrieve the translation
	 * @param key The translation key to look up
	 * @param parameters Optional parameters to substitute in the translation string
	 * @return The formatted translation string, or the key itself if no translation is found
	 */
	default String getLocalized(Locale locale, String key, Object... parameters) {
		String value = getRawTranslationString(key, locale);
		if (value != null) {
			return MessageFormat.format(value, parameters); // always use format, even without parameters, for consistent escaping!
		}
		return key;
	}

}
