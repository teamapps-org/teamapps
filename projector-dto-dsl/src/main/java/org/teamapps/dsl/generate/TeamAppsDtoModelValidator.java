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

import org.teamapps.dsl.generate.wrapper.TypeWrapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamAppsDtoModelValidator {

	private final TeamAppsIntermediateDtoModel model;

	public TeamAppsDtoModelValidator(TeamAppsIntermediateDtoModel model) {
		this.model = model;
	}

	public void validate() throws Exception {
		validateNoDuplicateClassDeclarations(model.getAllTypeDeclarations());
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

}