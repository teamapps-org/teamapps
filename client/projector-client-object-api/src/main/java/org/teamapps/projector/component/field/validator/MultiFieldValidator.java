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

import org.teamapps.projector.component.field.Field;
import org.teamapps.projector.component.field.FieldMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A validator that can validate multiple fields together using a custom validation logic.
 * <p>
 * This validator is useful for implementing cross-field validation rules, where the validity
 * of one field depends on the values of other fields. It applies validation messages to all
 * the fields that are being validated.
 * </p>
 */
public class MultiFieldValidator {

	private final Runnable runValidationHandler = this::validate;
	private final Runnable clearMessagesHandler = this::clearMessages;

	private final CustomValidator validation;
	private final List<Field<?>> fields;

	private List<FieldMessage> currentFieldMessages = List.of();

	/**
	 * Defines when the validation should be triggered.
	 */
	public enum TriggeringPolicy {
		/**
		 * Validation is only triggered manually by calling {@link #validate()}.
		 */
		MANUALLY,

		/**
		 * Validation is triggered manually, but validation messages are automatically cleared
		 * when any of the fields' values change.
		 */
		MANUALLY_WITH_AUTOCLEAR,

		/**
		 * Validation is automatically triggered whenever any of the fields' values change.
		 */
		ON_FIELD_CHANGE
	}

	private final TriggeringPolicy triggeringPolicy;

	/**
	 * Creates a new MultiFieldValidator with the specified triggering policy.
	 *
	 * @param validation the custom validation logic
	 * @param triggeringPolicy when the validation should be triggered
	 * @param fields the fields to validate
	 */
	public MultiFieldValidator(CustomValidator validation, TriggeringPolicy triggeringPolicy, Field<?>... fields) {
		this(validation, triggeringPolicy, Arrays.asList(fields));
	}

	/**
	 * Creates a new MultiFieldValidator with the specified triggering policy.
	 *
	 * @param validation the custom validation logic
	 * @param triggeringPolicy when the validation should be triggered
	 * @param fields the list of fields to validate
	 */
	public MultiFieldValidator(CustomValidator validation, TriggeringPolicy triggeringPolicy, List<Field<?>> fields) {
		this.validation = validation;
		this.fields = new ArrayList<>(fields);
		this.triggeringPolicy = triggeringPolicy;
		setupTriggeringPolicy();
	}

	/**
	 * Validates the fields using the custom validation logic.
	 * <p>
	 * This method applies the validation messages to all the fields and returns the list of messages.
	 * </p>
	 *
	 * @return the list of validation messages
	 */
	public List<FieldMessage> validate() {
		List<FieldMessage> validationResult = validation.validate();
		List<FieldMessage> newFieldMessages = validationResult != null ? validationResult : List.of();
		fields.forEach(f -> {
			currentFieldMessages.forEach(f::removeCustomFieldMessage);
			newFieldMessages.forEach(f::addCustomFieldMessage);
		});
		currentFieldMessages = newFieldMessages;
		return newFieldMessages;
	}

	private void setupTriggeringPolicy() {
		switch (triggeringPolicy) {
			case MANUALLY: {
				break;
			}
			case MANUALLY_WITH_AUTOCLEAR : {
				fields.forEach(f -> f.onValueChanged().addListener(clearMessagesHandler));
				break;
			}
			case ON_FIELD_CHANGE : {
				fields.forEach(f -> f.onValueChanged().addListener(runValidationHandler));
				break;
			}
		}
	}

	public TriggeringPolicy getTriggeringPolicy() {
		return triggeringPolicy;
	}

	/**
	 * Clears all validation messages from the fields.
	 */
	public void clearMessages() {
		fields.forEach(f -> {
			currentFieldMessages.forEach(f::removeCustomFieldMessage);
		});
	}
}
