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
package org.teamapps.projector.field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.dto.*;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.field.validator.FieldValidator;
import org.teamapps.projector.i18n.TeamAppsTranslationKeys;
import org.teamapps.projector.session.CurrentSessionContext;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractField<VALUE> extends AbstractComponent implements Field<VALUE>, DtoAbstractFieldEventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final FieldValidator<VALUE> requiredValidator = (value) ->
			this.isEmptyValue(value) ? Collections.singletonList(new FieldMessage(FieldMessage.Severity.ERROR,
					CurrentSessionContext.get().getLocalized(TeamAppsTranslationKeys.REQUIRED_FIELD.getKey()))) : List.of();

	private final FieldValidator<VALUE> requiredIfVisibleAndEditableValidator = (value) ->
			(this.isVisible() && (this.getEditingMode() == FieldEditingMode.EDITABLE || this.getEditingMode() == FieldEditingMode.EDITABLE_IF_FOCUSED) && this.isEmptyValue(value)) ?
					Collections.singletonList(new FieldMessage(FieldMessage.Severity.ERROR,
							CurrentSessionContext.get().getLocalized(TeamAppsTranslationKeys.REQUIRED_FIELD.getKey()))) : List.of();

	public final ProjectorEvent<VALUE> onFocus = createProjectorEventBoundToUiEvent(DtoAbstractField.FocusEvent.TYPE_ID);
	public final ProjectorEvent<VALUE> onBlur = createProjectorEventBoundToUiEvent(DtoAbstractField.BlurEvent.TYPE_ID);
	public final ProjectorEvent<VALUE> onValueChanged = createProjectorEventBoundToUiEvent(DtoAbstractField.ValueChangedEvent.TYPE_ID);
	public final ProjectorEvent<Boolean> onVisibilityChanged = new ProjectorEvent<>();

	private final DtoAbstractFieldClientObjectChannel clientObjectChannel = new DtoAbstractFieldClientObjectChannel(getClientObjectChannel());

	private FieldEditingMode editingMode = FieldEditingMode.EDITABLE;

	private final Set<FieldValidator<VALUE>> validators = new HashSet<>();
	private final Map<FieldValidator<VALUE>, List<FieldMessage>> fieldMessagesByValidator = new HashMap<>(); // null key for custom field messages (not bound to a validator)
	private FieldMessage.Position defaultMessagePosition = FieldMessage.Position.BELOW;
	private FieldMessage.Visibility defaultMessageVisibility = FieldMessage.Visibility.ALWAYS_VISIBLE;

	private final MultiWriteLockableValue<VALUE> value = new MultiWriteLockableValue<>(null);

	private boolean valueChangedByClient;

	@Override
	public ProjectorEvent<VALUE> onFocus() {
		return onFocus;
	}

	@Override
	public ProjectorEvent<VALUE> onBlur() {
		return onBlur;
	}

	@Override
	public ProjectorEvent<VALUE> onValueChanged() {
		return onValueChanged;
	}

	@Override
	public FieldEditingMode getEditingMode() {
		return editingMode;
	}

	@Override
	public void setEditingMode(FieldEditingMode editingMode) {
		this.editingMode = editingMode;
		clientObjectChannel.setEditingMode(editingMode.toDtoFieldEditingMode());
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		onVisibilityChanged.fire(visible);
	}

	@Override
	public void focus() {
		clientObjectChannel.focus();
	}

	protected void mapAbstractFieldAttributesToUiField(DtoAbstractField uiField) {
		mapAbstractUiComponentProperties(uiField);
		uiField.setValue(convertServerValueToClientValue(this.value.read()));
		uiField.setEditingMode(editingMode.toDtoFieldEditingMode());
		uiField.setFieldMessages(getFieldMessages().stream()
				.map(message -> message.createUiFieldMessage(defaultMessagePosition, defaultMessageVisibility))
				.collect(Collectors.toList()));
	}

	@Override
	public void setValue(VALUE value) {
		valueChangedByClient = false;
		MultiWriteLockableValue.Lock lock = this.value.writeAndLock(value);
		Object uiValue = this.convertServerValueToClientValue(value);

		boolean wasActuallySent = clientObjectChannel.setValue(uiValue, unused -> lock.release());

		if (!wasActuallySent) {
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
	public Object convertServerValueToClientValue(VALUE value) {
		return value;
	}

	@Override
	public VALUE getValue() {
		return value.read();
	}

	@Override
	public void handleValueChanged(DtoAbstractField.ValueChangedEventWrapper eventObject) {
		applyValueFromUi(eventObject.getValue());
		validate();
	}

	@Override
	public void handleFocus(DtoAbstractField.FocusEventWrapper eventObject) {
		onFocus.fire();
	}

	@Override
	public void handleBlur(DtoAbstractField.BlurEventWrapper eventObject) {
		onBlur.fire();
	}

	protected void applyValueFromUi(JsonWrapper value) {
		if (!this.value.isLocked()) {
			VALUE transformedValue = convertClientValueToServerValue(value);
			if (!this.value.isLocked()) {
				this.value.writeIfNotLocked(transformedValue);
				valueChangedByClient = true;
				onValueChanged.fire(transformedValue);
			}
		}
	}

	public VALUE convertClientValueToServerValue(JsonWrapper value) {
		if (value == null || value.getJsonNode().isNull()) {
			return null;
		}
		return doConvertClientValueToServerValue(value);
	}

	abstract public VALUE doConvertClientValueToServerValue(JsonWrapper value);

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

	@Override
	public Collection<FieldValidator<VALUE>> getValidators() {
		return Collections.unmodifiableSet(validators);
	}

	@Override
	public void addValidator(FieldValidator<VALUE> validator) {
		validators.add(validator);
	}

	@Override
	public void removeValidator(FieldValidator<VALUE> validator) {
		validators.remove(validator);
		fieldMessagesByValidator.remove(validator);
		updateFieldMessages();
	}

	@Override
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

	@Override
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

	@Override
	public List<FieldMessage> getFieldMessages() {
		return fieldMessagesByValidator.values().stream()
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public List<FieldMessage> getCustomFieldMessages() {
		return fieldMessagesByValidator.computeIfAbsent(null, v -> new ArrayList<>());
	}

	@Override
	public void setCustomFieldMessages(List<FieldMessage> fieldMessages) {
		fieldMessagesByValidator.put(null, new ArrayList<>(fieldMessages));
		updateFieldMessages();
	}

	private void updateFieldMessages() {
		List<DtoFieldMessage> uiFieldMessages = getFieldMessages().stream()
				.map(fieldMessage -> fieldMessage.createUiFieldMessage(defaultMessagePosition, defaultMessageVisibility))
				.collect(Collectors.toList());
		clientObjectChannel.setFieldMessages(uiFieldMessages);
	}

	@Override
	public FieldMessage.Position getDefaultMessagePosition() {
		return defaultMessagePosition;
	}

	@Override
	public void setDefaultMessagePosition(FieldMessage.Position defaultMessagePosition) {
		this.defaultMessagePosition = defaultMessagePosition;
	}

	@Override
	public FieldMessage.Visibility getDefaultMessageVisibility() {
		return defaultMessageVisibility;
	}

	@Override
	public void setDefaultMessageVisibility(FieldMessage.Visibility defaultMessageVisibility) {
		this.defaultMessageVisibility = defaultMessageVisibility;
	}

	@Override
	public boolean isValueChangedByClient() {
		return valueChangedByClient;
	}

	@Override
	public void resetValueChangedByClient() {
		this.valueChangedByClient = false;
	}


}
