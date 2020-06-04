package org.teamapps.ux.i18n;

import java.util.*;
import java.util.stream.Collectors;

public class RankingTranslationProvider implements TranslationProvider{

	private List<TranslationProvider> translationProviders;

	public RankingTranslationProvider() {
		translationProviders = new ArrayList<>();
	}

	public void addTranslationProvider(TranslationProvider translationProvider) {
		translationProviders.add(0, translationProvider);
	}

	@Override
	public List<Locale> getLanguages() {
		return translationProviders.stream()
				.flatMap(provider -> provider.getLanguages().stream())
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getKeys() {
		return translationProviders.stream()
				.flatMap(provider -> provider.getKeys().stream())
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public String getTranslation(String key, Locale locale) {
		for (TranslationProvider translationProvider : translationProviders) {
			String translation = translationProvider.getTranslation(key, locale);
			if (translation != null) {
				return translation;
			}
		}
		return null;
	}

	@Override
	public String getTranslation(String key, List<Locale> acceptedLanguages) {
		for (TranslationProvider translationProvider : translationProviders) {
			String translation = translationProvider.getTranslation(key, acceptedLanguages);
			if (translation != null) {
				return translation;
			}
		}
		return null;
	}
}
