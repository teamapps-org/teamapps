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
import {
	DtoRelativeWorkSpaceViewPosition,
	DtoSplitSizePolicy,
	DtoViewGroupPanelState,
	DtoWorkSpaceLayoutItem,
	DtoWorkSpaceLayoutView
} from "../../generated";
import {ViewInfo} from "./ViewInfo";
import {RelativeDropPosition} from "./RelativeDropPosition";
import {WindowLayoutDescriptor} from "./WindowLayoutDescriptor";
import {Component} from "projector-client-object-api";

export interface ViewContainer {
	windowId: string;

	refreshViewComponent(viewName: string, component: Component): void;

	refreshViewAttributes(viewName: string, tabIcon: string, tabCaption: string, tabCloseable: boolean, visible: boolean): void;

	removeView(viewName: string): void;

	setViewVisible(viewName: string, visible: boolean): void;

	redefineLayout(newLayout: DtoWorkSpaceLayoutItem, addedViewConfigs: DtoWorkSpaceLayoutView[]): void;

	moveViewToTopLevel(viewName: string, windowId: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: DtoSplitSizePolicy, referenceChildSize: number): void;

	moveViewRelativeToOtherView(viewName: string, existingViewName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: DtoSplitSizePolicy, referenceChildSize: number): void;

	moveViewToTab(viewName: string, existingViewName: string, select: boolean): void;

	addViewToTopLevel(newView: DtoWorkSpaceLayoutView, windowId: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: DtoSplitSizePolicy, referenceChildSize: number): void;

	addViewRelativeToOtherView(newView: DtoWorkSpaceLayoutView, existingViewName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: DtoSplitSizePolicy, referenceChildSize: number): void;

	addViewAsTab(newView: DtoWorkSpaceLayoutView, itemId: string, select: boolean): void;

	addViewAsNeighbourTab(newView: DtoWorkSpaceLayoutView, existingViewName: string, select: boolean): void;

	getViewInfo(viewName: string): ViewInfo;

	getLayoutDescriptor(): Promise<WindowLayoutDescriptor>;

	selectViewTab(viewName: string): void;

	setViewGroupPanelState(viewGroupId: string, panelState: DtoViewGroupPanelState): void;
}

export interface ViewContainerListener {
	handleViewDroppedFromOtherWindow(sourceWindowId: string, targetWindowId: string, viewInfo: ViewInfo, existingViewName: string, relativePosition: RelativeDropPosition): void;

	handleLocalLayoutChangedByUser(sourceWindowId: string): void;

	handleChildWindowCreated(childWindowId: string, grandChildWindoMessagePort: MessagePort, initialViewInfo: ViewInfo): void;

	handleChildWindowCreationFailed(viewName: string): void;
}
