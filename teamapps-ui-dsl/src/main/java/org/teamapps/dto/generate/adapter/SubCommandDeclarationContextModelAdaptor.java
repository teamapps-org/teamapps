/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.dto.generate.adapter;

import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.teamapps.dto.generate.TeamAppsDtoModel;
import org.teamapps.dto.TeamAppsDtoParser;

import java.util.function.Function;
import java.util.stream.Collectors;

public class SubCommandDeclarationContextModelAdaptor extends ReferencableEntityModelAdaptor<TeamAppsDtoParser.SubCommandDeclarationContext> {

	private final TeamAppsDtoModel astUtil;

	public SubCommandDeclarationContextModelAdaptor(TeamAppsDtoModel astUtil) {
		this.astUtil = astUtil;
	}

	@Override
	public Object getProperty(Interpreter interpreter, ST seld, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
		TeamAppsDtoParser.SubCommandDeclarationContext subCommandContext = (TeamAppsDtoParser.SubCommandDeclarationContext) o;
		if ("declaringClass".equals(propertyName)) {
			return TeamAppsDtoModel.getDeclaringClassOrInterface(subCommandContext);
		} else if ("allProperties".equals(propertyName)) {
			return subCommandContext.formalParameterWithDefault();
		} else if ("allRequiredProperties".equals(propertyName)) {
			return subCommandContext.formalParameterWithDefault();
		} else if ("requiredPropertiesNotImplementedBySuperClasses".equals(propertyName)) {
			return subCommandContext.formalParameterWithDefault();
		} else if ("superClassDecl".equals(propertyName)) {
			return null;
		} else if ("simplePropertiesByRelevance".equals(propertyName)) {
			return subCommandContext.formalParameterWithDefault().stream()
					.sorted((p1, p2) -> {
						Function<TeamAppsDtoParser.FormalParameterWithDefaultContext, Integer> getPriority = (p) -> {
							if (p.Identifier().getText().equals("id")) {
								return 50;
							} else if (p.Identifier().getText().equals("name")) {
								return 40;
							} else if (p.Identifier().getText().contains("Id")) {
								return 30;
							} else if (p.Identifier().getText().contains("Name")) {
								return 20;
							} else if (astUtil.findReferencedClass(p.type()) == null) {
								return 10;
							} else {
								return 0;
							}
						};
						return getPriority.apply(p2) - getPriority.apply(p1);
					})
					.collect(Collectors.toList());
		} else {
			return super.getProperty(interpreter, seld, o, property, propertyName);
		}
	}

	@Override
	protected String getTypeScriptIdentifier(TeamAppsDtoParser.SubCommandDeclarationContext node) {
		return getDeclaringTypeScriptFileBaseName(node) + "_" + StringUtils.capitalize(node.Identifier().getText()) + "SubCommand";
	}

	@Override
	protected String getJsonIdentifier(TeamAppsDtoParser.SubCommandDeclarationContext node) {
		return TeamAppsDtoModel.getDeclaringClassOrInterfaceName(node) + "." + node.Identifier().getText();
	}

	@Override
	protected String getJavaClassName(TeamAppsDtoParser.SubCommandDeclarationContext node) {
		return TeamAppsDtoModel.getDeclaringClassOrInterfaceName(node) + "." + StringUtils.capitalize(node.Identifier().getText()) + "SubCommand";
	}
}
