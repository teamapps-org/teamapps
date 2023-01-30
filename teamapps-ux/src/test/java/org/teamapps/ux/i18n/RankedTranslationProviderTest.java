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
package org.teamapps.ux.i18n;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class RankedTranslationProviderTest {

	@Test
	public void shouldDelegate() {
		final RankedTranslationProvider provider = new RankedTranslationProvider(
				new ResourceBundleTranslationProvider("translations/Translations"),
				new TranslationProvider() {
					@Override
					public String getTranslation(String key, Locale locale) {
						return "fallback " + key;
					}

					@Override
					public Collection<String> getKeys(Locale locale) {
						return List.of();
					}
				}
		);

		assertThat(provider.getTranslation("allLanguages", Locale.GERMAN)).isEqualTo("alle Sprachen");
		assertThat(provider.getTranslation("asdf", Locale.GERMAN)).isEqualTo("fallback asdf");
	}

	@Test
	public void shouldThrowMissingResourceExceptionIfKeyNotPresentInAnyDelegates() {
		final RankedTranslationProvider provider = new RankedTranslationProvider(
				new ResourceBundleTranslationProvider("translations/Translations"),
				new ResourceBundleTranslationProvider("translations/Translations")
		);

		assertThatThrownBy(() -> provider.getTranslation("nonExisting", Locale.GERMAN))
				.isInstanceOf(MissingResourceException.class);
	}
}
