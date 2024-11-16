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

import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.core.CoreComponentLibrary;
import org.teamapps.projector.component.core.panel.WindowButtonType;
import org.teamapps.projector.component.core.toolbutton.ToolButton;
import org.teamapps.projector.event.ProjectorEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.teamapps.commons.util.CollectionCastUtil.castList;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class TabPanel extends AbstractComponent implements DtoTabPanelEventHandler {

	private final DtoTabPanelClientObjectChannel clientObjectChannel = new DtoTabPanelClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<Tab> onTabSelected = new ProjectorEvent<>(clientObjectChannel::toggleTabSelectedEvent);
	public final ProjectorEvent<Tab> onTabClosed = new ProjectorEvent<>(clientObjectChannel::toggleTabClosedEvent);

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
		clientObjectChannel.addTab(tab.createUiTab(), select);
	}

	public void removeTab(Tab tab) {
		boolean wasRemoved = tabs.remove(tab);
		if (wasRemoved) {
			clientObjectChannel.removeTab(tab.getClientId());
		}
	}

	public void setSelectedTab(Tab tab) {
		if (tab != null) {
			this.selectedTab = tab;
			clientObjectChannel.selectTab(tab.getClientId());
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
		updateToolButtons();
	}

	public void removeToolButton(ToolButton toolButton) {
		this.toolButtons.remove(toolButton);
		updateToolButtons();
	}

	public void setToolButtons(List<ToolButton> toolButtons) {
		this.toolButtons.clear();
		if (toolButtons != null) {
			this.toolButtons.addAll(toolButtons);
		}
		updateToolButtons();
	}

	private void updateToolButtons() {
		clientObjectChannel.setToolButtons(castList(this.toolButtons));
	}

	public List<ToolButton> getToolButtons() {
		return toolButtons;
	}

	public boolean isHideTabBarIfSingleTab() {
		return hideTabBarIfSingleTab;
	}

	public void setHideTabBarIfSingleTab(boolean hideTabBarIfSingleTab) {
		this.hideTabBarIfSingleTab = hideTabBarIfSingleTab;
		clientObjectChannel.setHideTabBarIfSingleTab(hideTabBarIfSingleTab);
	}

	public TabPanelTabStyle getTabStyle() {
		return tabStyle;
	}

	public void setTabStyle(TabPanelTabStyle tabStyle) {
		this.tabStyle = tabStyle;
		clientObjectChannel.setTabStyle(tabStyle);
	}

	@Override
	public DtoComponent createConfig() {
		DtoTabPanel uiTabPanel = new DtoTabPanel();
		mapAbstractConfigProperties(uiTabPanel);
		List<DtoTab> uiTabs = tabs.stream()
				.map(tab -> tab != null ? tab.createUiTab() : null)
				.collect(Collectors.toList());
		uiTabPanel.setTabs(uiTabs);
		uiTabPanel.setSelectedTabId(this.getSelectedTab() != null ? this.getSelectedTab().getClientId() : null);
		uiTabPanel.setHideTabBarIfSingleTab(hideTabBarIfSingleTab);
		uiTabPanel.setTabStyle(tabStyle);
		uiTabPanel.setToolButtons(castList(toolButtons));
		return uiTabPanel;
	}

	private Tab getTabByClientId(String tabId) {
		return tabs.stream()
				.filter(tab -> Objects.equals(tab.getClientId(), tabId))
				.findFirst().orElse(null);
	}

	@Override
	public void handleTabSelected(String tabId) {
		if (tabId == null) {
			this.selectedTab = null;
		} else {
			Tab oldSelectedTab = this.selectedTab;
			Tab selectedTab = this.getTabByClientId(tabId);
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

	@Override
	public void handleTabNeedsRefresh(String tabId) {
		Tab tab = getTabByClientId(tabId);
		clientObjectChannel.setTabContent(tabId, tab.getContent());
	}

	@Override
	public void handleTabClosed(String tabId) {
		Tab closedTab = this.getTabByClientId(tabId);
		if (closedTab != null) {
			tabs.remove(closedTab);
			closedTab.onClosed.fire(null);
			onTabClosed.fire(closedTab);
		}
	}

	@Override
	public void handleWindowButtonClicked(WindowButtonType windowButton) {
		// TODO remove?
	}

	/*package-private*/ void handleTabSelected(Tab tab) {
		this.setSelectedTab(tab);
	}

	/*package-private*/ void handleTabToolbarChanged(Tab tab) {
		clientObjectChannel.setTabToolbar(tab.getClientId(), tab.getToolbar());
	}

	/*package-private*/ void handleTabContentChanged(Tab tab) {
		clientObjectChannel.setTabContent(tab.getClientId(), tab.getContent());
	}

	/*package-private*/ void handleTabConfigurationChanged(Tab tab) {
		String iconString = getSessionContext().resolveIcon(tab.getIcon());
		String caption = tab.getTitle();
		clientObjectChannel.setTabConfiguration(tab.getClientId(), iconString, caption, tab.isCloseable(), tab.isVisible(), tab.isRightSide());
	}

	/*package-private*/ void handleTabVisibilityChanged(Tab tab) {
		String iconString = getSessionContext().resolveIcon(tab.getIcon());
		String caption = tab.getTitle();
		clientObjectChannel.setTabConfiguration(tab.getClientId(), iconString, caption, tab.isCloseable(), tab.isVisible(), tab.isRightSide());
	}

}
