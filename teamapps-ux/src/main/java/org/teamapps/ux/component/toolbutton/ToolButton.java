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
package org.teamapps.ux.component.toolbutton;

import org.teamapps.projector.dto.DtoComponent;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoToolButton;
import org.teamapps.projector.clientobject.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.projector.clientobject.AbstractComponent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.projector.clientobject.ProjectorComponent;

import java.util.function.Supplier;

@ProjectorComponent(library = CoreComponentLibrary.class)
public class ToolButton extends AbstractComponent {

	public final ProjectorEvent<Void> onDropDownOpened = createProjectorEventBoundToUiEvent(DtoToolButton.DropDownOpenedEvent.TYPE_ID);

	private Icon<?, ?> icon;
	private Integer iconSize = null; // null = default defined by CSS
	private String caption;
	private String popoverText;
	private boolean grayOutIfNotHovered;

	private boolean openDropDownIfNotSet = false;
	private Component dropDownComponent;
	private Integer minDropDownWidth = 300;
	private Integer minDropDownHeight = 300;

	public final ProjectorEvent<Void> onClick = createProjectorEventBoundToUiEvent(DtoToolButton.ClickedEvent.TYPE_ID);

	public ToolButton(Icon<?, ?> icon) {
		this(icon, null, null);
	}

	public ToolButton(Icon<?, ?> icon, String popoverText) {
		this(icon, popoverText, null);
	}

	public ToolButton(Icon<?, ?> icon, String popoverText, Component dropDownComponent) {
		super();
		this.icon = icon;
		this.popoverText = popoverText;
		this.dropDownComponent = dropDownComponent;
	}

	public boolean isGrayOutIfNotHovered() {
		return grayOutIfNotHovered;
	}

	public void setGrayOutIfNotHovered(boolean grayOutIfNotHovered) {
		this.grayOutIfNotHovered = grayOutIfNotHovered;
	}

	@Override
	public DtoComponent createConfig() {
		String icon = getSessionContext().resolveIcon(this.icon);
		DtoToolButton uiToolButton = new DtoToolButton(icon, popoverText);
		uiToolButton.setIconSize(iconSize);
		uiToolButton.setCaption(caption);
		mapAbstractUiComponentProperties(uiToolButton);
		uiToolButton.setGrayOutIfNotHovered(grayOutIfNotHovered);
		uiToolButton.setDropDownComponent(this.dropDownComponent != null ? this.dropDownComponent.createClientReference() : null);
		uiToolButton.setMinDropDownWidth(minDropDownWidth != null ? minDropDownWidth : 0);
		uiToolButton.setMinDropDownHeight(minDropDownHeight != null ? minDropDownHeight : 0);
		uiToolButton.setMinDropDownHeight(minDropDownHeight);
		return uiToolButton;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoToolButton.ClickedEvent.TYPE_ID -> {
				var e = event.as(DtoToolButton.ClickedEventWrapper.class);
				this.onClick.fire(null);
			}
			case DtoToolButton.DropDownOpenedEvent.TYPE_ID -> {
				var e = event.as(DtoToolButton.DropDownOpenedEventWrapper.class);
				this.onDropDownOpened.fire(null);
			}
		}
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		getClientObjectChannel().sendCommandIfRendered(new DtoToolButton.SetIconCommand(getSessionContext().resolveIcon(icon)), null);
	}

	public Integer getIconSize() {
		return iconSize;
	}

	public void setIconSize(Integer iconSize) {
		this.iconSize = iconSize;
		getClientObjectChannel().sendCommandIfRendered(new DtoToolButton.SetIconSizeCommand(iconSize), null);
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		getClientObjectChannel().sendCommandIfRendered(new DtoToolButton.SetCaptionCommand(caption), null);
	}

	public String getPopoverText() {
		return popoverText;
	}

	public void setPopoverText(String popoverText) {
		this.popoverText = popoverText;
		getClientObjectChannel().sendCommandIfRendered(new DtoToolButton.SetPopoverTextCommand(popoverText), null);
	}

	public boolean isOpenDropDownIfNotSet() {
		return openDropDownIfNotSet;
	}

	public void setOpenDropDownIfNotSet(boolean openDropDownIfNotSet) {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
		getClientObjectChannel().sendCommandIfRendered(new DtoToolButton.SetOpenDropDownIfNotSetCommand(openDropDownIfNotSet), null);
	}

	public Component getDropDownComponent() {
		return dropDownComponent;
	}

	public void setDropDownComponent(Component dropDownComponent) {
		this.dropDownComponent = dropDownComponent;
		getClientObjectChannel().sendCommandIfRendered(new DtoToolButton.SetDropDownComponentCommand(dropDownComponent != null ? dropDownComponent.createClientReference() : null), null);
	}

	public Integer getMinDropDownWidth() {
		return minDropDownWidth;
	}

	public void setMinDropDownWidth(Integer minDropDownWidth) {
		this.minDropDownWidth = minDropDownWidth;
		getClientObjectChannel().sendCommandIfRendered(new DtoToolButton.SetDropDownSizeCommand(minDropDownWidth, minDropDownHeight), null);
	}

	public Integer getMinDropDownHeight() {
		return minDropDownHeight;
	}

	public void setMinDropDownHeight(Integer minDropDownHeight) {
		this.minDropDownHeight = minDropDownHeight;
		getClientObjectChannel().sendCommandIfRendered(new DtoToolButton.SetDropDownSizeCommand(minDropDownWidth, minDropDownHeight), null);
	}

	public void closeDropDown() {
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) DtoToolButton.CloseDropDownCommand::new).get(), null);
	}

}
