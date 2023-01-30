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
package org.teamapps.localize.store;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class FileLocalizationStore implements LocalizationStore {

	private String delimiter = ":\t";
	private String filePrefix = "localization_";
	private String fileSuffix = ".txt";

	private final File storeDirectory;
	private final Map<String, Map<String, String>> translationMap;

	public FileLocalizationStore(File storeDirectory) throws IOException {
		this.storeDirectory = storeDirectory;
		this.translationMap = new HashMap<>();
		if (!storeDirectory.exists()) {
			storeDirectory.mkdir();
		}
		loadEntries();
	}

	public void setPropertyFilesDefaults() {
		delimiter = "=";
		filePrefix = "captions_";
		fileSuffix = ".properties";
	}

	private void loadEntries() throws IOException {
		for (File file : storeDirectory.listFiles()) {
			String name = file.getName();
			if (name.endsWith(fileSuffix) && name.startsWith(filePrefix)) {
				String language = name.substring(filePrefix.length(), name.lastIndexOf('.'));
				Map<String, String> languageTranslations = translationMap.computeIfAbsent(language, s -> new HashMap<>());
				List<String> translations = FileUtils.readLines(file, StandardCharsets.UTF_8);
				for (String translation : translations) {
					if (translation.startsWith("#") || translation.startsWith("//")) {
						continue;
					}
					String[] parts = translation.split(delimiter);
					if (parts.length == 2) {
						languageTranslations.put(parts[0], parts[1]);
					}
				}
			}
		}
	}

	public void writeLocalizationFiles() throws IOException {
		for (Map.Entry<String, Map<String, String>> entry : translationMap.entrySet()) {
			String language = entry.getKey();
			Map<String, String> translations = entry.getValue();
			File file = new File(storeDirectory, filePrefix + language + fileSuffix);
			StringBuilder translationLines = new StringBuilder();
			translations.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(localizationEntry -> {
				String translationLine = createTranslationLine(localizationEntry.getKey(), localizationEntry.getValue());
				translationLines.append(translationLine);
			});
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8));
			writer.write(translationLines.toString());
			writer.close();
		}
	}


	private String createTranslationLine(String key, String value) {
		return key + delimiter + value + "\n";
	}

	@Override
	public String getLocalization(String language, String lookupKey) {
		Map<String, String> languageTranslations = translationMap.get(language);
		if (languageTranslations != null) {
			return languageTranslations.get(lookupKey);
		} else {
			return null;
		}
	}

	@Override
	public void addTranslationResult(String language, String lookupKey, String value) {
		translationMap.computeIfAbsent(language, s -> new HashMap<>()).put(lookupKey, value);
	}

	@Override
	public void startImportingApplicationNamespace(String applicationNamespace) {

	}

	@Override
	public void addExistingLocalizationEntry(String applicationNamespace, String language, String lookupKey, String value) {
		translationMap.computeIfAbsent(language, s -> new HashMap<>()).put(lookupKey, value);
	}

	@Override
	public void addDictionary(String applicationNamespace, String dictionaryId) {

	}

	@Override
	public void addDictionaryEntry(String applicationNamespace, String dictionaryId, String language, String lookupKey, String value) {
		translationMap.computeIfAbsent(language, s -> new HashMap<>()).put(lookupKey, value);
	}

	@Override
	public void finishImportingApplicationNamespace(String applicationNamespace) {

	}

	@Override
	public void finishStoreUpdates() {
		try {
			writeLocalizationFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getAllUsedStoreKeys() {
		return translationMap.values().stream()
				.flatMap(map -> map.keySet().stream())
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUsedLanguages() {
		return new ArrayList<>(translationMap.keySet());
	}
}
