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
import * as $ from "jquery";
import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiTabConfig} from "../generated/UiTabConfig";
import {bind} from "./util/Bind";
import {Emptyable, isEmptyable} from "./util/Emptyable";
import {UiToolButton} from "./micro-components/UiToolButton";
import {UiComponent} from "./UiComponent";
import {UiDropDown} from "./micro-components/UiDropDown";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenAttached} from "./util/ExecuteWhenAttached";
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
import {EventFactory} from "../generated/EventFactory";
import {UiTabPanelTabStyle} from "../generated/UiTabPanelTabStyle";
import {insertAtIndex, maximizeComponent} from "./Common";
import {UiWindowButtonType} from "../generated/UiWindowButtonType";
import {StaticIcons} from "./util/StaticIcons";


interface Tab {
	config: UiTabConfig;
	$button: JQuery;
	$wrapper: JQuery;
	$dropDownTabButton: JQuery;
	$toolbarContainer: JQuery;
	$contentContainer: JQuery;
	toolbar: UiToolbar;
	contentComponent: UiComponent<UiComponentConfig>;
	buttonWidth?: number;
	visible: boolean;
}

export class UiTabPanel extends UiComponent<UiTabPanelConfig> implements UiTabPanelCommandHandler, UiTabPanelEventSource, Emptyable {

	public readonly onTabSelected: TeamAppsEvent<UiTabPanel_TabSelectedEvent> = new TeamAppsEvent<UiTabPanel_TabSelectedEvent>(this);

	public readonly onTabNeedsRefresh: TeamAppsEvent<UiTabPanel_TabNeedsRefreshEvent> = new TeamAppsEvent<UiTabPanel_TabNeedsRefreshEvent>(this);
	public readonly onTabClosed: TeamAppsEvent<UiTabPanel_TabClosedEvent> = new TeamAppsEvent<UiTabPanel_TabClosedEvent>(this);
	public readonly onEmptyStateChanged: TeamAppsEvent<boolean> = new TeamAppsEvent(this);

	public readonly onWindowButtonClicked: TeamAppsEvent<UiTabPanel_WindowButtonClickedEvent> = new TeamAppsEvent(this);

	private readonly defaultToolButtons = {
		[UiWindowButtonType.MINIMIZE]: new UiToolButton(createUiToolButtonConfig("MINIMIZE", StaticIcons.MINIMIZE, "Minimize"), this._context),
		[UiWindowButtonType.MAXIMIZE_RESTORE]: new UiToolButton(createUiToolButtonConfig("MAXIMIZE_RESTORE", StaticIcons.MAXIMIZE, "Maximize/Restore"), this._context),
		[UiWindowButtonType.CLOSE]: new UiToolButton(createUiToolButtonConfig("CLOSE", StaticIcons.CLOSE, "Close"), this._context),
	};
	private readonly orderedDefaultToolButtonTypes = [
		UiWindowButtonType.MINIMIZE,
		UiWindowButtonType.MAXIMIZE_RESTORE,
		UiWindowButtonType.CLOSE
	];

	private $tabPanel: JQuery;
	private $leftButtonsWrapper: JQuery;
	private $rightButtonsWrapper: JQuery;
	private $dropDownButton: JQuery;
	private $dropDown: JQuery;
	private $dropButtonContainerLeft: JQuery;
	private $dropButtonContainerRight: JQuery;
	private $toolTabButton: JQuery;
	private $toolButtonContainer: JQuery;
	private $windowButtonContainer: JQuery;
	private $contentWrapper: JQuery;
	private $tabBar: JQuery;
	private $tabsContainer: JQuery;

	private leftTabs: Tab[] = [];
	private rightTabs: Tab[] = [];
	private selectedTab: Tab;
	private hideTabBarIfSingleTab: boolean;

	private toolButtons: { [id: string]: UiToolButton } = {};
	private toolButtonDropDown: UiDropDown;
	private windowButtons: UiWindowButtonType[];

	private restoreFunction: (animationCallback: () => void) => void;

