/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiTextInputHandlingField;
import org.teamapps.event.Event;

public interface TextInputHandlingField {

	Event<String> onTextInput();

	Event<SpecialKey> onSpecialKeyPressed();

	default boolean defaultHandleTextInputEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TEXT_INPUT_HANDLING_FIELD_TEXT_INPUT:
				UiTextInputHandlingField.TextInputEvent keyStrokeEvent = (UiTextInputHandlingField.TextInputEvent) event;
				this.onTextInput().fire(keyStrokeEvent.getEnteredString());
				return true;
			case UI_TEXT_INPUT_HANDLING_FIELD_SPECIAL_KEY_PRESSED:
				UiTextInputHandlingField.SpecialKeyPressedEvent specialKeyPressedEvent = (UiTextInputHandlingField.SpecialKeyPressedEvent) event;
				this.onSpecialKeyPressed().fire(SpecialKey.valueOf(specialKeyPressedEvent.getKey().name()));
				return true;
		}
		return false;
	}

}
