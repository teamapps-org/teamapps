package org.teamapps.localize.dictionary;

import org.teamapps.localize.LocalizationProvider;

import java.util.Arrays;
import java.util.List;

public class StandardDictionary implements Dictionary {

	private final String id;
	private final String language;
	private final List<DictionaryEntry> entries;
	private LocalizationProvider localizationProvider;

	public StandardDictionary(String id, String language, DictionaryEntry... entries) {
		this(id, language, Arrays.asList(entries));
	}

	public StandardDictionary(String id, String language, List<DictionaryEntry> entries) {
		this.id = id;
		this.language = language;
		this.entries = entries;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public List<DictionaryEntry> getEntries() {
		return entries;
	}

	@Override
	public void setLocalizationProvider(LocalizationProvider localizationProvider) {
		this.localizationProvider = localizationProvider;
	}

	public LocalizationProvider getLocalizationProvider() {
		return localizationProvider;
	}
}
