/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component.toolbar;

import org.teamapps.common.format.Color;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.AbstractUiToolContainer;
import org.teamapps.dto.UiDropDownButtonClickInfo;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiToolbar;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractToolContainer extends AbstractComponent {
	public final Event<ToolbarButtonClickEventData> onButtonClick = new Event<>();
	protected List<ToolbarButtonGroup> buttonGroups = new ArrayList<>();

	private Template buttonTemplate = BaseTemplate.TOOLBAR_BUTTON;
	private PropertyProvider propertyProvider = new BeanPropertyExtractor<>();

	public AbstractToolContainer() {
		super();
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case ABSTRACT_UI_TOOL_CONTAINER_TOOLBAR_BUTTON_CLICK: {
				UiToolbar.ToolbarButtonClickEvent clickEvent = (UiToolbar.ToolbarButtonClickEvent) event;
				ToolbarButton button = getButtonByClientId(clickEvent.getGroupId(), clickEvent.getButtonId());
				if (button != null && button.isVisible()) {
					UiDropDownButtonClickInfo uiDropDownButtonClickInfo = clickEvent.getDropDownClickInfo();
					if (uiDropDownButtonClickInfo != null && uiDropDownButtonClickInfo.getIsOpening() && !uiDropDownButtonClickInfo.getIsContentSet()) {
						Component dropdownComponent = button.getDropDownComponent();
						if (dropdownComponent != null) {
							getSessionContext().queueCommand(new UiToolbar.SetDropDownComponentCommand(getId(), clickEvent.getGroupId(),
									((AbstractUiToolContainer.ToolbarButtonClickEvent) event).getButtonId(), dropdownComponent.createUiReference()));
						}
					}
					button.onClick.fire(clickEvent);
					DropDownButtonClickInfo dropDownButtonClickInfo = uiDropDownButtonClickInfo != null ? new DropDownButtonClickInfo(uiDropDownButtonClickInfo.getIsOpening(),
							uiDropDownButtonClickInfo.getIsContentSet()) : null;
					onButtonClick.fire(new ToolbarButtonClickEventData(button, dropDownButtonClickInfo));
				}
				break;
			}
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
		queueCommandIfRendered(() -> new UiToolbar.AddButtonGroupCommand(getId(), buttonGroup.createUiToolbarButtonGroup(), buttonGroup.isRightSide()));
		return buttonGroup;
	}

	public void removeAllToolbarButtonGroups() {
		List.copyOf(buttonGroups).forEach(this::removeToolbarButtonGroup);
	}

	public void removeToolbarButtonGroup(ToolbarButtonGroup group) {
		buttonGroups.remove(group);
		queueCommandIfRendered(() -> new UiToolbar.RemoveButtonGroupCommand(getId(), group.getClientId()));
	}

	protected void handleGroupVisibilityChange(String groupId, boolean visible) {
		queueCommandIfRendered(() -> new UiToolbar.SetButtonGroupVisibleCommand(this.getId(), groupId, visible));
	}

	protected void handleButtonVisibilityChange(String groupClientId, String buttonClientId, boolean visible) {
		queueCommandIfRendered(() -> new UiToolbar.SetButtonVisibleCommand(this.getId(), groupClientId, buttonClientId, visible));
	}

	protected void handleButtonColorChange(String groupClientId, String buttonClientId, Color backgroundColor, Color hoverBackgroundColor) {
		queueCommandIfRendered(() -> new UiToolbar.SetButtonColorsCommand(this.getId(), groupClientId, buttonClientId, backgroundColor != null ? backgroundColor.toHtmlColorString() : null,
				hoverBackgroundColor != null ? hoverBackgroundColor.toHtmlColorString() : null));
	}

	protected void handleAddButton(ToolbarButtonGroup group, ToolbarButton button, String neighborButtonId, boolean beforeNeighbor) {
		queueCommandIfRendered(() -> new UiToolbar.AddButtonCommand(getId(), group.getClientId(), button.createUiToolbarButton(), neighborButtonId, beforeNeighbor));
	}

	protected void handleButtonRemoved(ToolbarButtonGroup group, ToolbarButton button) {
		queueCommandIfRendered(() -> new UiToolbar.RemoveButtonCommand(getId(), group.getClientId(), button.getClientId()));
	}

	protected void handleButtonSetDropDownComponent(ToolbarButtonGroup group, ToolbarButton button, Component component) {
		queueCommandIfRendered(() -> new UiToolbar.SetDropDownComponentCommand(getId(), group.getClientId(), button.getClientId(), component.createUiReference()));
	}

	protected void handleCloseDropdown(ToolbarButtonGroup group, ToolbarButton button) {
		queueCommandIfRendered(() -> new UiToolbar.CloseDropDownCommand(getId(), group.getClientId(), button.getClientId()));
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
