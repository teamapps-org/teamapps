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
/* @ts-ignore */
import ICON_MINIMIZE from "@material-symbols/svg-400/outlined/minimize.svg";
/* @ts-ignore */
import ICON_MAXIMIZE from "@material-symbols/svg-400/outlined/web_asset.svg";
/* @ts-ignore */
import ICON_CLOSE from "@material-symbols/svg-400/outlined/close.svg";
/* @ts-ignore */
import ICON_RESTORE from "@material-symbols/svg-400/outlined/select_window.svg";
import {Toolbar} from "./tool-container/toolbar/Toolbar";
import {
	AbstractComponent,
	bind,
	Component,
	executeAfterAttached,
	insertAtIndex,
	insertBefore,
	noOpServerObjectChannel,
	parseHtml,
	prependChild,
	ServerObjectChannel,
	ProjectorEvent
} from "projector-client-object-api";
import {DtoTab as DtoTab} from "../generated/DtoTab";
import {Emptyable, isEmptyable} from "../util/Emptyable";
import {ToolButton} from "./ToolButton";
import {
	DtoTabPanel,
	DtoTabPanel_TabClosedEvent,
	DtoTabPanel_TabNeedsRefreshEvent,
	DtoTabPanel_TabSelectedEvent,
	DtoTabPanel_WindowButtonClickedEvent,
	DtoTabPanelCommandHandler,
	DtoTabPanelEventSource, DtoToolButton,
	TabPanelTabStyle, TabPanelTabStyles,
	WindowButtonType, WindowButtonTypes
} from "../generated";
import {maximizeComponent} from "../util/Common";
import {positionDropdown} from "../util/dropdownPosition";
import {contentWidth} from "projector-client-object-api";


interface Tab {
	config: DtoTab;
	$button: HTMLElement;
	$wrapper: HTMLElement;
	$dropDownTabButton: HTMLElement;
	$toolbarContainer: HTMLElement;
	$contentContainer: HTMLElement;
	toolbar: Toolbar;
	contentComponent: Component;
	buttonWidth?: number;
	visible: boolean;
}

export class TabPanel extends AbstractComponent<DtoTabPanel> implements DtoTabPanelCommandHandler, DtoTabPanelEventSource, Emptyable {

	public readonly onTabSelected: ProjectorEvent<DtoTabPanel_TabSelectedEvent> = new ProjectorEvent<DtoTabPanel_TabSelectedEvent>();

	public readonly onTabNeedsRefresh: ProjectorEvent<DtoTabPanel_TabNeedsRefreshEvent> = new ProjectorEvent<DtoTabPanel_TabNeedsRefreshEvent>();
	public readonly onTabClosed: ProjectorEvent<DtoTabPanel_TabClosedEvent> = new ProjectorEvent<DtoTabPanel_TabClosedEvent>();
	public readonly onEmptyStateChanged: ProjectorEvent<boolean> = new ProjectorEvent();

	public readonly onWindowButtonClicked: ProjectorEvent<DtoTabPanel_WindowButtonClickedEvent> = new ProjectorEvent();

	private readonly defaultToolButtons = {
		[WindowButtonTypes.MINIMIZE]: new ToolButton({icon: ICON_MINIMIZE, title : "Minimize", iconSize: 16} as DtoToolButton, noOpServerObjectChannel),
		[WindowButtonTypes.MAXIMIZE_RESTORE]: new ToolButton({icon: ICON_MAXIMIZE, title : "Maximize/Restore", iconSize: 16} as DtoToolButton, noOpServerObjectChannel),
		[WindowButtonTypes.CLOSE]: new ToolButton({icon: ICON_CLOSE, title : "Close", iconSize: 16} as DtoToolButton, noOpServerObjectChannel),
	};
	private readonly orderedDefaultToolButtonTypes = [
		WindowButtonTypes.MINIMIZE,
		WindowButtonTypes.MAXIMIZE_RESTORE,
		WindowButtonTypes.CLOSE
	];

	private $tabPanel: HTMLElement;
	private $leftButtonsWrapper: HTMLElement;
	private $rightButtonsWrapper: HTMLElement;
	private $dropDownButton: HTMLElement;
	private $dropDown: HTMLElement;
	private $dropButtonContainerLeft: HTMLElement;
	private $dropButtonContainerRight: HTMLElement;
	private $toolsContainer: HTMLElement;
	private $toolButtonContainer: HTMLElement;
	private $windowButtonContainer: HTMLElement;
	private $contentWrapper: HTMLElement;
	private $tabBar: HTMLElement;
	private $tabsContainer: HTMLElement;

