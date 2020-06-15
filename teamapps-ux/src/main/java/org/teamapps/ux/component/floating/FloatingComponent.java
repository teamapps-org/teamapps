/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.component.floating;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiFloatingComponent;
import org.teamapps.event.Event;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;

public class FloatingComponent extends AbstractComponent {

	public final Event<Boolean> onExpandedOrCollapsed = new Event<>();

	private final Component containerComponent;
	private Component contentComponent;
	private int width = -1;
	private int height = -1;
	private int marginX;
	private int marginY;
	private FloatingPosition position;
	private Color backgroundColor = Color.TRANSPARENT;
	private Color expanderHandleColor = Color.WHITE;

	private boolean collapsible;
	private boolean expanded;


	public FloatingComponent(Component containerComponent, Component contentComponent) {
		this.containerComponent = containerComponent;
		this.contentComponent = contentComponent;
	}

	@Override
	public UiComponent createUiComponent() {
		UiFloatingComponent ui = new UiFloatingComponent();
		mapAbstractUiComponentProperties(ui);
		ui.setContainerComponent(containerComponent.createUiReference());
		ui.setContentComponent(Component.createUiClientObjectReference(contentComponent));
		ui.setWidth(width);
		ui.setHeight(height);
		ui.setMarginX(marginX);
		ui.setMarginY(marginY);
		ui.setPosition(position.toUiPosition());
		ui.setBackgroundColor(UiUtil.createUiColor(backgroundColor));
		ui.setExpanderHandleColor(UiUtil.createUiColor(expanderHandleColor));
		ui.setCollapsible(collapsible);
		ui.setExpanded(expanded);
		return ui;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetDimensionsCommand(getId(), width, height));
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetDimensionsCommand(getId(), width, height));
	}

	public int getMarginX() {
		return marginX;
	}

	public void setMarginX(int marginX) {
		this.marginX = marginX;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetMarginsCommand(getId(), marginX, marginY));
	}

	public int getMarginY() {
		return marginY;
	}

	public void setMarginY(int marginY) {
		this.marginY = marginY;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetMarginsCommand(getId(), marginX, marginY));
	}

	public FloatingPosition getPosition() {
		return position;
	}

	public void setPosition(FloatingPosition position) {
		this.position = position;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetPositionCommand(getId(), position.toUiPosition()));
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetBackgroundColorCommand(getId(), UiUtil.createUiColor(backgroundColor)));
	}

	public Color getExpanderHandleColor() {
		return expanderHandleColor;
	}

	public void setExpanderHandleColor(Color expanderHandleColor) {
		this.expanderHandleColor = expanderHandleColor;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetExpanderHandleColorCommand(getId(), UiUtil.createUiColor(expanderHandleColor)));
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetExpandedCommand(getId(), expanded));
	}

	public Component getContentComponent() {
		return contentComponent;
	}

	public void setContentComponent(Component contentComponent) {
		this.contentComponent = contentComponent;
		queueCommandIfRendered(() -> new UiFloatingComponent.SetContentComponentCommand(getId(), contentComponent.createUiReference()));
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_FLOATING_COMPONENT_EXPANDED_OR_COLLAPSED: {
				onExpandedOrCollapsed.fire(((UiFloatingComponent.ExpandedOrCollapsedEvent) event).getExpanded());
				break;
			}
		}
	}
}
