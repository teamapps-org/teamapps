/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.Test;
import org.teamapps.dto.TeamAppsDtoParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class TeamAppsTypeScriptDtoGeneratorTest {

	@Test
	public void classProperties() throws Exception {
		executeClassTest(
				"class A {\n"
						+ "\trequired String a;\n"
						+ "\tString b;\n"
						+ "\tList<Long> c;\n"
						+ "}",
				"A",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_classProperties.tsd"
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
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_classesImplementTheirInterfaces.tsd"
		);
	}

	@Test
	public void interfaces() throws Exception {
		executeInterfaceTest(
				"interface A { "
						+ " required String a;"
						+ " String b;"
						+ " command x1(String x);"
						+ " command x2(String x) returns List<Integer>;"
						+ " event y(String y);"
						+ " query z(String s) returns List<String>;"
						+ "}",
				"A",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_interfaces.tsd"
		);
	}

	@Test
	public void interfaceInteritance() throws Exception {
		executeInterfaceTest(
				"interface A { String a; }"
						+ "interface B extends A { String b; }",
				"B",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_interfaceInteritance.tsd"
		);
	}

	@Test
	public void classCommandInteritance() throws Exception {
		executeClassTest(
				"interface A { String a; command x1(String y); event z(); }"
						+ "class B implements A { String b; command x2(String y2); event z2();}"
						+ "class C extends B implements A { String c; command x3(String y3); event z3();}",
				"C",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_classCommandInteritance.tsd"
		);
	}

	@Test
	public void classTypeScriptFactoryMethod() throws Exception {
		executeClassTest(
				"@TypeScriptFactory class A { String nonReq1; required String req1; int nonReq2; required int req2;}",
				"A",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethod.tsd"
		);
	}

	@Test
	public void classTypeScriptFactoryMethodWithoutParameters() throws Exception {
		executeClassTest(
				"@TypeScriptFactory class A { }",
				"A",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithoutParameters.tsd"
		);
	}

	@Test
	public void classTypeScriptFactoryMethodWithOnlyRequiredParameters() throws Exception {
		executeClassTest(
				"@TypeScriptFactory class A { required String req1; required int req2;}",
				"A",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithOnlyRequiredParameters.tsd"
		);
	}


	@Test
	public void classTypeScriptFactoryMethodWithOnlyNonRequiredParameters() throws Exception {
		executeClassTest(
				"@TypeScriptFactory class A { String nonReq1; int nonReq2; }",
				"A",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_classTypeScriptFactoryMethodWithOnlyNonRequiredParameters.tsd"
		);
	}

	@Test
	public void referenceToClassListGeneratesCorrespondingImport() throws Exception {
		executeClassTest(
				"\tclass A {} \n"
						+ "\tclass B { List<A> aObjects; }",
				"B",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_referenceToClassListGeneratesCorrespondingImport.tsd"
		);
	}

	@Test
	public void referenceToEnumListGeneratesCorrespondingImport() throws Exception {
		executeClassTest(
				"\tenum UiWeekDay {SUNDAY, MONDAY }\n"
						+ "\tclass UiCalendar { List<UiWeekDay> workingDays; }",
				"UiCalendar",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_referenceToEnumListGeneratesCorrespondingImport.tsd"
		);
	}

	@Test
	public void importStatementsForSuperClassCommandAndEventHandler() throws Exception {
		executeClassTest(
				"\tclass A {command a(); event b();}\n"
						+ "\tinterface I {command i(); event j();}\n"
						+ "\tclass B extends A implements I {}",
				"B",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_importStatementsForSuperClassCommandAndEventHandler.tsd"
		);
	}

	@Test
	public void staticCommands() throws Exception {
		executeClassTest("abstract class A { static command a(String b); }",
				"A",
				"org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_staticCommands.tsd"
		);
	}

	@Test
	public void plainObjects() throws Exception {
		String dslString = "interface X {}"
				+ "class A { Object x; List<Object> y; Dictionary<Object> z; }";
		executeClassTest(
				dslString,
				"A",
				"org/teamapps/dto/TeamAppsTypeScriptDtoGeneratorTest_plainObjects.tsd"
		);
	}

	@Test
	public void commandExecutor() throws Exception {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(
				"abstract class A { static command staticCommand(String x); command nonStaticCommand(String x); }"
		)).classCollection();
		TeamAppsDtoModel model = new TeamAppsDtoModel(classCollectionContext);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsTypeScriptGenerator(model).generateCommandExecutor(model.getCommandDeclarations(), stringWriter);

		GeneratorTestUtil.compareCodeWithResource("org/teamapps/dto/TeamAppsTypeScriptGeneratorTest_commandExecutor.tsd", stringWriter.toString());
	}

	@Test
	public void queries() throws Exception {
		String dslString = "interface X { query queryEntries(int x) returns List<String>; }";
		executeInterfaceTest(
				dslString,
				"X",
				"org/teamapps/dto/TeamAppsTypeScriptDtoGeneratorTest_interfacesWithQueries.tsd"
		);
	}

	@Test
	public void queryFunctionAdder() throws Exception {
		String dslString = "interface X { query queryEntries(int x) returns List<String>; }";
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(dslString)).classCollection();
		TeamAppsDtoModel model = new TeamAppsDtoModel(classCollectionContext);

		StringWriter stringWriter = new StringWriter();
		final List<ParserRuleContext> allClassesAndInterfacesWithQueries = model.getAllClassesAndInterfacesWithQueries();
		new TeamAppsTypeScriptGenerator(model).generateQueryFunctionAdder(allClassesAndInterfacesWithQueries, stringWriter);

		GeneratorTestUtil.compareCodeWithResource("org/teamapps/dto/TeamAppsTypeScriptDtoGeneratorTest_queryFunctionAdder.tsd", stringWriter.toString());
	}

	private void executeClassTest(String dslString, String className, String expectedResultResourceName) throws IOException {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(
				dslString
		)).classCollection();
		TeamAppsDtoModel model = new TeamAppsDtoModel(classCollectionContext);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsTypeScriptGenerator(model).generateClassDefinition(model.findClassByName(className, false), stringWriter);

		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}

	private void executeInterfaceTest(String dslString, String interfaceName, String expectedResultResourceName) throws IOException {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(
				dslString
		)).classCollection();
		TeamAppsDtoModel model = new TeamAppsDtoModel(classCollectionContext);

		StringWriter stringWriter = new StringWriter();
		new TeamAppsTypeScriptGenerator(model).generateInterfaceDefinition(model.findInterfaceByName(interfaceName, false), stringWriter);

		GeneratorTestUtil.compareCodeWithResource(expectedResultResourceName, stringWriter.toString());
	}
}
