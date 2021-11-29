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
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiWorkSpaceLayoutViewConfig} from "./UiWorkSpaceLayoutViewConfig";
import {UiWorkSpaceLayoutItemConfig} from "./UiWorkSpaceLayoutItemConfig";
import {UiToolbarConfig} from "./UiToolbarConfig";
import {UiMultiProgressDisplayConfig} from "./UiMultiProgressDisplayConfig";
import {UiRelativeWorkSpaceViewPosition} from "./UiRelativeWorkSpaceViewPosition";
import {UiSplitSizePolicy} from "./UiSplitSizePolicy";
import {UiViewGroupPanelState} from "./UiViewGroupPanelState";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiWorkSpaceLayoutConfig extends UiComponentConfig {
	_type?: string;
	views: UiWorkSpaceLayoutViewConfig[];
	initialLayout: UiWorkSpaceLayoutItemConfig;
	childWindowPageTitle: string;
	toolbar?: unknown;
	newWindowBackgroundImage?: string;
	newWindowBlurredBackgroundImage?: string;
	multiProgressDisplay?: unknown
}

export interface UiWorkSpaceLayoutCommandHandler extends UiComponentCommandHandler {
	setToolbar(toolbar: unknown): any;
	addViewAsTab(newView: UiWorkSpaceLayoutViewConfig, viewGroupId: string, select: boolean): any;
	addViewAsNeighbourTab(newView: UiWorkSpaceLayoutViewConfig, existingViewName: string, select: boolean): any;
	addViewRelativeToOtherView(newView: UiWorkSpaceLayoutViewConfig, existingViewName: string, relativePosition: UiRelativeWorkSpaceViewPosition, sizePolicy: UiSplitSizePolicy, referenceChildSize: number): any;
	addViewToTopLevel(newView: UiWorkSpaceLayoutViewConfig, windowId: string, relativePosition: UiRelativeWorkSpaceViewPosition, sizePolicy: UiSplitSizePolicy, referenceChildSize: number): any;
	moveViewToNeighbourTab(viewName: string, existingViewName: string, select: boolean): any;
	moveViewRelativeToOtherView(viewName: string, existingViewName: string, relativePosition: UiRelativeWorkSpaceViewPosition, sizePolicy: UiSplitSizePolicy, referenceChildSize: number): any;
	moveViewToTopLevel(viewName: string, windowId: string, relativePosition: UiRelativeWorkSpaceViewPosition, sizePolicy: UiSplitSizePolicy, referenceChildSize: number): any;
	redefineLayout(layoutsByWindowId: {[name: string]: UiWorkSpaceLayoutItemConfig}, addedViews: UiWorkSpaceLayoutViewConfig[]): any;
	removeView(viewName: string): any;
	refreshViewAttributes(viewName: string, tabIcon: string, tabCaption: string, tabCloseable: boolean, visible: boolean): any;
	refreshViewComponent(viewName: string, component: unknown): any;
	selectView(viewName: string): any;
	setViewGroupPanelState(viewGroupId: string, panelState: UiViewGroupPanelState): any;
	setMultiProgressDisplay(multiProgressDisplay: unknown): any;
}

export interface UiWorkSpaceLayoutEventSource {
	onLayoutChanged: TeamAppsEvent<UiWorkSpaceLayout_LayoutChangedEvent>;
	onViewDraggedToNewWindow: TeamAppsEvent<UiWorkSpaceLayout_ViewDraggedToNewWindowEvent>;
	onViewNeedsRefresh: TeamAppsEvent<UiWorkSpaceLayout_ViewNeedsRefreshEvent>;
	onChildWindowCreationFailed: TeamAppsEvent<UiWorkSpaceLayout_ChildWindowCreationFailedEvent>;
	onChildWindowClosed: TeamAppsEvent<UiWorkSpaceLayout_ChildWindowClosedEvent>;
	onViewSelected: TeamAppsEvent<UiWorkSpaceLayout_ViewSelectedEvent>;
	onViewClosed: TeamAppsEvent<UiWorkSpaceLayout_ViewClosedEvent>;
	onViewGroupPanelStateChanged: TeamAppsEvent<UiWorkSpaceLayout_ViewGroupPanelStateChangedEvent>;
}

export interface UiWorkSpaceLayout_LayoutChangedEvent extends UiEvent {
	layoutsByWindowId: {[name: string]: UiWorkSpaceLayoutItemConfig}
}

export interface UiWorkSpaceLayout_ViewDraggedToNewWindowEvent extends UiEvent {
	windowId: string;
	viewName: string;
	layoutsByWindowId: {[name: string]: UiWorkSpaceLayoutItemConfig}
}

export interface UiWorkSpaceLayout_ViewNeedsRefreshEvent extends UiEvent {
	viewName: string
}

export interface UiWorkSpaceLayout_ChildWindowCreationFailedEvent extends UiEvent {
	viewName: string
}

export interface UiWorkSpaceLayout_ChildWindowClosedEvent extends UiEvent {
	windowId: string
}

export interface UiWorkSpaceLayout_ViewSelectedEvent extends UiEvent {
	viewGroupId: string;
	viewName: string;
	siblingViewNames: string[]
}

export interface UiWorkSpaceLayout_ViewClosedEvent extends UiEvent {
	viewName: string
}

export interface UiWorkSpaceLayout_ViewGroupPanelStateChangedEvent extends UiEvent {
	viewGroupId: string;
	panelState: UiViewGroupPanelState
}