	constructor(config: UiTabPanelConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$tabPanel = $(`<div id="${config.id}" class="UiTabPanel">
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
		this.$tabBar = this.$tabPanel.find('>.tab-panel-header');
		this.$tabsContainer = this.$tabBar.find('>.background-color-div');
		this.$leftButtonsWrapper = this.$tabsContainer.find('>.tab-button-container.left');
		this.$rightButtonsWrapper = this.$tabsContainer.find('>.tab-button-container.right');
		this.$toolTabButton = this.$tabsContainer.find('>.tool-tab-button');
		this.$toolButtonContainer = this.$toolTabButton.find('>.tool-button-container');
		this.$windowButtonContainer = this.$toolTabButton.find('>.window-button-container');
		this.$dropDownButton = this.$tabsContainer.find('>.dropdown-button');
		this.$dropDown = this.$tabsContainer.find('>.tab-panel-dropdown');
		this.$dropButtonContainerLeft = this.$dropDown.find('>.dropdown-button-container.left');
		this.$dropButtonContainerRight = this.$dropDown.find('>.dropdown-button-container.right');
		this.$contentWrapper = this.$tabPanel.find('.tabpanel-content-wrapper');

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

		this.$dropDownButton.click(() => {
			if (this.$dropDown.is(':visible')) {
				this.$dropDown.addClass("hidden");
			} else {
				this.$dropDown.removeClass("hidden");
				this.$dropDown.position({
					my: "right top",
					at: "right bottom",
					of: this.$dropDownButton
				});
			}
		}).blur(() => {
			this.$dropDown.addClass("hidden");
		});

		if (config.toolButtons != null) {
			this.setToolButtons(config.toolButtons);
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
				this.onWindowButtonClicked.fire(EventFactory.createUiTabPanel_WindowButtonClickedEvent(this.getId(), windowButtonType));
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
		this.restoreFunction = maximizeComponent(this, () => this.reLayout(true));
	}

	public restore(): void {
		this.defaultToolButtons[UiWindowButtonType.MAXIMIZE_RESTORE].setIcon(StaticIcons.MAXIMIZE);
		if (this.restoreFunction != null) {
			this.restoreFunction(() => this.reLayout(true));
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

	public getMainDomElement(): JQuery {
		return this.$tabPanel;
	}

	private _createTab(tabConfig: UiTabConfig, index: number = Number.MAX_SAFE_INTEGER): Tab {
		const $tabButton = this.createTabButton(tabConfig.tabId, tabConfig.icon, tabConfig.caption, tabConfig.closeable);
		const $dropDownTabButton = this.createTabButton(tabConfig.tabId, tabConfig.icon, tabConfig.caption, tabConfig.closeable);
		$tabButton.add($dropDownTabButton).mousedown(() => {
			this.selectTab(tabConfig.tabId, true);
		});

		const $tabContent = $(`<div class="tab-content-wrapper" data-tab-name="${tabConfig.tabId}">
                        <div class="tab-toolbar-container"/>
                        <div class="tab-component-container"/>
                    </div>`)
			.appendTo(this.$contentWrapper);

		let tab: Tab = {
			config: tabConfig,
			$button: $tabButton,
			$dropDownTabButton: $dropDownTabButton,
			$wrapper: $tabContent,
			$toolbarContainer: $tabContent.find(".tab-toolbar-container"),
			$contentContainer: $tabContent.find(".tab-component-container"),
			toolbar: null,
			contentComponent: null,
			visible: tabConfig.visible
		};
		this.putTabButtonsToIndex(tab, index);

		if (tabConfig.toolbar) {
			this.setTabToolbarInternal(tab, tabConfig.toolbar);
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

	private createTabButton(tabId: string, iconName: string, caption: string, closeable: boolean) {
		const $tabButton = $(`<div class="tab-button" data-tab-name="${tabId}" draggable="true">
                     ${iconName ? `<div class="tab-button-icon"><div class="img img-16" style="background-image: url(${this._context.getIconPath(iconName, 16)});"/></div>` : ''}
                     <div class="tab-button-caption">${caption}</div>
                     <div class="tab-button-filler"/>
                </div>`);

		if (closeable) {
			const closeIconPath = "/resources/window-close-grey.png";
			let closeButtonHtml = `<div class="tab-button-close-button">
                        <div class="img ${this._context.config.optimizedForTouch ? 'img-16' : 'img-12'}" style="background-image: url(${closeIconPath});"/>
                    </div>`;
			const $closeButton1 = $(closeButtonHtml).appendTo($tabButton);
			$closeButton1.mousedown(() => {
				this.removeTab(tabId);
				this.onTabClosed.fire(EventFactory.createUiTabPanel_TabClosedEvent(this._config.id, tabId));
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
		this.$leftButtonsWrapper
			.add(this.$rightButtonsWrapper)
			.add(this.$dropButtonContainerLeft)
			.add(this.$dropButtonContainerRight)
			.find('>.tab-button').removeClass('selected');
		tab.$button.addClass('selected');
		tab.$dropDownTabButton.addClass('selected');

		this.$contentWrapper.find('>.tab-content-wrapper').removeClass('selected');
		tab.$wrapper.addClass('selected');
		if (sendSelectionEvent) {
			this.onTabSelected.fire(EventFactory.createUiTabPanel_TabSelectedEvent(this.getId(), tabId));
		}
		if (this.selectedTab.contentComponent == null) {
			this.onTabNeedsRefresh.fire(EventFactory.createUiTabPanel_TabNeedsRefreshEvent(this.getId(), tabId));
		}
		if (tab.toolbar) {
			if (!tab.toolbar.attachedToDom) {
				tab.toolbar.attachedToDom = this.attachedToDom;
			} else {
				tab.toolbar.reLayout();
			}
		}
		if (tab.contentComponent) {
			if (!tab.contentComponent.attachedToDom) {
				tab.contentComponent.attachedToDom = this.attachedToDom;
			} else {
				tab.contentComponent.reLayout();
			}
		}
	}

	public setTabContent(tabId: string, content: UiComponent<UiComponentConfig>, fireLazyLoadEventIfNeeded = false) {
		const tab = this.getTabById(tabId);
		const $tabContentContainer = tab.$contentContainer;
		if (tab.contentComponent) {
			isEmptyable(tab.contentComponent) && tab.contentComponent.onEmptyStateChanged.removeListener(this.onChildEmptyStateChanged.bind(this));
			$tabContentContainer[0].innerHTML = '';
		}
		tab.contentComponent = content;
		if (tab.contentComponent) {
			isEmptyable(tab.contentComponent) && tab.contentComponent.onEmptyStateChanged.addListener(this.onChildEmptyStateChanged.bind(this));
			content.getMainDomElement().appendTo($tabContentContainer);
		}

		this.onChildEmptyStateChanged();

		// after (!!) the parent has been informed about the empty state change, we should think about setting attached (and resizing!!!)
		if (this.selectedTab && this.selectedTab.config.tabId === tabId) {
			if (tab.contentComponent) {
				tab.contentComponent.attachedToDom = this.attachedToDom;
			} else if (tab.config.lazyLoading && fireLazyLoadEventIfNeeded) {
				this.onTabNeedsRefresh.fire(EventFactory.createUiTabPanel_TabNeedsRefreshEvent(this.getId(), tabId));
			}
		}

		this.updateEmptyState();
	}

	setTabConfiguration(tabId: string, icon: string, caption: string, closeable: boolean, visible: boolean, rightSide: boolean): void {
		let tab = this.getTabById(tabId);
		tab.$button[0].innerHTML = '';
		tab.$button.append(this.createTabButton(tabId, icon, caption, closeable).find(">*"));
		tab.$dropDownTabButton[0].innerHTML = '';
		tab.$dropDownTabButton.append(this.createTabButton(tabId, icon, caption, closeable).find(">*"));
		if (rightSide && !tab.config.rightSide) {
			this.$rightButtonsWrapper.append(tab.$button);
			this.$dropButtonContainerRight.append(tab.$dropDownTabButton);
		} else if (!rightSide && tab.config.rightSide) {
			this.$leftButtonsWrapper.append(tab.$button);
			this.$dropButtonContainerLeft.append(tab.$dropDownTabButton);
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
		this.setTabContent(tabConfig.tabId, tabConfig.content, true);
		if (select || this.getAllTabs().length === 1) {
			this.selectTab(tabConfig.tabId, false);
		}
		let tabBarVisibilityChanged = this.updateTabBarVisibility();
		if (tabBarVisibilityChanged) {
			this.reLayout(true);
		} else {
			this.relayoutButtons();
		}
		this.updateEmptyState();
	}

	moveTab(tabId: string, index: number) {
		let tab = this.getTabById(tabId);
		this.putTabButtonsToIndex(tab, index);
	}

	protected onAttachedToDom() {
		Object.values(this.toolButtons).forEach(b => b.attachedToDom = true);
		this.getAllTabs().forEach(tab => {
			if (tab.toolbar) tab.toolbar.attachedToDom = true;
			if (tab.contentComponent) tab.contentComponent.attachedToDom = true;
		});
		this.reLayout();
	}

	public setTabToolbar(tabId: string, toolbar: UiToolbar) {
		let tab = this.getTabById(tabId);
		if (tab) {
			this.setTabToolbarInternal(tab, toolbar);
		}
	}

	private setTabToolbarInternal(tab: Tab, toolbar: UiToolbar) {
		if (tab.toolbar != null) {
			tab.toolbar.getMainDomElement().detach();
		}
		tab.toolbar = toolbar;
		if (toolbar) {
			tab.$toolbarContainer.append(toolbar.getMainDomElement());
		}
		if (this.selectedTab && this.selectedTab.config.tabId === tab.config.tabId && tab.toolbar) {
			tab.toolbar.attachedToDom = this.attachedToDom;
		}
	}

	public removeTab(tabId: string, warnIfNotFound = true) {
		let tab = this.getTabById(tabId, warnIfNotFound);

		if (!tab) return;

		tab.contentComponent != null && isEmptyable(tab.contentComponent) && tab.contentComponent.onEmptyStateChanged.removeListener(this.onChildEmptyStateChanged.bind(this));
		tab.$button.detach();
		tab.$dropDownTabButton.detach();
		tab.$wrapper.detach();

		this.leftTabs = this.leftTabs.filter(tab => tab.config.tabId !== tabId);
		this.rightTabs = this.rightTabs.filter(tab => tab.config.tabId !== tabId);

		if (tab === this.selectedTab) {
			this.selectFirstVisibleTab();
		}

		let tabBarVisibilityChanged = this.updateTabBarVisibility();
		if (tabBarVisibilityChanged) {
			this.reLayout(true);
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
			this.onTabSelected.fire(EventFactory.createUiTabPanel_TabSelectedEvent(this.getId(), null));
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
		let wasHidden = this.$tabBar.is('.hidden');
		let isHidden = this.hideTabBarIfSingleTab && this.getVisibleTabs().length <= 1;
		this.$tabBar.toggleClass("hidden", isHidden);
		this.$tabBar[0].offsetHeight; // trigger reflow!
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
		this.$toolButtonContainer[0].innerHTML = '';
		this.$toolButtonContainer.toggleClass("hidden", !toolButtons || toolButtons.length === 0);
		this.toolButtons = {};
		toolButtons.forEach(toolButton => {
			toolButton.getMainDomElement().appendTo(this.$toolButtonContainer);
			toolButton.attachedToDom = this.attachedToDom;
			this.toolButtons[toolButton.getId()] = toolButton;
		});
		this.relayoutButtons();
	}

	public getToolButtons() {
		return Object.values(this.toolButtons);
	}

	public setWindowButtons(buttonTypes:UiWindowButtonType[]):void{
		this.windowButtons = [];
		this.$windowButtonContainer[0].innerHTML = '';
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
		this.$windowButtonContainer.removeClass("hidden");
		this.windowButtons.push(toolButtonType);
		const button = this.defaultToolButtons[toolButtonType];
		if (this.$windowButtonContainer[0].children.length === 0) {
			button.getMainDomElement().prependTo(this.$windowButtonContainer);
		} else {
			let index = this.windowButtons
				.sort((a, b) =>this.orderedDefaultToolButtonTypes.indexOf(a) - this.orderedDefaultToolButtonTypes.indexOf(b))
				.indexOf(toolButtonType);
			if (index >= this.$windowButtonContainer[0].childNodes.length) {
				button.getMainDomElement().appendTo(this.$windowButtonContainer);
			} else {
				button.getMainDomElement().insertBefore(this.$windowButtonContainer[0].children[index]);
			}
		}
		this.relayoutButtons();
	}

	public removeWindowButton(uiToolButton: UiWindowButtonType) {
		this.defaultToolButtons[uiToolButton].getMainDomElement().detach();
		this.windowButtons = this.windowButtons.filter(tb => tb !== uiToolButton);
		if (this.windowButtons.length === 0) {
			this.$windowButtonContainer.addClass("hidden");
		}
	}

	public setTabStyle(tabStyle: UiTabPanelTabStyle) {
		this.$tabPanel.removeClass('tab-style-ears tab-style-blocks')
			.addClass(tabStyle === UiTabPanelTabStyle.EARS ? 'tab-style-ears' : 'tab-style-blocks');
		this.$tabBar.toggleClass("teamapps-blurredBackgroundImage", tabStyle === UiTabPanelTabStyle.BLOCKS);
		this.reLayout(true);
	}

	@executeWhenAttached(true)
	public onResize(): void {
		if (!this.attachedToDom || this.getMainDomElement()[0].offsetWidth <= 0) return;
		if (this.selectedTab) {
			this.selectedTab.toolbar && this.selectedTab.toolbar.reLayout();
			this.selectedTab.contentComponent && this.selectedTab.contentComponent.reLayout();
		}
		this.relayoutButtons();
	}

	@executeWhenAttached(true)
	private relayoutButtons() {
		if (this.$tabBar.is('.hidden')) {
			return;
		}
		let availableWidth = this.$tabsContainer.width() - this.$dropDownButton[0].offsetWidth - this.$toolTabButton[0].offsetWidth;
		let sumOfButtonWidths = 0;
		this.getAllTabs().forEach(tab => {
			let tabFilled = !this.tabIsDeFactoEmpty(tab);

			if (tabFilled) {
				if (!tab.buttonWidth) {
					tab.buttonWidth = tab.$button[0].offsetWidth
				}
				sumOfButtonWidths += tab.buttonWidth;

				if (sumOfButtonWidths < availableWidth) {
					tab.$button.removeClass("hidden");
					tab.$dropDownTabButton.addClass("hidden");
				} else {
					tab.$button.addClass("hidden");
					tab.$dropDownTabButton.removeClass("hidden");
				}
			} else {
				tab.$button.add(tab.$dropDownTabButton).toggleClass("hidden", true);
			}
		});
		this.$dropDownButton.toggleClass("hidden", sumOfButtonWidths <= availableWidth);
	}

	public destroy(): void {
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiTabPanel", UiTabPanel);
