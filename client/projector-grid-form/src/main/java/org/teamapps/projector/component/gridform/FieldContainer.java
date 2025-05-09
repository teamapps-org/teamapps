package org.teamapps.projector.component.gridform;

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.field.AbstractField;

import java.util.Map;

/**
 * Can be added to a form. Contains fields that will be treated by the form as it teats its on fields.
 * In particular
 * <ul>
 *     <li>{@link AbstractGridForm#validate()} will validate the {@link FieldContainer}'s fields</li>
 *     <li>{@link AbstractGridForm#applyRecordValuesToFields(Object)} and {@link AbstractGridForm#applyFieldValuesToRecord(Object)} will respect the {@link FieldContainer}'s fields</li>
 *     <li>{@link AbstractGridForm#getFields()} and {@link AbstractGridForm#getFields()} will contain the {@link FieldContainer}'s fields</li>
 * </ul>
 */
public interface FieldContainer {

	Component getMainComponent();

	/**
	 * Must not change between invocations!
	 */
	Map<String, AbstractField<?>> getFields();

}
