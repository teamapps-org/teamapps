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
import org.teamapps.dto.DtoCheckBox;
import org.teamapps.dto.DtoField;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CommonComponentLibrary.class)
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
	public DtoField createDto() {
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

	public CheckBox setCaption(String caption) {
		this.caption = caption;
		sendCommandIfRendered(() -> new DtoCheckBox.SetCaptionCommand(caption));
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public CheckBox setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		sendCommandIfRendered(() -> new DtoCheckBox.SetBackgroundColorCommand(backgroundColor != null ? backgroundColor.toHtmlColorString() : null));
		return this;
	}

	public Color getCheckColor() {
		return checkColor;
	}

	public CheckBox setCheckColor(Color checkColor) {
		this.checkColor = checkColor;
		sendCommandIfRendered(() -> new DtoCheckBox.SetCheckColorCommand(checkColor != null ? checkColor.toHtmlColorString() : null));
		return this;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public CheckBox setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		sendCommandIfRendered(() -> new DtoCheckBox.SetBorderColorCommand(borderColor != null ? borderColor.toHtmlColorString() : null));
		return this;
	}

	public boolean isHtmlEnabled() {
		return htmlEnabled;
	}

	public CheckBox setHtmlEnabled(boolean htmlEnabled) {
		this.htmlEnabled = htmlEnabled;
		reRenderIfRendered();
		return this;
	}
}
