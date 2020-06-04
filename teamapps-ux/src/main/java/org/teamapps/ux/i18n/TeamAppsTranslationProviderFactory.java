package org.teamapps.ux.i18n;

import java.util.Locale;

public class TeamAppsTranslationProviderFactory {

	public static TranslationProvider createProvider() {
		return new ResourceBundleTranslationProvider("org.teamapps.ux.i18n.DefaultCaptions", Locale.ENGLISH, Locale.GERMAN);
	}

}
