/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// TODO #componentGenerics
public abstract class AbstractField<VALUE> extends AbstractComponent {

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractField.class);

	public final Event<VALUE> onValueChanged = new Event<>();
	public final Event<Boolean> onVisibilityChanged = new Event<>();

	private FieldEditingMode editingMode = FieldEditingMode.EDITABLE;
	private boolean visible = true;

	private final List<FieldMessage> fieldMessages = new ArrayList<>(0);

	private MultiWriteLockableValue<VALUE> value = new MultiWriteLockableValue<>(null);

	public AbstractField() {
		getSessionContext().onDestroyed().addListener(aVoid -> this.destroy());
	}

	public FieldEditingMode getEditingMode() {
		return editingMode;
	}

	public void setEditingMode(FieldEditingMode editingMode) {
		this.editingMode = editingMode;
		queueCommandIfRendered(() -> new UiField.SetEditingModeCommand(getId(), editingMode.toUiFieldEditingMode()));
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		queueCommandIfRendered(() -> new UiField.SetVisibleCommand(getId(), visible));
		onVisibilityChanged.fire(visible);
	}

	public void focus() {
		queueCommandIfRendered(() -> new UiField.FocusCommand(getId()));
	}

	protected void mapAbstractFieldAttributesToUiField(UiField uiField) {
		mapAbstractUiComponentProperties(uiField);
		uiField.setValue(convertUxValueToUiValue(this.value.read()));
		uiField.setEditingMode(editingMode.toUiFieldEditingMode());
		uiField.setVisible(this.visible);
		uiField.setFieldMessages(fieldMessages.stream()
				.map(message -> message.createUiFieldMessage())
				.collect(Collectors.toList()));
	}

	public void setValue(VALUE value) {
		MultiWriteLockableValue.Lock lock = this.value.writeAndLock(value);
		Object uiValue = this.convertUxValueToUiValue(value);
		if (isRendered()) {
			getSessionContext().queueCommand(new UiField.SetValueCommand(getId(), uiValue), aVoid -> lock.release());
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
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_FIELD_VALUE_CHANGED:
				applyValueFromUi(((UiField.ValueChangedEvent) event).getValue());
				break;
		}
	}

	protected void applyValueFromUi(Object value) {
		if (!this.value.isLocked()) {
			VALUE transformedValue = convertUiValueToUxValue(value);
			if (!this.value.isLocked()) {
				this.value.writeIfNotLocked(transformedValue);
				onValueChanged.fire(transformedValue);
			}
		}
	}

	public VALUE convertUiValueToUxValue(Object value) {
		return (VALUE) value;
	}

	protected abstract void doDestroy();

	public List<FieldMessage> getFieldMessages() {
		return fieldMessages;
	}

	public void setFieldMessages(List<FieldMessage> fieldMessages) {
		this.fieldMessages.clear();
		this.fieldMessages.addAll(fieldMessages != null ? fieldMessages : Collections.emptyList());
		updateFieldMessages();
	}

	public void setValidationMessage(FieldMessage fieldMessage) {
		fieldMessages.removeIf(message -> message.getSeverity() != FieldMessage.Severity.INFO);
		fieldMessages.add(fieldMessage);
		updateFieldMessages();
	}

	public void removeValidationMessages() {
		if (fieldMessages.removeIf(message -> message.getSeverity() != FieldMessage.Severity.INFO)) {
			updateFieldMessages();
		}
	}

	public void addFieldMessage(FieldMessage fieldMessage) {
		fieldMessages.add(fieldMessage);
		updateFieldMessages();
	}

	public void removeFieldMessage(FieldMessage fieldMessage) {
		fieldMessages.remove(fieldMessage);
		updateFieldMessages();
	}

	public void removeFieldMessages(List<FieldMessage> fieldMessages) {
		fieldMessages.removeAll(fieldMessages);
		updateFieldMessages();
	}

	private void updateFieldMessages() {
		queueCommandIfRendered(() -> new UiField.SetFieldMessagesCommand(getId(), fieldMessages.stream()
				.map(message -> message.createUiFieldMessage())
				.collect(Collectors.toList())));
	}
}
