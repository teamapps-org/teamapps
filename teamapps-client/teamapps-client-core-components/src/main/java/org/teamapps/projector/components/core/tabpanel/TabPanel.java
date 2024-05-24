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
package org.teamapps.projector.components.core.tabpanel;

import org.teamapps.projector.dto.DtoComponent;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.dto.DtoTab;
import org.teamapps.projector.dto.DtoTabPanel;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.projector.clientobject.ClientObject;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.components.core.CoreComponentLibrary;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.ux.component.toolbutton.ToolButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
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
		getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.AddTabCommand(tab.createUiTab(), select), null);
	}

	public void removeTab(Tab tab) {
		boolean wasRemoved = tabs.remove(tab);
		if (wasRemoved) {
			getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.RemoveTabCommand(tab.getClientId()), null);
		}
	}

	public void setSelectedTab(Tab tab) {
		if (tab != null) {
			this.selectedTab = tab;
			getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.SelectTabCommand(tab.getClientId()), null);
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
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoTabPanel.SetToolButtonsCommand(this.toolButtons.stream()
				.map(toolButton -> toolButton.createClientReference())
				.collect(Collectors.toList()))).get(), null);
	}

	public List<ToolButton> getToolButtons() {
		return toolButtons;
	}

	public boolean isHideTabBarIfSingleTab() {
		return hideTabBarIfSingleTab;
	}

	public void setHideTabBarIfSingleTab(boolean hideTabBarIfSingleTab) {
		this.hideTabBarIfSingleTab = hideTabBarIfSingleTab;
		getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.SetHideTabBarIfSingleTabCommand(hideTabBarIfSingleTab), null);
	}

	public TabPanelTabStyle getTabStyle() {
		return tabStyle;
	}

	public void setTabStyle(TabPanelTabStyle tabStyle) {
		this.tabStyle = tabStyle;
		getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.SetTabStyleCommand(tabStyle.toUiTabPanelTabStyle()), null);
	}

	@Override
	public DtoComponent createConfig() {
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
				.map(toolButton -> (toolButton.createClientReference()))
				.collect(Collectors.toList()));
		return uiTabPanel;
	}

	private Tab getTabByClientId(String tabId) {
		return tabs.stream()
				.filter(tab -> Objects.equals(tab.getClientId(), tabId))
				.findFirst().orElse(null);
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
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
				getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.SetTabContentCommand(tab.getClientId(), ClientObject.createClientReference(tab.getContent())), null);
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
		getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.SetTabToolbarCommand(tab.getClientId(), ClientObject.createClientReference(tab.getToolbar())), null);
	}

	/*package-private*/ void handleTabContentChanged(Tab tab) {
		getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.SetTabContentCommand(tab.getClientId(), ClientObject.createClientReference(tab.getContent())), null);
	}

	/*package-private*/ void handleTabConfigurationChanged(Tab tab) {
		String iconString = getSessionContext().resolveIcon(tab.getIcon());
		String caption = tab.getTitle();
		getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.SetTabConfigurationCommand(tab.getClientId(), iconString, caption, tab.isCloseable(), tab.isVisible(), tab.isRightSide()), null);
	}

	/*package-private*/ void handleTabVisibilityChanged(Tab tab) {
		String iconString = getSessionContext().resolveIcon(tab.getIcon());
		String caption = tab.getTitle();
		getClientObjectChannel().sendCommandIfRendered(new DtoTabPanel.SetTabConfigurationCommand(tab.getClientId(), iconString, caption, tab.isCloseable(), tab.isVisible(), tab.isRightSide()), null);
	}

}
