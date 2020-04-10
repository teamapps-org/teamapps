package org.teamapps.ux.component.field;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Fields {

	private Fields() {
		// private
	}

	public static boolean validateAll(AbstractField<?>... fields) {
		List<FieldMessage> errorMessage = Arrays.stream(fields)
				.flatMap(field -> field.validate().stream())
				.filter(message -> message.getSeverity() == FieldMessage.Severity.ERROR)
				.collect(Collectors.toList()); // to List because we want ALL validators to run!
		return errorMessage.size() == 0;
	}

	public static boolean validateAll(List<AbstractField<?>> fields) {
		List<FieldMessage> errorMessage = fields.stream()
				.flatMap(field -> field.validate().stream())
				.filter(message -> message.getSeverity() == FieldMessage.Severity.ERROR)
				.collect(Collectors.toList()); // to List because we want ALL validators to run!
		return errorMessage.size() == 0;
	}

}
