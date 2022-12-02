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

import org.teamapps.dto.DtoField;
import org.teamapps.dto.DtoMultiLineTextField;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class MultiLineTextField extends TextField {

	private boolean adjustHeightToContent = false;

	public MultiLineTextField() {
		super();
	}

	@Override
	public DtoField createUiClientObject() {
		DtoMultiLineTextField uiField = new DtoMultiLineTextField();
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setMaxCharacters(getMaxCharacters());
		uiField.setShowClearButton(isShowClearButton());
		uiField.setPlaceholderText(getEmptyText());
		uiField.setAdjustHeightToContent(adjustHeightToContent);
		return uiField;
	}

	public void append(String s, boolean scrollToBottom) {
		MultiWriteLockableValue.Lock lock = setAndLockValue(s);
		if (isRendered()) {
			getSessionContext().sendCommand(getId(), new DtoMultiLineTextField.AppendCommand(s, scrollToBottom), aVoid -> lock.release());
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
