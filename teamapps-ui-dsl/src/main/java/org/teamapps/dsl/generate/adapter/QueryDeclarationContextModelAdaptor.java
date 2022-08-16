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
import org.teamapps.dsl.generate.ParserFactory;
import org.teamapps.dsl.generate.TeamAppsDtoModel;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryDeclarationContextModelAdaptor extends ReferencableEntityModelAdaptor<TeamAppsDtoParser.QueryDeclarationContext> {

    private static final TeamAppsDtoParser.FormalParameterWithDefaultContext COMPONENT_ID_PARAMETER;
    private final TeamAppsDtoModel astUtil;

    static {
        try {
            COMPONENT_ID_PARAMETER = ParserFactory.createParser(new StringReader("String componentId")).formalParameterWithDefault();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public QueryDeclarationContextModelAdaptor(TeamAppsDtoModel astUtil) {
        this.astUtil = astUtil;
    }

    @Override
    public Object getProperty(Interpreter interpreter, ST seld, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
        TeamAppsDtoParser.QueryDeclarationContext queryContext = (TeamAppsDtoParser.QueryDeclarationContext) o;
        if ("declaringClass".equals(propertyName)) {
            return TeamAppsDtoModel.getDeclaringClassOrInterface(queryContext);
        } else if ("typeScriptInterfaceName".equals(propertyName)) {
            return TeamAppsDtoModel.getDeclaringClassOrInterfaceName(queryContext) + "_" + StringUtils.capitalize(queryContext.Identifier().getText()) + "Query";
        } else if ("allProperties".equals(propertyName)) {
            return getAllParameters(queryContext);
        } else if ("allRequiredProperties".equals(propertyName)) {
            return getAllParameters(queryContext);
        } else if ("requiredPropertiesNotImplementedBySuperClasses".equals(propertyName)) {
            return getAllParameters(queryContext);
        } else if ("simplePropertiesByRelevance".equals(propertyName)) {
            return getAllParameters(queryContext).stream()
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
                            } else if (astUtil.findReferencedClass(p.type()) == null)  {
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
    protected String getTypeScriptIdentifier(TeamAppsDtoParser.QueryDeclarationContext node) {
        return getDeclaringTypeScriptFileBaseName(node) + "_" + StringUtils.capitalize(node.Identifier().getText()) + "Query";
    }

    @Override
    protected String getJsonIdentifier(TeamAppsDtoParser.QueryDeclarationContext node) {
        return TeamAppsDtoModel.getDeclaringClassOrInterfaceName(node) + "." + node.Identifier().getText();
    }

	@Override
	protected String getJavaClassName(TeamAppsDtoParser.QueryDeclarationContext node) {
        return TeamAppsDtoModel.getDeclaringClassOrInterfaceName(node) + "." + StringUtils.capitalize(node.Identifier().getText()) + "Query";
    }

	private List<TeamAppsDtoParser.FormalParameterWithDefaultContext> getAllParameters(TeamAppsDtoParser.QueryDeclarationContext commandContext) {
        ArrayList<TeamAppsDtoParser.FormalParameterWithDefaultContext> allProperties = new ArrayList<>(commandContext.formalParameterWithDefault());
        allProperties.add(0, COMPONENT_ID_PARAMETER);
        return allProperties;
    }

}
