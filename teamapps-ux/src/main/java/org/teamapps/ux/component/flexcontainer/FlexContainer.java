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
package org.teamapps.ux.component.flexcontainer;

import org.teamapps.dto.DtoFlexContainer;
import org.teamapps.dto.JsonWrapper;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.annotations.ProjectorComponent;
import org.teamapps.ux.css.CssAlignItems;
import org.teamapps.ux.css.CssFlexDirection;
import org.teamapps.ux.css.CssJustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class FlexContainer extends AbstractComponent {

	private List<org.teamapps.ux.component.Component> components = new ArrayList<>();
	private CssFlexDirection flexDirection = CssFlexDirection.ROW;
	private CssAlignItems alignItems = CssAlignItems.STRETCH;
	private CssJustifyContent justifyContent = CssJustifyContent.START;

	@Override
	public DtoFlexContainer createConfig() {
		DtoFlexContainer uiFlexContainer = new DtoFlexContainer();
		mapAbstractUiComponentProperties(uiFlexContainer);
		uiFlexContainer.setComponents(components.stream()
				.map(c -> c.createClientReference())
				.collect(Collectors.toList()));
		uiFlexContainer.setFlexDirection(flexDirection.toDto());
		uiFlexContainer.setAlignItems(alignItems.toDto());
		uiFlexContainer.setJustifyContent(justifyContent.toDto());
		return uiFlexContainer;
	}

	public void addComponent(org.teamapps.ux.component.Component component) {
		this.components.add(component);
		sendCommandIfRendered(() -> new DtoFlexContainer.SetComponentsCommand(components.stream().map(c -> c.createClientReference()).toList()));
	}

	public void addComponent(org.teamapps.ux.component.Component component, FlexSizingPolicy sizingPolicy) {
		component.setCssStyle("flex", sizingPolicy.toCssValue());
		addComponent(component);
	}

	public void removeComponent(org.teamapps.ux.component.Component component) {
		this.components.remove(component);
		sendCommandIfRendered(() -> new DtoFlexContainer.SetComponentsCommand(components.stream().map(c -> c.createClientReference()).toList()));
	}

	public void removeAllComponents() {
		this.components.clear();
		sendCommandIfRendered(() -> new DtoFlexContainer.SetComponentsCommand(List.of()));
	}

	public void setComponents(List<Component> components) {
		this.components = new ArrayList<>(components);
		sendCommandIfRendered(() -> new DtoFlexContainer.SetComponentsCommand(components.stream().map(c -> c.createClientReference()).toList()));
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {

	}

	public CssFlexDirection getFlexDirection() {
		return flexDirection;
	}

	public void setFlexDirection(CssFlexDirection flexDirection) {
		this.flexDirection = flexDirection;
		sendCommandIfRendered(() -> new DtoFlexContainer.SetFlexDirectionCommand(flexDirection.toDto()));
	}

	public CssAlignItems getAlignItems() {
		return alignItems;
	}

	public void setAlignItems(CssAlignItems alignItems) {
		this.alignItems = alignItems;
		sendCommandIfRendered(() -> new DtoFlexContainer.SetAlignItemsCommand(alignItems.toDto()));

	}

	public CssJustifyContent getJustifyContent() {
		return justifyContent;
	}

	public void setJustifyContent(CssJustifyContent justifyContent) {
		this.justifyContent = justifyContent;
		sendCommandIfRendered(() -> new DtoFlexContainer.SetJustifyContentCommand(justifyContent.toDto()));
	}

	public List<org.teamapps.ux.component.Component> getComponents() {
		return Collections.unmodifiableList(components);
	}
}
