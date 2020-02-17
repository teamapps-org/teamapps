package org.teamapps.ux.component.field.validator;

import org.teamapps.ux.component.field.FieldMessage;

import java.util.List;

public interface FieldValidator<VALUE> {

	List<FieldMessage> validate(VALUE value);

}
