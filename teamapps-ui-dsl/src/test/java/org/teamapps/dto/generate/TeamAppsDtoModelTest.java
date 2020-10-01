/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.dto.generate;

import org.antlr.v4.runtime.ParserRuleContext;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.teamapps.dto.TeamAppsDtoParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TeamAppsDtoModelTest {

	private static TeamAppsDtoModel createModel(String dslString) throws IOException {
		TeamAppsDtoParser.ClassCollectionContext classCollectionContext = ParserFactory.createParser(new StringReader(dslString)).classCollection();
		return new TeamAppsDtoModel(classCollectionContext);
	}

	@Test
	public void getClassesAndInterfacesReferencedForSubEvents() throws Exception {
		TeamAppsDtoModel model = createModel("interface I { subevent i(int i); }"
				+ "class M { subevent m(int m); }"
				+ "class N extends M { subevent n(int n); }"
				+ "class A { event x(String a1, subevent<M> a2); }"
				+ "class B { event y(String b1, subevent<M> b2); }");

		List<ParserRuleContext> result = model.getClassesAndInterfacesReferencedForSubEvents();

		Assertions.assertThat(result).extracting(parserRuleContext -> {
			if (parserRuleContext instanceof TeamAppsDtoParser.ClassDeclarationContext) {
				return ((TeamAppsDtoParser.ClassDeclarationContext) parserRuleContext).Identifier().getText();
			} else {
				return ((TeamAppsDtoParser.InterfaceDeclarationContext) parserRuleContext).Identifier().getText();
			}
		}).containsExactly("M");
	}

	@Test
	public void testFindAllSubEventsInHierarchy() throws Exception {
		TeamAppsDtoModel model = createModel("interface I { subevent i(int i); }"
				+ "interface I2 { subevent i2(int i2); }"
				+ "class M implements I { subevent m(int m); }"
				+ "class N extends M implements I2 { subevent n(int n); }"
				+ "class A { event x(String x1, subevent<M> x2); }");

		List<TeamAppsDtoParser.SubEventDeclarationContext> result = model.findAllSubEventsInHierarchy(model.findClassByName("M", false));

		Assertions.assertThat(result).extracting(subEventDeclarationContext -> subEventDeclarationContext.Identifier().getText())
				.containsExactlyInAnyOrder("i", "i2", "m", "n");
	}

	@Test
	public void testFindAncestorOfType() throws Exception {
		TeamAppsDtoModel model = createModel("interface I { subevent i(int i); }");
		TeamAppsDtoParser.SubEventDeclarationContext subEvent = model.getSubEventDeclarations().get(0);
		TeamAppsDtoParser.InterfaceDeclarationContext result = TeamAppsDtoModel.findAncestorOfType(subEvent, TeamAppsDtoParser.InterfaceDeclarationContext.class);

		assertEquals("I", result.Identifier().getText());
	}
}
