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
package org.teamapps.ux.component.flexcontainer;

import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiFlexContainer;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.css.CssAlignItems;
import org.teamapps.ux.css.CssFlexDirection;
import org.teamapps.ux.css.CssJustifyContent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlexContainer extends AbstractComponent {

	private List<Component> components = new ArrayList<>();
	private CssFlexDirection flexDirection = CssFlexDirection.ROW;
	private CssAlignItems alignItems = CssAlignItems.STRETCH;
	private CssJustifyContent justifyContent = CssJustifyContent.FLEX_START;

	@Override
	public UiFlexContainer createUiComponent() {
		UiFlexContainer uiFlexContainer = new UiFlexContainer(getId());
		mapAbstractUiComponentProperties(uiFlexContainer);
		uiFlexContainer.setComponents(components.stream()
				.map(c -> c.createUiComponentReference())
				.collect(Collectors.toList()));
		uiFlexContainer.setFlexDirection(flexDirection.toUiCssFlexDirection());
		uiFlexContainer.setAlignItems(alignItems.toCssAlignItems());
		uiFlexContainer.setJustifyContent(justifyContent.toUiCssJustifyContent());
		return uiFlexContainer;
	}

	public void addComponent(Component component) {
		this.components.add(component);
		queueCommandIfRendered(() -> new UiFlexContainer.AddComponentCommand(getId(), component.createUiComponentReference()));
	}

	public void addComponent(Component component, FlexSizingPolicy sizingPolicy) {
		component.setCssStyle("flex", sizingPolicy.toCssValue());
		addComponent(component);
	}

	public void removeComponent(Component component) {
		this.components.remove(component);
		queueCommandIfRendered(() -> new UiFlexContainer.RemoveComponentCommand(getId(), component.createUiComponentReference()));
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

	public CssFlexDirection getFlexDirection() {
		return flexDirection;
	}

	public void setFlexDirection(CssFlexDirection flexDirection) {
		this.flexDirection = flexDirection;
		reRenderIfRendered(); // TODO
	}

	public CssAlignItems getAlignItems() {
		return alignItems;
	}

	public void setAlignItems(CssAlignItems alignItems) {
		this.alignItems = alignItems;
		reRenderIfRendered(); // TODO
	}

	public CssJustifyContent getJustifyContent() {
		return justifyContent;
	}

	public void setJustifyContent(CssJustifyContent justifyContent) {
		this.justifyContent = justifyContent;
		reRenderIfRendered(); // TODO
	}

	@Override
	protected void doDestroy() {

	}
}
