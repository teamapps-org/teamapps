package org.teamapps.ux.component.field.validator;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class FieldValidatorTest {

	@Test
	public void fromPredicate() {
		FieldValidator<String> validator = FieldValidator.fromPredicate(s -> s.length() >= 3, "Length has to be at least 3");

		Assertions.assertThat(validator.validate("a")).extracting(m -> m.getMessage()).containsExactly("Length has to be at least 3");
		Assertions.assertThat(validator.validate("abc")).isEmpty();
	}

	@Test
	public void fromErrorMessageFunction() {
		FieldValidator<String> validator = FieldValidator.fromErrorMessageFunction(s -> s.length() < 3 ? "Length has to be at least 3" : null);

		Assertions.assertThat(validator.validate("a")).extracting(m -> m.getMessage()).containsExactly("Length has to be at least 3");
		Assertions.assertThat(validator.validate("abc")).isEmpty();
	}
}