	private leftTabs: Tab[] = [];
	private rightTabs: Tab[] = [];
	private selectedTab: Tab;
	private hideTabBarIfSingleTab: boolean;

	private toolButtons: ToolButton[] = [];
	private windowButtons: WindowButtonType[];

	private restoreFunction: (animationCallback?: () => void) => void;

	constructor(config: DtoTabPanel, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$tabPanel = parseHtml(`<div class="TabPanel">
    <div class="tab-panel-header teamapps-blurredBackgroundImage">
        <div class="background-color-div">
	        <div class="tab-button-container left"></div>
	        <div class="dropdown-button" tabindex="0"></div>
	        <div class="spacer"></div>
	        <div class="tab-button-container right"></div>
	        <div class="tools-container">
	            <div class="tool-button-container"></div>
	            <div class="window-button-container"></div>
			</div>  	
	        <div class="tab-panel-dropdown hidden teamapps-blurredBackgroundImage">
	            <div class="dropdown-button-container left"></div>
	            <div class="dropdown-button-container right"></div>
	        </div>
        </div>
    </div>
    <div class="tabpanel-content-wrapper-wrapper">
        <div class="tabpanel-content-wrapper"></div>
    </div>
</div>`);
		this.$tabBar = this.$tabPanel.querySelector<HTMLElement>(':scope >.tab-panel-header');
		this.$tabsContainer = this.$tabBar.querySelector<HTMLElement>(':scope >.background-color-div');
		this.$leftButtonsWrapper = this.$tabsContainer.querySelector<HTMLElement>(':scope >.tab-button-container.left');
		this.$rightButtonsWrapper = this.$tabsContainer.querySelector<HTMLElement>(':scope >.tab-button-container.right');
		this.$toolsContainer = this.$tabsContainer.querySelector<HTMLElement>(':scope >.tools-container');
		this.$toolButtonContainer = this.$toolsContainer.querySelector<HTMLElement>(':scope >.tool-button-container');
		this.$windowButtonContainer = this.$toolsContainer.querySelector<HTMLElement>(':scope >.window-button-container');
		this.$dropDownButton = this.$tabsContainer.querySelector<HTMLElement>(':scope >.dropdown-button');
		this.$dropDown = this.$tabsContainer.querySelector<HTMLElement>(':scope >.tab-panel-dropdown');
		this.$dropButtonContainerLeft = this.$dropDown.querySelector<HTMLElement>(':scope >.dropdown-button-container.left');
		this.$dropButtonContainerRight = this.$dropDown.querySelector<HTMLElement>(':scope >.dropdown-button-container.right');
		this.$contentWrapper = this.$tabPanel.querySelector<HTMLElement>(':scope .tabpanel-content-wrapper');

		this.setHideTabBarIfSingleTab(!!config.hideTabBarIfSingleTab);
		this.setTabStyle(config.tabStyle);

		for (let i = 0; config.tabs && i < config.tabs.length; i++) {
			this.addTab(config.tabs[i], false);
		}
		if (config.selectedTabId) {
			this.selectTab(config.selectedTabId, false);
		} else {
			this.selectFirstVisibleTab();
		}

		this.$dropDownButton.addEventListener("click", () => {
			if (!this.$dropDown.classList.contains('hidden')) {
				this.$dropDown.classList.add("hidden");
			} else {
				this.$dropDown.classList.remove("hidden");
				positionDropdown(this.$dropDownButton, this.$dropDown);
			}
		});
		this.$dropDownButton.addEventListener("blur", () => {
			this.$dropDown.classList.add("hidden");
		});

		if (config.toolButtons != null) {
			this.setToolButtons(config.toolButtons as ToolButton[]);
		}
		this.defaultToolButtons[WindowButtonTypes.MAXIMIZE_RESTORE].onClick.addListener(() => {
			if (this.restoreFunction == null) {
				this.maximize();
			} else {
				this.restore();
			}
		});
		this.orderedDefaultToolButtonTypes.forEach(windowButtonType => {
			this.defaultToolButtons[windowButtonType].onClick.addListener(() => {
				this.onWindowButtonClicked.fire({
					windowButton: windowButtonType
				});
			});
		});
		this.setWindowButtons(config.windowButtons);
		this.setFillTabBarWidth(config.fillTabBarWidth ?? false);
		this.setTabBarHeight(config.tabBarHeight);
	}

