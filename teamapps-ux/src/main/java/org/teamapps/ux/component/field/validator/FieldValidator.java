/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.ux.component.field.validator;

import org.teamapps.ux.component.field.FieldMessage;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface FieldValidator<VALUE> {

	List<FieldMessage> validate(VALUE value);

	static <VALUE> FieldValidator<VALUE> fromPredicate(Predicate<VALUE> validationPredicate, String errorMessage) {
		return value -> validationPredicate.test(value) ? Collections.emptyList() : Collections.singletonList(new FieldMessage(FieldMessage.Severity.ERROR, errorMessage));
	}

	static <VALUE> FieldValidator<VALUE> fromErrorMessageFunction(Function<VALUE, String> errorMessageOrNullFunction) {
		return value -> {
			String errorMessage = errorMessageOrNullFunction.apply(value);
			return errorMessage == null ? Collections.emptyList() : Collections.singletonList(new FieldMessage(FieldMessage.Severity.ERROR, errorMessage));
		};
	}

}
