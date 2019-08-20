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
package org.teamapps.ux.component.toolbar;

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.AbstractUiToolContainer;
import org.teamapps.dto.UiDropDownButtonClickInfo;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiToolbar;
import org.teamapps.event.Event;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractToolContainer extends AbstractComponent {
	public final Event<ToolbarButtonClickEventData> onButtonClick = new Event<>();
	public final Event<ToolbarButton> onDropDownItemClick = new Event<>();
	protected List<ToolbarButtonGroup> toolbarButtonGroups = new ArrayList<>();

	private Template buttonTemplate = BaseTemplate.TOOLBAR_BUTTON;
	private PropertyExtractor propertyExtractor = new BeanPropertyExtractor<>();

	public AbstractToolContainer() {
		super();
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case ABSTRACT_UI_TOOL_CONTAINER_TOOLBAR_BUTTON_CLICK: {
				UiToolbar.ToolbarButtonClickEvent clickEvent = (UiToolbar.ToolbarButtonClickEvent) event;
				ToolbarButton button = getButtonByClientId(clickEvent.getGroupId(), clickEvent.getButtonId());
				if (button != null) {
					UiDropDownButtonClickInfo uiDropDownButtonClickInfo = clickEvent.getDropDownClickInfo();
					if (uiDropDownButtonClickInfo != null && uiDropDownButtonClickInfo.getIsOpening() && !uiDropDownButtonClickInfo.getIsContentSet()) {
						Component dropdownComponent = button.getDropDownComponent();
						if (dropdownComponent != null) {
							getSessionContext().queueCommand(new UiToolbar.SetDropDownComponentCommand(getId(), clickEvent.getGroupId(), ((AbstractUiToolContainer.ToolbarButtonClickEvent) event).getButtonId(), dropdownComponent.createUiComponentReference()));
						}
					}
					button.onClick.fire(clickEvent);
					DropDownButtonClickInfo dropDownButtonClickInfo = uiDropDownButtonClickInfo != null ? new DropDownButtonClickInfo(uiDropDownButtonClickInfo.getIsOpening(),
							uiDropDownButtonClickInfo.getIsContentSet()) : null;
					onButtonClick.fire(new ToolbarButtonClickEventData(button, dropDownButtonClickInfo));
				}
				break;
			}
			case ABSTRACT_UI_TOOL_CONTAINER_TOOLBAR_DROP_DOWN_ITEM_CLICK: {
				AbstractUiToolContainer.ToolbarDropDownItemClickEvent downItemClickEvent = (UiToolbar.ToolbarDropDownItemClickEvent) event;
				ToolbarButton button = getButtonByClientId(downItemClickEvent.getGroupId(), downItemClickEvent.getButtonId());
				if (button != null) {
					button.onDropDownItemClick.fire(downItemClickEvent);
					onDropDownItemClick.fire(button);
				}
				break;
			}
		}
	}

	private ToolbarButton getButtonByClientId(String groupId, String buttonId) {
		return toolbarButtonGroups.stream()
				.filter(group -> Objects.equals(group.getClientId(), groupId))
				.flatMap(group -> group.getButtons().stream())
				.filter(button -> Objects.equals(button.getClientId(), buttonId))
				.findAny().orElse(null);
	}

	public boolean isEmpty() {
		return toolbarButtonGroups.isEmpty();
	}

	public ToolbarButtonGroup addButtonGroup(ToolbarButtonGroup buttonGroup) {
		toolbarButtonGroups.add(buttonGroup);
		buttonGroup.setToolContainer(this);
		queueCommandIfRendered(() -> new UiToolbar.AddButtonGroupCommand(getId(), buttonGroup.createUiToolbarButtonGroup()));
		return buttonGroup;
	}

	public void removeAllToolbarButtonGroups() {
		toolbarButtonGroups.stream()
				.collect(Collectors.toList())
				.forEach(this::removeToolbarButtonGroup);
	}

	public void removeToolbarButtonGroup(ToolbarButtonGroup group) {
		toolbarButtonGroups.remove(group);
		queueCommandIfRendered(() -> new UiToolbar.RemoveButtonGroupCommand(getId(), group.getClientId()));
	}

	protected void handleGroupVisibilityChange(String groupId, boolean visible) {
		queueCommandIfRendered(() -> new UiToolbar.SetButtonGroupVisibleCommand(this.getId(), groupId, visible));
	}

	protected void handleButtonVisibilityChange(String groupClientId, String buttonClientId, boolean visible) {
		queueCommandIfRendered(() -> new UiToolbar.SetButtonVisibleCommand(this.getId(), groupClientId, buttonClientId, visible));
	}

	protected void handleButtonColorChange(String groupClientId, String buttonClientId, Color backgroundColor, Color hoverBackgroundColor) {
		queueCommandIfRendered(() -> new UiToolbar.SetButtonColorsCommand(this.getId(), groupClientId, buttonClientId, UiUtil.createUiColor(backgroundColor), UiUtil.createUiColor(hoverBackgroundColor)));
	}

	protected void handleAddButton(ToolbarButtonGroup group, ToolbarButton button, String neighborButtonId, boolean beforeNeighbor) {
		queueCommandIfRendered(() -> new UiToolbar.AddButtonCommand(getId(), group.getClientId(), button.createUiToolbarButton(), neighborButtonId, beforeNeighbor));
	}

	protected void handleButtonRemoved(ToolbarButtonGroup group, ToolbarButton button) {
		queueCommandIfRendered(() -> new UiToolbar.RemoveButtonCommand(getId(), group.getClientId(), button.getClientId()));
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.setCssStyle("> .background-color-div", "background-color", backgroundColor.toHtmlColorString());
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}

	public List<ToolbarButtonGroup> getToolbarButtonGroups() {
		return toolbarButtonGroups;
	}

	public Template getButtonTemplate() {
		return buttonTemplate;
	}

	public void setButtonTemplate(Template buttonTemplate) {
		this.buttonTemplate = buttonTemplate;
	}

	public PropertyExtractor getPropertyExtractor() {
		return propertyExtractor;
	}

	public void setPropertyExtractor(PropertyExtractor propertyExtractor) {
		this.propertyExtractor = propertyExtractor;
	}
}
