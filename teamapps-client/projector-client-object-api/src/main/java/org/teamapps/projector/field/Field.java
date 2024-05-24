package org.teamapps.projector.field;

import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.field.validator.FieldValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface Field<VALUE> extends Component {

	ProjectorEvent<VALUE> onFocus();

	ProjectorEvent<VALUE> onBlur();

	ProjectorEvent<VALUE> onValueChanged();

	FieldEditingMode getEditingMode();

	void setEditingMode(FieldEditingMode editingMode);

	void focus();

	VALUE getValue();

	void setValue(VALUE value);

	Object convertServerValueToClientValue(VALUE value);

	VALUE convertClientValueToServerValue(Object value);

	Collection<FieldValidator<VALUE>> getValidators();

	void addValidator(FieldValidator<VALUE> validator);

	void removeValidator(FieldValidator<VALUE> validator);

	void clearValidatorMessages();

	List<FieldMessage> validate();

	List<FieldMessage> getFieldMessages();

	List<FieldMessage> getCustomFieldMessages();

	void setCustomFieldMessages(List<FieldMessage> fieldMessages);


	FieldMessage.Position getDefaultMessagePosition();

	void setDefaultMessagePosition(FieldMessage.Position defaultMessagePosition);

	FieldMessage.Visibility getDefaultMessageVisibility();

	void setDefaultMessageVisibility(FieldMessage.Visibility defaultMessageVisibility);

	boolean isValueChangedByClient();

	void resetValueChangedByClient();


	default void addCustomFieldMessage(FieldMessage fieldMessage) {
		ArrayList<FieldMessage> messages = new ArrayList<>(getCustomFieldMessages());
		messages.add(fieldMessage);
		setCustomFieldMessages(messages);
	}

	default void removeCustomFieldMessage(FieldMessage fieldMessage) {
		ArrayList<FieldMessage> messages = new ArrayList<>(getCustomFieldMessages());
		messages.remove(fieldMessage);
		setCustomFieldMessages(messages);
	}

	default FieldMessage.Severity getMaxFieldMessageSeverity() {
		return getFieldMessages().stream()
				.map(fieldMessage -> fieldMessage.getSeverity())
				.max(Comparator.comparing(severity -> severity.ordinal()))
				.orElse(null);
	}

	default boolean hasErrorMessages() {
		return getMaxFieldMessageSeverity() == FieldMessage.Severity.ERROR;
	}
}
