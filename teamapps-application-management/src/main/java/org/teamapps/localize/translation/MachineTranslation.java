/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MachineTranslation implements TranslationService {

    private DeepLTranslation deepLTranslation;
    private GoogleTranslation googleTranslation;
    private List<TranslationService> services = new ArrayList<>();
    private Set<String> supportedLanguages = new HashSet<>();

    public MachineTranslation() {
    }

    public void setGoogleTranslationKey(String googleKey) {
        if (googleKey != null && googleTranslation == null) {
            try {
                googleTranslation = new GoogleTranslation(googleKey);
                services.add(googleTranslation);
                supportedLanguages.addAll(googleTranslation.getSupportedLanguages());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDeepLKey(String deepLKey) {
        if (deepLKey != null && deepLTranslation == null) {
            deepLTranslation = new DeepLTranslation(deepLKey);
            services.add(deepLTranslation);
            supportedLanguages.addAll(deepLTranslation.getSupportedLanguages());
        }
    }

    public boolean translationServiceAvailable() {
        return !services.isEmpty();
    }

    @Override
    public Set<String> getSupportedLanguages() {
        Set<String> supportedLanguages = new HashSet<>();
        services.forEach(service -> supportedLanguages.addAll(service.getSupportedLanguages()));
        return supportedLanguages;
    }

    @Override
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        for (TranslationService service : services) {
            if (service.canTranslate(sourceLanguage, targetLanguage)) {
                String result = service.translate(transformParameters(text), sourceLanguage, targetLanguage);
                return restoreParameters(result);
            }
        }
        return null;
    }

    private static String transformParameters(String s) {
        if (s == null) return s;
        for (int i = 0; i < 10; i++) {
            s = s.replace("{" + i + "}", "<attr-" + i + "/>");
        }
        return s;
    }

    private static String restoreParameters(String s) {
        if (s == null) return s;
        for (int i = 0; i < 10; i++) {
            s = s.replace("<attr-" + i + "/>", "{" + i + "}");
        }
        return s;
    }


    @Override
    public long getTranslatedCharacters() {
        return services.stream().mapToLong(service -> service.getTranslatedCharacters()).sum();
    }

    @Override
    public boolean canTranslate(String sourceLanguage, String targetLanguage) {
        if (supportedLanguages.contains(sourceLanguage) && supportedLanguages.contains(targetLanguage)) {
            return true;
        } else {
            return false;
        }
    }
}
