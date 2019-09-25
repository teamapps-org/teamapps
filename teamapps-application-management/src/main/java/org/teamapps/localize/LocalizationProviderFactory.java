package org.teamapps.localize;

public interface LocalizationProviderFactory {

	LocalizationProvider createLocalizationProvider(String applicationNamespace, ExistingLocalizationsInfo existingLocalizationsInfo);

}
