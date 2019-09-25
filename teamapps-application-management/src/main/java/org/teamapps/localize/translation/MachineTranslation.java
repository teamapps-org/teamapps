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
                return service.translate(text, sourceLanguage, targetLanguage);
            }
        }
        return null;
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
