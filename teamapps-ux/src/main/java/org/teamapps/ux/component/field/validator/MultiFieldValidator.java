package org.teamapps.ux.component.field.validator;

import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MultiFieldValidator {

	private final Runnable runValidationHandler = this::validate;
	private final Runnable clearMessagesHandler = this::clearMessages;

	private final CustomValidator validation;
	private final List<AbstractField<?>> fields;

	private List<FieldMessage> currentFieldMessages = List.of();

	public enum TriggeringPolicy {
		MANUALLY,
		MANUALLY_WITH_AUTOCLEAR,
		ON_FIELD_CHANGE
	}

	private final TriggeringPolicy triggeringPolicy;

	public MultiFieldValidator(CustomValidator validation, AbstractField<?>... fields) {
		this(validation, TriggeringPolicy.MANUALLY, Arrays.asList(fields));
	}

	public MultiFieldValidator(CustomValidator validation, TriggeringPolicy triggeringPolicy, AbstractField<?>... fields) {
		this(validation, triggeringPolicy, Arrays.asList(fields));
	}

	public MultiFieldValidator(CustomValidator validation, TriggeringPolicy triggeringPolicy, List<AbstractField<?>> fields) {
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
				fields.forEach(f -> f.onValueChanged.addListener(clearMessagesHandler));
				break;
			}
			case ON_FIELD_CHANGE : {
				fields.forEach(f -> f.onValueChanged.addListener(runValidationHandler));
				break;
			}
		}
	}

	public TriggeringPolicy getTriggeringPolicy() {
		return triggeringPolicy;
	}

	public void clearMessages() {
		fields.forEach(AbstractField::clearCustomFieldMessages);
	}
}
