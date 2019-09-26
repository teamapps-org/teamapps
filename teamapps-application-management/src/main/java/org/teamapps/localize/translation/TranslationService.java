package org.teamapps.localize.translation;

import java.util.Set;

public interface TranslationService {

    Set<String> getSupportedLanguages();

    String translate(String text, String sourceLanguage, String targetLanguage);

    long getTranslatedCharacters();

    default boolean canTranslate(String sourceLanguage, String targetLanguage) {
        Set<String> supportedLanguages = getSupportedLanguages();
        if (supportedLanguages.contains(sourceLanguage) && supportedLanguages.contains(targetLanguage)) {
            return true;
        } else {
            return false;
        }
    }
}
