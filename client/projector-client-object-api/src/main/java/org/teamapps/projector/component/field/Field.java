package org.teamapps.projector.component.field;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.component.field.validator.FieldValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Main interface for input components that allows users to enter, edit, and validate data.
 * It provides methods for handling field values, validation, and displaying messages to the user.
 * <p>
 * Fields can be in different editing modes (editable, read-only, disabled) and can display validation
 * or information messages with different severities, positions, and visibility settings.
 *
 * @param <VALUE> the type of value this field handles
 */
public interface Field<VALUE> extends Component {

	/**
	 * Returns an event that is triggered when the field receives focus.
	 *
	 * @return the focus event
	 */
	ProjectorEvent<VALUE> onFocus();

	/**
	 * Returns an event that is triggered when the field loses focus.
	 *
	 * @return the blur event
	 */
	ProjectorEvent<VALUE> onBlur();

	/**
	 * Returns an event that is triggered when the field's value changes.
	 *
	 * @return the value changed event
	 */
	ProjectorEvent<VALUE> onValueChanged();

	/**
	 * Returns the current editing mode of the field.
	 * 
	 * @return the current editing mode (EDITABLE, EDITABLE_IF_FOCUSED, DISABLED, or READONLY)
	 */
	FieldEditingMode getEditingMode();

	/**
	 * Sets the editing mode of the field.
	 * 
	 * @param editingMode the editing mode to set (EDITABLE, EDITABLE_IF_FOCUSED, DISABLED, or READONLY)
	 */
	void setEditingMode(FieldEditingMode editingMode);

	/**
	 * Sets the focus to this field.
	 */
	void focus();

	/**
	 * Returns the current value of the field.
	 *
	 * @return the current value
	 */
	VALUE getValue();

	/**
	 * Sets the value of the field.
	 *
	 * @param value the value to set
	 */
	void setValue(VALUE value);

	/**
	 * Converts a server-side value to a client-side value.
	 * This is used when sending the value to the client.
	 *
	 * @param value the server-side value to convert
	 * @return the client-side value
	 */
	Object convertServerValueToClientValue(VALUE value);

	/**
	 * Converts a client-side value to a server-side value.
	 * This is used when receiving a value from the client.
	 *
	 * @param value the client-side value to convert
	 * @return the server-side value
	 */
	VALUE convertClientValueToServerValue(JsonNode value);

	/**
	 * Returns all validators associated with this field.
	 *
	 * @return a collection of validators
	 */
	Collection<FieldValidator<VALUE>> getValidators();

	/**
	 * Adds a validator to this field.
	 *
	 * @param validator the validator to add
	 */
	void addValidator(FieldValidator<VALUE> validator);

	/**
	 * Removes a validator from this field.
	 *
	 * @param validator the validator to remove
	 */
	void removeValidator(FieldValidator<VALUE> validator);

	/**
	 * Clears all validator messages.
	 */
	void clearValidationMessages();

	/**
	 * Validates the current value of the field using all associated validators.
	 *
	 * @return a list of field messages resulting from the validation
	 */
	List<FieldMessage> validate();

	/**
	 * Returns all field messages, including both validator messages and custom messages.
	 *
	 * @return a list of all field messages
	 */
	List<FieldMessage> getFieldMessages();

	/**
	 * Returns only the custom field messages (not including validator messages).
	 *
	 * @return a list of custom field messages
	 */
	List<FieldMessage> getCustomFieldMessages();

	/**
	 * Sets the custom field messages, replacing any existing custom messages.
	 *
	 * @param fieldMessages the list of custom field messages to set
	 */
	void setCustomFieldMessages(List<FieldMessage> fieldMessages);


	/**
	 * Returns the default position for displaying field messages.
	 *
	 * @return the default message position (ABOVE, BELOW, or POPOVER)
	 */
	FieldMessagePosition getDefaultMessagePosition();

	/**
	 * Sets the default position for displaying field messages.
	 *
	 * @param defaultMessagePosition the default message position to set (ABOVE, BELOW, or POPOVER)
	 */
	void setDefaultMessagePosition(FieldMessagePosition defaultMessagePosition);

	/**
	 * Returns the default visibility mode for field messages.
	 *
	 * @return the default message visibility (ON_FOCUS, ON_HOVER_OR_FOCUS, or ALWAYS_VISIBLE)
	 */
	FieldMessageVisibility getDefaultMessageVisibility();

	/**
	 * Sets the default visibility mode for field messages.
	 *
	 * @param defaultMessageVisibility the default message visibility to set (ON_FOCUS, ON_HOVER_OR_FOCUS, or ALWAYS_VISIBLE)
	 */
	void setDefaultMessageVisibility(FieldMessageVisibility defaultMessageVisibility);

	/**
	 * Checks if the field's value has been changed by the client.
	 *
	 * @return true if the value was changed by the client, false otherwise
	 */
	boolean isValueChangedByClient();

	/**
	 * Resets the flag that indicates whether the field's value has been changed by the client.
	 */
	void resetValueChangedByClient();


	/**
	 * Adds a custom field message to the existing custom messages.
	 *
	 * @param fieldMessage the field message to add
	 */
	default void addCustomFieldMessage(FieldMessage fieldMessage) {
		ArrayList<FieldMessage> messages = new ArrayList<>(getCustomFieldMessages());
		messages.add(fieldMessage);
		setCustomFieldMessages(messages);
	}

	/**
	 * Removes a custom field message from the existing custom messages.
	 *
	 * @param fieldMessage the field message to remove
	 */
	default void removeCustomFieldMessage(FieldMessage fieldMessage) {
		ArrayList<FieldMessage> messages = new ArrayList<>(getCustomFieldMessages());
		messages.remove(fieldMessage);
		setCustomFieldMessages(messages);
	}

	/**
	 * Returns the highest severity level from all field messages.
	 *
	 * @return the highest severity level, or null if there are no messages
	 */
	default FieldMessageSeverity getMaxFieldMessageSeverity() {
		return getFieldMessages().stream()
				.map(fieldMessage -> fieldMessage.getSeverity())
				.max(Comparator.comparing(severity -> severity.ordinal()))
				.orElse(null);
	}

	/**
	 * Checks if the field has any error messages.
	 *
	 * @return true if there are error messages, false otherwise
	 */
	default boolean hasErrorMessages() {
		return getMaxFieldMessageSeverity() == FieldMessageSeverity.ERROR;
	}
}
