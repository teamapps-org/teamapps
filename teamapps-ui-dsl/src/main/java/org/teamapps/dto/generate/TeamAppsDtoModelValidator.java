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
import java.util.Map;
import java.util.stream.Collectors;

public class TeamAppsDtoModelValidator {

	private final TeamAppsDtoModel model;

	public TeamAppsDtoModelValidator(TeamAppsDtoModel model) {
		this.model = model;
	}

	public void validate() {
		List<TeamAppsDtoParser.ClassDeclarationContext> classDeclarations = model.getClassDeclarations();

		validateNoDuplicateClassDeclarations(classDeclarations);
		for (TeamAppsDtoParser.ClassDeclarationContext classDeclaration : classDeclarations) {
			validateRequiredPropertiesHaveNoDefaultValue(classDeclaration);
		}

		List<TeamAppsDtoParser.EnumDeclarationContext> enumDeclarations = model.getEnumDeclarations();

		validateNoDuplicateEnumDeclarations(enumDeclarations);
	}

	private void validateNoDuplicateClassDeclarations(List<TeamAppsDtoParser.ClassDeclarationContext> classDeclarations) {
		Map<String, Long> cardinalities = classDeclarations.stream()
				.collect(Collectors.groupingBy(classDeclarationContext -> classDeclarationContext.Identifier().getText(), Collectors.counting()));
		validateNoMultipleEntries(cardinalities);
	}

	private void validateRequiredPropertiesHaveNoDefaultValue(TeamAppsDtoParser.ClassDeclarationContext classDeclaration) {
		for (TeamAppsDtoParser.PropertyDeclarationContext pd : classDeclaration.propertyDeclaration()) {
			if (pd.requiredModifier() != null && pd.defaultValueAssignment() != null) {
				throw new IllegalArgumentException("A required property declaration may not have a default value! Erroneous declaration: " + ((TeamAppsDtoParser.ClassDeclarationContext) pd
						.getParent()).Identifier().getText() + "." + pd.Identifier().getText());
			}
		}
	}

	private void validateNoDuplicateEnumDeclarations(List<TeamAppsDtoParser.EnumDeclarationContext> enumDeclarations) {
		Map<String, Long> cardinalities = enumDeclarations.stream()
				.collect(Collectors.groupingBy(classDeclarationContext -> classDeclarationContext.Identifier().getText(), Collectors.counting()));
		validateNoMultipleEntries(cardinalities);
	}

	private void validateNoMultipleEntries(Map<String, Long> cardinalities) {
		String errorMessage = cardinalities.entrySet().stream()
				.filter(e -> e.getValue() > 1)
				.map(e -> e.getKey() + " is declared " + e.getValue() + " times.")
				.collect(Collectors.joining(";\n"));
		if (errorMessage.length() > 0) {
			throw new IllegalArgumentException(errorMessage);
		}
	}
}
