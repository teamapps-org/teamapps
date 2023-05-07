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
package org.teamapps.projector.components.common.grid.layout;

import org.teamapps.projector.components.common.dto.DtoComponentGridPlacement;
import org.teamapps.ux.component.Component;

import java.util.Collections;
import java.util.List;

public class ComponentGridPlacement extends AbstractGridPlacement {

	private Component component;

	public ComponentGridPlacement() {
	}

	public ComponentGridPlacement(int row, int column, Component component) {
		super(row, column);
		this.component = component;
	}

	@Override
	public DtoComponentGridPlacement createUiGridPlacement() {
		DtoComponentGridPlacement uiPlacement = new DtoComponentGridPlacement(component.createDtoReference());
		mapAbstractGridPlacementUiProperties(uiPlacement);
		return uiPlacement;
	}

	@Override
	public List<Component> getComponents() {
		return Collections.singletonList(component);
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
}
