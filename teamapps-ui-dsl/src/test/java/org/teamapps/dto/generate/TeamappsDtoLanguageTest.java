/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import org.teamapps.dto.TeamAppsDtoParser;

import java.io.IOException;
import java.io.StringReader;

public class TeamappsDtoLanguageTest {

	@Test(expected = ParseCancellationException.class)
	public void testErrorHandling() throws IOException {
		System.err.println("The following parser error message is intended by the test case: ");
		TeamAppsDtoParser parser = ParserFactory.createParser(new StringReader("interface asdf extends x blah"));
		parser.interfaceDeclaration();
	}

	@Test
	public void testInterface() throws IOException {
		TeamAppsDtoParser parser = ParserFactory.createParser(new StringReader("interface A extends B, C {"
				+ "  command showToolButtonDropDown(String fieldName, String toolButtonId, UiSymbolView dropDownSymbolView);"
				+ "  event formDataSubmit(UiRecordData data);"
				+ "}"));
		parser.interfaceDeclaration();
	}

	@Test
	public void testListProperty() throws IOException {
		TeamAppsDtoParser parser = ParserFactory.createParser(new StringReader("class A {\n"
				+ "  required List<UiField> fields;"
				+ "}"));
		parser.classDeclaration();
	}

}
