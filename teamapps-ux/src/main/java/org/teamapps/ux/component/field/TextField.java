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
package org.teamapps.ux.component.field;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.dto.UiTextField;
import org.teamapps.event.Event;

public class TextField extends AbstractField<String> implements TextInputHandlingField {

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();

	private int maxCharacters;
	private boolean showClearButton;
	private String emptyText;
	private boolean autofill = false;

	public TextField() {
		super();
	}

	public int getMaxCharacters() {
		return maxCharacters;
	}

	public TextField setMaxCharacters(int maxCharacters) {
		this.maxCharacters = maxCharacters;
		queueCommandIfRendered(() -> new UiTextField.SetMaxCharactersCommand(getId(), maxCharacters));
		return this;
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public TextField setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		queueCommandIfRendered(() -> new UiTextField.SetShowClearButtonCommand(getId(), showClearButton));
		return this;
	}

	public String getEmptyText() {
		return emptyText;
	}

	public TextField setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		queueCommandIfRendered(() -> new UiTextField.SetEmptyTextCommand(getId(), emptyText));
		return this;
	}

	public boolean isAutofill() {
		return autofill;
	}

	public void setAutofill(boolean autofill) {
		this.autofill = autofill;
	}

	@Override
	public UiField createUiComponent() {
		UiTextField uiField = new UiTextField();
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxCharacters(maxCharacters);
		uiField.setShowClearButton(showClearButton);
		uiField.setEmptyText(emptyText);
		uiField.setAutofill(autofill);
		return uiField;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		defaultHandleTextInputEvent(event);
	}

	@Override
	public boolean isEmpty() {
		return StringUtils.isBlank(getValue());
	}

	@Override
	public Event<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public Event<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}
}
