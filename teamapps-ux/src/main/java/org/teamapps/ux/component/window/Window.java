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
package org.teamapps.ux.component.window;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoWindow;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.component.panel.Panel;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class Window extends Panel {

	private boolean modal = false;
	private int width = 0; // 0 = full width
	private int height = 0; // 0 = full height; -1 = auto
	private Color modalBackgroundDimmingColor = new RgbaColor(0, 0, 0, 0.2f);
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
	public DtoComponent createDto() {
		DtoWindow window = new DtoWindow();
		mapUiPanelProperties(window);
		window.setModal(modal);
		window.setWidth(width);
		window.setHeight(height);
		if (height < 0) { // auto-height -> do not stretch the content (#safariflex). TODO remove once Safari got fixed!
			window.setStretchContent(false);
		}
		window.setModalBackgroundDimmingColor(modalBackgroundDimmingColor != null ? modalBackgroundDimmingColor.toHtmlColorString() : null);
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
		sendCommandIfRendered(() -> new DtoWindow.SetModalCommand(modal));
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		setSize(width, height);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		setSize(width, height);
	}

	public void enableAutoHeight() {
		setHeight(-1);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		if (height < 0) { // auto-height -> do not stretch the content (#safariflex). TODO remove once Safari got fixed!
			this.setStretchContent(false);
		}
		sendCommandIfRendered(() -> new DtoWindow.SetSizeCommand(width, height));
	}

	public Color getModalBackgroundDimmingColor() {
		return modalBackgroundDimmingColor;
	}

	public void setModalBackgroundDimmingColor(Color modalBackgroundDimmingColor) {
		this.modalBackgroundDimmingColor = modalBackgroundDimmingColor;
		sendCommandIfRendered(() -> new DtoWindow.SetModalBackgroundDimmingColorCommand(modalBackgroundDimmingColor != null ? modalBackgroundDimmingColor.toHtmlColorString() : null));
	}

	public void show() {
		show(200);
	}

	public void show(int animationDuration) {
		getSessionContext().renderClientObject(this);
		sendCommandIfRendered(() -> new DtoWindow.ShowCommand(animationDuration));
	}

	public void close() {
		close(200);
	}

	public void close(int animationDuration) {
		sendCommandIfRendered(() -> new DtoWindow.CloseCommand(animationDuration));
	}

	public boolean isCloseable() {
		return closeable;
	}

	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
		sendCommandIfRendered(() -> new DtoWindow.SetCloseableCommand(closeable));
	}

	public boolean isCloseOnEscape() {
		return closeOnEscape;
	}

	public void setCloseOnEscape(boolean closeOnEscape) {
		this.closeOnEscape = closeOnEscape;
		sendCommandIfRendered(() -> new DtoWindow.SetCloseOnEscapeCommand(closeOnEscape));
	}

	public boolean isCloseOnClickOutside() {
		return closeOnClickOutside;
	}

	public void setCloseOnClickOutside(boolean closeOnClickOutside) {
		this.closeOnClickOutside = closeOnClickOutside;
		sendCommandIfRendered(() -> new DtoWindow.SetCloseOnClickOutsideCommand(closeOnClickOutside));
	}
}
