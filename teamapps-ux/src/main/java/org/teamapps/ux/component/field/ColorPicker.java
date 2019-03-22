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

import org.teamapps.dto.UiColor;
import org.teamapps.dto.UiColorPicker;
import org.teamapps.dto.UiField;
import org.teamapps.common.format.Color;

import static org.teamapps.util.UiUtil.createUiColor;

public class ColorPicker extends AbstractField<Color> {

	private Color defaultColor = new Color(0, 0, 0);
	private String saveButtonCaption = "Save";
	private String clearButtonCaption = "Clear";

	public ColorPicker() {
		super();
	}

	@Override
	public UiField createUiComponent() {
		UiColorPicker uiColorPicker = new UiColorPicker(getId());
		mapAbstractFieldAttributesToUiField(uiColorPicker);
		uiColorPicker.setDefaultColor(defaultColor != null ? createUiColor(defaultColor) : null);
		uiColorPicker.setSaveButtonCaption(saveButtonCaption);
		uiColorPicker.setClearButtonCaption(clearButtonCaption);
		return uiColorPicker;
	}

	@Override
	public Color convertUiValueToUxValue(Object value) {
		if (value == null) {
			return null;
		} else {
			UiColor uiColor = (UiColor) value;
			return new Color(uiColor.getRed(), uiColor.getGreen(), uiColor.getBlue(), uiColor.getAlpha());
		}
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	public String getSaveButtonCaption() {
		return saveButtonCaption;
	}

	public void setSaveButtonCaption(String saveButtonCaption) {
		this.saveButtonCaption = saveButtonCaption;
	}

	public String getClearButtonCaption() {
		return clearButtonCaption;
	}

	public void setClearButtonCaption(String clearButtonCaption) {
		this.clearButtonCaption = clearButtonCaption;
	}
}
