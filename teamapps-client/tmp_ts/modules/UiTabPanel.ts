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

import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiTabConfig} from "../generated/UiTabConfig";
import {bind} from "./util/Bind";
import {Emptyable, isEmptyable} from "./util/Emptyable";
import {UiToolButton} from "./micro-components/UiToolButton";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiDropDown} from "./micro-components/UiDropDown";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {
	UiTabPanel_TabClosedEvent,
	UiTabPanel_TabNeedsRefreshEvent,
	UiTabPanel_TabSelectedEvent,
	UiTabPanel_WindowButtonClickedEvent,
	UiTabPanelCommandHandler,
	UiTabPanelConfig,
	UiTabPanelEventSource
} from "../generated/UiTabPanelConfig";
import {createUiToolButtonConfig} from "../generated/UiToolButtonConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTabPanelTabStyle} from "../generated/UiTabPanelTabStyle";
import {insertAtIndex, insertBefore, maximizeComponent, parseHtml, prependChild} from "./Common";
import {UiWindowButtonType} from "../generated/UiWindowButtonType";
import {StaticIcons} from "./util/StaticIcons";
import {UiComponent} from "./UiComponent";


interface Tab {
	config: UiTabConfig;
	$button: HTMLElement;
	$wrapper: HTMLElement;
	$dropDownTabButton: HTMLElement;
	$toolbarContainer: HTMLElement;
	$contentContainer: HTMLElement;
	toolbar: UiToolbar;
	contentComponent: UiComponent<UiComponentConfig>;
	buttonWidth?: number;
	visible: boolean;
}

export class UiTabPanel extends AbstractUiComponent<UiTabPanelConfig> implements UiTabPanelCommandHandler, UiTabPanelEventSource, Emptyable {

	public readonly onTabSelected: TeamAppsEvent<UiTabPanel_TabSelectedEvent> = new TeamAppsEvent<UiTabPanel_TabSelectedEvent>();

	public readonly onTabNeedsRefresh: TeamAppsEvent<UiTabPanel_TabNeedsRefreshEvent> = new TeamAppsEvent<UiTabPanel_TabNeedsRefreshEvent>();
	public readonly onTabClosed: TeamAppsEvent<UiTabPanel_TabClosedEvent> = new TeamAppsEvent<UiTabPanel_TabClosedEvent>();
	public readonly onEmptyStateChanged: TeamAppsEvent<boolean> = new TeamAppsEvent();

	public readonly onWindowButtonClicked: TeamAppsEvent<UiTabPanel_WindowButtonClickedEvent> = new TeamAppsEvent();

	private readonly defaultToolButtons = {
		[UiWindowButtonType.MINIMIZE]: new UiToolButton(createUiToolButtonConfig(StaticIcons.MINIMIZE, "Minimize", {debuggingId: "window-button-minimize"}), this._context),
		[UiWindowButtonType.MAXIMIZE_RESTORE]: new UiToolButton(createUiToolButtonConfig(StaticIcons.MAXIMIZE, "Maximize/Restore", {debuggingId: "window-button-maximize"}), this._context),
		[UiWindowButtonType.CLOSE]: new UiToolButton(createUiToolButtonConfig(StaticIcons.CLOSE, "Close", {debuggingId: "window-button-close"}), this._context),
	};
	private readonly orderedDefaultToolButtonTypes = [
		UiWindowButtonType.MINIMIZE,
		UiWindowButtonType.MAXIMIZE_RESTORE,
		UiWindowButtonType.CLOSE
	];

	private $tabPanel: HTMLElement;
	private $leftButtonsWrapper: HTMLElement;
	private $rightButtonsWrapper: HTMLElement;
	private $dropDownButton: HTMLElement;
	private $dropDown: HTMLElement;
	private $dropButtonContainerLeft: HTMLElement;
	private $dropButtonContainerRight: HTMLElement;
	private $toolTabButton: HTMLElement;
	private $toolButtonContainer: HTMLElement;
	private $windowButtonContainer: HTMLElement;
	private $contentWrapper: HTMLElement;
	private $tabBar: HTMLElement;
	private $tabsContainer: HTMLElement;

	private leftTabs: Tab[] = [];
	private rightTabs: Tab[] = [];
	private selectedTab: Tab;
	private hideTabBarIfSingleTab: boolean;

