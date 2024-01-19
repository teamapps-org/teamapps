/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import org.junit.Test;
import org.teamapps.dto.TeamAppsDtoParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class TeamAppsJavaDtoGeneratorTest {

	@Test
	public void classProperties() throws Exception {
		executeClassTest(
				"class A { "
						+ " required String a; "
						+ " String b; "
						+ " List<Long> c; "
						+ "}",
				"A",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_classProperties.java"
		);
	}

	@Test
	public void classesImplementTheirInterfaces() throws Exception {
		executeClassTest(
				"class A {}"
						+ "interface B { String bProperty; }"
						+ "interface C { required List<Integer> cProperty; }"
						+ "class D extends A implements B, C {}",
				"D",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_classesImplementTheirInterfaces.java"
		);
	}

	@Test
	public void doubleInheritedInterfaceProperties() throws Exception {
		executeClassTest(
				"interface B { String bProperty; }"
						+ "interface C { required List<Integer> cProperty; }"
						+ "class A implements B {} "
						+ "class D extends A implements B, C {}",
				"D",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_doubleInheritedInterfaceProperties.java"
		);
	}

	@Test
	public void interfaces() throws Exception {
		executeInterfaceTest(
				"interface A { "
						+ " required String a;"
						+ " String b;"
						+ " command x(String x);"
						+ " command x2(String x2) returns boolean;"
						+ " event y(String y);"
						+ " query q(String y) returns int;"
						+ "}",
				"A",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_interfaces.java"
		);
	}

	@Test
	public void interfaceInteritance() throws Exception {
		executeInterfaceTest(
				"interface A { String a; }"
						+ "interface B extends A { String b; }",
				"B",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_interfaceInteritance.java"
		);
	}

	@Test
	public void fromJsonWithEnums() throws Exception {
		executeClassTest(
				"enum E { "
						+ " A, B "
						+ "} "
						+ "class C { "
						+ "    E e = E.A; "
						+ "}",
				"C",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_fromJsonWithEnums.java"
		);
	}

	@Test
	public void staticCommands() throws Exception {
		String dslString = "abstract class A { static command a(String b); }";
		executeClassTest(
				dslString,
				"A",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_staticCommands.java"
		);
	}

	@Test
	public void dictionaryOfList() throws Exception {
		String dslString = "interface X {}"
				+ "class A { Dictionary<List<X>> x; }";
		executeClassTest(
				dslString,
				"A",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_dictionaryOfList.java"
		);
	}

	@Test
	public void plainObjects() throws Exception {
		String dslString = "interface X {}"
				+ "class A { Object x; List<Object> y; Dictionary<Object> z; }";
		executeClassTest(
				dslString,
				"A",
				"org/teamapps/dto/TeamAppsJavaDtoGeneratorTest_plainObjects.java"
		);
	}

	private void executeClassTest(String dslString, String className, String expectedResultResourceName) throws IOException {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(
				dslString
		)).classCollection();
		TeamAppsDtoModel model = new TeamAppsDtoModel(classCollectionContext);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsJavaDtoGenerator(model).generateClass(model.findClassByName(className, false), stringWriter);

		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private void executeInterfaceTest(String dslString, String interfaceName, String expectedResultResourceName) throws IOException {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(
				dslString
		)).classCollection();
		TeamAppsDtoModel model = new TeamAppsDtoModel(classCollectionContext);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsJavaDtoGenerator(model).generateInterface(model.findInterfaceByName(interfaceName, false), stringWriter);

		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}
}