	public setMaximized(maximized: boolean) {
		if (maximized) {
			this.maximize();
		} else {
			this.restore();
		}
	}

	public maximize(): void {
		this.defaultToolButtons[WindowButtonTypes.MAXIMIZE_RESTORE].setIcon(ICON_RESTORE);
		this.restoreFunction = maximizeComponent(this);
	}

	public restore(): void {
		this.defaultToolButtons[WindowButtonTypes.MAXIMIZE_RESTORE].setIcon(ICON_MAXIMIZE);
		if (this.restoreFunction != null) {
			this.restoreFunction();
		}
		this.restoreFunction = null;
	}

	setFillTabBarWidth(fillTabBarWidth: boolean): any {
		this.config.fillTabBarWidth = fillTabBarWidth;
		this.$tabPanel.classList.toggle("fill-tab-bar-width", fillTabBarWidth);
		this.relayoutButtons();
	}

	setTabBarHeight(tabBarHeight: string): any {
		this.config.tabBarHeight = tabBarHeight;
		if (tabBarHeight) {
			this.$tabPanel.style.setProperty("--ta-tab-button-height", tabBarHeight);
		} else {
			this.$tabPanel.style.removeProperty("--ta-tab-button-height");
		}
		this.relayoutButtons();
	}

	public setHideTabBarIfSingleTab(hideTabBarIfSingleTab: boolean) {
		this.hideTabBarIfSingleTab = hideTabBarIfSingleTab;
		this.updateTabBarVisibility();
	}

	private getAllTabs(): Tab[] {
		return this.leftTabs.concat(this.rightTabs);
	}

	public doGetMainElement(): HTMLElement {
		return this.$tabPanel;
	}

	private _createTab(tabConfig: DtoTab, index: number = Number.MAX_SAFE_INTEGER): Tab {
		const $tabButton = this.createTabButton(tabConfig.id, tabConfig.icon, tabConfig.caption, tabConfig.closeable);
		const $dropDownTabButton = this.createTabButton(tabConfig.id, tabConfig.icon, tabConfig.caption, tabConfig.closeable);
		$tabButton.addEventListener("mousedown", () => this.selectTab(tabConfig.id, true));
		$dropDownTabButton.addEventListener("mousedown", () => this.selectTab(tabConfig.id, true));

		const $tabContent = parseHtml(`<div class="tab-content-wrapper" data-tab-name="${tabConfig.id}">
                        <div class="tab-toolbar-container"></div>
                        <div class="tab-component-container"></div>
                    </div>`);
		this.$contentWrapper.appendChild($tabContent);

		let tab: Tab = {
			config: tabConfig,
			$button: $tabButton,
			$dropDownTabButton: $dropDownTabButton,
			$wrapper: $tabContent,
			$toolbarContainer: $tabContent.querySelector<HTMLElement>(":scope .tab-toolbar-container"),
			$contentContainer: $tabContent.querySelector<HTMLElement>(":scope .tab-component-container"),
			toolbar: null,
			contentComponent: null,
			visible: tabConfig.visible
		};
		this.putTabButtonsToIndex(tab, index);

		if (tabConfig.toolbar) {
			this.setTabToolbarInternal(tab, tabConfig.toolbar as Toolbar);
		}

		return tab;
	}

	private putTabButtonsToIndex(tab: Tab, index: number) {
		if (tab.config.rightSide) {
			insertAtIndex(this.$rightButtonsWrapper, tab.$button, index);
			insertAtIndex(this.$dropButtonContainerRight, tab.$dropDownTabButton, index);
		} else {
			insertAtIndex(this.$leftButtonsWrapper, tab.$button, index);
			insertAtIndex(this.$dropButtonContainerLeft, tab.$dropDownTabButton, index);
		}
	}

