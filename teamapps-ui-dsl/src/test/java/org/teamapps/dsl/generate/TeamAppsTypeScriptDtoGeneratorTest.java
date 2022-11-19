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

public class TeamAppsTypeScriptDtoGeneratorTest {

	@Test
	public void classProperties() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; class A {\n"
						+ "\trequired String a;\n"
						+ "\tString b;\n"
						+ "\tList<Long> c;\n"
						+ "}",
				"A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classProperties.tsd"
		);
	}

	@Test
	public void classesImplementTheirInterfaces() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; class A {}"
						+ "interface B { String bProperty; }"
						+ "interface C { required List<Integer> cProperty; }"
						+ "class D extends A implements B, C {}",
				"D",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classesImplementTheirInterfaces.tsd"
		);
	}

	@Test
	public void interfaces() throws Exception {
		executeInterfaceTest(
				"package org.teamapps.dto; " +
						"interface A { "
						+ " required String a;"
						+ " String b;"
						+ " command x1(String x);"
						+ " command x2(String x) returns List<Integer>;"
						+ " event y(String y);"
						+ " query z(String s) returns List<String>;"
						+ "}",
				"A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_interfaces.tsd"
		);
	}

	@Test
	public void interfaceInteritance() throws Exception {
		executeInterfaceTest(
				"package org.teamapps.dto; " +
						"interface A { String a; }"
						+ "interface B extends A { String b; }",
				"B",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_interfaceInteritance.tsd"
		);
	}

	@Test
	public void classCommandInteritance() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; interface A { String a; command x1(String y); event z(); }"
						+ "managed class B implements A { String b; command x2(String y2); event z2();}"
						+ "managed class C extends B implements A { String c; command x3(String y3); event z3();}",
				"C",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classCommandInteritance.tsd"
		);
	}

	@Test
	public void classTypeScriptFactoryMethod() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; @TypeScriptFactory class A { String nonReq1; required String req1; int nonReq2; required int req2;}",
				"A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethod.tsd"
		);
	}

	@Test
	public void classTypeScriptFactoryMethodWithoutParameters() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; @TypeScriptFactory class A { }",
				"A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithoutParameters.tsd"
		);
	}

	@Test
	public void classTypeScriptFactoryMethodWithOnlyRequiredParameters() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; @TypeScriptFactory class A { required String req1; required int req2;}",
				"A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithOnlyRequiredParameters.tsd"
		);
	}


	@Test
	public void classTypeScriptFactoryMethodWithOnlyNonRequiredParameters() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; @TypeScriptFactory class A { String nonReq1; int nonReq2; }",
				"A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithOnlyNonRequiredParameters.tsd"
		);
	}

	@Test
	public void referenceToClassListGeneratesCorrespondingImport() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; \tclass A {} \n"
						+ "\tclass B { List<A> aObjects; }",
				"B",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_referenceToClassListGeneratesCorrespondingImport.tsd"
		);
	}

	@Test
	public void referenceToEnumListGeneratesCorrespondingImport() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; \tenum DtoWeekDay {SUNDAY, MONDAY }\n"
						+ "\tclass DtoCalendar { List<DtoWeekDay> workingDays; }",
				"DtoCalendar",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_referenceToEnumListGeneratesCorrespondingImport.tsd"
		);
	}

	@Test
	public void importStatementsForSuperClassCommandAndEventHandler() throws Exception {
		executeClassTest(
				"package org.teamapps.dto; \tmanaged class A {command a(); event b();}\n"
						+ "\tinterface I {command i(); event j();}\n"
						+ "\tmanaged class B extends A implements I {}",
				"B",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_importStatementsForSuperClassCommandAndEventHandler.tsd"
		);
	}

	@Test
	public void staticCommands() throws Exception {
		executeClassTest("package org.teamapps.dto; abstract managed class A { static command a(String b); }",
				"A",
				"org/teamapps/dsl/TeamAppsTypeScriptGeneratorTest_staticCommands.tsd"
		);
	}

	@Test
	public void plainObjects() throws Exception {
		String dslString = "package org.teamapps.dto; interface X {}"
				+ "class A { Object x; List<Object> y; Dictionary<Object> z; }";
		executeClassTest(
				dslString,
				"A",
				"org/teamapps/dsl/TeamAppsTypeScriptDtoGeneratorTest_plainObjects.tsd"
		);
	}

	@Test
	public void queries() throws Exception {
		String dslString = "package org.teamapps.dto; interface X { query queryEntries(int x) returns List<String>; }";
		executeInterfaceTest(
				dslString,
				"X",
				"org/teamapps/dsl/TeamAppsTypeScriptDtoGeneratorTest_interfacesWithQueries.tsd"
		);
	}

	private void executeClassTest(String dslString, String className, String expectedResultResourceName) throws IOException {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(
				dslString
		)).classCollection();
		TeamAppsIntermediateDtoModel model = new TeamAppsIntermediateDtoModel(classCollectionContext);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsTypeScriptGenerator(model).generateClassDefinition(model.findClassByName(className, false), stringWriter);

		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private void executeInterfaceTest(String dslString, String interfaceName, String expectedResultResourceName) throws IOException {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(
				dslString
		)).classCollection();
		TeamAppsIntermediateDtoModel model = new TeamAppsIntermediateDtoModel(classCollectionContext);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsTypeScriptGenerator(model).generateInterfaceDefinition(model.findInterfaceByName(interfaceName, false), stringWriter);

		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}
}
