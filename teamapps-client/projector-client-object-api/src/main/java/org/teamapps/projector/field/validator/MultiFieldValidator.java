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
package org.teamapps.projector.field.validator;

import org.teamapps.projector.field.Field;
import org.teamapps.projector.field.FieldMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiFieldValidator {

	private final Runnable runValidationHandler = this::validate;
	private final Runnable clearMessagesHandler = this::clearMessages;

	private final CustomValidator validation;
	private final List<Field<?>> fields;

	private List<FieldMessage> currentFieldMessages = List.of();

	public enum TriggeringPolicy {
		MANUALLY,
		MANUALLY_WITH_AUTOCLEAR,
		ON_FIELD_CHANGE
	}

	private final TriggeringPolicy triggeringPolicy;

	public MultiFieldValidator(CustomValidator validation, Field<?>... fields) {
		this(validation, TriggeringPolicy.MANUALLY, Arrays.asList(fields));
	}

	public MultiFieldValidator(CustomValidator validation, TriggeringPolicy triggeringPolicy, Field<?>... fields) {
		this(validation, triggeringPolicy, Arrays.asList(fields));
	}

	public MultiFieldValidator(CustomValidator validation, TriggeringPolicy triggeringPolicy, List<Field<?>> fields) {
		this.validation = validation;
		this.fields = new ArrayList<>(fields);
		this.triggeringPolicy = triggeringPolicy;
		setupTriggeringPolicy();
	}

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

	public void clearMessages() {
		fields.forEach(f -> {
			currentFieldMessages.forEach(f::removeCustomFieldMessage);
		});
	}
}
