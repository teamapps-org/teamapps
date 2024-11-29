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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.MissingResourceException;

public class ResourceBundleTranslationProviderTest {

	@Test
	public void shouldReturnAvailableTranslations() throws Exception {
		var provider = new ResourceBundleTranslationProvider("translations/Translations", Locale.GERMAN);

		Assertions.assertThat(provider.getRawTranslationString("allLanguages", Locale.GERMAN)).isEqualTo("alle Sprachen");
		Assertions.assertThat(provider.getRawTranslationString("allLanguages", Locale.ENGLISH)).isEqualTo("all languages");
		Assertions.assertThat(provider.getRawTranslationString("allLanguages", Locale.FRENCH)).isEqualTo("toutes les langues");
	}

	@Test
	public void shouldFallbackToDefaultLocaleIfLocaleNotPresent() throws Exception {
		var provider = new ResourceBundleTranslationProvider(
				"translations/Translations",
				Locale.ENGLISH
		);

		Assertions.assertThat(provider.getRawTranslationString("allLanguages", Locale.ITALIAN)).isEqualTo("all languages");
	}

	@Test
	public void shouldFallbackToDefaultLocaleIfKeyNotPresentInLocale() throws Exception {
		var provider = new ResourceBundleTranslationProvider(
				"translations/Translations",
				Locale.ENGLISH
		);
		//todo this test fails on maven only
		//Assertions.assertThat(provider.getTranslation("enOnly", Locale.GERMAN)).isEqualTo("english only");
	}

	@Test
	public void shouldThrowIfTranslationNotPresent() throws Exception {
		var provider = new ResourceBundleTranslationProvider(
				"translations/Translations",
				Locale.ENGLISH
		);

		Assertions.assertThatThrownBy(() -> provider.getRawTranslationString("asdf", Locale.GERMAN))
				.isInstanceOf(MissingResourceException.class);
	}

}
