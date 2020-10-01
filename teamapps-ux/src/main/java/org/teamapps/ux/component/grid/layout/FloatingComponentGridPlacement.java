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
package org.teamapps.ux.component.grid.layout;

import org.teamapps.dto.UiFloatingComponentGridPlacement;
import org.teamapps.dto.UiFloatingComponentGridPlacementItem;
import org.teamapps.ux.component.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FloatingComponentGridPlacement extends AbstractGridPlacement {

	private List<FloatingComponentGridPlacementItem> items = new ArrayList<>();
	private boolean wrap;
	private int horizontalSpacing = 5;
	private int verticalSpacing = 3;

	public FloatingComponentGridPlacement() {
	}

	public FloatingComponentGridPlacement(int row, int column, List<FloatingComponentGridPlacementItem> items) {
		super(row, column);
		this.items = items;
	}

	@Override
	public UiFloatingComponentGridPlacement createUiGridPlacement() {
		List<UiFloatingComponentGridPlacementItem> items = this.items.stream()
				.map(floatingField -> floatingField.createUiFloatingComponentGridPlacementItem())
				.collect(Collectors.toList());
		UiFloatingComponentGridPlacement uiPlacement = new UiFloatingComponentGridPlacement(items)
				.setWrap(wrap)
				.setVerticalSpacing(verticalSpacing)
				.setHorizontalSpacing(horizontalSpacing);
		mapAbstractGridPlacementUiProperties(uiPlacement);
		return uiPlacement;
	}

	@Override
	public List<Component> getComponents() {
		return items.stream()
				.map(item -> item.getComponent())
				.collect(Collectors.toList());
	}

	public List<FloatingComponentGridPlacementItem> getItems() {
		return items;
	}

	public void setComponents(List<FloatingComponentGridPlacementItem> components) {
		this.items = components;
	}

	public boolean isWrap() {
		return wrap;
	}

	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}

	public int getHorizontalSpacing() {
		return horizontalSpacing;
	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
	}

	public int getVerticalSpacing() {
		return verticalSpacing;
	}

	public void setVerticalSpacing(int verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
	}
}
