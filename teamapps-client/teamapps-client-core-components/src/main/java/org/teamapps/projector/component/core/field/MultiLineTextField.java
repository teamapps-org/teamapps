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
import org.teamapps.projector.component.field.MultiWriteLockableValue;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.DtoMultiLineTextField;
import org.teamapps.projector.component.core.DtoMultiLineTextFieldClientObjectChannel;
import org.teamapps.projector.component.core.DtoTextField;
import org.teamapps.projector.component.core.DtoTextFieldEventHandler;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class MultiLineTextField extends TextField implements DtoTextFieldEventHandler {

	private final DtoMultiLineTextFieldClientObjectChannel clientObjectChannel = new DtoMultiLineTextFieldClientObjectChannel(getClientObjectChannel());

	private boolean adjustHeightToContent = false;

	public MultiLineTextField() {
		super();
	}

	@Override
	public DtoTextField createDto() {
		DtoMultiLineTextField uiField = new DtoMultiLineTextField();
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxCharacters(getMaxCharacters());
		uiField.setShowClearButton(isShowClearButton());
		uiField.setPlaceholderText(getPlaceholderText());
		uiField.setAdjustHeightToContent(adjustHeightToContent);
		return uiField;
	}

	public void append(String s, boolean scrollToBottom) {
		MultiWriteLockableValue.Lock lock = setAndLockValue(getValue() + s);
		resetValueChangedByClient();
		if (clientObjectChannel.isRendered()) {
			clientObjectChannel.append(s, scrollToBottom, aVoid -> lock.release());
		} else {
			lock.release();
		}
	}

	public boolean isAdjustHeightToContent() {
		return adjustHeightToContent;
	}

	public void setAdjustHeightToContent(boolean adjustHeightToContent) {
		this.adjustHeightToContent = adjustHeightToContent;
	}
}
