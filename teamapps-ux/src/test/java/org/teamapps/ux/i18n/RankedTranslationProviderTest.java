package org.teamapps.ux.i18n;

import org.junit.Test;

import java.util.Locale;
import java.util.MissingResourceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class RankedTranslationProviderTest {

	@Test
	public void shouldDelegate() {
		final RankedTranslationProvider provider = new RankedTranslationProvider(
				new ResourceBundleTranslationProvider("translations/Translations"),
				(key, locale) -> "fallback " + key
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