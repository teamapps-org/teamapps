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

import org.teamapps.common.format.Color;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractToolContainer extends AbstractComponent implements DtoAbstractToolContainerEventHandler {

	private final DtoAbstractToolContainerClientObjectChannel clientObjectChannel = new DtoAbstractToolContainerClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<ToolbarButtonClickEventData> onButtonClick = new ProjectorEvent<>(clientObjectChannel::toggleToolbarButtonClickEvent);

	protected List<ToolbarButtonGroup> buttonGroups = new ArrayList<>();
	private Template buttonTemplate = BaseTemplates.TOOLBAR_BUTTON;
	private PropertyProvider<?> propertyProvider = new BeanPropertyExtractor<>();

	public AbstractToolContainer() {
		super();
	}

	@Override
	public void handleToolbarButtonClick(DtoAbstractToolContainer.ToolbarButtonClickEventWrapper e) {
		ToolbarButton button = getButtonByClientId(e.getGroupId(), e.getButtonId());
		if (button != null) {
			if (e.getDropDownClickInfo() != null && e.getDropDownClickInfo().isOpening() && !e.getDropDownClickInfo().isContentSet()) {
				Component dropdownComponent = button.getDropDownComponent();
				clientObjectChannel.setDropDownComponent(e.getGroupId(), e.getButtonId(), dropdownComponent);
			}
			button.onClick.fire(new ToolbarButtonClickEvent(e.getDropDownClickInfo() != null && e.getDropDownClickInfo().isOpening(), e.getDropDownClickInfo() != null && e.getDropDownClickInfo().isContentSet()));
			onButtonClick.fire(new ToolbarButtonClickEventData(button, e.getDropDownClickInfo() != null ? e.getDropDownClickInfo().unwrap() : null));
		}
	}

	private ToolbarButton getButtonByClientId(String groupId, String buttonId) {
		return buttonGroups.stream()
				.filter(group -> Objects.equals(group.getClientId(), groupId))
				.flatMap(group -> group.getButtons().stream())
				.filter(button -> Objects.equals(button.getClientId(), buttonId))
				.findAny().orElse(null);
	}

	public boolean isEmpty() {
		return buttonGroups.isEmpty();
	}

	public ToolbarButtonGroup addButtonGroup(ToolbarButtonGroup buttonGroup) {
		buttonGroups.add(buttonGroup);
		buttonGroup.setToolContainer(this);
		clientObjectChannel.addButtonGroup(buttonGroup.createUiToolbarButtonGroup(), buttonGroup.isRightSide());
		return buttonGroup;
	}

	public void removeAllToolbarButtonGroups() {
		List.copyOf(buttonGroups).forEach(this::removeToolbarButtonGroup);
	}

	public void removeToolbarButtonGroup(ToolbarButtonGroup group) {
		buttonGroups.remove(group);
		clientObjectChannel.removeButtonGroup(group.getClientId());
	}

	protected void handleGroupVisibilityChange(String groupId, boolean visible) {
		clientObjectChannel.setButtonGroupVisible(groupId, visible);
	}

	protected void handleButtonVisibilityChange(String groupClientId, String buttonClientId, boolean visible) {
		clientObjectChannel.setButtonVisible(groupClientId, buttonClientId, visible);
	}

	protected void handleButtonColorChange(String groupClientId, String buttonClientId, Color backgroundColor, Color hoverBackgroundColor) {
		clientObjectChannel.setButtonColors(groupClientId, buttonClientId,
				backgroundColor != null ? backgroundColor.toHtmlColorString() : null,
				hoverBackgroundColor != null ? hoverBackgroundColor.toHtmlColorString() : null
		);
	}

	protected void handleAddButton(ToolbarButtonGroup group, ToolbarButton button, String neighborButtonId, boolean beforeNeighbor) {
		clientObjectChannel.addButton(group.getClientId(), button.createUiToolbarButton(), neighborButtonId, beforeNeighbor);
	}

	protected void handleButtonRemoved(ToolbarButtonGroup group, ToolbarButton button) {
		clientObjectChannel.removeButton(group.getClientId(), button.getClientId());
	}

	protected void handleButtonSetDropDownComponent(ToolbarButtonGroup group, ToolbarButton button, Component component) {
		clientObjectChannel.setDropDownComponent(group.getClientId(), button.getClientId(), component);
	}

	protected void handleCloseDropdown(ToolbarButtonGroup group, ToolbarButton button) {
		clientObjectChannel.closeDropDown(group.getClientId(), button.getClientId());
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.setCssStyle("> .background-color-div", "background-color", backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
	}

	public List<ToolbarButtonGroup> getToolbarButtonGroups() {
		ArrayList<ToolbarButtonGroup> groups = new ArrayList<>();
		groups.addAll(buttonGroups);
		return groups;
	}

	public Template getButtonTemplate() {
		return buttonTemplate;
	}

	public void setButtonTemplate(Template buttonTemplate) {
		this.buttonTemplate = buttonTemplate;
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
}
