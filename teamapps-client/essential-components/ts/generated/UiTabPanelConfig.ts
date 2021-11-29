/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "./UiComponentConfig";
import {EmptyableConfig} from "./EmptyableConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiTabConfig} from "./UiTabConfig";
import {UiToolButtonConfig} from "./UiToolButtonConfig";
import {UiToolbarConfig} from "./UiToolbarConfig";
import {UiTabPanelTabStyle} from "./UiTabPanelTabStyle";
import {UiWindowButtonType} from "./UiWindowButtonType";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiTabPanelConfig extends UiComponentConfig, EmptyableConfig {
	_type?: string;
	tabs?: UiTabConfig[];
	selectedTabId?: string;
	hideTabBarIfSingleTab?: boolean;
	tabStyle?: UiTabPanelTabStyle;
	toolButtons?: unknown[];
	windowButtons?: UiWindowButtonType[]
}

export interface UiTabPanelCommandHandler extends UiComponentCommandHandler {
	setHideTabBarIfSingleTab(hideTabBarIfSingleTab: boolean): any;
	setTabStyle(tabStyle: UiTabPanelTabStyle): any;
	setToolButtons(toolButtons: unknown[]): any;
	setWindowButtons(windowButtons: UiWindowButtonType[]): any;
	selectTab(tabId: string): any;
	addTab(tab: UiTabConfig, select: boolean): any;
	removeTab(tabId: string): any;
	setTabToolbar(tabId: string, toolbar: unknown): any;
	setTabContent(tabId: string, component: unknown): any;
	setTabConfiguration(tabId: string, icon: string, caption: string, closeable: boolean, visible: boolean, rightSide: boolean): any;
}

export interface UiTabPanelEventSource {
	onTabSelected: TeamAppsEvent<UiTabPanel_TabSelectedEvent>;
	onTabNeedsRefresh: TeamAppsEvent<UiTabPanel_TabNeedsRefreshEvent>;
	onTabClosed: TeamAppsEvent<UiTabPanel_TabClosedEvent>;
	onWindowButtonClicked: TeamAppsEvent<UiTabPanel_WindowButtonClickedEvent>;
}

export interface UiTabPanel_TabSelectedEvent extends UiEvent {
	tabId: string
}

export interface UiTabPanel_TabNeedsRefreshEvent extends UiEvent {
	tabId: string
}

export interface UiTabPanel_TabClosedEvent extends UiEvent {
	tabId: string
}

export interface UiTabPanel_WindowButtonClickedEvent extends UiEvent {
	windowButton: UiWindowButtonType
}

