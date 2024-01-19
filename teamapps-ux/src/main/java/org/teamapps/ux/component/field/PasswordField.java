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

import org.teamapps.dto.UiField;
import org.teamapps.dto.UiPasswordField;

public class PasswordField extends TextField {

	private boolean sendValueAsMd5;
	private String salt; // if sendValueAsMd5 == true and salt != null, then submit md5(salt + md5(fieldValue))
	private boolean passwordVisibilityToggleEnabled;

	public PasswordField() {
		super();
	}

	@Override
	public UiField createUiComponent() {
		UiPasswordField uiField = new UiPasswordField();
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxCharacters(getMaxCharacters());
		uiField.setShowClearButton(isShowClearButton());
		uiField.setPlaceholderText(getEmptyText());
		uiField.setAutofill(isAutofill());
		uiField.setSendValueAsMd5(sendValueAsMd5);
		uiField.setSalt(salt);
		uiField.setPasswordVisibilityToggleEnabled(passwordVisibilityToggleEnabled);
		return uiField;
	}

	public boolean isSendValueAsMd5() {
		return sendValueAsMd5;
	}

	public void setSendValueAsMd5(boolean sendValueAsMd5) {
		this.sendValueAsMd5 = sendValueAsMd5;
		queueCommandIfRendered(() -> new UiPasswordField.SetSendValueAsMd5Command(getId(), sendValueAsMd5));
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
		queueCommandIfRendered(() -> new UiPasswordField.SetSaltCommand(getId(), salt));
	}

	public boolean isPasswordVisibilityToggleEnabled() {
		return passwordVisibilityToggleEnabled;
	}

	public void setPasswordVisibilityToggleEnabled(boolean enabled) {
		this.passwordVisibilityToggleEnabled = enabled;
		queueCommandIfRendered(() -> new UiPasswordField.SetPasswordVisibilityToggleEnabledCommand(getId(), enabled));
	}
}
