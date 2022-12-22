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
import org.teamapps.dsl.TeamAppsDtoParser.CommandDeclarationContext;
import org.teamapps.dsl.generate.TeamAppsIntermediateDtoModel;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandDeclarationContextModelAdaptor extends ReferencableEntityModelAdaptor<CommandDeclarationContext> {

    private final TeamAppsIntermediateDtoModel model;

    public CommandDeclarationContextModelAdaptor(TeamAppsIntermediateDtoModel model) {
        this.model = model;
    }

    @Override
    public Object getProperty(Interpreter interpreter, ST seld, CommandDeclarationContext context, Object property, String propertyName) throws STNoSuchPropertyException {
        if ("name".equals(propertyName)) {
            return context.Identifier().getText();
        } else if ("declaringClass".equals(propertyName)) {
            return model.getDeclaringClassOrInterface(context);
        } else if ("allProperties".equals(propertyName)) {
            return new ArrayList<>(context.formalParameterWithDefault());
        } else if ("allRequiredProperties".equals(propertyName)) {
            return new ArrayList<>(context.formalParameterWithDefault());
        } else if ("requiredPropertiesNotImplementedBySuperClasses".equals(propertyName)) {
            return new ArrayList<>(context.formalParameterWithDefault());
        } else if ("superClassDecl".equals(propertyName)) {
            return null;
        } else if ("simplePropertiesSortedByRelevance".equals(propertyName)) {
            return new ArrayList<>(context.formalParameterWithDefault()).stream()
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
                            } else if (model.findReferencedClass(p.type()) == null)  {
                                return 10;
                            } else {
                                return 0;
                            }
                        };
                        return getPriority.apply(p2) - getPriority.apply(p1);
                    })
                    .collect(Collectors.toList());
        } else {
            return super.getProperty(interpreter, seld, context, property, propertyName);
        }
    }

    @Override
    protected String getTypeScriptIdentifier(CommandDeclarationContext node) {
	    return model.getDeclaringClassOrInterface(node).getName() + "_" + StringUtils.capitalize(node.Identifier().getText()) + "Command";
    }

    @Override
    protected String getJsonIdentifier(CommandDeclarationContext node) {
        return model.getDeclaringClassOrInterface(node).getName() + "." + node.Identifier().getText();
    }

	@Override
	protected String getJavaClassName(CommandDeclarationContext node) {
        return StringUtils.capitalize(node.Identifier().getText()) + "Command";
    }
}
