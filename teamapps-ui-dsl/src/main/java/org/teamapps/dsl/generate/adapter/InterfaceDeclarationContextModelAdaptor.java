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
package org.teamapps.dsl.generate.adapter;

import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;
import org.teamapps.dsl.TeamAppsDtoParser;

import java.util.stream.Collectors;

public class InterfaceDeclarationContextModelAdaptor extends ReferencableEntityModelAdaptor<TeamAppsDtoParser.InterfaceDeclarationContext> {

	private final TeamAppsIntermediateDtoModel model;

	public InterfaceDeclarationContextModelAdaptor(TeamAppsIntermediateDtoModel model) {
		super();
		this.model = model;
	}

	@Override
	public Object getProperty(Interpreter interpreter, ST seld, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
		TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext = (TeamAppsDtoParser.InterfaceDeclarationContext) o;
		if ("packageName".equals(propertyName)) {
			return TeamAppsIntermediateDtoModel.getPackageName(interfaceContext);
		} else if ("imports".equals(propertyName)) {
			return model.getAllImports(interfaceContext);
		} else if ("allProperties".equals(propertyName)) {
			return model.findAllProperties(interfaceContext);
		} else if ("requiredProperties".equals(propertyName)) {
			return model.filterRequiredProperties(interfaceContext.propertyDeclaration(), true);
		} else if ("allRequiredProperties".equals(propertyName)) {
			return model.filterRequiredProperties(model.findAllProperties(interfaceContext), true);
		} else if ("nonRequiredProperties".equals(propertyName)) {
			return interfaceContext.propertyDeclaration().stream().filter(p -> p.requiredModifier() == null).collect(Collectors.toList());
		} else if ("allNonRequiredProperties".equals(propertyName)) {
			return model.filterRequiredProperties(model.findAllProperties(interfaceContext), false);
		} else if ("superInterfaces".equals(propertyName)) {
			return model.findSuperInterfaces(interfaceContext);
		} else if ("superClassAndDirectlyImplementedInterfaces".equals(propertyName)) {
			return model.findSuperInterfaces(interfaceContext);
		} else if ("allSubClasses".equals(propertyName)) {
			return model.findAllSubClasses(interfaceContext);
		} else if ("allSuperInterfaces".equals(propertyName)) {
			return model.findAllSuperInterfaces(interfaceContext);
		} else if ("subEventBaseClassName".equals(propertyName)) {
			return interfaceContext.Identifier().getText() + "SubEvent";
		} else if ("allImplementingClasses".equals(propertyName)) {
			return model.findAllImplementingClasses(interfaceContext);
		} else if ("nonStaticCommandDeclarations".equals(propertyName)) {
			return interfaceContext.commandDeclaration().stream()
					.filter(cmd -> cmd.staticModifier() == null)
					.collect(Collectors.toList());
		} else if ("nonStaticEventDeclarations".equals(propertyName)) {
			return interfaceContext.eventDeclaration().stream()
					.filter(cmd -> cmd.staticModifier() == null)
					.collect(Collectors.toList());
		} else if ("hasCommands".equals(propertyName)) {
			return !model.getAllCommands(interfaceContext).isEmpty();
		} else if ("hasEvents".equals(propertyName)) {
			return !model.getAllEvents(interfaceContext).isEmpty();
		} else if ("hasQueries".equals(propertyName)) {
			return !model.getAllQueries(interfaceContext).isEmpty();
		} else if ("allEvents".equals(propertyName)) {
			return model.getAllEvents(interfaceContext);
		} else if ("allNonStaticEvents".equals(propertyName)) {
			return model.getAllEvents(interfaceContext).stream()
					.filter(eventDeclarationContext -> eventDeclarationContext.staticModifier() == null)
					.collect(Collectors.toList());
		} else if ("allStaticEvents".equals(propertyName)) {
			return model.getAllEvents(interfaceContext).stream()
					.filter(eventDeclarationContext -> eventDeclarationContext.staticModifier() != null)
					.collect(Collectors.toList());
		} else if ("allQueries".equals(propertyName)) {
			return model.getAllQueries(interfaceContext);
		} else if ("superInterfacesWithCommands".equals(propertyName)) {
			return model.getSuperInterfacesWithCommands(interfaceContext);
		} else if ("superInterfacesWithEvents".equals(propertyName)) {
			return model.getSuperInterfacesWithEvents(interfaceContext);
		} else if ("allReferencedClassesAndInterfaces".equals(propertyName)) {
			return model.findAllReferencedClassesAndInterfaces(interfaceContext);
		} else if ("allReferencedEnums".equals(propertyName)) {
			return model.findAllReferencedEnums(interfaceContext);
		} else if ("simplePropertiesByRelevance".equals(propertyName)) {
			return model.getSimplePropertiesSortedByRelevance(model.findAllProperties(interfaceContext));
		} else if ("propertiesNotImplementedBySuperClasses".equals(propertyName)) {
			return interfaceContext.propertyDeclaration();
		} else if ("referencableProperties".equals(propertyName)) {
			return model.getReferencableProperties(interfaceContext);
		} else if ("referencableBaseClass".equals(propertyName)) {
			return model.isReferencableBaseInterface(interfaceContext);
		} else {
			return super.getProperty(interpreter, seld, o, property, propertyName);
		}
	}

	@Override
	protected String getTypeScriptIdentifier(TeamAppsDtoParser.InterfaceDeclarationContext node) {
		return ModelUtil.getInterfaceTypeScriptIdentifier(node);
	}

	@Override
	protected String getJsonIdentifier(TeamAppsDtoParser.InterfaceDeclarationContext node) {
		return node.Identifier().getText();
	}

	@Override
	protected String getJavaClassName(TeamAppsDtoParser.InterfaceDeclarationContext node) {
		return StringUtils.capitalize(node.Identifier().getText());
	}
}
