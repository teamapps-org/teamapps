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
package org.teamapps.projector.components.core.toolbar;

import org.teamapps.common.format.Color;
import org.teamapps.projector.dto.DtoAbstractToolContainer;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoToolbar;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.session.SessionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractToolContainer extends AbstractComponent {
	public final ProjectorEvent<ToolbarButtonClickEventData> onButtonClick = createProjectorEventBoundToUiEvent(DtoAbstractToolContainer.ToolbarButtonClickEvent.TYPE_ID);
	protected List<ToolbarButtonGroup> buttonGroups = new ArrayList<>();

	private Template buttonTemplate = BaseTemplate.TOOLBAR_BUTTON;
	private PropertyProvider<?> propertyProvider = new BeanPropertyExtractor<>();

	public AbstractToolContainer() {
		super();
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoAbstractToolContainer.ToolbarButtonClickEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoAbstractToolContainer.ToolbarButtonClickEventWrapper.class);
				ToolbarButton button = getButtonByClientId(clickEvent.getGroupId(), clickEvent.getButtonId());
				if (button != null) {
					var uiDropDownButtonClickInfo = clickEvent.getDropDownClickInfo();
					if (uiDropDownButtonClickInfo != null && uiDropDownButtonClickInfo.getIsOpening() && !uiDropDownButtonClickInfo.getIsContentSet()) {
						Component dropdownComponent = button.getDropDownComponent();
						if (dropdownComponent != null) {
							SessionContext sessionContext = getSessionContext();
							sessionContext.sendCommandIfRendered(this, new DtoToolbar.SetDropDownComponentCommand(clickEvent.getGroupId(),
														clickEvent.getButtonId(), dropdownComponent.createClientReference()), null);
						}
					}
					button.onClick.fire(new ToolbarButtonClickEvent(clickEvent.getDropDownClickInfo().getIsOpening(), clickEvent.getDropDownClickInfo().getIsContentSet()));
					DropDownButtonClickInfo dropDownButtonClickInfo = uiDropDownButtonClickInfo != null ? new DropDownButtonClickInfo(uiDropDownButtonClickInfo.getIsOpening(),
							uiDropDownButtonClickInfo.getIsContentSet()) : null;
					onButtonClick.fire(new ToolbarButtonClickEventData(button, dropDownButtonClickInfo));
				}
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
		getClientObjectChannel().sendCommandIfRendered(new DtoToolbar.AddButtonGroupCommand(buttonGroup.createUiToolbarButtonGroup(), buttonGroup.isRightSide()), null);
		return buttonGroup;
	}

	public void removeAllToolbarButtonGroups() {
		List.copyOf(buttonGroups).forEach(this::removeToolbarButtonGroup);
	}

	public void removeToolbarButtonGroup(ToolbarButtonGroup group) {
		buttonGroups.remove(group);
		getClientObjectChannel().sendCommandIfRendered(new DtoToolbar.RemoveButtonGroupCommand(group.getClientId()), null);
	}

	protected void handleGroupVisibilityChange(String groupId, boolean visible) {
		getClientObjectChannel().sendCommandIfRendered(new DtoToolbar.SetButtonGroupVisibleCommand(groupId, visible), null);
	}

	protected void handleButtonVisibilityChange(String groupClientId, String buttonClientId, boolean visible) {
		getClientObjectChannel().sendCommandIfRendered(new DtoToolbar.SetButtonVisibleCommand(groupClientId, buttonClientId, visible), null);
	}

	protected void handleButtonColorChange(String groupClientId, String buttonClientId, Color backgroundColor, Color hoverBackgroundColor) {
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoToolbar.SetButtonColorsCommand(groupClientId, buttonClientId, backgroundColor != null ? backgroundColor.toHtmlColorString() : null,
				hoverBackgroundColor != null ? hoverBackgroundColor.toHtmlColorString() : null)).get(), null);
	}

	protected void handleAddButton(ToolbarButtonGroup group, ToolbarButton button, String neighborButtonId, boolean beforeNeighbor) {
		getClientObjectChannel().sendCommandIfRendered(new DtoToolbar.AddButtonCommand(group.getClientId(), button.createUiToolbarButton(), neighborButtonId, beforeNeighbor), null);
	}

	protected void handleButtonRemoved(ToolbarButtonGroup group, ToolbarButton button) {
		getClientObjectChannel().sendCommandIfRendered(new DtoToolbar.RemoveButtonCommand(group.getClientId(), button.getClientId()), null);
	}

	protected void handleButtonSetDropDownComponent(ToolbarButtonGroup group, ToolbarButton button, Component component) {
		getClientObjectChannel().sendCommandIfRendered(new DtoToolbar.SetDropDownComponentCommand(group.getClientId(), button.getClientId(), component.createClientReference()), null);
	}

	protected void handleCloseDropdown(ToolbarButtonGroup group, ToolbarButton button) {
		getClientObjectChannel().sendCommandIfRendered(new DtoToolbar.CloseDropDownCommand(group.getClientId(), button.getClientId()), null);
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
