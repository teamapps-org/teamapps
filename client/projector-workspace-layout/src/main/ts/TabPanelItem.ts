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
import {Component, noOpServerObjectChannel, parseHtml, ProjectorEvent} from "projector-client-object-api";
import {
	TabPanelTabStyle,
	TabPanel,
	WindowButtonType,
	DtoTabPanel,
	WindowButtonTypes, TabPanelTabStyles
} from "projector-client-core-components";
import {ItemTreeItem} from "./ItemTree";
import {View} from "./View";
import {SplitPaneItem} from "./SplitPaneItem";
import {ViewGroupPanelState, ViewGroupPanelStates} from "./generated";

class MinimizableTabPanel extends TabPanel {

	private _minimized = false;

	set minimized(minimized: boolean) {
		this._minimized = minimized;
		this.onEmptyStateChanged.fireIfChanged(this.empty);
	}

	get empty() {
		return this.getNumberOfVisibleTabButtons() === 0 || this._minimized;
	}
}

export class TabPanelItem implements ItemTreeItem<TabPanel> {

	public readonly id: string;
	public readonly persistent: boolean;
	public readonly component: MinimizableTabPanel;
	parent: SplitPaneItem | null;
	private _tabs: {
		view: View,
		windowButtonListener: (buttonType: WindowButtonType) => void
	}[] = [];

	public get tabs() {
		return this._tabs.map(tab => tab.view);
	}

	$minimizedTrayButton: HTMLElement = parseHtml(`<div class="minimized-tabpanel-button"></div>`);

	public readonly onTabSelected: ProjectorEvent<{ tabPanelItemId: string, tabId: string }> = new ProjectorEvent();
	public readonly onTabNeedsRefresh: ProjectorEvent<{ tabId: string }> = new ProjectorEvent();
	public readonly onTabClosed: ProjectorEvent<string> = new ProjectorEvent();
	public readonly onPanelStateChangeTriggered: ProjectorEvent<ViewGroupPanelState> = new ProjectorEvent();

	private _state: ViewGroupPanelState = ViewGroupPanelStates.NORMAL;

	constructor(id: string, persistent: boolean, parent: SplitPaneItem) {
		this.id = id;
		this.persistent = persistent;
		this.parent = parent;
		this.component = new MinimizableTabPanel({
			_type: "DtoTabPanel",
			hideTabBarIfSingleTab: true,
			tabStyle: TabPanelTabStyles.EARS
		} as DtoTabPanel, noOpServerObjectChannel);
		this.component.setWindowButtons(this.createWindowButtonList());
		this.component.onWindowButtonClicked.addListener(eventObject => {
			if (eventObject.windowButton === WindowButtonTypes.MINIMIZE) {
				this.component.restore(); // could be maximized, so first restore!
				this.onPanelStateChangeTriggered.fire(ViewGroupPanelStates.MINIMIZED);
			}
		});
		this.component.onTabSelected.addListener(eventObject => this.onTabSelected.fire({tabPanelItemId: this.id, tabId: eventObject.tabId}));
		this.component.onTabNeedsRefresh.addListener(eventObject => this.onTabNeedsRefresh.fire(eventObject));
		this.component.onTabClosed.addListener(eventObject => {
			this._tabs = this._tabs.filter(tab => tab.view.viewName !== eventObject.tabId);
			this.onTabClosed.fire(eventObject.tabId);
			this.updateWindowToolButtons();
		});
	}

	get itemIds() {
		return [this.id];
	}

	get viewNames() {
		return this._tabs.map(tab => tab.view.viewName);
	}

	private createWindowButtonList(closeButton?: boolean): WindowButtonType[] {
		let toolButtons: WindowButtonType[] = [WindowButtonTypes.MINIMIZE, WindowButtonTypes.MAXIMIZE_RESTORE];
		if (closeButton) {
			toolButtons.push(WindowButtonTypes.CLOSE);
		}
		return toolButtons;
	}

