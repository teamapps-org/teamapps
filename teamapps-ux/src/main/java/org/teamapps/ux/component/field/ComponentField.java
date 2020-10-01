/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.component.field;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponentField;
import org.teamapps.dto.UiField;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.format.Border;

public class ComponentField extends AbstractField<Void> {

	private Component component;
	private int width;
	private int height;
	private Border border;
	private Color backgroundColor;

	public ComponentField(Component component) {
		super();
		this.component = component;
	}

	@Override
	public UiField createUiComponent() {
		UiComponentField uiField = new UiComponentField();
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setComponent(Component.createUiClientObjectReference(component));
		uiField.setWidth(width);
		uiField.setHeight(height);
		uiField.setBorder(border != null ? border.createUiBorder(): null);
		uiField.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		return uiField;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
		queueCommandIfRendered(() -> new UiComponentField.SetComponentCommand(getId(), Component.createUiClientObjectReference(component)));
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		queueCommandIfRendered(() -> new UiComponentField.SetSizeCommand(getId(), width, height));
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		queueCommandIfRendered(() -> new UiComponentField.SetSizeCommand(getId(), width, height));
	}

	public Border getBorder() {
		return border;
	}

	public void setBorder(Border border) {
		this.border = border;
		queueCommandIfRendered(() -> new UiComponentField.SetBorderCommand(getId(), border != null ? border.createUiBorder() : null));
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		queueCommandIfRendered(() -> new UiComponentField.SetBackgroundColorCommand(getId(), backgroundColor != null ? backgroundColor.toHtmlColorString() : null));
	}
}
