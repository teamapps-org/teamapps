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

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.DtoPasswordField;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class PasswordField extends TextField {

	private boolean sendValueAsMd5;
	private String salt; // if sendValueAsMd5 == true and salt != null, then submit md5(salt + md5(fieldValue))

	public PasswordField() {
		super();
	}

	@Override
	public DtoPasswordField createDto() {
		DtoPasswordField uiField = new DtoPasswordField();
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxCharacters(getMaxCharacters());
		uiField.setShowClearButton(isShowClearButton());
		uiField.setPlaceholderText(getPlaceholderText());
		uiField.setAutofill(isAutofill());
		return uiField;
	}

}
