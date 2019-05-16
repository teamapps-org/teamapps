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
package org.teamapps.ux.component.window;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiWindow;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.panel.Panel;

import static org.teamapps.util.UiUtil.createUiColor;

public class Window extends Panel {

	private boolean modal = false;
	private int width = 0;
	private int height = 0;
	private Color modalBackgroundDimmingColor = new Color(0, 0, 0, 0.2f);
	private boolean closeable;
	private boolean closeOnEscape;
	private boolean closeOnClickOutside;

	public Window() {
	}

	public Window(Component content) {
		this(0, 0, content);
	}

	public Window(int width, int height, Component content) {
		this(null, null, width, height, content);
	}

	public Window(Icon icon, String title, int width, int height, Component content) {
		this(icon, title, content, width, height, false, false, false);
	}

	public Window(Icon icon, String title, Component content, int width, int height, boolean closeable, boolean closeOnEscape, boolean closeOnClickOutside) {
		super(icon, title, content);
		this.width = width;
		this.height = height;
		this.closeable = closeable;
		this.closeOnEscape = closeOnEscape;
		this.closeOnClickOutside = closeOnClickOutside;
	}

	@Override
	public UiComponent createUiComponent() {
		UiWindow window = new UiWindow(getId());
		mapUiPanelProperties(window);
		window.setModal(modal);
		window.setWidth(width);
		window.setHeight(height);
		window.setModalBackgroundDimmingColor(createUiColor(modalBackgroundDimmingColor));
		window.setCloseable(closeable);
		window.setCloseOnClickOutside(closeOnClickOutside);
		window.setCloseOnEscape(closeOnEscape);
		return window;
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
		queueCommandIfRendered(() -> new UiWindow.SetModalCommand(getId(), modal));
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		queueCommandIfRendered(() -> new UiWindow.SetSizeCommand(getId(), width, height));
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		queueCommandIfRendered(() -> new UiWindow.SetSizeCommand(getId(), width, height));
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		queueCommandIfRendered(() -> new UiWindow.SetSizeCommand(getId(), width, height));
	}

	public Color getModalBackgroundDimmingColor() {
		return modalBackgroundDimmingColor;
	}

	public void setModalBackgroundDimmingColor(Color modalBackgroundDimmingColor) {
		this.modalBackgroundDimmingColor = modalBackgroundDimmingColor;
		queueCommandIfRendered(() -> new UiWindow.SetModalBackgroundDimmingColorCommand(getId(), createUiColor(modalBackgroundDimmingColor)));
	}

	public void show() {
		show(200);
	}

	public void show(int animationDuration) {
		getSessionContext().showWindow(this, animationDuration);
	}

	public void close() {
		close(200);
	}

	public void close(int animationDuration) {
		queueCommandIfRendered(() -> new UiWindow.CloseCommand(getId(), animationDuration));
	}

	public boolean isCloseable() {
		return closeable;
	}

	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
		queueCommandIfRendered(() -> new UiWindow.SetCloseableCommand(getId(), closeable));
	}

	public boolean isCloseOnEscape() {
		return closeOnEscape;
	}

	public void setCloseOnEscape(boolean closeOnEscape) {
		this.closeOnEscape = closeOnEscape;
		queueCommandIfRendered(() -> new UiWindow.SetCloseOnEscapeCommand(getId(), closeOnEscape));
	}

	public boolean isCloseOnClickOutside() {
		return closeOnClickOutside;
	}

	public void setCloseOnClickOutside(boolean closeOnClickOutside) {
		this.closeOnClickOutside = closeOnClickOutside;
		queueCommandIfRendered(() -> new UiWindow.SetCloseOnClickOutsideCommand(getId(), closeOnClickOutside));
	}
}
