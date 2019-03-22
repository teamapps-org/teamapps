/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import java.util.stream.Collectors;

public class InterfaceDeclarationContextModelAdaptor extends ReferencableEntityModelAdaptor<TeamAppsDtoParser.InterfaceDeclarationContext> {

	private final TeamAppsDtoModel astUtil;

	public InterfaceDeclarationContextModelAdaptor(TeamAppsDtoModel astUtil) {
		this.astUtil = astUtil;
	}

	@Override
	public Object getProperty(Interpreter interpreter, ST seld, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
		TeamAppsDtoParser.InterfaceDeclarationContext interfaceContext = (TeamAppsDtoParser.InterfaceDeclarationContext) o;
		if ("allProperties".equals(propertyName)) {
			return astUtil.findAllProperties(interfaceContext);
		} else if ("requiredProperties".equals(propertyName)) {
			return astUtil.filterRequiredProperties(interfaceContext.propertyDeclaration(), true);
		} else if ("allRequiredProperties".equals(propertyName)) {
			return astUtil.filterRequiredProperties(astUtil.findAllProperties(interfaceContext), true);
		} else if ("nonRequiredProperties".equals(propertyName)) {
			return interfaceContext.propertyDeclaration().stream().filter(p -> p.requiredModifier() == null).collect(Collectors.toList());
		} else if ("allNonRequiredProperties".equals(propertyName)) {
			return astUtil.filterRequiredProperties(astUtil.findAllProperties(interfaceContext), false);
		} else if ("superInterfaces".equals(propertyName)) {
			return astUtil.findSuperInterfaces(interfaceContext);
		} else if ("superClassAndDirectlyImplementedInterfaces".equals(propertyName)) {
			return astUtil.findSuperInterfaces(interfaceContext);
		} else if ("allSubClasses".equals(propertyName)) {
			return astUtil.findAllSubClasses(interfaceContext);
		} else if ("allSuperInterfaces".equals(propertyName)) {
			return astUtil.findAllSuperInterfaces(interfaceContext);
		} else if ("allSubEventsInHierarchy".equals(propertyName)) {
			return astUtil.findAllSubEventsInHierarchy(interfaceContext);
		} else if ("subEventBaseClassName".equals(propertyName)) {
			return interfaceContext.Identifier().getText() + "SubEvent";
		} else if ("isDescendantOfClassOrInterfaceReferencedForSubEvents".equals(propertyName)) {
			return astUtil.isDescendantOfClassOrInterfaceReferencedForSubEvents(interfaceContext);
		} else if ("allImplementingClasses".equals(propertyName)) {
			return astUtil.findAllImplementingClasses(interfaceContext);
		} else if ("nonStaticCommandDeclaration".equals(propertyName)) {
			return interfaceContext.commandDeclaration().stream()
					.filter(cmd -> cmd.staticModifier() == null)
					.collect(Collectors.toList());
		} else if ("hasCommands".equals(propertyName)) {
			return !astUtil.getAllCommands(interfaceContext).isEmpty();
		} else if ("hasSubCommands".equals(propertyName)) {
			return !astUtil.getAllSubCommands(interfaceContext).isEmpty();
		} else if ("hasEvents".equals(propertyName)) {
			return !astUtil.getAllEvents(interfaceContext).isEmpty();
		} else if ("allEvents".equals(propertyName)) {
			return astUtil.getAllEvents(interfaceContext);
		} else if ("hasSubEvents".equals(propertyName)) {
			return !astUtil.getAllSubEvents(interfaceContext).isEmpty();
		} else if ("superInterfacesWithCommands".equals(propertyName)) {
			return astUtil.getSuperInterfacesWithCommands(interfaceContext);
		} else if ("superInterfacesWithSubCommands".equals(propertyName)) {
			return astUtil.getSuperInterfacesWithSubCommands(interfaceContext);
		} else if ("superInterfacesWithEvents".equals(propertyName)) {
			return astUtil.getSuperInterfacesWithEvents(interfaceContext);
		} else if ("superInterfacesWithSubEvents".equals(propertyName)) {
			return astUtil.getSuperInterfacesWithSubEvents(interfaceContext);
		} else if ("inlineEnumProperties".equals(propertyName)) {
			return interfaceContext.propertyDeclaration().stream().filter(p -> p.type().inlineEnum() != null).collect(Collectors.toList());
		} else if ("allReferencedClassesAndInterfaces".equals(propertyName)) {
			return astUtil.findAllReferencedClassesAndInterfaces(interfaceContext);
		} else if ("allReferencedEnums".equals(propertyName)) {
			return astUtil.findAllReferencedEnums(interfaceContext);
		} else if ("simplePropertiesByRelevance".equals(propertyName)) {
			return astUtil.getSimplePropertiesSortedByRelevance(astUtil.findAllProperties(interfaceContext));
		} else if ("subCommandInterfaceNeeded".equals(propertyName)) {
			return astUtil.interfaceOrDescendantHasSubCommandDeclarations(interfaceContext);
		} else if ("subEventInterfaceNeeded".equals(propertyName)) {
			return astUtil.interfaceOrDescendantHasSubEventDeclarations(interfaceContext);
		} else {
			return super.getProperty(interpreter, seld, o, property, propertyName);
		}
	}

	@Override
	protected String getTypeScriptIdentifier(TeamAppsDtoParser.InterfaceDeclarationContext node) {
		return node.Identifier().getText() + "Config";
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