	public addTab(view: View, select: boolean, index = Number.MAX_SAFE_INTEGER): void {
		this.component.addTab({
			content: view.component,
			id: view.viewName,
			icon: view.tabIcon,
			caption: view.tabCaption,
			closeable: view.tabCloseable,
			rightSide: false,
			lazyLoading: view.lazyLoading,
			visible: view.visible
		}, select, index);


		const windowButtonListener = (windowButtonType: WindowButtonType) => {
			if (windowButtonType === WindowButtonTypes.MINIMIZE) {
				this.onPanelStateChangeTriggered.fire(ViewGroupPanelStates.MINIMIZED);
			} else if (windowButtonType === WindowButtonTypes.CLOSE) {
				this.removeTab(view);
				this.onTabClosed.fire(view.viewName);
			}
		};
		view.onPanelWindowButtonClicked.addListener(windowButtonListener);

		this._tabs.push({view, windowButtonListener});

		this.updateWindowToolButtons();

		view.parent = this;

		if (this.state === ViewGroupPanelStates.MINIMIZED) {
			this.updateMinimizedButton();
		}
	}

	moveTab(viewName: string, index: number) {
		this.component.moveTab(viewName, index);
	}

	public removeTab(view: View) {
		this.component.removeTab(view.viewName);
		let tab = this._tabs.filter(tab => tab.view === view)[0];
		tab.view.onPanelWindowButtonClicked.removeListener(tab.windowButtonListener);
		this._tabs = this._tabs.filter(tab => tab.view !== view);
		this.updateWindowToolButtons();

		if (this.state == ViewGroupPanelStates.MINIMIZED) {
			this.updateMinimizedButton();
		}
	}

	public updateTab(viewName: string, component: Component) {
		this.component.setTabContent(viewName, component, true);
		this.updateWindowToolButtons();
	}

	setTabConfiguration(viewName: string, tabIcon: string, tabCaption: string, tabCloseable: boolean, visible: boolean) {
		this.component.setTabConfiguration(viewName, tabIcon, tabCaption, tabCloseable, visible, false);
		this.updateWindowToolButtons();
	}

	selectTab(viewName: string) {
		this.component.selectTab(viewName, false);
	}

	private updateWindowToolButtons() {
		if (this.component.getNumberOfVisibleTabButtons() > 1) {
			this._tabs.forEach(tab => {
				tab.view.setWindowButtons([]);
			});
			this.component.setWindowButtons(this.createWindowButtonList());
		} else {
			this._tabs.forEach(tab => {
				tab.view.setWindowButtons(this.createWindowButtonList(tab.view.tabCloseable));
			});
		}
	}

	get maximized() {
		return this._state === ViewGroupPanelStates.MAXIMIZED
	}

	set state(state: ViewGroupPanelState) {
		this._state = state;
		let minimized = state === ViewGroupPanelStates.MINIMIZED;
		this.component.minimized = minimized;
		if (minimized) {
			this.updateMinimizedButton();
		}
		this.updateWindowToolButtons();
	}

	private updateMinimizedButton() {
		let iconSize = 12; // TODO this.context.config.optimizedForTouch ? 16 : 12
		this.$minimizedTrayButton.innerHTML = '';
		// noinspection CssUnknownTarget
		this.$minimizedTrayButton.append(parseHtml(`<div class="tab-icon img img-${iconSize} ta-icon-window-restore"></div>`));
		this._tabs.forEach(tab => this.$minimizedTrayButton.append(parseHtml(`<div class="tab-icon img img-${iconSize}" style="background-image: url('${tab.view.tabIcon}')"></div>`)));
		this.$minimizedTrayButton.addEventListener("click", () => this.onPanelStateChangeTriggered.fire(ViewGroupPanelStates.NORMAL));
	}

	get state(): ViewGroupPanelState {
		return this._state;
	}
}


