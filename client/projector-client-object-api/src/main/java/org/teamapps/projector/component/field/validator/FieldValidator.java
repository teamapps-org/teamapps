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
package org.teamapps.projector.component.field.validator;

import org.teamapps.projector.component.field.FieldMessage;
import org.teamapps.projector.component.field.FieldMessageSeverity;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A generic interface for validating field values.
 * <p>
 * This interface is used to validate a single field's value and produce validation messages
 * when the validation fails. It is typically added to a field using the 
 * {@link org.teamapps.projector.component.field.Field#addValidator(FieldValidator)} method.
 * </p>
 *
 * @param <VALUE> the type of value to be validated
 */
public interface FieldValidator<VALUE> {

	/**
	 * Validates the provided value and returns a list of field messages.
	 * <p>
	 * Returning an empty list without {@link FieldMessageSeverity#ERROR} messages is regarded as
	 * a successful validation.
	 * Therefore, an empty list also represents a successful validation.
	 *
	 * @param value the value to validate
	 * @return a list of {@link FieldMessage} objects representing validation errors or warnings,
	 *         or an empty list if validation passes
	 */
	List<FieldMessage> validate(VALUE value);

	/**
	 * Creates a field validator from a predicate and an error message.
	 * <p>
	 * The validator will return the specified {@link FieldMessageSeverity#ERROR} message
	 * when the predicate returns false.
	 * </p>
	 *
	 * @param <VALUE> the type of value to be validated
	 * @param validationPredicate the predicate that determines if the value is valid
	 * @param errorMessage the error message to display when validation fails
	 * @return a new {@link FieldValidator} instance
	 */
	static <VALUE> FieldValidator<VALUE> fromPredicate(Predicate<VALUE> validationPredicate, String errorMessage) {
		return value -> validationPredicate.test(value) ? Collections.emptyList() : Collections.singletonList(new FieldMessage(FieldMessageSeverity.ERROR, errorMessage));
	}

	/**
	 * Creates a field validator from a function that returns an error message or null.
	 * <p>
	 * The validator will return an error message when the function returns a non-null string.
	 * </p>
	 *
	 * @param <VALUE> the type of value to be validated
	 * @param errorMessageOrNullFunction a function that returns an error message if validation fails, or null if validation passes
	 * @return a new {@link FieldValidator} instance
	 */
	static <VALUE> FieldValidator<VALUE> fromErrorMessageFunction(Function<VALUE, String> errorMessageOrNullFunction) {
		return value -> {
			String errorMessage = errorMessageOrNullFunction.apply(value);
			return errorMessage == null ? Collections.emptyList() : Collections.singletonList(new FieldMessage(FieldMessageSeverity.ERROR, errorMessage));
		};
	}

}