	private createTabButton(tabId: string, icon: string, caption: string, closeable: boolean) {
		const $tabButton = parseHtml(`<div class="tab-button" data-tab-name="${tabId}" draggable="true">
                     ${icon ? `<div class="tab-button-icon"><div class="img img-16" style="background-image: url('${icon}');"></div></div>` : ''}
                     <div class="tab-button-caption">${caption}</div>                                                                                                                          	
                </div>`);

		if (closeable) {
			let closeButtonHtml = `<div class="tab-button-close-button">
                        <div class="img img-12 ta-icon-close"></div>
                    </div>`;
			const $closeButton1 = $tabButton.appendChild(parseHtml(closeButtonHtml));
			$closeButton1.addEventListener("mousedown", () => {
				this.removeTab(tabId);
				this.onTabClosed.fire({
					tabId: tabId
				});
			});
		}
		return $tabButton;
	}

	public selectTab(tabId: string, sendSelectionEvent = false) {
		let tab = this.getTabById(tabId);
		if (!tab) {
			console.error("Cannot select non-existing tab " + tabId);
			return;
		}
		this.selectedTab = tab;
		[this.$leftButtonsWrapper, this.$rightButtonsWrapper, this.$dropButtonContainerLeft, this.$dropButtonContainerRight]
			.forEach(el => el.querySelectorAll<HTMLElement>(':scope >.tab-button').forEach(el => el.classList.remove('selected')));
		tab.$button.classList.add('selected');
		tab.$dropDownTabButton.classList.add('selected');

		this.$contentWrapper.querySelectorAll<HTMLElement>(':scope >.tab-content-wrapper').forEach(el => el.classList.remove('selected'));
		tab.$wrapper.classList.add('selected');
		if (sendSelectionEvent) {
			this.onTabSelected.fire({
				tabId: tabId
			});
		}
		if (this.selectedTab.contentComponent == null) {
			this.onTabNeedsRefresh.fire({
				tabId: tabId
			});
		}
	}

	public setTabContent(tabId: string, content: Component, fireLazyLoadEventIfNeeded = false) {
		const tab = this.getTabById(tabId);
		const $tabContentContainer = tab.$contentContainer;
		if (tab.contentComponent) {
			isEmptyable(tab.contentComponent) && tab.contentComponent.onEmptyStateChanged.removeListener(this.onChildEmptyStateChanged.bind(this));
			$tabContentContainer.innerHTML = '';
		}
		tab.contentComponent = content;
		if (tab.contentComponent) {
			isEmptyable(tab.contentComponent) && tab.contentComponent.onEmptyStateChanged.addListener(this.onChildEmptyStateChanged.bind(this));
			$tabContentContainer.appendChild(content.getMainElement());
		}

		this.onChildEmptyStateChanged();

		// after (!!) the parent has been informed about the empty state change, we should think about setting attached (and resizing!!!)
		if (this.selectedTab && this.selectedTab.config.id === tabId) {
			if (!tab.contentComponent && tab.config.lazyLoading && fireLazyLoadEventIfNeeded) {
				this.onTabNeedsRefresh.fire({
					tabId: tabId
				});
			}
		}

		this.updateEmptyState();
	}

	setTabConfiguration(tabId: string, icon: string, caption: string, closeable: boolean, visible: boolean, rightSide: boolean): void {
		let tab = this.getTabById(tabId);
		tab.$button.innerHTML = '';
		tab.$button.append(...Array.from(this.createTabButton(tabId, icon, caption, closeable).querySelectorAll<HTMLElement>(":scope >*")));
		tab.$dropDownTabButton.innerHTML = '';
		tab.$dropDownTabButton.append(...Array.from(this.createTabButton(tabId, icon, caption, closeable).querySelectorAll<HTMLElement>(":scope >*")));
		if (rightSide && !tab.config.rightSide) {
			this.$rightButtonsWrapper.appendChild(tab.$button);
			this.$dropButtonContainerRight.appendChild(tab.$dropDownTabButton);
		} else if (!rightSide && tab.config.rightSide) {
			this.$leftButtonsWrapper.appendChild(tab.$button);
			this.$dropButtonContainerLeft.appendChild(tab.$dropDownTabButton);
		}
		tab.config.id = tabId;
		tab.config.icon = icon;
		tab.config.caption = caption;
		tab.config.closeable = closeable;
		tab.config.rightSide = rightSide;

		if (tab.visible != visible) {
			tab.visible = visible;
			this.onChildEmptyStateChanged();
		}
	}

