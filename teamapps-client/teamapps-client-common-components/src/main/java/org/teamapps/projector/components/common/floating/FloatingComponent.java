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
package org.teamapps.projector.components.common.floating;

import org.teamapps.common.format.Color;
import org.teamapps.projector.components.common.dto.DtoComponent;
import org.teamapps.projector.components.common.dto.DtoFloatingComponent;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.ClientObject;
import org.teamapps.ux.component.Component;

public class FloatingComponent extends AbstractComponent {

	public final ProjectorEvent<Boolean> onExpandedOrCollapsed = createProjectorEventBoundToUiEvent(DtoFloatingComponent.ExpandedOrCollapsedEvent.TYPE_ID);

	private final Component containerComponent;
	private Component contentComponent;
	private int width = -1;
	private int height = -1;
	private int marginX;
	private int marginY;
	private FloatingPosition position;
	private Color backgroundColor = null;
	private Color expanderHandleColor = null;

	private boolean collapsible;
	private boolean expanded;


	public FloatingComponent(Component containerComponent, Component contentComponent) {
		this.containerComponent = containerComponent;
		this.contentComponent = contentComponent;
	}

	@Override
	public DtoComponent createDto() {
		DtoFloatingComponent ui = new DtoFloatingComponent();
		mapAbstractUiComponentProperties(ui);
		ui.setContainerComponent(containerComponent.createDtoReference());
		ui.setContentComponent(ClientObject.createDtoReference(contentComponent));
		ui.setWidth(width);
		ui.setHeight(height);
		ui.setMarginX(marginX);
		ui.setMarginY(marginY);
		ui.setPosition(position.toUiPosition());
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setExpanderHandleColor(expanderHandleColor != null ? expanderHandleColor.toHtmlColorString() : null);
		ui.setCollapsible(collapsible);
		ui.setExpanded(expanded);
		return ui;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetDimensionsCommand(width, height));
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetDimensionsCommand(width, height));
	}

	public int getMarginX() {
		return marginX;
	}

	public void setMarginX(int marginX) {
		this.marginX = marginX;
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetMarginsCommand(marginX, marginY));
	}

	public int getMarginY() {
		return marginY;
	}

	public void setMarginY(int marginY) {
		this.marginY = marginY;
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetMarginsCommand(marginX, marginY));
	}

	public FloatingPosition getPosition() {
		return position;
	}

	public void setPosition(FloatingPosition position) {
		this.position = position;
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetPositionCommand(position.toUiPosition()));
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetBackgroundColorCommand(backgroundColor != null ? backgroundColor.toHtmlColorString() : null));
	}

	public Color getExpanderHandleColor() {
		return expanderHandleColor;
	}

	public void setExpanderHandleColor(Color expanderHandleColor) {
		this.expanderHandleColor = expanderHandleColor;
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetExpanderHandleColorCommand(expanderHandleColor != null ? expanderHandleColor.toHtmlColorString() : null));
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
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetExpandedCommand(expanded));
	}

	public Component getContentComponent() {
		return contentComponent;
	}

	public void setContentComponent(Component contentComponent) {
		this.contentComponent = contentComponent;
		sendCommandIfRendered(() -> new DtoFloatingComponent.SetContentComponentCommand(contentComponent != null ? contentComponent.createDtoReference() : null));
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoFloatingComponent.ExpandedOrCollapsedEvent.TYPE_ID -> {
				var e = event.as(DtoFloatingComponent.ExpandedOrCollapsedEventWrapper.class);
				onExpandedOrCollapsed.fire(e.getExpanded());
			}
		}
	}
}