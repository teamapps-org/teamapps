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
package org.teamapps.dsl.generate;

import org.teamapps.dsl.generate.wrapper.ClassWrapper;
import org.teamapps.dsl.generate.wrapper.InterfaceWrapper;
import org.teamapps.dsl.generate.wrapper.TypeWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.teamapps.dsl.generate.ErrorMessageUtil.runWithExceptionMessagePrefix;

public class TeamAppsDtoModelValidator {

	private final TeamAppsIntermediateDtoModel model;

	public TeamAppsDtoModelValidator(TeamAppsIntermediateDtoModel model) {
		this.model = model;
	}

	public void validate() throws Exception {
		List<ClassWrapper> classDeclarations = model.getClassDeclarations();

		validateNoDuplicateClassDeclarations(model.getAllTypeDeclarations());
		for (ClassWrapper classDeclaration : classDeclarations) {
			checkClassWithCommandsOrEventsIsManaged(classDeclaration);
			checkManagedClassDefinesIdProperty(classDeclaration);
		}
		for (InterfaceWrapper interfaceDeclaration : model.getInterfaceDeclarations()) {
			checkManagedInterfaceDefinesIdProperty(interfaceDeclaration);
		}
	}

	private void validateNoDuplicateClassDeclarations(List<TypeWrapper<?>> typeDeclarations) {
		Map<String, Long> cardinalities = typeDeclarations.stream()
				.collect(Collectors.groupingBy(TypeWrapper::getQualifiedName, Collectors.counting()));
		String errorMessage = cardinalities.entrySet().stream()
				.filter(e -> e.getValue() > 1)
				.map(e -> e.getKey() + " is declared " + e.getValue() + " times.")
				.collect(Collectors.joining(";\n"));
		if (errorMessage.length() > 0) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	private void checkClassWithCommandsOrEventsIsManaged(ClassWrapper classDeclaration) throws IOException {
		runWithExceptionMessagePrefix(() -> {
			boolean hasCommandOrEvent = !classDeclaration.getAllCommands().isEmpty() || !classDeclaration.getAllEvents().isEmpty();
			if (hasCommandOrEvent && !classDeclaration.isManaged()) {
				throw new ModelValidationException("Dto class " + classDeclaration.getName() + " declares a command or event but is not managed!");
			}
		}, "Error while validating class " + classDeclaration.getName());
	}

	private void checkManagedClassDefinesIdProperty(ClassWrapper classDeclaration) throws IOException {
		runWithExceptionMessagePrefix(() -> {
			if (classDeclaration.isManaged() && classDeclaration.getAllProperties().stream().noneMatch(p -> p.type().getText().equals("String") && p.Identifier().getText().equals("id"))) {
				throw new ModelValidationException("Dto class " + classDeclaration.getName() + " is managed but does not declare an id property (String id)!");
			}
		}, "Error while validating class " + classDeclaration.getName());
	}

	private void checkManagedInterfaceDefinesIdProperty(InterfaceWrapper classDeclaration) throws IOException {
		runWithExceptionMessagePrefix(() -> {
			if (classDeclaration.isManaged() && classDeclaration.getAllProperties().stream().noneMatch(p -> p.type().getText().equals("String") && p.Identifier().getText().equals("id"))) {
				throw new ModelValidationException("Dto interface " + classDeclaration.getName() + " is managed but does not declare an id property (String id)!");
			}
		}, "Error while validating interface " + classDeclaration.getName());
	}
}