	public addTab(tabConfig: DtoTab, select: boolean, index = Number.MAX_SAFE_INTEGER) {
		this.removeTab(tabConfig.id, false);
		let tab = this._createTab(tabConfig, index);
		if (tabConfig.rightSide) {
			this.rightTabs.push(tab);
		} else {
			this.leftTabs.push(tab);
		}
		this.setTabContent(tabConfig.id, tabConfig.content as Component, true);
		if (select || this.getAllTabs().length === 1) {
			this.selectTab(tabConfig.id, false);
		}
		let tabBarVisibilityChanged = this.updateTabBarVisibility();
		if (tabBarVisibilityChanged) {
		} else {
			this.relayoutButtons();
		}
		this.updateEmptyState();
	}

	moveTab(tabId: string, index: number) {
		let tab = this.getTabById(tabId);
		this.putTabButtonsToIndex(tab, index);
	}

	public setTabToolbar(tabId: string, toolbar: Toolbar) {
		let tab = this.getTabById(tabId);
		if (tab) {
			this.setTabToolbarInternal(tab, toolbar);
		}
	}

	private setTabToolbarInternal(tab: Tab, toolbar: Toolbar) {
		if (tab.toolbar != null) {
			tab.toolbar.getMainElement().remove();
		}
		tab.toolbar = toolbar;
		if (toolbar) {
			tab.$toolbarContainer.append(toolbar.getMainElement());
		}
	}

	public removeTab(tabId: string, warnIfNotFound = true) {
		let tab = this.getTabById(tabId, warnIfNotFound);

		if (!tab) return;

		tab.contentComponent != null && isEmptyable(tab.contentComponent) && tab.contentComponent.onEmptyStateChanged.removeListener(this.onChildEmptyStateChanged.bind(this));
		tab.$button.remove();
		tab.$dropDownTabButton.remove();
		tab.$wrapper.remove();

		this.leftTabs = this.leftTabs.filter(tab => tab.config.id !== tabId);
		this.rightTabs = this.rightTabs.filter(tab => tab.config.id !== tabId);

		if (tab === this.selectedTab) {
			this.selectFirstVisibleTab();
		}

		let tabBarVisibilityChanged = this.updateTabBarVisibility();
		if (tabBarVisibilityChanged) {
		} else {
			this.relayoutButtons();
		}
		this.updateEmptyState();
	}

	private selectFirstVisibleTab() {
		if (this.getVisibleTabs().length > 0) {
			this.selectTab(this.getVisibleTabs()[0].config.id, true);
		} else if (this.selectedTab != null) {
			this.selectedTab = null;
			this.onTabSelected.fire({
				tabId: null
			});
		}
	}

	@bind
	private onChildEmptyStateChanged() {
		if (!this.selectedTab || this.tabIsDeFactoEmpty(this.selectedTab)) {
			this.selectFirstVisibleTab();
		}
		this.relayoutButtons();
		this.updateTabBarVisibility();
		this.updateEmptyState();
	}

	private updateEmptyState() {
		this.onEmptyStateChanged.fireIfChanged(this.empty);
	}

	private getTabById(tabId: string, warnIfNull: boolean = true): Tab {
		const tab = this.getAllTabs().filter(tab => tab.config.id === tabId)[0];
		if (tab == null && warnIfNull) {
			console.error(`Cannot find tab by id: ${tabId}`);
		}
		return tab;
	}

	private updateTabBarVisibility(): boolean {
		let wasHidden = this.$tabBar.classList.contains('hidden');
		let isHidden = this.hideTabBarIfSingleTab && this.getVisibleTabs().length <= 1;
		this.$tabBar.classList.toggle("hidden", isHidden);
		this.$tabBar.offsetHeight; // trigger reflow!
		return wasHidden != isHidden;
	}

	get empty(): boolean {
		return this.getNumberOfVisibleTabButtons() === 0;
	}

	private getVisibleTabs(): Tab[] {
		return this.getAllTabs()
			.filter(t => !this.tabIsDeFactoEmpty(t));

	}

	public getNumberOfVisibleTabButtons() {
		return this.getVisibleTabs().length;
	}

	private tabIsDeFactoEmpty(tab: Tab) {
		const tabIsInvisible = !tab.visible;
		const tabIsEmpty = tab.contentComponent == null && !tab.config.lazyLoading;
		const contentIsEmpty = tab.contentComponent != null && isEmptyable(tab.contentComponent) && tab.contentComponent.empty;
		return tabIsInvisible || tabIsEmpty || contentIsEmpty;
	}

