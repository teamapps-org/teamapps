/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
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
