/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

public class FieldMessage {

	private final Severity severity;
	private final String message;
	private final Position position;
	private final VisibilityMode visibilityMode;

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

	public enum VisibilityMode {
		ALWAYS_VISIBLE,
		ON_FOCUS,
		ON_HOVER_OR_FOCUS;

		public UiFieldMessageVisibilityMode toUiFieldMessageVisibilityMode() {
			return UiFieldMessageVisibilityMode.valueOf(name());
		}
	}

	public FieldMessage(Severity severity, String message) {
		this(Position.BELOW, VisibilityMode.ON_FOCUS, severity, message);
	}

	public FieldMessage(Position position, VisibilityMode visibilityMode, Severity severity, String message) {
		this.position = position;
		this.visibilityMode = visibilityMode;
		this.severity = severity;
		this.message = message;
	}

	public UiFieldMessage createUiFieldMessage() {
		return new UiFieldMessage(severity.toUiFieldMessageSeverity(), message, position.toUiFieldMessagePosition(), visibilityMode.toUiFieldMessageVisibilityMode());
	}

	public Position getPosition() {
		return position;
	}

	public VisibilityMode getVisibilityMode() {
		return visibilityMode;
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
				", visibilityMode=" + visibilityMode +
				'}';
	}
}
