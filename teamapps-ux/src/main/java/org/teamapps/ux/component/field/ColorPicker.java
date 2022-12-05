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

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.dto.DtoColorPicker;
import org.teamapps.dto.DtoField;

public class ColorPicker extends AbstractField<Color> {

	private Color defaultColor = new RgbaColor(0, 0, 0);
	private String saveButtonCaption = "Save";
	private String clearButtonCaption = "Clear";

	public ColorPicker() {
		super();
	}

	@Override
	public DtoField createDto() {
		DtoColorPicker uiColorPicker = new DtoColorPicker();
		mapAbstractFieldAttributesToUiField(uiColorPicker);
		uiColorPicker.setDefaultColor(defaultColor != null ? defaultColor.toHtmlColorString() : null);
		uiColorPicker.setSaveButtonCaption(saveButtonCaption);
		uiColorPicker.setClearButtonCaption(clearButtonCaption);
		return uiColorPicker;
	}

	@Override
	public Color convertUiValueToUxValue(Object value) {
		if (value == null) {
			return null;
		} else {
			return Color.fromHtmlString((String) value);
		}
	}

	@Override
	public Object convertUxValueToUiValue(Color color) {
		return color != null ? color.toHtmlColorString() : null;
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
