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
package org.teamapps.ux.component.grid.layout;

import org.teamapps.dto.UiFloatingComponentGridPlacementItem;
import org.teamapps.ux.component.Component;

public class FloatingComponentGridPlacementItem {

	protected Component component;
	protected int minWidth;
	protected int maxWidth;
	protected int minHeight;
	protected int maxHeight;

	public FloatingComponentGridPlacementItem() {
	}

	public FloatingComponentGridPlacementItem(Component component) {
		this.component = component;
	}

	public UiFloatingComponentGridPlacementItem createUiFloatingComponentGridPlacementItem() {
		UiFloatingComponentGridPlacementItem uiItem = new UiFloatingComponentGridPlacementItem(component.createUiReference());
		uiItem.setMinWidth(minWidth);
		uiItem.setMaxWidth(maxWidth);
		uiItem.setMinHeight(minHeight);
		uiItem.setMaxHeight(maxHeight);
		return uiItem;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
}
