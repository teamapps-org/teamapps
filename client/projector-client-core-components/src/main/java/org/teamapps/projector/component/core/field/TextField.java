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
package org.teamapps.projector.component.core.field;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.DtoTextField;
import org.teamapps.projector.component.core.DtoTextFieldClientObjectChannel;
import org.teamapps.projector.component.core.DtoTextFieldEventHandler;
import org.teamapps.projector.component.core.SpecialKey;
import org.teamapps.projector.event.ProjectorEvent;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class TextField extends AbstractField<String> implements DtoTextFieldEventHandler {

	private final DtoTextFieldClientObjectChannel clientObjectChannel = new DtoTextFieldClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<String> onTextInput = new ProjectorEvent<>(clientObjectChannel::toggleTextInputEvent);
	public final ProjectorEvent<SpecialKey> onSpecialKeyPressed = new ProjectorEvent<>(clientObjectChannel::toggleSpecialKeyPressedEvent);

	private int maxCharacters;
	private boolean showClearButton;
	private String placeholderText;
	private boolean autofill = false;

	public TextField() {
		super();
	}

	public int getMaxCharacters() {
		return maxCharacters;
	}

	public TextField setMaxCharacters(int maxCharacters) {
		this.maxCharacters = maxCharacters;
		clientObjectChannel.setMaxCharacters(maxCharacters);
		return this;
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public TextField setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		clientObjectChannel.setShowClearButton(showClearButton);
		return this;
	}

	public String getPlaceholderText() {
		return placeholderText;
	}

	public TextField setPlaceholderText(String placeholderText) {
		this.placeholderText = placeholderText;
		clientObjectChannel.setPlaceholderText(placeholderText);
		return this;
	}

	public boolean isAutofill() {
		return autofill;
	}

	public void setAutofill(boolean autofill) {
		this.autofill = autofill;
	}

	@Override
	public DtoTextField createDto() {
		DtoTextField uiField = new DtoTextField();
		mapAbstractFieldAttributes(uiField);
		uiField.setMaxCharacters(maxCharacters);
		uiField.setShowClearButton(showClearButton);
		uiField.setPlaceholderText(placeholderText);
		uiField.setAutofill(autofill);
		return uiField;
	}


	@Override
	public String doConvertClientValueToServerValue(JsonNode value) {
		return value.textValue();
	}

	@Override
	public boolean isEmptyValue(String value) {
		return StringUtils.isBlank(value);
	}

	@Override
	public void handleTextInput(String enteredString) {
		onTextInput.fire(enteredString);
	}

	@Override
	public void handleSpecialKeyPressed(SpecialKey key) {
		onSpecialKeyPressed.fire(key);
	}
}
