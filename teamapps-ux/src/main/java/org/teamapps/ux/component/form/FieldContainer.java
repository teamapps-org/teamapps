package org.teamapps.ux.component.form;

import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.field.AbstractField;

import java.util.Map;

/**
 * Can be added to a form. Contains fields that will be treated by the form as it teats its on fields.
 * In particular
 * <ul>
 *     <li>{@link AbstractForm#validate()} will validate the {@link FieldContainer}'s fields</li>
 *     <li>{@link AbstractForm#applyRecordValuesToFields(Object)} and {@link AbstractForm#applyFieldValuesToRecord(Object)} will respect the {@link FieldContainer}'s fields</li>
 *     <li>{@link AbstractForm#getFields()} and {@link AbstractForm#getFieldsMap()} will contain the {@link FieldContainer}'s fields</li>
 * </ul>
 */
public interface FieldContainer {

	Component getMainComponent();

	/**
	 * Must not change between invocations!
	 */
	Map<String, AbstractField<?>> getFields();

}
