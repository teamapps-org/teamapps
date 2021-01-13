/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.ux.component.form.layoutpolicy;

import org.teamapps.dto.UiFormSectionFloatingField;
import org.teamapps.ux.component.field.AbstractField;

public class FormSectionFloatingField {

	private final AbstractField field;
	private int minWidth;
	private int maxWidth;
	private int minHeight;
	private int maxHeight;

	public FormSectionFloatingField(AbstractField field) {
		this.field = field;
	}

	public AbstractField getField() {
		return field;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public UiFormSectionFloatingField createUiFormSectionFloatingField() {
		UiFormSectionFloatingField floatingField = new UiFormSectionFloatingField(field.createUiReference());
		floatingField.setMinWidth(minWidth);
		floatingField.setMaxWidth(maxWidth);
		floatingField.setMinHeight(minHeight);
		floatingField.setMaxHeight(maxHeight);
		return floatingField;
	}
}
