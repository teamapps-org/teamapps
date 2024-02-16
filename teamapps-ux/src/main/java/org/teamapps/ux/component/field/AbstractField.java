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
package org.teamapps.ux.component.field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.DtoField;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.field.validator.FieldValidator;
import org.teamapps.ux.i18n.TeamAppsDictionary;
import org.teamapps.ux.session.CurrentSessionContext;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractField<VALUE> extends AbstractComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final FieldValidator<VALUE> requiredValidator = (value) ->
			this.isEmptyValue(value) ? Collections.singletonList(new FieldMessage(FieldMessage.Severity.ERROR,
					CurrentSessionContext.get().getLocalized(TeamAppsDictionary.REQUIRED_FIELD.getKey()))) : List.of();

	private final FieldValidator<VALUE> requiredIfVisibleAndEditableValidator = (value) ->
			(this.isVisible() && (this.getEditingMode() == FieldEditingMode.EDITABLE || this.getEditingMode() == FieldEditingMode.EDITABLE_IF_FOCUSED) && this.isEmptyValue(value)) ?
					Collections.singletonList(new FieldMessage(FieldMessage.Severity.ERROR,
							CurrentSessionContext.get().getLocalized(TeamAppsDictionary.REQUIRED_FIELD.getKey()))) : List.of();

	public final ProjectorEvent<VALUE> onFocus = createProjectorEventBoundToUiEvent(DtoField.FocusEvent.TYPE_ID);
	public final ProjectorEvent<VALUE> onBlur = createProjectorEventBoundToUiEvent(DtoField.BlurEvent.TYPE_ID);
	public final ProjectorEvent<VALUE> onValueChanged = createProjectorEventBoundToUiEvent(DtoField.ValueChangedEvent.TYPE_ID);
	public final ProjectorEvent<Boolean> onVisibilityChanged = new ProjectorEvent<>();

	private FieldEditingMode editingMode = FieldEditingMode.EDITABLE;

	private final Set<FieldValidator<VALUE>> validators = new HashSet<>();
	private final Map<FieldValidator<VALUE>, List<FieldMessage>> fieldMessagesByValidator = new HashMap<>(); // null key for custom field messages (not bound to a validator)
	private FieldMessage.Position defaultMessagePosition = FieldMessage.Position.BELOW;
	private FieldMessage.Visibility defaultMessageVisibility = FieldMessage.Visibility.ALWAYS_VISIBLE;

	private final MultiWriteLockableValue<VALUE> value = new MultiWriteLockableValue<>(null);

	private boolean valueChangedByClient;

	public FieldEditingMode getEditingMode() {
		return editingMode;
	}

	public void setEditingMode(FieldEditingMode editingMode) {
		this.editingMode = editingMode;
		sendCommandIfRendered(() -> new DtoField.SetEditingModeCommand(editingMode.toUiFieldEditingMode()));
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		onVisibilityChanged.fire(visible);
	}

	public void focus() {
		sendCommandIfRendered(() -> new DtoField.FocusCommand());
	}

	protected void mapAbstractFieldAttributesToUiField(DtoField uiField) {
		mapAbstractUiComponentProperties(uiField);
		uiField.setValue(convertUxValueToUiValue(this.value.read()));
		uiField.setEditingMode(editingMode.toUiFieldEditingMode());
		uiField.setFieldMessages(getFieldMessages().stream()
				.map(message -> message.createUiFieldMessage(defaultMessagePosition, defaultMessageVisibility))
				.collect(Collectors.toList()));
	}

	public void setValue(VALUE value) {
		valueChangedByClient = false;
		MultiWriteLockableValue.Lock lock = this.value.writeAndLock(value);
		Object uiValue = this.convertUxValueToUiValue(value);
		if (isRendered()) {
			final DtoField.SetValueCommand setValueCommand = new DtoField.SetValueCommand(uiValue);
			getSessionContext().sendCommandIfRendered(this, () -> setValueCommand, aVoid -> lock.release());
		} else {
			lock.release();
		}
	}

	protected MultiWriteLockableValue.Lock setAndLockValue(VALUE value) {
		return this.value.writeAndLock(value);
	}

	/**
	 * Converts a server-side value to a client-side field-specific value.
	 * Implementations must not have any side effects to the component!
	 *
	 * @param value the server-side value
	 * @return the object to be sent to the ui
	 */
	public Object convertUxValueToUiValue(VALUE value) {
		return value;
	}

	public VALUE getValue() {
		return value.read();
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoField.ValueChangedEvent.TYPE_ID -> {
		 var e = event.as(DtoField.ValueChangedEventWrapper.class);
				applyValueFromUi(e.getValue());
				validate();
			}
			case DtoField.FocusEvent.TYPE_ID -> onFocus.fire();
			case DtoField.BlurEvent.TYPE_ID -> onBlur.fire();
		}
	}

	protected void applyValueFromUi(Object value) {
		if (!this.value.isLocked()) {
			VALUE transformedValue = convertUiValueToUxValue(value);
			if (!this.value.isLocked()) {
				this.value.writeIfNotLocked(transformedValue);
				valueChangedByClient = true;
				onValueChanged.fire(transformedValue);
			}
		}
	}

	public VALUE convertUiValueToUxValue(Object value) {
		return (VALUE) value;
	}

	/**
	 * Whether this value be regarded as empty / "no user input".
	 * Override for field-specific behaviour.
	 *
	 * @return true if the value can be regarded as "empty".
	 */
	protected boolean isEmptyValue(VALUE value) {
		return value == null;
	}

	public boolean isEmpty() {
		return isEmptyValue(getValue());
	}

	public Collection<FieldValidator<VALUE>> getValidators() {
		return Collections.unmodifiableSet(validators);
	}

	public void addValidator(FieldValidator<VALUE> validator) {
		validators.add(validator);
	}

	public void removeValidator(FieldValidator<VALUE> validator) {
		validators.remove(validator);
		fieldMessagesByValidator.remove(validator);
		updateFieldMessages();
	}

	public List<FieldMessage> validate() {
		List<FieldMessage> allValidatorMessages = new ArrayList<>();
		if (validators.size() > 0) {
			for (FieldValidator<VALUE> validator : validators) {
				fieldMessagesByValidator.remove(validator);
				List<FieldMessage> messages = validator.validate(getValue());
				if (messages == null) {
					messages = Collections.emptyList();
				}
				fieldMessagesByValidator.put(validator, messages);
				allValidatorMessages.addAll(messages);
			}
			updateFieldMessages();
		}
		return allValidatorMessages;
	}

	public void clearValidatorMessages() {
		fieldMessagesByValidator.clear();
		updateFieldMessages();
	}

	/**
	 * field may not be null (empty)
	 */
	public void setRequired(boolean required) {
		if (required) {
			addValidator(requiredValidator);
		} else {
			removeValidator(requiredValidator);
		}
	}

	public void setRequiredIfVisibleAndEditable(boolean required) {
		if (required) {
			addValidator(requiredIfVisibleAndEditableValidator);
		} else {
			removeValidator(requiredIfVisibleAndEditableValidator);
		}
	}

	public boolean isRequired() {
		return validators.contains(requiredValidator);
	}

	public List<FieldMessage> getFieldMessages() {
		return fieldMessagesByValidator.values().stream()
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	public List<FieldMessage> getCustomFieldMessages() {
		return fieldMessagesByValidator.computeIfAbsent(null, v -> new ArrayList<>());
	}

	public void setCustomFieldMessages(List<FieldMessage> fieldMessages) {
		fieldMessagesByValidator.put(null, new ArrayList<>(fieldMessages));
		updateFieldMessages();
	}

	public void addCustomFieldMessage(FieldMessage.Severity severity, String text) {
		addCustomFieldMessage(new FieldMessage(severity, text));
	}

	public void addCustomFieldMessage(FieldMessage fieldMessage) {
		getCustomFieldMessages().add(fieldMessage);
		updateFieldMessages();
	}

	public void removeCustomFieldMessage(FieldMessage fieldMessage) {
		getCustomFieldMessages().remove(fieldMessage);
		updateFieldMessages();
	}

	public void clearCustomFieldMessages() {
		getCustomFieldMessages().clear();
		updateFieldMessages();
	}

	private void updateFieldMessages() {
		sendCommandIfRendered(() -> new DtoField.SetFieldMessagesCommand(getFieldMessages().stream()
				.map(fieldMessage -> fieldMessage.createUiFieldMessage(defaultMessagePosition, defaultMessageVisibility))
				.collect(Collectors.toList())));
	}

	public boolean isValid() {
		return getMaxFieldMessageSeverity() != FieldMessage.Severity.ERROR;
	}

	public FieldMessage.Severity getMaxFieldMessageSeverity() {
		return getFieldMessages().stream()
				.map(fieldMessage -> fieldMessage.getSeverity())
				.max(Comparator.comparing(severity -> severity.ordinal()))
				.orElse(null);
	}

	public FieldMessage.Position getDefaultMessagePosition() {
		return defaultMessagePosition;
	}

	public void setDefaultMessagePosition(FieldMessage.Position defaultMessagePosition) {
		this.defaultMessagePosition = defaultMessagePosition;
	}

	public FieldMessage.Visibility getDefaultMessageVisibility() {
		return defaultMessageVisibility;
	}

	public void setDefaultMessageVisibility(FieldMessage.Visibility defaultMessageVisibility) {
		this.defaultMessageVisibility = defaultMessageVisibility;
	}

	public boolean isValueChangedByClient() {
		return valueChangedByClient;
	}

	public void setValueChangedByClient(boolean valueChangedByClient) {
		this.valueChangedByClient = valueChangedByClient;
	}
}
