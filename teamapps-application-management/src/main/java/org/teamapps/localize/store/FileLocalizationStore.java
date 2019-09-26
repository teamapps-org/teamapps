package org.teamapps.localize.store;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileLocalizationStore implements LocalizationStore {

	private static final String DELIMITER = ":\t";
	private static final String FILE_PREFIX = "localization_";
	private static final String FILE_SUFFIX = ".txt";

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

	private void loadEntries() throws IOException {
		for (File file : storeDirectory.listFiles()) {
			String name = file.getName();
			if (name.endsWith(FILE_SUFFIX) && name.startsWith(FILE_PREFIX)) {
				String language = name.substring(FILE_PREFIX.length(), name.lastIndexOf('.'));
				Map<String, String> languageTranslations = translationMap.computeIfAbsent(language, s -> new HashMap<>());
				List<String> translations = FileUtils.readLines(file, StandardCharsets.UTF_8);
				for (String translation : translations) {
					if (translation.startsWith("#") || translation.startsWith("//")) {
						continue;
					}
					String[] parts = translation.split(DELIMITER);
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
			File file = new File(storeDirectory, FILE_PREFIX + language + FILE_SUFFIX);
			StringBuilder translationLines = new StringBuilder();
			for (Map.Entry<String, String> localizationEntry : translations.entrySet()) {
				String translationLine = createTranslationLine(localizationEntry.getKey(), localizationEntry.getValue());
				translationLines.append(translationLine);
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8));
			writer.write(translationLines.toString());
			writer.close();
		}
	}


	private String createTranslationLine(String key, String value) {
		return key + DELIMITER + value + "\n";
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
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUsedLanguages() {
		return new ArrayList<>(translationMap.keySet());
	}
}
