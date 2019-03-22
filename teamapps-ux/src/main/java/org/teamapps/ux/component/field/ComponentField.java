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

import org.teamapps.dto.UiComponentField;
import org.teamapps.dto.UiField;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.format.Border;
import org.teamapps.common.format.Color;

import static org.teamapps.util.UiUtil.createUiColor;

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
		UiComponentField uiField = new UiComponentField(getId());
		mapAbstractFieldAttributesToUiField(uiField);
		uiField.setComponent(Component.createUiComponentReference(component));
		uiField.setWidth(width);
		uiField.setHeight(height);
		uiField.setBorder(border != null ? border.createUiBorder(): null);
		uiField.setBackgroundColor(backgroundColor != null ? createUiColor(backgroundColor) : null);
		return uiField;
	}

	@Override
	protected void doDestroy() {
		this.component.destroy();
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
		queueCommandIfRendered(() -> new UiComponentField.SetComponentCommand(getId(), Component.createUiComponentReference(component)));
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
		queueCommandIfRendered(() -> new UiComponentField.SetBackgroundColorCommand(getId(), backgroundColor != null ? createUiColor(backgroundColor) : null));
	}
}
