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

import org.junit.Test;
import org.teamapps.dsl.TeamAppsDtoParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class TeamAppsJavaDtoGeneratorTest {

	@Test
	public void classProperties() throws Exception {
		executeClassTest(
				"package \"x\":org.teamapps.dto222; " +
						"import org.teamapps.dto.blah.DtoComponent; " +
						"class A { "
						+ " required String aasdf; "
						+ " String b; "
						+ " List<Long> c; "
						+ "}",
				"org.teamapps.dto222.A",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_classProperties.java"
		);
	}

	@Test
	public void classesImplementTheirInterfaces() throws Exception {
		executeClassTest(
				"package \"x\":org.teamapps.dto; class A {}"
						+ "interface B { String bProperty; }"
						+ "interface C { required List<Integer> cProperty; }"
						+ "class D extends A implements B, C {}",
				"org.teamapps.dto.D",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_classesImplementTheirInterfaces.java"
		);
	}

	@Test
	public void doubleInheritedInterfaceProperties() throws Exception {
		executeClassTest(
				"package \"x\":org.teamapps.dto; interface B { String bProperty; }"
						+ "interface C { required List<Integer> cProperty; }"
						+ "class A implements B {} "
						+ "class D extends A implements B, C {}",
				"org.teamapps.dto.D",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_doubleInheritedInterfaceProperties.java"
		);
	}

	@Test
	public void interfaces() throws Exception {
		executeInterfaceTest(
				"package \"x\":org.teamapps.dto; " +
						"interface A { "
						+ " required String a;"
						+ " String b;"
						+ " command x(String x);"
						+ " command x2(String x2) returns boolean;"
						+ " event y(String y);"
						+ " query q(String y) returns int;"
						+ "}",
				"org.teamapps.dto.A",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_interfaces.java"
		);
	}

	@Test
	public void interfaceInteritance() throws Exception {
		executeInterfaceTest(
				"package \"x\":org.teamapps.dto; interface A { String a; }"
						+ "interface B extends A { String b; }",
				"org.teamapps.dto.B",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_interfaceInteritance.java"
		);
	}

	@Test
	public void fromJsonWithEnums() throws Exception {
		executeClassTest(
				"package \"x\":org.teamapps.dto; enum E { "
						+ " A, B "
						+ "} "
						+ "class C { "
						+ "    E e = E.A; "
						+ "}",
				"org.teamapps.dto.C",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_fromJsonWithEnums.java"
		);
	}

	@Test
	public void staticCommands() throws Exception {
		String dslString = "package \"x\":org.teamapps.dto; abstract managed class A { static command a(String b); }";
		executeClassTest(
				dslString,
				"org.teamapps.dto.A",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_staticCommands.java"
		);
	}

	@Test
	public void dictionaryOfList() throws Exception {
		String dslString = "package \"x\":org.teamapps.dto; interface X {}"
				+ "class A { Dictionary<List<X>> x; }";
		executeClassTest(
				dslString,
				"org.teamapps.dto.A",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_dictionaryOfList.java"
		);
	}

	@Test
	public void plainObjects() throws Exception {
		String dslString = "package \"x\":org.teamapps.dto; interface X {}"
				+ "class A { Object x; List<Object> y; Dictionary<Object> z; }";
		executeClassTest(
				dslString,
				"org.teamapps.dto.A",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_plainObjects.java"
		);
	}

	@Test
	public void enums() throws Exception {
		executeEnumTest(
				"package \"x\":org.teamapps.dto; enum E { "
						+ " A, B "
						+ "} ",
				"org.teamapps.dto.E",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_enums.java"
		);
	}

	@Test
	public void stringEnums() throws Exception {
		executeEnumTest(
				"package \"x\":org.teamapps.dto; enum E { "
						+ " A = \"a\", B = \"b\" "
						+ "} ",
				"org.teamapps.dto.E",
				"org/teamapps/dsl/TeamAppsJavaDtoGeneratorTest_stringEnums.java"
		);
	}

	private void executeClassTest(String dslString, String qualifiedClassName, String expectedResultResourceName) throws IOException {
		TeamAppsIntermediateDtoModel model = createModel(dslString);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsJavaDtoGenerator(model).generateClass(model.findClassByQualifiedName(qualifiedClassName).orElseThrow(), stringWriter);
		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private void executeInterfaceTest(String dslString, String qualifiedInterfaceName, String expectedResultResourceName) throws IOException {
		TeamAppsIntermediateDtoModel model = createModel(dslString);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsJavaDtoGenerator(model).generateInterface(model.findInterfaceByQualifiedName(qualifiedInterfaceName).orElseThrow(), stringWriter);
		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private void executeEnumTest(String dslString, String qualifiedEnumName, String expectedResultResourceName) throws IOException {
		TeamAppsIntermediateDtoModel model = createModel(dslString);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsJavaDtoGenerator(model).generateEnum(model.findEnumByQualifiedName(qualifiedEnumName).orElseThrow(), stringWriter);
		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private static TeamAppsIntermediateDtoModel createModel(String dslString) throws IOException {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(dslString))
				.classCollection();
		return new TeamAppsIntermediateDtoModel(classCollectionContext);
	}
}
