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
package org.teamapps.projector.component.core.tabpanel;

import org.teamapps.icons.Icon;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.toolbar.Toolbar;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.session.CurrentSessionContext;
import org.teamapps.projector.session.SessionContext;

import java.util.UUID;

public class Tab {

	public final ProjectorEvent<Void> onSelected = new ProjectorEvent<>();
	public final ProjectorEvent<Void> onDeselected = new ProjectorEvent<>();
	public final ProjectorEvent<Void> onClosed = new ProjectorEvent<>();

	private final String clientId = UUID.randomUUID().toString();
	private TabPanel tabPanel;

	private Icon icon;
	private String title;
	private boolean closeable;
	private boolean lazyLoading;
	private boolean rightSide;
	private boolean visible = true;
	private Component content;

	private Toolbar toolbar;

	public Tab() {
		this(null, null, null, false);
	}

	public Tab(Icon icon, String title, Component content) {
		this(icon, title, content, false);
	}

	public Tab(Icon icon, String title, Component content, boolean lazyLoading) {
		this.title = title;
		this.icon = icon;
		this.content = content;
		this.lazyLoading = lazyLoading;
	}

	public DtoTab createDtoTab() {
		SessionContext context = CurrentSessionContext.get();
		DtoTab uiTab = new DtoTab(clientId, context.resolveIcon(icon), title);
		uiTab.setCloseable(closeable);
		uiTab.setLazyLoading(this.isLazyLoading());
		uiTab.setRightSide(this.rightSide);
		uiTab.setToolbar(this.toolbar);
		uiTab.setContent(content);
		uiTab.setVisible(visible);
		return uiTab;
	}

	/*package-private*/ void setTabPanel(TabPanel tabPanel) {
		this.tabPanel = tabPanel;
	}

	public void select() {
		tabPanel.handleTabSelected(this);
	}

	/*package-private*/ String getClientId() {
		return clientId;
	}

	public String getTitle() {
		return title;
	}

	public Tab setTitle(String title) {
		this.title = title;
		if (tabPanel != null) {
			tabPanel.handleTabConfigurationChanged(this);
		}
		return this;
	}

	public Icon getIcon() {
		return icon;
	}

	public Tab setIcon(Icon icon) {
		this.icon = icon;
		if (tabPanel != null) {
			tabPanel.handleTabConfigurationChanged(this);
		}
		return this;
	}

	public Component getContent() {
		return content;
	}

	public Tab setContent(Component content) {
		this.content = content;
		if (tabPanel != null) {
			tabPanel.handleTabContentChanged(this);
		}
		return this;
	}

	public boolean isRightSide() {
		return rightSide;
	}

	public Tab setRightSide(boolean rightSide) {
		this.rightSide = rightSide;
		if (tabPanel != null) {
			tabPanel.handleTabConfigurationChanged(this);
		}
		return this;
	}

	public boolean isLazyLoading() {
		return lazyLoading;
	}

	public Tab setLazyLoading(boolean lazyLoading) {
		this.lazyLoading = lazyLoading;
		return this;
	}

	public boolean isCloseable() {
		return closeable;
	}

	public Tab setCloseable(boolean closeable) {
		this.closeable = closeable;
		if (tabPanel != null) {
			tabPanel.handleTabConfigurationChanged(this);
		}
		return this;
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public Tab setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
		if (toolbar != null) {
			tabPanel.handleTabToolbarChanged(this);
		}
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public Tab setVisible(boolean visible) {
		this.visible = visible;
		if (tabPanel != null) {
			tabPanel.handleTabVisibilityChanged(this);
		}
		return this;
	}

}
