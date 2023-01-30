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
