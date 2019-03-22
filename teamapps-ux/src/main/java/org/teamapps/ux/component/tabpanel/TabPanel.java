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
package org.teamapps.ux.component.tabpanel;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiTab;
import org.teamapps.dto.UiTabPanel;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.Container;
import org.teamapps.ux.component.toolbutton.ToolButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TabPanel extends AbstractComponent implements Container {

	public final Event<Tab> onTabSelected = new Event<>();
	public final Event<Tab> onTabClosed = new Event<>();

	private final List<Tab> tabs = new ArrayList<>();
	private Tab selectedTab;
	private List<ToolButton> toolButtons = new ArrayList<>();
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
		queueCommandIfRendered(() -> new UiTabPanel.AddTabCommand(getId(), tab.createUiTab(), select));
	}

	public void removeTab(Tab tab) {
		boolean wasRemoved = tabs.remove(tab);
		if (wasRemoved) {
			queueCommandIfRendered(() -> new UiTabPanel.RemoveTabCommand(getId(), tab.getClientId()));
		}
	}

	public void setSelectedTab(Tab tab) {
		if (tab != null) {
			this.selectedTab = tab;
			queueCommandIfRendered(() -> new UiTabPanel.SelectTabCommand(getId(), tab.getClientId()));
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
		queueCommandIfRendered(() -> new UiTabPanel.SetToolButtonsCommand(getId(), this.toolButtons.stream()
				.map(toolButton -> toolButton.createUiComponentReference())
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
		this.queueCommandIfRendered(() -> new UiTabPanel.SetHideTabBarIfSingleTabCommand(getId(), hideTabBarIfSingleTab));
	}

	public TabPanelTabStyle getTabStyle() {
		return tabStyle;
	}

	public void setTabStyle(TabPanelTabStyle tabStyle) {
		this.tabStyle = tabStyle;
		this.queueCommandIfRendered(() -> new UiTabPanel.SetTabStyleCommand(getId(), tabStyle.toUiTabPanelTabStyle()));
	}

	@Override
	public UiComponent createUiComponent() {
		UiTabPanel uiTabPanel = new UiTabPanel(getId());
		mapAbstractUiComponentProperties(uiTabPanel);
		List<UiTab> uiTabs = tabs.stream()
				.map(tab -> tab != null ? tab.createUiTab() : null)
				.collect(Collectors.toList());
		uiTabPanel.setTabs(uiTabs);
		uiTabPanel.setSelectedTabId(this.getSelectedTab() != null ? this.getSelectedTab().getClientId() : null);
		uiTabPanel.setHideTabBarIfSingleTab(hideTabBarIfSingleTab);
		uiTabPanel.setTabStyle(tabStyle.toUiTabPanelTabStyle());
		uiTabPanel.setToolButtons(toolButtons.stream()
				.map(toolButton -> (toolButton.createUiComponentReference()))
				.collect(Collectors.toList()));
		return uiTabPanel;
	}

	@Override
	protected void doDestroy() {
		this.tabs.forEach(Tab::destroy);
	}

	private Tab getTabByClientId(String tabId) {
		return tabs.stream()
				.filter(tab -> Objects.equals(tab.getClientId(), tabId))
				.findFirst().orElse(null);
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TAB_PANEL_TAB_SELECTED:
				UiTabPanel.TabSelectedEvent tabSelectedEvent = (UiTabPanel.TabSelectedEvent) event;
				if (tabSelectedEvent.getTabId() == null) {
					this.selectedTab = null;
				} else {
					Tab oldSelectedTab = this.selectedTab;
					Tab selectedTab = this.getTabByClientId(tabSelectedEvent.getTabId());
					this.selectedTab = selectedTab;
					oldSelectedTab.onDeselected.fire(null);
					selectedTab.onSelected.fire(null);
				}
				onTabSelected.fire(selectedTab);
				break;
			case UI_TAB_PANEL_TAB_NEEDS_REFRESH:
				UiTabPanel.TabNeedsRefreshEvent tabNeedsRefreshEvent = (UiTabPanel.TabNeedsRefreshEvent) event;
				Tab tab = getTabByClientId(tabNeedsRefreshEvent.getTabId());
				queueCommandIfRendered(() -> new UiTabPanel.SetTabContentCommand(getId(), tab.getClientId(), Component.createUiComponentReference(tab.getContent())));
				break;
			case UI_TAB_PANEL_TAB_CLOSED:
				UiTabPanel.TabClosedEvent tabClosedEvent = (UiTabPanel.TabClosedEvent) event;
				String tabId = tabClosedEvent.getTabId();
				Tab closedTab = this.getTabByClientId(tabId);
				if (closedTab != null) {
					tabs.remove(closedTab);
					closedTab.onClosed.fire(null);
					onTabClosed.fire(closedTab);
				}
				break;
		}
	}

	/*package-private*/ void handleTabSelected(Tab tab) {
		this.setSelectedTab(tab);
	}

	/*package-private*/ void handleTabToolbarChanged(Tab tab) {
		queueCommandIfRendered(() -> new UiTabPanel.SetTabToolbarCommand(getId(), tab.getClientId(), Component.createUiComponentReference(tab.getToolbar())));
	}

	/*package-private*/ void handleTabContentChanged(Tab tab) {
		queueCommandIfRendered(() -> new UiTabPanel.SetTabContentCommand(getId(), tab.getClientId(), Component.createUiComponentReference(tab.getContent())));
	}

	/*package-private*/ void handleTabConfigurationChanged(Tab tab) {
		String iconString = getSessionContext().resolveIcon(tab.getIcon());
		String caption = tab.getTitle();
		queueCommandIfRendered(() -> new UiTabPanel.SetTabConfigurationCommand(getId(), tab.getClientId(), iconString, caption, tab.isCloseable(), tab.isVisible(), tab.isRightSide()));
	}

	/*package-private*/ void handleTabVisibilityChanged(Tab tab) {
		String iconString = getSessionContext().resolveIcon(tab.getIcon());
		String caption = tab.getTitle();
		queueCommandIfRendered(() -> new UiTabPanel.SetTabConfigurationCommand(getId(), tab.getClientId(), iconString, caption, tab.isCloseable(), tab.isVisible(), tab.isRightSide()));
	}

	@Override
	public boolean isChildVisible(Component child) {
		boolean isActiveTab = this.getSelectedTab() != null && this.getSelectedTab().getContent() == child;
		boolean isToolButton = this.toolButtons.contains(child);
		return this.isEffectivelyVisible() && (isActiveTab || isToolButton);
	}
}
