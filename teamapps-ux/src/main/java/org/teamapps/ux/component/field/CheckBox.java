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
import org.teamapps.projector.dto.DtoCheckBox;
import org.teamapps.projector.dto.DtoAbstractField;
import org.teamapps.projector.field.AbstractField;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.projector.annotation.ClientObjectLibrary;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class CheckBox extends AbstractField<Boolean> {

	private String caption;
	private Color backgroundColor = new RgbaColor(255, 255, 255);
	private Color checkColor = new RgbaColor(70, 70, 70);
	private Color borderColor = new RgbaColor(204, 204, 204);
	private boolean htmlEnabled = false;

	public CheckBox(String caption) {
		super();
		setValue(false);
		this.caption = caption;
	}

	public CheckBox() {
		this(null);
	}

	@Override
	public DtoAbstractField createConfig() {
		DtoCheckBox uiCheckBox = new DtoCheckBox();
		mapAbstractFieldAttributesToUiField(uiCheckBox);
		uiCheckBox.setCaption(caption);
		uiCheckBox.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiCheckBox.setCheckColor(checkColor != null ? checkColor.toHtmlColorString() : null);
		uiCheckBox.setBorderColor(borderColor != null ? borderColor.toHtmlColorString() : null);
		uiCheckBox.setHtmlEnabled(htmlEnabled);
		return uiCheckBox;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		getClientObjectChannel().sendCommandIfRendered(new DtoCheckBox.SetCaptionCommand(caption), null);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		getClientObjectChannel().sendCommandIfRendered(new DtoCheckBox.SetBackgroundColorCommand(backgroundColor != null ? backgroundColor.toHtmlColorString() : null), null);
	}

	public Color getCheckColor() {
		return checkColor;
	}

	public void setCheckColor(Color checkColor) {
		this.checkColor = checkColor;
		getClientObjectChannel().sendCommandIfRendered(new DtoCheckBox.SetCheckColorCommand(checkColor != null ? checkColor.toHtmlColorString() : null), null);
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		getClientObjectChannel().sendCommandIfRendered(new DtoCheckBox.SetBorderColorCommand(borderColor != null ? borderColor.toHtmlColorString() : null), null);
	}

	public boolean isHtmlEnabled() {
		return htmlEnabled;
	}

	public void setHtmlEnabled(boolean htmlEnabled) {
		this.htmlEnabled = htmlEnabled;
		getClientObjectChannel().sendCommandIfRendered(new DtoCheckBox.SetHtmlEnabledCommand(htmlEnabled), null);
	}
}
