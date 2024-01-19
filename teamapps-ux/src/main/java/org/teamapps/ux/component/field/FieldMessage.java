/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.ux.component.field;

import org.teamapps.dto.UiFieldMessage;
import org.teamapps.dto.UiFieldMessagePosition;
import org.teamapps.dto.UiFieldMessageSeverity;
import org.teamapps.dto.UiFieldMessageVisibilityMode;

import java.util.Objects;

public class FieldMessage {

	private final Severity severity;
	private final String message;
	private final Position position;
	private final Visibility visibility;

	public enum Severity {
		INFO,
		SUCCESS,
		WARNING,
		ERROR;

		public UiFieldMessageSeverity toUiFieldMessageSeverity() {
			return UiFieldMessageSeverity.valueOf(name());
		}
	}

	public enum Position {
		ABOVE,
		BELOW,
		POPOVER;

		public UiFieldMessagePosition toUiFieldMessagePosition() {
			return UiFieldMessagePosition.valueOf(name());
		}
	}

	public enum Visibility {
		ALWAYS_VISIBLE,
		ON_FOCUS,
		ON_HOVER_OR_FOCUS;

		public UiFieldMessageVisibilityMode toUiFieldMessageVisibilityMode() {
			return UiFieldMessageVisibilityMode.valueOf(name());
		}
	}

	public FieldMessage(Severity severity, String message) {
		this(null, null, severity, message);
	}

	public FieldMessage(Position position, Visibility visibility, Severity severity, String message) {
		this.position = position;
		this.visibility = visibility;
		this.severity = severity;
		this.message = message;
	}

	public UiFieldMessage createUiFieldMessage(Position defaultPosition, Visibility defaultVisibility) {
		return new UiFieldMessage(
				severity.toUiFieldMessageSeverity(),
				message,
				position != null ? position.toUiFieldMessagePosition(): defaultPosition.toUiFieldMessagePosition(),
				visibility != null ? visibility.toUiFieldMessageVisibilityMode() : defaultVisibility.toUiFieldMessageVisibilityMode()
		);
	}

	public Position getPosition() {
		return position;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public Severity getSeverity() {
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
