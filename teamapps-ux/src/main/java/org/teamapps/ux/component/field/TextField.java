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
package org.teamapps.ux.component.field;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.dto.DtoAbstractField;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoTextField;
import org.teamapps.projector.dto.DtoTextInputHandlingField;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.projector.clientobject.ProjectorComponent;

import java.util.function.Supplier;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class TextField extends AbstractField<String> implements TextInputHandlingField {

	public final ProjectorEvent<String> onTextInput = createProjectorEventBoundToUiEvent(DtoTextInputHandlingField.TextInputEvent.TYPE_ID);
	public final ProjectorEvent<SpecialKey> onSpecialKeyPressed = createProjectorEventBoundToUiEvent(DtoTextInputHandlingField.SpecialKeyPressedEvent.TYPE_ID);

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
		getClientObjectChannel().sendCommandIfRendered(new DtoTextField.SetMaxCharactersCommand(maxCharacters), null);
		return this;
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public TextField setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		getClientObjectChannel().sendCommandIfRendered(new DtoTextField.SetShowClearButtonCommand(showClearButton), null);
		return this;
	}

	public String getEmptyText() {
		return emptyText;
	}

	public TextField setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		getClientObjectChannel().sendCommandIfRendered(new DtoTextField.SetPlaceholderTextCommand(emptyText), null);
		return this;
	}

	public boolean isAutofill() {
		return autofill;
	}

	public void setAutofill(boolean autofill) {
		this.autofill = autofill;
	}

	@Override
	public DtoAbstractField createConfig() {
		DtoTextField uiField = new DtoTextField();
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxCharacters(maxCharacters);
		uiField.setShowClearButton(showClearButton);
		uiField.setPlaceholderText(emptyText);
		uiField.setAutofill(autofill);
		return uiField;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		super.handleUiEvent(name, params);
		defaultHandleTextInputEvent(event);
	}

	@Override
	public boolean isEmptyValue(String value) {
		return StringUtils.isBlank(value);
	}

	@Override
	public ProjectorEvent<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public ProjectorEvent<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}
}
