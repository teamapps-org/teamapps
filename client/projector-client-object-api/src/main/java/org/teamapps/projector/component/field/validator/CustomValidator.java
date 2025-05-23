/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.projector.component.field.validator;

import org.teamapps.projector.component.field.FieldMessage;
import org.teamapps.projector.component.field.FieldMessageSeverity;

import java.util.List;

/**
 * An interface for implementing custom validation logic that is not (or not only) depending on the value
 * of a single field. See for example {@link MultiFieldValidator}.
 */
public interface CustomValidator {

	/**
	 * Performs the validation and returns a list of field messages.
	 * <p>
	 * Returning an empty list without {@link FieldMessageSeverity#ERROR} messages is regarded as
	 * a successful validation.
	 * Therefore, an empty list also represents a successful validation.
	 *
	 * @return a list of {@link FieldMessage} objects representing validation errors or warnings,
	 *         or an empty list
	 */
	List<FieldMessage> validate();

}
