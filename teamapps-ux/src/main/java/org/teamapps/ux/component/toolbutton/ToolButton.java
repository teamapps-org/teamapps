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

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEventWrapper;
import org.teamapps.dto.UiToolButton;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class ToolButton extends AbstractComponent {

	public final ProjectorEvent<Void> onDropDownOpened = createProjectorEventBoundToUiEvent(UiToolButton.DropDownOpenedEvent.TYPE_ID);

	private Icon icon;
	private String caption;
	private String popoverText;
	private boolean grayOutIfNotHovered;

	private boolean openDropDownIfNotSet = false;
	private Component dropDownComponent;
	private Integer minDropDownWidth = 300;
	private Integer minDropDownHeight = 300;

	public final ProjectorEvent<Void> onClick = createProjectorEventBoundToUiEvent(UiToolButton.ClickedEvent.TYPE_ID);

	public ToolButton(Icon icon) {
		this(icon, null, null);
	}

	public ToolButton(Icon icon, String popoverText) {
		this(icon, popoverText, null);
	}

	public ToolButton(Icon icon, String popoverText, Component dropDownComponent) {
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
	public UiComponent createUiClientObject() {
		String icon = getSessionContext().resolveIcon(this.icon);
		UiToolButton uiToolButton = new UiToolButton(icon, popoverText);
		uiToolButton.setCaption(caption);
		mapAbstractUiComponentProperties(uiToolButton);
		uiToolButton.setGrayOutIfNotHovered(grayOutIfNotHovered);
		uiToolButton.setDropDownComponent(this.dropDownComponent != null ? this.dropDownComponent.createUiReference() : null);
		uiToolButton.setMinDropDownWidth(minDropDownWidth != null ? minDropDownWidth : 0);
		uiToolButton.setMinDropDownHeight(minDropDownHeight != null ? minDropDownHeight : 0);
		uiToolButton.setMinDropDownHeight(minDropDownHeight);
		return uiToolButton;
	}

	@Override
	public void handleUiEvent(UiEventWrapper event) {
		switch (event.getTypeId()) {
			case UiToolButton.ClickedEvent.TYPE_ID -> {
				var e = event.as(UiToolButton.ClickedEventWrapper.class);
				this.onClick.fire(null);
			}
			case UiToolButton.DropDownOpenedEvent.TYPE_ID -> {
				var e = event.as(UiToolButton.DropDownOpenedEventWrapper.class);
				this.onDropDownOpened.fire(null);
			}
		}
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		sendCommandIfRendered(() -> new UiToolButton.SetIconCommand(getSessionContext().resolveIcon(icon)));
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		// TODO handle change after rendered...
	}

	public String getPopoverText() {
		return popoverText;
	}

	public void setPopoverText(String popoverText) {
		this.popoverText = popoverText;
		sendCommandIfRendered(() -> new UiToolButton.SetPopoverTextCommand(popoverText));
	}

	public boolean isOpenDropDownIfNotSet() {
		return openDropDownIfNotSet;
	}

	public void setOpenDropDownIfNotSet(boolean openDropDownIfNotSet) {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
		sendCommandIfRendered(() -> new UiToolButton.SetOpenDropDownIfNotSetCommand(openDropDownIfNotSet));
	}

	public Component getDropDownComponent() {
		return dropDownComponent;
	}

	public void setDropDownComponent(Component dropDownComponent) {
		this.dropDownComponent = dropDownComponent;
		sendCommandIfRendered(() -> new UiToolButton.SetDropDownComponentCommand(dropDownComponent != null ? dropDownComponent.createUiReference() : null));
	}

	public Integer getMinDropDownWidth() {
		return minDropDownWidth;
	}

	public void setMinDropDownWidth(Integer minDropDownWidth) {
		this.minDropDownWidth = minDropDownWidth;
		sendCommandIfRendered(() -> new UiToolButton.SetDropDownSizeCommand(minDropDownWidth, minDropDownHeight));
	}

	public Integer getMinDropDownHeight() {
		return minDropDownHeight;
	}

	public void setMinDropDownHeight(Integer minDropDownHeight) {
		this.minDropDownHeight = minDropDownHeight;
		sendCommandIfRendered(() -> new UiToolButton.SetDropDownSizeCommand(minDropDownWidth, minDropDownHeight));
	}

}
