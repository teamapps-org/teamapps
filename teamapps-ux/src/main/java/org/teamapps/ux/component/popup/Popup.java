/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.ux.component.popup;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiPopup;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

public class Popup extends AbstractComponent {

	private Component contentComponent;
	private int x;
	private int y;
	private int width; // 0 = full width, -1 = auto
	private int height; // 0 = full height, -1 = auto
	private Color backgroundColor;
	private boolean modal = false;
	private Color dimmingColor = new RgbaColor(0, 0, 0, .2f);
	private boolean closeOnEscape; // close if the user presses escape
	private boolean closeOnClickOutside; // close if the user clicks onto the area outside the window

	public Popup(Component contentComponent) {
		this.contentComponent = contentComponent;
	}

	@Override
	public UiComponent createUiComponent() {
		UiPopup ui = new UiPopup();
		mapAbstractUiComponentProperties(ui);
		ui.setContentComponent(contentComponent.createUiReference());
		ui.setX(x);
		ui.setY(y);
		ui.setWidth(width);
		ui.setHeight(height);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setModal(modal);
		ui.setDimmingColor(dimmingColor != null ? dimmingColor.toHtmlColorString() : null);
		ui.setCloseOnEscape(closeOnEscape);
		ui.setCloseOnClickOutside(closeOnClickOutside);
		return ui;
	}

	public Component getContentComponent() {
		return contentComponent;
	}

	public void setContentComponent(Component contentComponent) {
		this.contentComponent = contentComponent;
		// queueCommandIfRendered(() -> new UiPopup.SetContentComponentCommand(getId(), contentComponent));
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		queueCommandIfRendered(() -> new UiPopup.SetPositionCommand(getId(), x, y));
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		queueCommandIfRendered(() -> new UiPopup.SetPositionCommand(getId(), x, y));
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		queueCommandIfRendered(() -> new UiPopup.SetPositionCommand(getId(), x, y));
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		queueCommandIfRendered(() -> new UiPopup.SetDimensionsCommand(getId(), width, height));
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		queueCommandIfRendered(() -> new UiPopup.SetDimensionsCommand(getId(), width, height));
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		queueCommandIfRendered(() -> new UiPopup.SetBackgroundColorCommand(getId(), backgroundColor != null ? backgroundColor.toHtmlColorString() : null));
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
		// queueCommandIfRendered(() -> new UiPopup.SetModalCommand(getId(), modal));
	}

	public Color getDimmingColor() {
		return dimmingColor;
	}

	public void setDimmingColor(Color dimmingColor) {
		this.dimmingColor = dimmingColor;
		queueCommandIfRendered(() -> new UiPopup.SetDimmingColorCommand(getId(), dimmingColor != null ? dimmingColor.toHtmlColorString() : null));
	}

	public boolean isCloseOnEscape() {
		return closeOnEscape;
	}

	public void setCloseOnEscape(boolean closeOnEscape) {
		this.closeOnEscape = closeOnEscape;
		// queueCommandIfRendered(() -> new UiPopup.SetCloseOnEscapeCommand(getId(), closeOnEscape));
	}

	public boolean isCloseOnClickOutside() {
		return closeOnClickOutside;
	}

	public void setCloseOnClickOutside(boolean closeOnClickOutside) {
		this.closeOnClickOutside = closeOnClickOutside;
		// queueCommandIfRendered(() -> new UiPopup.SetCloseOnClickOutsideCommand(getId(), closeOnClickOutside));
	}

	public void close() {
		queueCommandIfRendered(() -> new UiPopup.CloseCommand(getId()));
	}
}
