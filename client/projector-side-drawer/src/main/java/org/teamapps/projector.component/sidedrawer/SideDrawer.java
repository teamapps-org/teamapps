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
package org.teamapps.projector.component.sidedrawer;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.DtoComponentConfig;
import org.teamapps.projector.event.ProjectorEvent;

public class SideDrawer extends AbstractComponent implements DtoSideDrawerEventHandler {

	private final DtoSideDrawerClientObjectChannel clientObjectChannel = new DtoSideDrawerClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Boolean> onExpandedOrCollapsed = new ProjectorEvent<>(clientObjectChannel::toggleExpandedOrCollapsedEvent);

	private final Component containerComponent;
	private Component contentComponent;
	private int width = -1;
	private int height = -1;
	private int marginX;
	private int marginY;
	private DrawerPosition position;
	private Color backgroundColor = null;
	private Color expanderHandleColor = null;

	private boolean collapsible;
	private boolean expanded;


	public SideDrawer(Component containerComponent, Component contentComponent) {
		this.containerComponent = containerComponent;
		this.contentComponent = contentComponent;
	}

	@Override
	public DtoComponentConfig createDto() {
		DtoSideDrawer ui = new DtoSideDrawer();
		mapAbstractConfigProperties(ui);
		ui.setContainerComponent(containerComponent);
		ui.setContentComponent(contentComponent);
		ui.setWidth(width);
		ui.setHeight(height);
		ui.setMarginX(marginX);
		ui.setMarginY(marginY);
		ui.setPosition(position);
		ui.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		ui.setExpanderHandleColor(expanderHandleColor != null ? expanderHandleColor.toHtmlColorString() : null);
		ui.setCollapsible(collapsible);
		ui.setExpanded(expanded);
		return ui;
	}

	@Override
	public void handleExpandedOrCollapsed(boolean expanded) {
		onExpandedOrCollapsed.fire(expanded);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		clientObjectChannel.setDimensions(width, height);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		clientObjectChannel.setDimensions(width, height);
	}

	public int getMarginX() {
		return marginX;
	}

	public void setMarginX(int marginX) {
		this.marginX = marginX;
		clientObjectChannel.setMargins(marginX, marginY);
	}

	public int getMarginY() {
		return marginY;
	}

	public void setMarginY(int marginY) {
		this.marginY = marginY;
		clientObjectChannel.setMargins(marginX, marginY);
	}

	public DrawerPosition getPosition() {
		return position;
	}

	public void setPosition(DrawerPosition position) {
		this.position = position;
		clientObjectChannel.setPosition(position);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		clientObjectChannel.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
	}

	public Color getExpanderHandleColor() {
		return expanderHandleColor;
	}

	public void setExpanderHandleColor(Color expanderHandleColor) {
		this.expanderHandleColor = expanderHandleColor;
		clientObjectChannel.setExpanderHandleColor(expanderHandleColor != null ? expanderHandleColor.toHtmlColorString() : null);
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
		clientObjectChannel.setExpanded(expanded);
	}

	public Component getContentComponent() {
		return contentComponent;
	}

	public void setContentComponent(Component contentComponent) {
		this.contentComponent = contentComponent;
		clientObjectChannel.setContentComponent(contentComponent);
	}

}
