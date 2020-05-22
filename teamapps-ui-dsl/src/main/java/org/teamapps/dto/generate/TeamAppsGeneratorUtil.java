/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.dto.generate;

import org.teamapps.dto.TeamAppsDtoParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TeamAppsGeneratorUtil {

	public static List<File> getFilesInDirectory(File sourceDir) {
		File[] files = sourceDir.listFiles();
		if (files == null) {
			throw new IllegalArgumentException("Directory does not exist: " + sourceDir);
		}
		return Arrays.asList(files);
	}

	public static List<TeamAppsDtoParser.ClassCollectionContext> parseClassCollections(File sourceDir) {
		return TeamAppsGeneratorUtil.getFilesInDirectory(sourceDir).stream()
				.map(dtoFile -> {
					try {
						InputStreamReader reader = new InputStreamReader(new FileInputStream(dtoFile), "UTF-8");
						return ParserFactory.createParser(reader).classCollection();
					} catch (Exception e1) {
						throw new IllegalArgumentException("Exception while parsing " + dtoFile.getPath() + ": " + e1.getMessage(), e1);
					}
				})
				.collect(Collectors.toList());
	}
}
