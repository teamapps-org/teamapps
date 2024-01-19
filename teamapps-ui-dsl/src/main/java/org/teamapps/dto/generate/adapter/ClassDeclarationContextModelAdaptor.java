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
package org.teamapps.dto.generate.adapter;

import org.apache.commons.lang3.StringUtils;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.teamapps.dto.generate.TeamAppsDtoModel;
import org.teamapps.dto.TeamAppsDtoParser;

import java.util.stream.Collectors;

public class ClassDeclarationContextModelAdaptor extends ReferencableEntityModelAdaptor<TeamAppsDtoParser.ClassDeclarationContext> {

    private final TeamAppsDtoModel astUtil;

    public ClassDeclarationContextModelAdaptor(TeamAppsDtoModel astUtil) {
        this.astUtil = astUtil;
    }

    @Override
    public Object getProperty(Interpreter interpreter, ST seld, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
        TeamAppsDtoParser.ClassDeclarationContext classContext = (TeamAppsDtoParser.ClassDeclarationContext) o;
        if ("allProperties".equals(propertyName)) {
            return astUtil.findAllProperties(classContext);
        } else if ("allRequiredProperties".equals(propertyName)) {
            return astUtil.filterRequiredProperties(astUtil.findAllProperties(classContext), true);
        } else if ("nonRequiredProperties".equals(propertyName)) {
            return classContext.propertyDeclaration().stream().filter(p -> p.requiredModifier() == null).collect(Collectors.toList());
        } else if ("allNonRequiredProperties".equals(propertyName)) {
            return astUtil.filterRequiredProperties(astUtil.findAllProperties(classContext), false);
        } else if ("superClass".equals(propertyName)) {
            return astUtil.findSuperClass(classContext);
        } else if ("allSuperClasses".equals(propertyName)) {
            return astUtil.findAllSuperClasses(classContext);
        } else if ("superClassAndDirectlyImplementedInterfaces".equals(propertyName)) {
            return astUtil.findSuperClassAndDirectlyImplementedInterfaces(classContext);
        } else if ("hasCommands".equals(propertyName)) {
            return !astUtil.getAllCommands(classContext).isEmpty();
        }  else if ("nonStaticCommandDeclarations".equals(propertyName)) {
            return classContext.commandDeclaration().stream()
                    .filter(cmd -> cmd.staticModifier() == null)
                    .collect(Collectors.toList());
        }  else if ("nonStaticEventDeclarations".equals(propertyName)) {
            return classContext.eventDeclaration().stream()
                    .filter(cmd -> cmd.staticModifier() == null)
                    .collect(Collectors.toList());
        } else if ("hasEvents".equals(propertyName)) {
            return !astUtil.getAllEvents(classContext).isEmpty();
        } else if ("allEvents".equals(propertyName)) {
            return astUtil.getAllEvents(classContext);
        } else if ("allNonStaticEvents".equals(propertyName)) {
            return astUtil.getAllEvents(classContext).stream()
                    .filter(eventDeclarationContext -> eventDeclarationContext.staticModifier() == null)
                    .collect(Collectors.toList());
        } else if ("allStaticEvents".equals(propertyName)) {
            return astUtil.getAllEvents(classContext).stream()
                    .filter(eventDeclarationContext -> eventDeclarationContext.staticModifier() != null)
                    .collect(Collectors.toList());
        } else if ("allQueries".equals(propertyName)) {
            return astUtil.getAllQueries(classContext);
        } else if ("superClassAndDirectlyImplementedInterfacesWithCommands".equals(propertyName)) {
            return astUtil.superClassAndDirectlyImplementedInterfacesWithCommands(classContext);
        } else if ("superClassAndDirectlyImplementedInterfacesWithEvents".equals(propertyName)) {
            return astUtil.superClassAndDirectlyImplementedInterfacesWithEvents(classContext);
        } else if ("subEventBaseClassName".equals(propertyName)) {
            return classContext.Identifier().getText() + "SubEvent";
        } else if ("isDescendantOfClassOrInterfaceReferencedForSubEvents".equals(propertyName)) {
            return astUtil.isDescendantOfClassOrInterfaceReferencedForSubEvents(classContext);
        } else if ("allSubClasses".equals(propertyName)) {
            return astUtil.findAllSubClasses(classContext);
        } else if ("hasSubTypes".equals(propertyName)) {
            return astUtil.findAllSubClasses(classContext).size() > 0;
        } else if ("inlineEnumProperties".equals(propertyName)) {
            return classContext.propertyDeclaration().stream().filter(p -> p.type().inlineEnum() != null).collect(Collectors.toList());
        } else if ("allReferencedClassesAndInterfaces".equals(propertyName)) {
            return astUtil.findAllReferencedClassesAndInterfaces(classContext);
        } else if ("allReferencedEnums".equals(propertyName)) {
            return astUtil.findAllReferencedEnums(classContext);
        } else if ("propertiesNotImplementedBySuperClasses".equals(propertyName)) {
            return astUtil.findPropertiesNotImplementedBySuperClasses(classContext);
        } else if ("requiredPropertiesNotImplementedBySuperClasses".equals(propertyName)) {
            return astUtil.filterRequiredProperties(astUtil.findPropertiesNotImplementedBySuperClasses(classContext), true);
        } else if ("nonRequiredPropertiesNotImplementedBySuperClasses".equals(propertyName)) {
            return astUtil.filterRequiredProperties(astUtil.findPropertiesNotImplementedBySuperClasses(classContext), false);
        } else if ("simplePropertiesByRelevance".equals(propertyName)) {
            return astUtil.getSimplePropertiesSortedByRelevance(astUtil.findAllProperties(classContext));
        } else if ("referenceableProperties".equals(propertyName)) {
            return astUtil.getReferenceableProperties(classContext);
        } else if ("referenceableBaseClass".equals(propertyName)) {
            return astUtil.isReferenceableBaseClass(classContext);
        } else {
            return super.getProperty(interpreter, seld, o, property, propertyName);
        }
    }

	@Override
	protected String getTypeScriptIdentifier(TeamAppsDtoParser.ClassDeclarationContext node) {
		return node.Identifier().getText() + "Config";
	}

    @Override
    protected String getJsonIdentifier(TeamAppsDtoParser.ClassDeclarationContext node) {
        return node.Identifier().getText();
    }

	@Override
	protected String getJavaClassName(TeamAppsDtoParser.ClassDeclarationContext node) {
		return StringUtils.capitalize(node.Identifier().getText());
	}

}
