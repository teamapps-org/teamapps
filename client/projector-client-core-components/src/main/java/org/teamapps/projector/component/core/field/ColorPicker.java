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
import org.teamapps.common.format.Color;
import org.teamapps.projector.component.core.DtoColorPicker;
import org.teamapps.projector.component.core.DtoColorPickerClientObjectChannel;
import org.teamapps.projector.component.core.DtoColorPickerEventHandler;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.field.DtoAbstractField;

public class ColorPicker extends AbstractField<Color> implements DtoColorPickerEventHandler {

	private final DtoColorPickerClientObjectChannel clientObjectChannel = new DtoColorPickerClientObjectChannel(getClientObjectChannel());

	private boolean clearable;

	public ColorPicker() {
		super();
	}

	@Override
	public DtoAbstractField createDto() {
		DtoColorPicker uiColorPicker = new DtoColorPicker();
		mapAbstractFieldAttributesToUiField(uiColorPicker);
		uiColorPicker.setClearable(clearable);
		return uiColorPicker;
	}

	@Override
	public Color doConvertClientValueToServerValue(JsonNode value) {
		return Color.fromHtmlString(value.textValue());
	}

	@Override
	public Object convertServerValueToClientValue(Color color) {
		return color != null ? color.toHtmlColorString() : null;
	}

	public boolean isClearable() {
		return clearable;
	}

	public void setClearable(boolean clearable) {
		this.clearable = clearable;
		clientObjectChannel.setClearable(clearable);
	}
}
