package org.teamapps.projector.component.field;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.component.field.validator.FieldValidator;

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

	VALUE convertClientValueToServerValue(JsonNode value);

	Collection<FieldValidator<VALUE>> getValidators();

	void addValidator(FieldValidator<VALUE> validator);

	void removeValidator(FieldValidator<VALUE> validator);

	void clearValidatorMessages();

	List<FieldMessage> validate();

	List<FieldMessage> getFieldMessages();

	List<FieldMessage> getCustomFieldMessages();

	void setCustomFieldMessages(List<FieldMessage> fieldMessages);


	FieldMessagePosition getDefaultMessagePosition();

	void setDefaultMessagePosition(FieldMessagePosition defaultMessagePosition);

	FieldMessageVisibility getDefaultMessageVisibility();

	void setDefaultMessageVisibility(FieldMessageVisibility defaultMessageVisibility);

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

	default FieldMessageSeverity getMaxFieldMessageSeverity() {
		return getFieldMessages().stream()
				.map(fieldMessage -> fieldMessage.getSeverity())
				.max(Comparator.comparing(severity -> severity.ordinal()))
				.orElse(null);
	}

	default boolean hasErrorMessages() {
		return getMaxFieldMessageSeverity() == FieldMessageSeverity.ERROR;
	}
}
