package org.teamapps.localize;

import org.teamapps.localize.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExistingLocalizationsInfo {

	private final String applicationNamespace;
	private List<Dictionary> dictionaries = new ArrayList<>();
	private List<ResourceBundleInfo> resourceBundleInfos = new ArrayList<>();

	public ExistingLocalizationsInfo(String applicationNamespace) {
		this.applicationNamespace = applicationNamespace;
	}

	public String getApplicationNamespace() {
		return applicationNamespace;
	}

	public void addDictionary(Dictionary dictionary) {
		dictionaries.add(dictionary);
	}

	public void addResourceBundleInfo(String baseName, Locale... translations) {
		addResourceBundleInfo(ResourceBundleInfo.create(baseName, translations));
	}

	public void addResourceBundleInfo(String baseName, String resourceFileSuffix, Locale... translations) {
		addResourceBundleInfo(ResourceBundleInfo.create(baseName, resourceFileSuffix, translations));
	}

	public void addResourceBundleInfo(ResourceBundleInfo resourceBundleInfo) {
		resourceBundleInfos.add(resourceBundleInfo);
	}

	public List<Dictionary> getDictionaries() {
		return dictionaries;
	}

	public List<ResourceBundleInfo> getResourceBundleInfos() {
		return resourceBundleInfos;
	}
}
