/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.localize;

import org.teamapps.localize.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExistingLocalizationsInfo {

	private final String applicationNamespace;
	private final List<Dictionary> dictionaries = new ArrayList<>();
	private final List<ResourceBundleInfo> resourceBundleInfos = new ArrayList<>();

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
