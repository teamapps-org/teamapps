/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.i18n;

import java.util.*;
import java.util.stream.Collectors;

public class RankingTranslationProvider implements TranslationProvider{

	private final List<TranslationProvider> translationProviders;

	public RankingTranslationProvider() {
		translationProviders = new ArrayList<>();
	}

	public void addTranslationProvider(TranslationProvider translationProvider) {
		translationProviders.add(0, translationProvider);
	}

	@Override
	public List<Locale> getLanguages() {
		return translationProviders.stream()
				.flatMap(provider -> provider.getLanguages().stream())
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getKeys() {
		return translationProviders.stream()
				.flatMap(provider -> provider.getKeys().stream())
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public String getTranslation(String key, Locale locale) {
		for (TranslationProvider translationProvider : translationProviders) {
			String translation = translationProvider.getTranslation(key, locale);
			if (translation != null) {
				return translation;
			}
		}
		return null;
	}

	@Override
	public String getTranslation(String key, List<Locale> acceptedLanguages) {
		for (TranslationProvider translationProvider : translationProviders) {
			String translation = translationProvider.getTranslation(key, acceptedLanguages);
			if (translation != null) {
				return translation;
			}
		}
		return null;
	}
}
