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
package org.teamapps.ux.component.tabpanel;

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoTab;
import org.teamapps.dto.DtoTabPanel;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.CommonComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.component.toolbutton.ToolButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class TabPanel extends AbstractComponent implements Component {

	public final ProjectorEvent<Tab> onTabSelected = createProjectorEventBoundToUiEvent(DtoTabPanel.TabSelectedEvent.TYPE_ID);
	public final ProjectorEvent<Tab> onTabClosed = createProjectorEventBoundToUiEvent(DtoTabPanel.TabClosedEvent.TYPE_ID);

	private final List<Tab> tabs = new ArrayList<>();
	private Tab selectedTab;
	private final List<ToolButton> toolButtons = new ArrayList<>();
	private boolean hideTabBarIfSingleTab = false;
	private TabPanelTabStyle tabStyle = TabPanelTabStyle.BLOCKS;

	public TabPanel() {
	}

	public TabPanel(List<Tab> tabs) {
		this.tabs.clear();
		this.tabs.addAll(tabs);
		tabs.forEach(tab -> tab.setTabPanel(this));
	}

	public void addTab(Tab tab) {
		addTab(tab, false);
	}

	public void addTab(Tab tab, boolean select) {
		if (select || this.tabs.isEmpty()) {
			this.selectedTab = tab;
		}
		tabs.add(tab);
		tab.setTabPanel(this);
		sendCommandIfRendered(() -> new DtoTabPanel.AddTabCommand(tab.createUiTab(), select));
	}

	public void removeTab(Tab tab) {
		boolean wasRemoved = tabs.remove(tab);
		if (wasRemoved) {
			sendCommandIfRendered(() -> new DtoTabPanel.RemoveTabCommand(tab.getClientId()));
		}
	}

	public void setSelectedTab(Tab tab) {
		if (tab != null) {
			this.selectedTab = tab;
			sendCommandIfRendered(() -> new DtoTabPanel.SelectTabCommand(tab.getClientId()));
		}
	}

	public Tab getSelectedTab() {
		return selectedTab;
	}

	public List<Tab> getTabs() {
		return new ArrayList<>(tabs);
	}

	public void addToolButton(ToolButton toolButton) {
		this.toolButtons.add(toolButton);
		toolButton.setParent(this);
		updateToolButtons();
	}

	public void removeToolButton(ToolButton toolButton) {
		this.toolButtons.remove(toolButton);
		toolButton.setParent(null);
		updateToolButtons();
	}

	public void setToolButtons(List<ToolButton> toolButtons) {
		this.toolButtons.clear();
		if (toolButtons != null) {
			this.toolButtons.addAll(toolButtons);
			this.toolButtons.forEach(toolButton -> toolButton.setParent(this));
		}
		updateToolButtons();
	}

	private void updateToolButtons() {
		sendCommandIfRendered(() -> new DtoTabPanel.SetToolButtonsCommand(this.toolButtons.stream()
				.map(toolButton -> toolButton.createUiReference())
				.collect(Collectors.toList())));
	}

	public List<ToolButton> getToolButtons() {
		return toolButtons;
	}

	public boolean isHideTabBarIfSingleTab() {
		return hideTabBarIfSingleTab;
	}

	public void setHideTabBarIfSingleTab(boolean hideTabBarIfSingleTab) {
		this.hideTabBarIfSingleTab = hideTabBarIfSingleTab;
		this.sendCommandIfRendered(() -> new DtoTabPanel.SetHideTabBarIfSingleTabCommand(hideTabBarIfSingleTab));
	}

	public TabPanelTabStyle getTabStyle() {
		return tabStyle;
	}

	public void setTabStyle(TabPanelTabStyle tabStyle) {
		this.tabStyle = tabStyle;
		this.sendCommandIfRendered(() -> new DtoTabPanel.SetTabStyleCommand(tabStyle.toUiTabPanelTabStyle()));
	}

	@Override
	public DtoComponent createUiClientObject() {
		DtoTabPanel uiTabPanel = new DtoTabPanel();
		mapAbstractUiComponentProperties(uiTabPanel);
		List<DtoTab> uiTabs = tabs.stream()
				.map(tab -> tab != null ? tab.createUiTab() : null)
				.collect(Collectors.toList());
		uiTabPanel.setTabs(uiTabs);
		uiTabPanel.setSelectedTabId(this.getSelectedTab() != null ? this.getSelectedTab().getClientId() : null);
		uiTabPanel.setHideTabBarIfSingleTab(hideTabBarIfSingleTab);
		uiTabPanel.setTabStyle(tabStyle.toUiTabPanelTabStyle());
		uiTabPanel.setToolButtons(toolButtons.stream()
				.map(toolButton -> (toolButton.createUiReference()))
				.collect(Collectors.toList()));
		return uiTabPanel;
	}

	private Tab getTabByClientId(String tabId) {
		return tabs.stream()
				.filter(tab -> Objects.equals(tab.getClientId(), tabId))
				.findFirst().orElse(null);
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoTabPanel.TabSelectedEvent.TYPE_ID -> {
				var tabSelectedEvent = event.as(DtoTabPanel.TabSelectedEventWrapper.class);
				if (tabSelectedEvent.getTabId() == null) {
					this.selectedTab = null;
				} else {
					Tab oldSelectedTab = this.selectedTab;
					Tab selectedTab = this.getTabByClientId(tabSelectedEvent.getTabId());
					this.selectedTab = selectedTab;
					if (oldSelectedTab != null) {
						oldSelectedTab.onDeselected.fire(null);
					}
					if (selectedTab != null) {
						selectedTab.onSelected.fire(null);
					}
				}
				onTabSelected.fire(selectedTab);
			}
			case DtoTabPanel.TabNeedsRefreshEvent.TYPE_ID -> {
				var tabNeedsRefreshEvent = event.as(DtoTabPanel.TabNeedsRefreshEventWrapper.class);
				Tab tab = getTabByClientId(tabNeedsRefreshEvent.getTabId());
				sendCommandIfRendered(() -> new DtoTabPanel.SetTabContentCommand(tab.getClientId(), Component.createUiClientObjectReference(tab.getContent())));
			}
			case DtoTabPanel.TabClosedEvent.TYPE_ID -> {
				var tabClosedEvent = event.as(DtoTabPanel.TabClosedEventWrapper.class);
				String tabId = tabClosedEvent.getTabId();
				Tab closedTab = this.getTabByClientId(tabId);
				if (closedTab != null) {
					tabs.remove(closedTab);
					closedTab.onClosed.fire(null);
					onTabClosed.fire(closedTab);
				}
			}
		}
	}

	/*package-private*/ void handleTabSelected(Tab tab) {
		this.setSelectedTab(tab);
	}

	/*package-private*/ void handleTabToolbarChanged(Tab tab) {
		sendCommandIfRendered(() -> new DtoTabPanel.SetTabToolbarCommand(tab.getClientId(), Component.createUiClientObjectReference(tab.getToolbar())));
	}

	/*package-private*/ void handleTabContentChanged(Tab tab) {
		sendCommandIfRendered(() -> new DtoTabPanel.SetTabContentCommand(tab.getClientId(), Component.createUiClientObjectReference(tab.getContent())));
	}

	/*package-private*/ void handleTabConfigurationChanged(Tab tab) {
		String iconString = getSessionContext().resolveIcon(tab.getIcon());
		String caption = tab.getTitle();
		sendCommandIfRendered(() -> new DtoTabPanel.SetTabConfigurationCommand(tab.getClientId(), iconString, caption, tab.isCloseable(), tab.isVisible(), tab.isRightSide()));
	}

	/*package-private*/ void handleTabVisibilityChanged(Tab tab) {
		String iconString = getSessionContext().resolveIcon(tab.getIcon());
		String caption = tab.getTitle();
		sendCommandIfRendered(() -> new DtoTabPanel.SetTabConfigurationCommand(tab.getClientId(), iconString, caption, tab.isCloseable(), tab.isVisible(), tab.isRightSide()));
	}

}
