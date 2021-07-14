package org.teamapps.ux.i18n;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Locale;
import java.util.MissingResourceException;

public class ResourceBundleTranslationProviderTest {

	@Test
	public void shouldReturnAvailableTranslations() throws Exception {
		var provider = new ResourceBundleTranslationProvider("translations/Translations", Locale.GERMAN);

		Assertions.assertThat(provider.getTranslation("allLanguages", Locale.GERMAN)).isEqualTo("alle Sprachen");
		Assertions.assertThat(provider.getTranslation("allLanguages", Locale.ENGLISH)).isEqualTo("all languages");
		Assertions.assertThat(provider.getTranslation("allLanguages", Locale.FRENCH)).isEqualTo("toutes les langues");
	}

	@Test
	public void shouldFallbackToDefaultLocaleIfLocaleNotPresent() throws Exception {
		var provider = new ResourceBundleTranslationProvider(
				"translations/Translations",
				Locale.ENGLISH
		);

		Assertions.assertThat(provider.getTranslation("allLanguages", Locale.ITALIAN)).isEqualTo("all languages");
	}

	@Test
	public void shouldFallbackToDefaultLocaleIfKeyNotPresentInLocale() throws Exception {
		var provider = new ResourceBundleTranslationProvider(
				"translations/Translations",
				Locale.ENGLISH
		);

		Assertions.assertThat(provider.getTranslation("enOnly", Locale.GERMAN)).isEqualTo("english only");
	}

	@Test
	public void shouldThrowIfTranslationNotPresent() throws Exception {
		var provider = new ResourceBundleTranslationProvider(
				"translations/Translations",
				Locale.ENGLISH
		);

		Assertions.assertThatThrownBy(() -> provider.getTranslation("asdf", Locale.GERMAN))
				.isInstanceOf(MissingResourceException.class);
	}

}