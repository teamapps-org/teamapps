package org.teamapps.localize;

public interface LocalizationProviderFactory {

	LocalizationProvider createLocalizationProvider(ExistingLocalizationsInfo existingLocalizationsInfo);

}
