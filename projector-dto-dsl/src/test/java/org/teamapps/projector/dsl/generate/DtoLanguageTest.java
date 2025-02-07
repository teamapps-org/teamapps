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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;
import org.teamapps.projector.dsl.TeamAppsDtoParser;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DtoLanguageTest {

	@Test
	public void testErrorHandling() throws IOException {
		System.err.println("The following parser error message is intended by the test case: ");
		TeamAppsDtoParser parser = ParserFactory.createParser(new StringReader("interface asdf extends x blah"));
		
		assertThatThrownBy(() -> parser.interfaceDeclaration())
				.isInstanceOf(ParseCancellationException.class);
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
				+ "  required List<DtoAbstractField> fields;"
				+ "}"));
		parser.classDeclaration();
	}

	@Test
	public void testDoubleSemicolon() throws IOException {
		TeamAppsDtoParser parser = ParserFactory.createParser(new StringReader("""
				package "asdf":org.teamapps.blah;
				import org.teamapps.projector.component.field.DtoAbstractField;;
				class A {
				  required List<DtoAbstractField> fields;\
				}"""));
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = parser.classCollection();
		assertThat(classCollectionContext.typeDeclaration()).hasSize(1);
	}

}
