/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import java.util.stream.Collectors;

public class RankedTranslationProvider implements TranslationProvider {

	private final List<TranslationProvider> translationProviders;

	public RankedTranslationProvider(TranslationProvider... translationProviders) {
		this.translationProviders = Arrays.asList(translationProviders);
	}

	@Override
	public String getTranslation(String key, Locale locale) {
		for (TranslationProvider translationProvider : translationProviders) {
			try {
				return translationProvider.getTranslation(key, locale);
			} catch (MissingResourceException e) {
				// ignore
			}
		}
		throw new MissingResourceException("Can't find resource " + key, this.getClass().getName(), key);
	}

	@Override
	public Set<String> getKeys(Locale locale) {
		return translationProviders.stream()
				.flatMap(tp -> tp.getKeys(locale).stream())
				.collect(Collectors.toSet());
	}

}
