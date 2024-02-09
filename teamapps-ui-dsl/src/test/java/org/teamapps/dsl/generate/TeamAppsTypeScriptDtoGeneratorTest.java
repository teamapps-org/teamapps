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
import org.teamapps.common.util.ExceptionUtil;
import org.teamapps.dsl.TeamAppsDtoParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class TeamAppsTypeScriptDtoGeneratorTest {

	@Test
	public void classProperties() throws Exception {
		executeClassTest(
				"org.teamapps.dto.A", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classProperties.tsd", "package \"x\":org.teamapps.dto; class A {\n"
						+ "\trequired String a;\n"
						+ "\tString b;\n"
						+ "\tList<Long> c;\n"
						+ "}"
		);
	}

	@Test
	public void commandsAndEvents() throws Exception {
		executeClassTest(
				"org.teamapps.dto.A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_commandsAndEvents.tsd",
				"package \"x\":org.teamapps.dto; class A { command c(String s, int x); event z3(int a, long b);}"
		);
	}

	@Test
	public void mutableProperties() throws Exception {
		executeClassTest(
				"org.teamapps.dto.A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_mutableProperties.tsd",
				"package \"x\":org.teamapps.dto; class A { mutable String s;}"
		);
	}

	@Test
	public void classesImplementTheirInterfaces() throws Exception {
		executeClassTest(
				"org.teamapps.dto.D", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classesImplementTheirInterfaces.tsd", "package \"x\":org.teamapps.dto; class A {}"
						+ "interface B { String bProperty; }"
						+ "interface C { required List<Integer> cProperty; }"
						+ "class D extends A implements B, C {}"
		);
	}

	@Test
	public void interfaces() throws Exception {
		executeInterfaceTest(
				"org.teamapps.dto.A", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_interfaces.tsd", "package \"x\":org.teamapps.dto; " +
						"interface A { "
						+ " required String a;"
						+ " String b;"
						+ " command x1(String x);"
						+ " command x2(String x) returns List<Integer>;"
						+ " event y(String y);"
						+ " query z(String s) returns List<String>;"
						+ "}"
		);
	}

	@Test
	public void interfaceInteritance() throws Exception {
		executeInterfaceTest(
				"org.teamapps.dto.B", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_interfaceInteritance.tsd", "package \"x\":org.teamapps.dto; " +
						"interface A { String a; }"
						+ "interface B extends A { String b; }"
		);
	}

	@Test
	public void classCommandInteritance() throws Exception {
		executeClassTest(
				"org.teamapps.dto.C", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classCommandInteritance.tsd", "package \"x\":org.teamapps.dto; interface A { String a; command x1(String y); event z(); }"
						+ "managed class B implements A { String b; command x2(String y2); event z2();}"
						+ "managed class C extends B implements A { String c; command x3(String y3); event z3();}"
		);
	}

	@Test
	public void classTypeScriptFactoryMethod() throws Exception {
		executeClassTest(
				"org.teamapps.dto.A", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethod.tsd", "package \"x\":org.teamapps.dto; @TypeScriptFactory class A { String nonReq1; required String req1; int nonReq2; required int req2;}"
		);
	}

	@Test
	public void classTypeScriptFactoryMethodWithoutParameters() throws Exception {
		executeClassTest(
				"org.teamapps.dto.A", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithoutParameters.tsd", "package \"x\":org.teamapps.dto; @TypeScriptFactory class A { }"
		);
	}

	@Test
	public void classTypeScriptFactoryMethodWithOnlyRequiredParameters() throws Exception {
		executeClassTest(
				"org.teamapps.dto.A", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithOnlyRequiredParameters.tsd", "package \"x\":org.teamapps.dto; @TypeScriptFactory class A { required String req1; required int req2;}"
		);
	}


	@Test
	public void classTypeScriptFactoryMethodWithOnlyNonRequiredParameters() throws Exception {
		executeClassTest(
				"org.teamapps.dto.A", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithOnlyNonRequiredParameters.tsd", "package \"x\":org.teamapps.dto; @TypeScriptFactory class A { String nonReq1; int nonReq2; }"
		);
	}

	@Test
	public void referenceToClassListGeneratesCorrespondingImport() throws Exception {
		executeClassTest(
				"org.teamapps.dto.B", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_referenceToClassListGeneratesCorrespondingImport.tsd", "package \"x\":org.teamapps.dto; \tclass A {} \n"
						+ "\tclass B { List<A> aObjects; }"
		);
	}

	@Test
	public void referenceToEnumListGeneratesCorrespondingImport() throws Exception {
		executeClassTest(
				"org.teamapps.dto.DtoCalendar", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_referenceToEnumListGeneratesCorrespondingImport.tsd", "package \"x\":org.teamapps.dto; \tenum DtoWeekDay {SUNDAY, MONDAY }\n"
						+ "\tclass DtoCalendar { List<DtoWeekDay> workingDays; }"
		);
	}

	@Test
	public void importStatementsForSuperClassCommandAndEventHandler() throws Exception {
		executeClassTest(
				"org.teamapps.dto.B", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_importStatementsForSuperClassCommandAndEventHandler.tsd", "package \"x\":org.teamapps.dto; \tmanaged class A {command a(); event b();}\n"
						+ "\tinterface I {command i(); event j();}\n"
						+ "\tmanaged class B extends A implements I {}"
		);
	}

	@Test
	public void staticCommands() throws Exception {
		executeClassTest("org.teamapps.dto.A", "org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_staticCommands.tsd", "package \"x\":org.teamapps.dto; abstract managed class A { static command a(String b); }"
		);
	}

	@Test
	public void plainObjects() throws Exception {
		String dslString = "package \"x\":org.teamapps.dto; interface X {}"
				+ "class A { Object x; List<Object> y; Dictionary<Object> z; }";
		executeClassTest(
				"org.teamapps.dto.A", "org/teamapps/dsl/TeamAppsTypeScriptDtoGeneratorTest_plainObjects.tsd", dslString
		);
	}

	@Test
	public void queries() throws Exception {
		String dslString = "package \"x\":org.teamapps.dto; interface X { query queryEntries(int x) returns List<String>; }";
		executeInterfaceTest(
				"org.teamapps.dto.X", "org/teamapps/dsl/TeamAppsTypeScriptDtoGeneratorTest_interfacesWithQueries.tsd", dslString
		);
	}

	@Test
	public void enums() throws Exception {
		executeEnumTest(
				"org.teamapps.dto.E", "org/teamapps/dsl/TeamAppsTypeScriptDtoGeneratorTest_enums.tsd", "package \"x\":org.teamapps.dto; enum E { "
						+ " A, B "
						+ "} "
		);
	}

	@Test
	public void stringEnums() throws Exception {
		executeEnumTest(
				"org.teamapps.dto.E", "org/teamapps/dsl/TeamAppsTypeScriptDtoGeneratorTest_stringEnums.tsd", "package \"x\":org.teamapps.dto; enum E { "
						+ " A = \"a\", B = \"b\" "
						+ "} "
		);
	}

	@Test
	public void crossPackageImports() throws Exception {
		executeClassTest(
				"some.other.pkg.X",
				"org/teamapps/dsl/TeamAppsTypeScriptDtoGeneratorTest_crossPackageImports.tsd",
				"package \"xxx\":org.teamapps.dto; class Location { String href; }",
				"package \"yyy\":some.other.pkg; import org.teamapps.dto.Location; class X { Location location; }"
		);
	}

	private void executeClassTest(String qualifiedClassName, String expectedResultResourceName, String... dslString) throws IOException {
		TeamAppsIntermediateDtoModel model = createModel(dslString);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsTypeScriptGenerator(model).generateClassDefinition(model.findClassByQualifiedName(qualifiedClassName).orElseThrow(), stringWriter);
		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private void executeInterfaceTest(String qualifiedInterfaceName, String expectedResultResourceName, String... dslString) throws IOException {
		TeamAppsIntermediateDtoModel model = createModel(dslString);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsTypeScriptGenerator(model).generateInterfaceDefinition(model.findInterfaceByQualifiedName(qualifiedInterfaceName).orElseThrow(), stringWriter);
		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private void executeEnumTest(String qualifiedEnumName, String expectedResultResourceName, String... dslString) throws IOException {
		TeamAppsIntermediateDtoModel model = createModel(dslString);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsTypeScriptGenerator(model).generateEnum(model.findEnumByQualifiedName(qualifiedEnumName).orElseThrow(), stringWriter);
		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private static TeamAppsIntermediateDtoModel createModel(String... dslString) throws IOException {
		List<TeamAppsDtoParser.ClassCollectionContext> classCollectionContexts = Arrays.stream(dslString)
				.map(s -> ExceptionUtil.softenExceptions(() -> ParserFactory.createParser(new StringReader(s), s.substring(0, Math.min(50, s.length())) + "...").classCollection()))
				.toList();
		return new TeamAppsIntermediateDtoModel(classCollectionContexts, "test");
	}
}
