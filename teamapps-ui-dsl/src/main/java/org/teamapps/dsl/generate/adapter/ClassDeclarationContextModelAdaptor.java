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
import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDeclarationContextModelAdaptor extends ReferencableEntityModelAdaptor<TeamAppsDtoParser.ClassDeclarationContext> {

    private final TeamAppsIntermediateDtoModel model;

    public ClassDeclarationContextModelAdaptor(TeamAppsIntermediateDtoModel model) {
        super();
        this.model = model;
    }

    @Override
    public Object getProperty(Interpreter interpreter, ST seld, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
        TeamAppsDtoParser.ClassDeclarationContext classContext = (TeamAppsDtoParser.ClassDeclarationContext) o;
        if ("packageName".equals(propertyName)) {
            return TeamAppsIntermediateDtoModel.getPackageName(classContext);
        } else if ("imports".equals(propertyName)) {
            return model.getAllImports(classContext);
        } else if ("allProperties".equals(propertyName)) {
            return model.findAllProperties(classContext);
        } else if ("allRequiredProperties".equals(propertyName)) {
            return model.filterRequiredProperties(model.findAllProperties(classContext), true);
        } else if ("nonRequiredProperties".equals(propertyName)) {
            return classContext.propertyDeclaration().stream().filter(p -> p.requiredModifier() == null).collect(Collectors.toList());
        } else if ("allNonRequiredProperties".equals(propertyName)) {
            return model.filterRequiredProperties(model.findAllProperties(classContext), false);
        } else if ("superClass".equals(propertyName)) {
            return model.findSuperClass(classContext);
        } else if ("allSuperClasses".equals(propertyName)) {
            return model.findAllSuperClasses(classContext);
        } else if ("superClassAndDirectlyImplementedInterfaces".equals(propertyName)) {
            return model.findSuperClassAndDirectlyImplementedInterfaces(classContext);
        } else if ("hasCommands".equals(propertyName)) {
            return !model.getAllCommands(classContext).isEmpty();
        }  else if ("nonStaticCommandDeclarations".equals(propertyName)) {
            return classContext.commandDeclaration().stream()
                    .filter(cmd -> cmd.staticModifier() == null)
                    .collect(Collectors.toList());
        }  else if ("nonStaticEventDeclarations".equals(propertyName)) {
            return classContext.eventDeclaration().stream()
                    .filter(cmd -> cmd.staticModifier() == null)
                    .collect(Collectors.toList());
        } else if ("hasEvents".equals(propertyName)) {
            return !model.getAllEvents(classContext).isEmpty();
        } else if ("hasQueries".equals(propertyName)) {
            return !model.getAllQueries(classContext).isEmpty();
        } else if ("allEvents".equals(propertyName)) {
            return model.getAllEvents(classContext);
        } else if ("allNonStaticEvents".equals(propertyName)) {
            return model.getAllEvents(classContext).stream()
                    .filter(eventDeclarationContext -> eventDeclarationContext.staticModifier() == null)
                    .collect(Collectors.toList());
        } else if ("allStaticEvents".equals(propertyName)) {
            return model.getAllEvents(classContext).stream()
                    .filter(eventDeclarationContext -> eventDeclarationContext.staticModifier() != null)
                    .collect(Collectors.toList());
        } else if ("allQueries".equals(propertyName)) {
            return model.getAllQueries(classContext);
        } else if ("superClassAndDirectlyImplementedInterfacesWithCommands".equals(propertyName)) {
            return model.superClassAndDirectlyImplementedInterfacesWithCommands(classContext);
        } else if ("superClassAndDirectlyImplementedInterfacesWithEvents".equals(propertyName)) {
            return model.superClassAndDirectlyImplementedInterfacesWithEvents(classContext);
        } else if ("allSubClasses".equals(propertyName)) {
            return model.findAllSubClasses(classContext);
        } else if ("hasSubTypes".equals(propertyName)) {
            return model.findAllSubClasses(classContext).size() > 0;
        } else if ("allReferencedClassesAndInterfaces".equals(propertyName)) {
            return model.findAllReferencedClassesAndInterfaces(classContext);
        } else if ("allReferencedEnums".equals(propertyName)) {
            return model.findAllReferencedEnums(classContext);
        } else if ("propertiesNotImplementedBySuperClasses".equals(propertyName)) {
            return model.findPropertiesNotImplementedBySuperClasses(classContext);
        } else if ("requiredPropertiesNotImplementedBySuperClasses".equals(propertyName)) {
            return model.filterRequiredProperties(model.findPropertiesNotImplementedBySuperClasses(classContext), true);
        } else if ("nonRequiredPropertiesNotImplementedBySuperClasses".equals(propertyName)) {
            return model.filterRequiredProperties(model.findPropertiesNotImplementedBySuperClasses(classContext), false);
        } else if ("simplePropertiesByRelevance".equals(propertyName)) {
            return model.getSimplePropertiesSortedByRelevance(model.findAllProperties(classContext));
        } else if ("referenceableProperties".equals(propertyName)) {
            return model.getReferenceableProperties(classContext);
        } else if ("referenceableBaseClass".equals(propertyName)) {
            return model.isReferenceableBaseClass(classContext);
        } else if ("innerClasses".equals(propertyName)) {
            List<Object> innerClassDeclarations = new ArrayList<>();
            innerClassDeclarations.addAll(classContext.eventDeclaration());
            innerClassDeclarations.addAll(classContext.queryDeclaration());
            innerClassDeclarations.addAll(classContext.commandDeclaration());
            return innerClassDeclarations;
        } else {
            return super.getProperty(interpreter, seld, o, property, propertyName);
        }
    }

	@Override
	protected String getTypeScriptIdentifier(TeamAppsDtoParser.ClassDeclarationContext node) {
        return ModelUtil.getClassTypeScriptIdentifier(node);
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
