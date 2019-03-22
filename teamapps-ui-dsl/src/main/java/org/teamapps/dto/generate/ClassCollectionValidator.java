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
package org.teamapps.dto.generate;

import org.teamapps.dto.TeamAppsDtoParser;

import java.util.List;
import java.util.stream.Collectors;

public class ClassCollectionValidator {

    public static void validate(TeamAppsDtoParser.ClassCollectionContext classCollectionContext) {
        List<TeamAppsDtoParser.ClassDeclarationContext> classDeclarations = findClassDeclarations(classCollectionContext.typeDeclaration());
        for (TeamAppsDtoParser.ClassDeclarationContext classDeclaration : classDeclarations) {
            for (TeamAppsDtoParser.PropertyDeclarationContext pd : classDeclaration.propertyDeclaration()) {
                if (pd.requiredModifier() != null && pd.defaultValueAssignment() != null) {
                    throw new IllegalArgumentException("A required property declaration may not have a default value! Erroneous declaration: " + ((TeamAppsDtoParser.ClassDeclarationContext) pd.getParent()).Identifier().getText() + "." + pd.Identifier().getText());
                }
            }
        }
    }

    private static List<TeamAppsDtoParser.ClassDeclarationContext> findClassDeclarations(List<TeamAppsDtoParser.TypeDeclarationContext> types) {
        return types.stream()
                .filter(typeContext -> typeContext.classDeclaration() != null)
                .map(typeContext -> typeContext.classDeclaration())
                .collect(Collectors.toList());
    }
}