	private toolButtons: { [id: string]: UiToolButton } = {};
	private toolButtonDropDown: UiDropDown;
	private windowButtons: UiWindowButtonType[];

	private restoreFunction: (animationCallback?: () => void) => void;

	constructor(config: UiTabPanelConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$tabPanel = parseHtml(`<div class="UiTabPanel">
    <div class="tab-panel-header teamapps-blurredBackgroundImage">
        <div class="background-color-div">
	        <div class="tab-button-container left"></div>
	        <div class="dropdown-button" tabindex="0"></div>
	        <div class="spacer"></div>
	        <div class="tab-button-container right"></div>
	        <div class="tab-button tool-tab-button">
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
		this.$toolTabButton = this.$tabsContainer.querySelector<HTMLElement>(':scope >.tool-tab-button');
		this.$toolButtonContainer = this.$toolTabButton.querySelector<HTMLElement>(':scope >.tool-button-container');
		this.$windowButtonContainer = this.$toolTabButton.querySelector<HTMLElement>(':scope >.window-button-container');
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
			if ($(this.$dropDown).is(':visible')) {
				this.$dropDown.classList.add("hidden");
			} else {
				this.$dropDown.classList.remove("hidden");
				$(this.$dropDown).position({
					my: "right top",
					at: "right bottom",
					of: this.$dropDownButton
				});
			}
		});
		this.$dropDownButton.addEventListener("blur", () => {
			this.$dropDown.classList.add("hidden");
		});

		if (config.toolButtons != null) {
			this.setToolButtons(config.toolButtons as UiToolButton[]);
		}
		this.toolButtonDropDown = new UiDropDown();

		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].onClicked.addListener(() => {
			if (this.restoreFunction == null) {
				this.maximize();
			} else {
				this.restore();
			}
		});
		this.orderedDefaultToolButtonTypes.forEach(windowButtonType => {
			this.defaultToolButtons[windowButtonType].onClicked.addListener(() => {
				this.onWindowButtonClicked.fire({
					windowButton: windowButtonType
				});
			});
		});
		this.setWindowButtons(config.windowButtons);
	}

	public setMaximized(maximized: boolean) {
		if (maximized) {
			this.maximize();
		} else {
			this.restore();
		}
	}

	public maximize(): void {
		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].setIcon(StaticIcons.RESTORE);
		this.restoreFunction = maximizeComponent(this);
	}

	public restore(): void {
		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].setIcon(StaticIcons.MAXIMIZE);
		if (this.restoreFunction != null) {
			this.restoreFunction();
		}
		this.restoreFunction = null;
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

	private _createTab(tabConfig: UiTabConfig, index: number = Number.MAX_SAFE_INTEGER): Tab {
		const $tabButton = this.createTabButton(tabConfig.tabId, tabConfig.icon, tabConfig.caption, tabConfig.closeable);
		const $dropDownTabButton = this.createTabButton(tabConfig.tabId, tabConfig.icon, tabConfig.caption, tabConfig.closeable);
		$tabButton.addEventListener("mousedown", () => this.selectTab(tabConfig.tabId, true));
		$dropDownTabButton.addEventListener("mousedown", () => this.selectTab(tabConfig.tabId, true));

		const $tabContent = parseHtml(`<div class="tab-content-wrapper" data-tab-name="${tabConfig.tabId}">
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
			this.setTabToolbarInternal(tab, tabConfig.toolbar as UiToolbar);
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
                     <div class="tab-button-filler"></div>
                </div>`);

		if (closeable) {
			const closeIconPath = "/resources/window-close-grey.png";
			let closeButtonHtml = `<div class="tab-button-close-button">
                        <div class="img ${this._context.config.optimizedForTouch ? 'img-16' : 'img-12'}" style="background-image: url('${closeIconPath}');"></div>
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
			this.logger.error("Cannot select non-existing tab " + this._config.id + "~" + tabId);
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

	public setTabContent(tabId: string, content: UiComponent<UiComponentConfig>, fireLazyLoadEventIfNeeded = false) {
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
		if (this.selectedTab && this.selectedTab.config.tabId === tabId) {
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
		tab.config.tabId = tabId;
		tab.config.icon = icon;
		tab.config.caption = caption;
		tab.config.closeable = closeable;
		tab.config.rightSide = rightSide;

		if (tab.visible != visible) {
			tab.visible = visible;
			this.onChildEmptyStateChanged();
		}
	}

	public addTab(tabConfig: UiTabConfig, select: boolean, index = Number.MAX_SAFE_INTEGER) {
		this.removeTab(tabConfig.tabId, false);
		let tab = this._createTab(tabConfig, index);
		if (tabConfig.rightSide) {
			this.rightTabs.push(tab);
		} else {
			this.leftTabs.push(tab);
		}
		this.setTabContent(tabConfig.tabId, tabConfig.content as UiComponent, true);
		if (select || this.getAllTabs().length === 1) {
			this.selectTab(tabConfig.tabId, false);
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

	public setTabToolbar(tabId: string, toolbar: UiToolbar) {
		let tab = this.getTabById(tabId);
		if (tab) {
			this.setTabToolbarInternal(tab, toolbar);
		}
	}

	private setTabToolbarInternal(tab: Tab, toolbar: UiToolbar) {
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

		this.leftTabs = this.leftTabs.filter(tab => tab.config.tabId !== tabId);
		this.rightTabs = this.rightTabs.filter(tab => tab.config.tabId !== tabId);

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
			this.selectTab(this.getVisibleTabs()[0].config.tabId, true); // was !this._context.executingCommand
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
		const tab = this.getAllTabs().filter(tab => tab.config.tabId === tabId)[0];
		if (tab == null && warnIfNull) {
			this.logger.error(`Cannot find tab by id: ${tabId}`);
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
		return this.selectedTab && this.selectedTab.config.tabId;
	}

	public setToolButtons(toolButtons: UiToolButton[]) {
		this.$toolButtonContainer.innerHTML = '';
		this.$toolButtonContainer.classList.toggle("hidden", !toolButtons || toolButtons.length === 0);
		this.toolButtons = {};
		toolButtons.forEach(toolButton => {
			this.$toolButtonContainer.appendChild(toolButton.getMainElement());
			this.toolButtons[toolButton.getId()] = toolButton;
		});
		this.relayoutButtons();
	}

	public getToolButtons() {
		return Object.values(this.toolButtons);
	}

	public setWindowButtons(buttonTypes:UiWindowButtonType[]):void{
		this.windowButtons = [];
		this.$windowButtonContainer.innerHTML = '';
		if (buttonTypes && buttonTypes.length > 0) {
			buttonTypes.forEach(toolButton => {
				this.addWindowButton(toolButton);
			});
		} else {
			this.windowButtons.slice().forEach(button => this.removeWindowButton(button));
		}
	}

	private addWindowButton(toolButtonType: UiWindowButtonType) {
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

	public removeWindowButton(uiToolButton: UiWindowButtonType) {
		this.defaultToolButtons[uiToolButton].getMainElement().remove();
		this.windowButtons = this.windowButtons.filter(tb => tb !== uiToolButton);
		if (this.windowButtons.length === 0) {
			this.$windowButtonContainer.classList.add("hidden");
		}
	}

	public setTabStyle(tabStyle: UiTabPanelTabStyle) {
		this.$tabPanel.classList.remove('tab-style-ears', 'tab-style-blocks');
		this.$tabPanel.classList.add(tabStyle === UiTabPanelTabStyle.EARS ? 'tab-style-ears' : 'tab-style-blocks');
		this.$tabBar.classList.toggle("teamapps-blurredBackgroundImage", tabStyle === UiTabPanelTabStyle.BLOCKS);
		this.onResize();
	}

	@executeWhenFirstDisplayed(true)
	public onResize(): void {
		this.relayoutButtons();
	}

	@executeWhenFirstDisplayed(true)
	private relayoutButtons() {
		if (this.$tabBar.classList.contains('hidden')) {
			return;
		}
		let availableWidth = $(this.$tabsContainer).width() - this.$dropDownButton.offsetWidth - this.$toolTabButton.offsetWidth;
		let sumOfButtonWidths = 0;
		this.getAllTabs().forEach(tab => {
			let tabFilled = !this.tabIsDeFactoEmpty(tab);

			if (tabFilled) {
				if (!tab.buttonWidth) {
					tab.buttonWidth = tab.$button.offsetWidth
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

TeamAppsUiComponentRegistry.registerComponentClass("UiTabPanel", UiTabPanel);
