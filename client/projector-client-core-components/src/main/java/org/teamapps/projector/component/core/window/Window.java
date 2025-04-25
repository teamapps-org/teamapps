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
package org.teamapps.projector.component.core.window;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.Showable;
import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.panel.*;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.icon.Icon;

import java.util.List;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class Window extends Panel implements Showable, DtoWindowEventHandler {

	private final DtoWindowClientObjectChannel clientObjectChannel = new DtoWindowClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Void> onClosed = new ProjectorEvent<>();

	private boolean modal = false;
	private int width = 0; // 0 = full width
	private int height = 0; // 0 = full height; -1 = auto
	private int minWidth = 200; // 0 = full height; -1 = auto
	private int minHeight = 100; // 0 = full height; -1 = auto
	private boolean resizable = true;
	private boolean movable = true;
	private boolean keepInViewport = true;
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
		window.setMinWidth(minWidth);
		window.setMinHeight(minHeight);
		window.setResizable(resizable);
		window.setMovable(movable);
		window.setKeepInViewport(keepInViewport);
		if (height < 0) { // auto-height -> do not stretch the content (#safariflex). TODO remove once Safari got fixed!
			window.setContentStretchingEnabled(false);
		}
		window.setModalBackgroundDimmingColor(modalBackgroundDimmingColor != null ? modalBackgroundDimmingColor.toHtmlColorString() : null);
		window.setCloseable(closeable);
		window.setCloseOnClickOutside(closeOnClickOutside);
		window.setCloseOnEscape(closeOnEscape);
		return window;
	}

	@Override
	public void handleEvent(String name, JsonWrapper eventObject) {
		new DtoWindowEventMethodInvoker(this).handleEvent(name, eventObject);
	}

	@Override
	public Object handleQuery(String name, List<JsonWrapper> params) {
		return new DtoWindowQueryMethodInvoker(this).handleQuery(name, params);
	}

	@Override
	public void handleClosed() {
		onClosed.fire();
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
		clientObjectChannel.setModal(modal);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.clientObjectChannel.setWidth(width);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		this.clientObjectChannel.setHeight(height);
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		this.clientObjectChannel.setMinWidth(minWidth);
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		this.clientObjectChannel.setMinHeight(minHeight);
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
		this.clientObjectChannel.setResizable(resizable);
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
		this.clientObjectChannel.setMovable(movable);
	}

	public boolean isKeepInViewport() {
		return keepInViewport;
	}

	public void setKeepInViewport(boolean keepInViewport) {
		this.keepInViewport = keepInViewport;
		this.clientObjectChannel.setKeepInViewport(keepInViewport);
	}

	public void enableAutoHeight() {
		setHeight(-1);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		if (height < 0) { // auto-height -> do not stretch the content (#safariflex). TODO remove once Safari got fixed!
			this.setContentStretchingEnabled(false);
		}
		clientObjectChannel.setSize(width, height);
	}

	public Color getModalBackgroundDimmingColor() {
		return modalBackgroundDimmingColor;
	}

	public void setModalBackgroundDimmingColor(Color modalBackgroundDimmingColor) {
		this.modalBackgroundDimmingColor = modalBackgroundDimmingColor;
		clientObjectChannel.setModalBackgroundDimmingColor(modalBackgroundDimmingColor != null ? modalBackgroundDimmingColor.toHtmlColorString() : null);
	}

	public void show() {
		show(200);
	}

	public void show(int animationDuration) {
		clientObjectChannel.forceRender();
		clientObjectChannel.show(animationDuration);
	}

	public void close() {
		close(200);
	}

	public void close(int animationDuration) {
		clientObjectChannel.close(animationDuration);
	}

	public boolean isCloseable() {
		return closeable;
	}

	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
		clientObjectChannel.setCloseable(closeable);
	}

	public boolean isCloseOnEscape() {
		return closeOnEscape;
	}

	public void setCloseOnEscape(boolean closeOnEscape) {
		this.closeOnEscape = closeOnEscape;
		clientObjectChannel.setCloseOnEscape(closeOnEscape);
	}

	public boolean isCloseOnClickOutside() {
		return closeOnClickOutside;
	}

	public void setCloseOnClickOutside(boolean closeOnClickOutside) {
		this.closeOnClickOutside = closeOnClickOutside;
		clientObjectChannel.setCloseOnClickOutside(closeOnClickOutside);
	}
}
