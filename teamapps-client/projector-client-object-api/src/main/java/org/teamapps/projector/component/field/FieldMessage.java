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
package org.teamapps.projector.component.field;

import java.util.Objects;

public class FieldMessage {

	private final FieldMessageSeverity severity;
	private final String message;
	private final FieldMessagePosition position;
	private final FieldMessageVisibility visibility;


	public FieldMessage(FieldMessageSeverity severity, String message) {
		this(null, null, severity, message);
	}

	public FieldMessage(FieldMessagePosition position, FieldMessageVisibility visibility, FieldMessageSeverity severity, String message) {
		this.position = position;
		this.visibility = visibility;
		this.severity = severity;
		this.message = message;
	}

	public DtoFieldMessage createUiFieldMessage(FieldMessagePosition defaultPosition, FieldMessageVisibility defaultVisibility) {
		return new DtoFieldMessage(
				severity,
				message,
				position != null ? position: defaultPosition,
				visibility != null ? visibility : defaultVisibility
		);
	}

	public FieldMessagePosition getPosition() {
		return position;
	}

	public FieldMessageVisibility getVisibility() {
		return visibility;
	}

	public FieldMessageSeverity getSeverity() {
		return severity;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "FieldMessage{" +
				"severity=" + severity +
				", message='" + message + '\'' +
				", position=" + position +
				", visibilityMode=" + visibility +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FieldMessage that = (FieldMessage) o;
		return severity == that.severity && Objects.equals(message, that.message) && position == that.position && visibility == that.visibility;
	}

	@Override
	public int hashCode() {
		return Objects.hash(severity, message, position, visibility);
	}
}
