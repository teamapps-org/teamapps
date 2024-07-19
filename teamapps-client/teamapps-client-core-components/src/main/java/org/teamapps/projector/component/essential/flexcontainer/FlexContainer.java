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
package org.teamapps.projector.component.essential.flexcontainer;

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.essential.CoreComponentLibrary;
import org.teamapps.projector.component.essential.dto.DtoFlexContainer;
import org.teamapps.projector.component.essential.dto.DtoFlexContainerClientObjectChannel;
import org.teamapps.projector.component.essential.dto.DtoFlexContainerEventHandler;
import org.teamapps.projector.format.AlignItems;
import org.teamapps.projector.format.FlexDirection;
import org.teamapps.projector.format.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class FlexContainer extends AbstractComponent implements DtoFlexContainerEventHandler {

	private final DtoFlexContainerClientObjectChannel clientObjectChannel = new DtoFlexContainerClientObjectChannel(getClientObjectChannel());

	private List<Component> components = new ArrayList<>();
	private FlexDirection flexDirection = FlexDirection.ROW;
	private AlignItems alignItems = AlignItems.STRETCH;
	private JustifyContent justifyContent = JustifyContent.START;

	@Override
	public DtoFlexContainer createConfig() {
		DtoFlexContainer uiFlexContainer = new DtoFlexContainer();
		mapAbstractUiComponentProperties(uiFlexContainer);
		uiFlexContainer.setComponents(components.stream()
				.map(c -> c)
				.collect(Collectors.toList()));
		uiFlexContainer.setFlexDirection(flexDirection);
		uiFlexContainer.setAlignItems(alignItems);
		uiFlexContainer.setJustifyContent(justifyContent);
		return uiFlexContainer;
	}

	public void addComponent(Component component) {
		this.components.add(component);
		clientObjectChannel.setComponents(List.copyOf(components));
	}

	public void addComponent(Component component, FlexSizingPolicy sizingPolicy) {
		component.setCssStyle("flex", sizingPolicy.toCssValue());
		addComponent(component);
	}

	public void removeComponent(Component component) {
		this.components.remove(component);
		clientObjectChannel.setComponents(List.copyOf(components));
	}

	public void removeAllComponents() {
		this.components.clear();
		clientObjectChannel.setComponents(List.of());
	}

	public void setComponents(List<Component> components) {
		this.components = new ArrayList<>(components);
		clientObjectChannel.setComponents(List.copyOf(components));
	}

	public FlexDirection getFlexDirection() {
		return flexDirection;
	}

	public void setFlexDirection(FlexDirection flexDirection) {
		this.flexDirection = flexDirection;
		clientObjectChannel.setFlexDirection(flexDirection);
	}

	public AlignItems getAlignItems() {
		return alignItems;
	}

	public void setAlignItems(AlignItems alignItems) {
		this.alignItems = alignItems;
		clientObjectChannel.setAlignItems(alignItems);

	}

	public JustifyContent getJustifyContent() {
		return justifyContent;
	}

	public void setJustifyContent(JustifyContent justifyContent) {
		this.justifyContent = justifyContent;
		clientObjectChannel.setJustifyContent(justifyContent);
	}

	public List<Component> getComponents() {
		return Collections.unmodifiableList(components);
	}
}
