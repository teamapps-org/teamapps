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
package org.teamapps.ux.component.toolbutton;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiToolButton;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

public class ToolButton extends AbstractComponent {

	public final Event<Void> onDropDownOpened = new Event<>();

	private Icon<?, ?> icon;
	private Integer iconSize = null; // null = default defined by CSS
	private String caption;
	private String title;
	private boolean grayOutIfNotHovered;

	private boolean openDropDownIfNotSet = false;
	private Component dropDownComponent;
	private Integer minDropDownWidth = 300;
	private Integer minDropDownHeight = 300;

	public final Event<Void> onClick = new Event<>();

	public ToolButton(Icon<?, ?> icon) {
		this(icon, null, null);
	}

	public ToolButton(Icon<?, ?> icon, String title) {
		this(icon, title, null);
	}

	public ToolButton(Icon<?, ?> icon, String title, Component dropDownComponent) {
		super();
		this.icon = icon;
		this.title = title;
		this.dropDownComponent = dropDownComponent;
	}

	public boolean isGrayOutIfNotHovered() {
		return grayOutIfNotHovered;
	}

	public void setGrayOutIfNotHovered(boolean grayOutIfNotHovered) {
		this.grayOutIfNotHovered = grayOutIfNotHovered;
	}

	@Override
	public UiComponent createUiComponent() {
		String icon = getSessionContext().resolveIcon(this.icon);
		UiToolButton uiToolButton = new UiToolButton(icon, title);
		uiToolButton.setIconSize(iconSize);
		uiToolButton.setCaption(caption);
		mapAbstractUiComponentProperties(uiToolButton);
		uiToolButton.setGrayOutIfNotHovered(grayOutIfNotHovered);
		uiToolButton.setDropDownComponent(this.dropDownComponent != null ? this.dropDownComponent.createUiReference() : null);
		uiToolButton.setMinDropDownWidth(minDropDownWidth != null ? minDropDownWidth : 0);
		uiToolButton.setMinDropDownHeight(minDropDownHeight != null ? minDropDownHeight : 0);
		uiToolButton.setMinDropDownHeight(minDropDownHeight);
		uiToolButton.setTitle(title);
		return uiToolButton;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TOOL_BUTTON_CLICKED: {
				if (isVisible()) {
					this.onClick.fire(null);
				}
				break;
			}
			case UI_BUTTON_DROP_DOWN_OPENED:
				if (isVisible()) {
					this.onDropDownOpened.fire(null);
				}
				break;
		}
	}

	public Icon<?, ?> getIcon() {
		return icon;
	}

	public void setIcon(Icon<?, ?> icon) {
		this.icon = icon;
		queueCommandIfRendered(() -> new UiToolButton.SetIconCommand(getId(), getSessionContext().resolveIcon(icon)));
	}

	public Integer getIconSize() {
		return iconSize;
	}

	public void setIconSize(Integer iconSize) {
		this.iconSize = iconSize;
		queueCommandIfRendered(() -> new UiToolButton.SetIconSizeCommand(getId(), iconSize));
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		queueCommandIfRendered(() -> new UiToolButton.SetCaptionCommand(getId(), caption));
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		queueCommandIfRendered(() -> new UiToolButton.SetTitleCommand(getId(), title));
	}

	public boolean isOpenDropDownIfNotSet() {
		return openDropDownIfNotSet;
	}

	public void setOpenDropDownIfNotSet(boolean openDropDownIfNotSet) {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
		queueCommandIfRendered(() -> new UiToolButton.SetOpenDropDownIfNotSetCommand(getId(), openDropDownIfNotSet));
	}

	public Component getDropDownComponent() {
		return dropDownComponent;
	}

	public void setDropDownComponent(Component dropDownComponent) {
		this.dropDownComponent = dropDownComponent;
		queueCommandIfRendered(() -> new UiToolButton.SetDropDownComponentCommand(getId(), dropDownComponent != null ? dropDownComponent.createUiReference() : null));
	}

	public Integer getMinDropDownWidth() {
		return minDropDownWidth;
	}

	public void setMinDropDownWidth(Integer minDropDownWidth) {
		this.minDropDownWidth = minDropDownWidth;
		queueCommandIfRendered(() -> new UiToolButton.SetDropDownSizeCommand(getId(), minDropDownWidth, minDropDownHeight));
	}

	public Integer getMinDropDownHeight() {
		return minDropDownHeight;
	}

	public void setMinDropDownHeight(Integer minDropDownHeight) {
		this.minDropDownHeight = minDropDownHeight;
		queueCommandIfRendered(() -> new UiToolButton.SetDropDownSizeCommand(getId(), minDropDownWidth, minDropDownHeight));
	}

	public void closeDropDown() {
		queueCommandIfRendered(() -> new UiToolButton.CloseDropDownCommand(getId()));
	}

}
