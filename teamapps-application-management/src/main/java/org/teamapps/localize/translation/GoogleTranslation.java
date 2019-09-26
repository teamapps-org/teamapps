package org.teamapps.localize.translation;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GoogleTranslation implements TranslationService {

    private Translate translationService;
    private long translatedCharacters = 0;
    private Set<String> supportedLanguages;

    public GoogleTranslation(String key) throws IOException {
        initialize(key);
    }

    private void initialize(String key) throws IOException {
        translationService = TranslateOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new ByteArrayInputStream(key.getBytes(StandardCharsets.UTF_8))))
                .build()
                .getService();
    }

    @Override
    public Set<String> getSupportedLanguages() {
        if (supportedLanguages != null && !supportedLanguages.isEmpty()) {
            return supportedLanguages;
        } else {
            List<Language> languages = translationService.listSupportedLanguages();
            supportedLanguages = languages.stream()
                    .map(language -> language.getCode())
                    .collect(Collectors.toSet());
            return supportedLanguages;
        }
    }

    @Override
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        Translation translation = translationService.translate(
                        text,
                        Translate.TranslateOption.sourceLanguage(sourceLanguage),
                        Translate.TranslateOption.targetLanguage(targetLanguage));
        String translatedText = translation.getTranslatedText();
        if (translatedText != null) {
            translatedCharacters += translatedText.length();
        }
        return translatedText;
    }

    @Override
    public long getTranslatedCharacters() {
        return translatedCharacters;
    }


}
