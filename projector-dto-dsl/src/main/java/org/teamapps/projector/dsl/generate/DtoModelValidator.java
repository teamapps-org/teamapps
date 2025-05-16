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
package org.teamapps.projector.dsl.generate;

import org.teamapps.projector.dsl.generate.wrapper.ClassOrInterfaceWrapper;
import org.teamapps.projector.dsl.generate.wrapper.TypeWrapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DtoModelValidator {

	private final IntermediateDtoModel model;

	public DtoModelValidator(IntermediateDtoModel model) {
		this.model = model;
	}

	public void validate() throws Exception {
		validateNoDuplicateClassDeclarations(model.getAllTypeDeclarations());
		validateImports();
		validateReferences(model.getOwnClassAndInterfaceDeclarations());
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

	private void validateImports() {
		model.getCompilationUnits().stream()
				.flatMap(cu -> cu.importDeclaration().stream())
				.filter(i -> i.externalInterfaceTypeModifier() == null)
				.forEach(i -> model.findTypeByQualifiedName(i.qualifiedTypeName().getText())
						.orElseThrow(() -> {
							String message = "Cannot resolve import " + i.qualifiedTypeName().getText();
							String unqualifiedName = i.qualifiedTypeName().Identifier().getText();
							Optional<String> importSuggestion = model.createImportSuggestion(unqualifiedName);
							if (importSuggestion.isPresent()) {
								message += " Did you mean: import " + importSuggestion.get() + ";";

//								try {
//									new FileSearchAndReplace(Path.of("."), ".dto", "import .*\\b" + unqualifiedName + ";", "import " + importSuggestion.get() + ";")
//											.execute();
//								} catch (IOException e) {
//									throw new RuntimeException(e);
//								}
							}

							return new ModelValidationException(i, message);
						}));
	}

	private void validateReferences(List<ClassOrInterfaceWrapper<?>> classesAndInterfaces) {
		classesAndInterfaces.stream()
				.forEach(t -> t.checkSuperTypeResolvability());
		classesAndInterfaces.stream()
				.flatMap(t -> t.getProperties().stream())
				.forEach(p -> p.getType().checkResolvability());
		classesAndInterfaces.stream()
				.flatMap(t -> t.getCommands().stream())
				.flatMap(c -> c.getParameters().stream())
				.map(p -> p.getType())
				.forEach(t -> t.checkResolvability());
		classesAndInterfaces.stream()
				.flatMap(t -> t.getEvents().stream())
				.flatMap(c -> c.getParameters().stream())
				.map(p -> p.getType())
				.forEach(t -> t.checkResolvability());
		classesAndInterfaces.stream()
				.flatMap(t -> t.getQueries().stream())
				.flatMap(c -> c.getParameters().stream())
				.map(p -> p.getType())
				.forEach(t -> t.checkResolvability());
	}

}
