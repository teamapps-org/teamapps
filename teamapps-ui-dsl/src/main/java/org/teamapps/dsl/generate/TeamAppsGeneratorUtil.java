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
package org.teamapps.dsl.generate;

import org.teamapps.dsl.TeamAppsDtoParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TeamAppsGeneratorUtil {

	public static List<TeamAppsDtoParser.ClassCollectionContext> parseClassCollections(File sourceDir) throws IOException {
		return Files.find(Paths.get(sourceDir.getPath()), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toString().endsWith(".dto"))
				.map(dtoFile -> {
					try {
						InputStreamReader reader = new InputStreamReader(new FileInputStream(dtoFile.toFile()), StandardCharsets.UTF_8);
						return ParserFactory.createParser(reader).classCollection();
					} catch (Exception e1) {
						throw new IllegalArgumentException("Exception while parsing " + dtoFile + ": " + e1.getMessage(), e1);
					}
				})
				.collect(Collectors.toList());
	}
}
