package org.teamapps.localize.dictionary;

import org.teamapps.localize.LocalizationProvider;

import java.util.List;

public interface Dictionary {

	String getId();

	String getLanguage();

	List<DictionaryEntry> getEntries();

	void setLocalizationProvider(LocalizationProvider localizationProvider);

	LocalizationProvider getLocalizationProvider();


}
