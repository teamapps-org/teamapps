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
package org.teamapps.projector.component.core.toolbar;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ToolbarButtonGroup implements Comparable<ToolbarButtonGroup> {

	private AbstractToolContainer toolContainer;
	private String clientId = UUID.randomUUID().toString();

	private final List<ToolbarButton> buttons = new ArrayList<>();
	private ToolbarButtonGroupPosition position;
	private boolean rightSide;

	private boolean visible = true;
	private boolean showGroupSeparator = true;

	private Template buttonTemplate; // if null, will fallback to toolbar's valueExtractor
	private PropertyProvider propertyProvider; // if null, will fallback to toolbar's valueExtractor

	public ToolbarButtonGroup() {
		this(new ArrayList<>(), ToolbarButtonGroupPosition.CENTER);
	}

	public ToolbarButtonGroup(ToolbarButtonGroupPosition position) {
		this(new ArrayList<>(), position);
	}

	public ToolbarButtonGroup(List<ToolbarButton> buttons, ToolbarButtonGroupPosition position) {
		buttons.forEach(this::addButton);
		this.position = position;
	}

	public void setToolContainer(AbstractToolContainer toolContainer) {
		this.toolContainer = toolContainer;
	}

	public void setButtons(List<ToolbarButton> buttons) {
		new ArrayList<>(this.buttons).forEach(button -> removeButton(button));
		buttons.forEach(button -> addButton(button));
	}

	public ToolbarButton addButton(ToolbarButton button) {
		return addButton(button, null, false);
	}

	public ToolbarButton addButton(ToolbarButton button, ToolbarButton neighborButton, boolean beforeNeighbor) {
		buttons.add(button);
		button.setToolbarButtonGroup(this);
		if (toolContainer != null) {
			toolContainer.handleAddButton(this, button, neighborButton != null ? neighborButton.getClientId() : null, beforeNeighbor);
		}
		return button;
	}

	public void removeButton(ToolbarButton button) {
		buttons.remove(button);
		button.setToolbarButtonGroup(null);
		if (toolContainer != null) {
			toolContainer.handleButtonRemoved(this, button);
		}
	}

	protected String getClientId() {
		return clientId;
	}

	public List<ToolbarButton> getButtons() {
		return buttons;
	}

	public ToolbarButtonGroupPosition getPosition() {
		return position;
	}

	public DtoToolbarButtonGroup createDtoToolbarButtonGroup() {
		List<DtoToolbarButton> buttons = this.buttons.stream()
				.map(button -> button.createDtoToolbarButton())
				.collect(Collectors.toList());
		DtoToolbarButtonGroup buttonGroup = new DtoToolbarButtonGroup(clientId, buttons);
		buttonGroup.setVisible(visible);
		buttonGroup.setShowGroupSeparator(showGroupSeparator);
		return buttonGroup;
	}

	public AbstractToolContainer getToolContainer() {
		return toolContainer;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		boolean oldValue = this.visible;
		this.visible = visible;
		if (oldValue != visible && this.toolContainer != null) {
			toolContainer.handleGroupVisibilityChange(this.clientId, visible);
		}
	}

	public boolean isShowGroupSeparator() {
		return showGroupSeparator;
	}

	public void setShowGroupSeparator(boolean showGroupSeparator) {
		this.showGroupSeparator = showGroupSeparator;
	}

	public void setPosition(ToolbarButtonGroupPosition position) {
		this.position = position;
	}

	@Override
	public int compareTo(ToolbarButtonGroup o) {
		return position.compareTo(o.getPosition());
	}

	/*package-private*/ void handleButtonVisibilityChange(String buttonClientId, boolean visible) {
		if (this.toolContainer != null) {
			this.toolContainer.handleButtonVisibilityChange(this.clientId, buttonClientId, visible);
		}
	}

	/*package-private*/ void handleDropDownComponentUpdate(ToolbarButton button, Component component) {
		if (this.toolContainer != null) {
			this.toolContainer.handleButtonSetDropDownComponent(this, button, component);
		}
	}

	/*package-private*/ void handleCloseDropdown(ToolbarButton button) {
		if (this.toolContainer != null) {
			this.toolContainer.handleCloseDropdown(this, button);
		}
	}

	public void handleColorChange(String buttonClientId, Color backgroundColor, Color hoverBackgroundColor) {
		if (this.toolContainer != null) {
			this.toolContainer.handleButtonColorChange(this.clientId, buttonClientId, backgroundColor, hoverBackgroundColor);
		}
	}

	public Template getButtonTemplate() {
		return buttonTemplate;
	}

	public void setButtonTemplate(Template buttonTemplate) {
		this.buttonTemplate = buttonTemplate;
	}

	public Template getAppliedTemplate() {
		return this.buttonTemplate != null ? this.buttonTemplate : this.toolContainer.getButtonTemplate();
	}

	public PropertyProvider getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(PropertyProvider propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void setPropertyExtractor(PropertyExtractor propertyExtractor) {
		this.setPropertyProvider(propertyExtractor);
	}
	public PropertyProvider getAppliedPropertyProvider() {
		return propertyProvider != null ? propertyProvider : toolContainer.getPropertyProvider();
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public boolean isRightSide() {
		return rightSide;
	}

	public void setRightSide(boolean rightSide) {
		this.rightSide = rightSide;
	}
}
