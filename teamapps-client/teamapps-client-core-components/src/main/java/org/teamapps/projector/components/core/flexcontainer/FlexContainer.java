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
package org.teamapps.projector.components.core.flexcontainer;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.dto.DtoFlexContainer;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.format.AlignItems;
import org.teamapps.projector.format.FlexDirection;
import org.teamapps.projector.format.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class FlexContainer extends AbstractComponent {

	private List<Component> components = new ArrayList<>();
	private FlexDirection flexDirection = FlexDirection.ROW;
	private AlignItems alignItems = AlignItems.STRETCH;
	private JustifyContent justifyContent = JustifyContent.START;

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

	public void addComponent(Component component) {
		this.components.add(component);
		getClientObjectChannel().sendCommandIfRendered(new DtoFlexContainer.SetComponentsCommand(components.stream().map(c -> c.createClientReference()).toList()), null);
	}

	public void addComponent(Component component, FlexSizingPolicy sizingPolicy) {
		component.setCssStyle("flex", sizingPolicy.toCssValue());
		addComponent(component);
	}

	public void removeComponent(Component component) {
		this.components.remove(component);
		getClientObjectChannel().sendCommandIfRendered(new DtoFlexContainer.SetComponentsCommand(components.stream().map(c -> c.createClientReference()).toList()), null);
	}

	public void removeAllComponents() {
		this.components.clear();
		getClientObjectChannel().sendCommandIfRendered(new DtoFlexContainer.SetComponentsCommand(List.of()), null);
	}

	public void setComponents(List<Component> components) {
		this.components = new ArrayList<>(components);
		getClientObjectChannel().sendCommandIfRendered(new DtoFlexContainer.SetComponentsCommand(components.stream().map(c -> c.createClientReference()).toList()), null);
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {

	}

	public FlexDirection getFlexDirection() {
		return flexDirection;
	}

	public void setFlexDirection(FlexDirection flexDirection) {
		this.flexDirection = flexDirection;
		getClientObjectChannel().sendCommandIfRendered(new DtoFlexContainer.SetFlexDirectionCommand(flexDirection.toDto()), null);
	}

	public AlignItems getAlignItems() {
		return alignItems;
	}

	public void setAlignItems(AlignItems alignItems) {
		this.alignItems = alignItems;
		getClientObjectChannel().sendCommandIfRendered(new DtoFlexContainer.SetAlignItemsCommand(alignItems.toDto()), null);

	}

	public JustifyContent getJustifyContent() {
		return justifyContent;
	}

	public void setJustifyContent(JustifyContent justifyContent) {
		this.justifyContent = justifyContent;
		getClientObjectChannel().sendCommandIfRendered(new DtoFlexContainer.SetJustifyContentCommand(justifyContent.toDto()), null);
	}

	public List<Component> getComponents() {
		return Collections.unmodifiableList(components);
	}
}