	public getSelectedTabId() {
		return this.selectedTab && this.selectedTab.config.id;
	}

	public setToolButtons(toolButtons: ToolButton[]) {
		this.$toolButtonContainer.innerHTML = '';
		this.$toolButtonContainer.classList.toggle("hidden", !toolButtons || toolButtons.length === 0);
		this.toolButtons = toolButtons;
		toolButtons.forEach(toolButton => {
			this.$toolButtonContainer.appendChild(toolButton.getMainElement());
		});
		this.relayoutButtons();
	}

	public getToolButtons() {
		return Object.values(this.toolButtons);
	}

	public setWindowButtons(buttonTypes: WindowButtonType[]): void {
		this.windowButtons = [];
		this.$windowButtonContainer.innerHTML = '';
		buttonTypes?.forEach(toolButton => {
				this.addWindowButton(toolButton);
			});
	}

	private addWindowButton(toolButtonType: WindowButtonType) {
		if (this.windowButtons.filter(tb => tb === toolButtonType).length > 0){
			this.removeWindowButton(toolButtonType);
		}
		this.$windowButtonContainer.classList.remove("hidden");
		this.windowButtons.push(toolButtonType);
		const button = this.defaultToolButtons[toolButtonType];
		if (this.$windowButtonContainer.children.length === 0) {
			prependChild(this.$windowButtonContainer, button.getMainElement());
		} else {
			let index = this.windowButtons
				.sort((a, b) =>this.orderedDefaultToolButtonTypes.indexOf(a) - this.orderedDefaultToolButtonTypes.indexOf(b))
				.indexOf(toolButtonType);
			if (index >= this.$windowButtonContainer.childNodes.length) {
				this.$windowButtonContainer.appendChild(button.getMainElement());
			} else {
				insertBefore(button.getMainElement(), this.$windowButtonContainer.children[index]);
			}
		}
		this.relayoutButtons();
	}

	public removeWindowButton(uiToolButton: WindowButtonType) {
		this.defaultToolButtons[uiToolButton].getMainElement().remove();
		this.windowButtons = this.windowButtons.filter(tb => tb !== uiToolButton);
		if (this.windowButtons.length === 0) {
			this.$windowButtonContainer.classList.add("hidden");
		}
	}

	public setTabStyle(tabStyle: TabPanelTabStyle) {
		this.$tabPanel.classList.remove('tab-style-ears', 'tab-style-blocks');
		this.$tabPanel.classList.add(tabStyle === TabPanelTabStyles.EARS ? 'tab-style-ears' : 'tab-style-blocks');
		this.$tabBar.classList.toggle("teamapps-blurredBackgroundImage", tabStyle === TabPanelTabStyles.BLOCKS);
		this.onResize();
	}

	@executeAfterAttached(true)
	public onResize(): void {
		this.relayoutButtons();
	}

	@executeAfterAttached(true)
	private relayoutButtons() {
		if (this.$tabBar.classList.contains('hidden')) {
			return;
		}
		this.$toolsContainer.classList.toggle("hidden", this.toolButtons.length === 0 && this.windowButtons.length === 0);
		let availableWidth = this.$tabsContainer.offsetWidth - this.$dropDownButton.offsetWidth - this.$toolsContainer.offsetWidth;
		let sumOfButtonWidths = 0;
		this.getAllTabs().forEach(tab => {
			let tabFilled = !this.tabIsDeFactoEmpty(tab);

			if (tabFilled) {
				if (!tab.buttonWidth) {
					tab.$button.style.flex = "0 0 auto";
					tab.buttonWidth = tab.$button.offsetWidth;
					tab.$button.style.removeProperty("flex");
				}
				sumOfButtonWidths += tab.buttonWidth;

				if (sumOfButtonWidths < availableWidth) {
					tab.$button.classList.remove("hidden");
					tab.$dropDownTabButton.classList.add("hidden");
				} else {
					tab.$button.classList.add("hidden");
					tab.$dropDownTabButton.classList.remove("hidden");
				}
			} else {
				tab.$button.classList.add("hidden");
				tab.$dropDownTabButton.classList.add("hidden");
			}
		});
		this.$dropDownButton.classList.toggle("hidden", sumOfButtonWidths <= availableWidth);
	}

}


