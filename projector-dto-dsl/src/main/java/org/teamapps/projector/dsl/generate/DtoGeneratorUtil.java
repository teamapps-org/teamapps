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
package org.teamapps.projector.dsl.generate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.teamapps.projector.dsl.TeamAppsDtoParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DtoGeneratorUtil {

	public static final Map<ParserRuleContext, String> originNamesByParserRuleContext = new HashMap<>();

	public static List<TeamAppsDtoParser.ClassCollectionContext> parseClassCollections(File sourceDir) throws IOException {
		return Files.find(Paths.get(sourceDir.getPath()), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toString().endsWith(".dto"))
				.map(dtoFile -> {
					try {
						TeamAppsDtoParser parser = ParserFactory.createParser(dtoFile);
						parser.addParseListener(new ParseTreeListener() {
							@Override
							public void visitTerminal(TerminalNode terminalNode) {

							}

							@Override
							public void visitErrorNode(ErrorNode errorNode) {

							}

							@Override
							public void enterEveryRule(ParserRuleContext parserRuleContext) {

							}

							@Override
							public void exitEveryRule(ParserRuleContext parserRuleContext) {
								originNamesByParserRuleContext.put(parserRuleContext, dtoFile.toAbsolutePath().toString());
							}
						});
						return parser.classCollection();
					} catch (Exception e1) {
						throw new IllegalArgumentException("Exception while parsing " + dtoFile + ": " + e1.getMessage(), e1);
					}
				})
				.collect(Collectors.toList());
	}
}
