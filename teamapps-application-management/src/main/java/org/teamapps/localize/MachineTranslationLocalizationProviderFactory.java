package org.teamapps.localize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.localize.store.FileLocalizationStore;
import org.teamapps.localize.store.LocalizationStore;
import org.teamapps.localize.translation.MachineTranslation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MachineTranslationLocalizationProviderFactory extends AbstractLocalizationProviderFactory {

	private final static Logger LOGGER = LoggerFactory.getLogger(MachineTranslationLocalizationProviderFactory.class);

	private final static List<String> preferredTranslationLanguages = Arrays.asList(new String[]{"en", "de", "fr", "es", "pt", "nl", "it", "pl", "ru"});

	public static MachineTranslationLocalizationProviderFactory create(LocalizationStore localizationStore, String deepLKey, String googleKey, String ... requiredLanguages) {
		return new MachineTranslationLocalizationProviderFactory(localizationStore, deepLKey, googleKey, Arrays.asList(requiredLanguages));
	}

	public static MachineTranslationLocalizationProviderFactory createWithFileStore(File directory, String deepLKey, String googleKey, String ... requiredLanguages) throws IOException {
		FileLocalizationStore fileLocalizationStore = new FileLocalizationStore(directory);

		return new MachineTranslationLocalizationProviderFactory(fileLocalizationStore, deepLKey, googleKey, Arrays.asList(requiredLanguages));
	}

	public static MachineTranslationLocalizationProviderFactory createWithFileStore(File directory, String deepLKey, String googleKey, Locale ... requiredLanguages) throws IOException {
		FileLocalizationStore fileLocalizationStore = new FileLocalizationStore(directory);
		return new MachineTranslationLocalizationProviderFactory(fileLocalizationStore, deepLKey, googleKey, getLocales(requiredLanguages));
	}

	private static List<String> getLocales(Locale[] locales) {
		return Arrays.asList(locales).stream()
				.map(locale -> locale.getLanguage())
				.collect(Collectors.toList());
	}

	private final LocalizationStore localizationStore;
	private final MachineTranslation machineTranslationService;
	private final List<String> requiredLanguages;

	protected MachineTranslationLocalizationProviderFactory(LocalizationStore localizationStore, String deepLKey, String googleKey, List<String> requiredLanguages) {
		this.localizationStore = localizationStore;
		machineTranslationService = new MachineTranslation();
		machineTranslationService.setDeepLKey(deepLKey);
		machineTranslationService.setGoogleTranslationKey(googleKey);
		this.requiredLanguages = requiredLanguages;
	}


	public void machineTranslateAllMissingEntries() {
		LOGGER.info("Start translating entries");
		int countTranslations = 0;
		List<String> usedLanguages = localizationStore.getAllUsedLanguages();
		List<String> keys = localizationStore.getAllUsedStoreKeys();
		for (String requiredLanguage : requiredLanguages) {
			if (!machineTranslationService.canTranslate(requiredLanguage, requiredLanguage)) {
				continue;
			}
			for (String key : keys) {
				if (localizationStore.getLocalization(requiredLanguage, key) == null) {
					String sourceText = null;
					String sourceLanguage = null;
					for (String preferredSourceLanguage : preferredTranslationLanguages) {
						String text = localizationStore.getLocalization(preferredSourceLanguage, key);
						if (text != null) {
							sourceText = text;
							sourceLanguage = preferredSourceLanguage;
							break;
						}
					}
					if (sourceText == null) {
						for (String usedLanguage : usedLanguages) {
							String text = localizationStore.getLocalization(usedLanguage, key);
							if (text != null && machineTranslationService.canTranslate(usedLanguage, requiredLanguage)) {
								sourceText = text;
								sourceLanguage = usedLanguage;
								break;
							}
						}
					}
					if (sourceText != null && sourceLanguage != null) {
						String translation = machineTranslationService.translate(sourceText, sourceLanguage, requiredLanguage);
						countTranslations++;
						translation = firstUpperIfSourceUpper(sourceText, translation);
						localizationStore.addTranslationResult(requiredLanguage, key, translation);
					}
				}
			}
		}
		localizationStore.finishStoreUpdates();
		LOGGER.info("Translated entries: " + countTranslations + ", characters:" + machineTranslationService.getTranslatedCharacters());
	}

	private String firstUpperIfSourceUpper(String source, String text) {
		if (source == null || text == null || source.isEmpty() || text.isEmpty()) {
			return text;
		}
		char c = source.substring(0, 1).charAt(0);
		if (Character.isUpperCase(c)) {
			return text.substring(0,1).toUpperCase() + text.substring(1);
		} else {
			return text;
		}
	}


	@Override
	public LocalizationStore getLocalizationStore() {
		return localizationStore;
	}
}
