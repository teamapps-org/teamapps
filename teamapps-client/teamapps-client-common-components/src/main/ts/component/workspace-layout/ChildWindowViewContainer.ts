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
import * as log from "loglevel";
import {DtoRelativeWorkSpaceViewPosition, DtoSplitSizePolicy, DtoWorkSpaceLayoutItem} from "../../generated";
import {Component, DeferredExecutor, generateUUID, TeamAppsUiContext, TeamAppsUiContextInternalApi} from "teamapps-client-core";
import {DtoWorkSpaceLayoutView as DtoWorkSpaceLayoutView} from "../../generated/DtoWorkSpaceLayoutView";
import {ViewInfo} from "./ViewInfo";
import {DtoWorkspaceLayoutSubWindowProtocol_INIT_OK, WorkSpaceLayout} from "./WorkSpaceLayout";
import {ViewContainer, ViewContainerListener} from "./ViewContainer";
import {DtoEvent as DtoEvent} from "teamapps-client-communication";
import {WindowLayoutDescriptor} from "./WindowLayoutDescriptor";

export interface ChildWindowViewContainerListener extends ViewContainerListener {
	handleInitialized(windowId: string, initialViewInfo: ViewInfo): void;

	handleUiEvent(uiEvent: DtoEvent): void;

	handleClosed(childWindowViewContainer: ChildWindowViewContainer): void;
}


interface RemoteMethodInvocationCallback {
	resolve: (x: any) => void,
	reject: (x: any) => void
}

export class ChildWindowViewContainer implements ViewContainer {

	private static logger: log.Logger = log.getLogger("DtoWorkSpaceLayout.ChildWindowViewContainer");

	private _viewInfos: { [viewName: string]: ViewInfo } = {};
	private deferredExecutor: DeferredExecutor = new DeferredExecutor();
	private methodInvocationResponseCallbacksByCorrelationId: { [correlationId: string]: RemoteMethodInvocationCallback } = {};

	constructor(private childWindow: Window,
	            public readonly windowId: string,
	            private port: MessagePort,
	            private initialViewInfo: ViewInfo,
	            private owner: WorkSpaceLayout,
	            private context: TeamAppsUiContext,
	            private newWindowBackgroundImage: string,
	            private newWindowBlurredBackgroundImage: string,
	            private listener: ChildWindowViewContainerListener) {
		this._viewInfos[initialViewInfo.viewName] = initialViewInfo;
		port.onmessage = (e) => {
			this.handlePortMessage(e, port, windowId);
		};
		childWindow.addEventListener('unload', () => {
			this.listener.handleClosed(this);
		});
	}

	public get viewNames() {
		return Object.keys(this._viewInfos);
	}

	public getViewInfo(viewName: string) {
		return this._viewInfos[viewName];
	}

	private handlePortMessage(e: MessageEvent, port: MessagePort, childWindowId: string) {
		ChildWindowViewContainer.logger.debug(e.data);

		if (e.data._type === 'INIT') {
			const initOkMessage: DtoWorkspaceLayoutSubWindowProtocol_INIT_OK = {
				_type: 'INIT_OK',
				sessionId: this.context.sessionId,
				workspaceLayoutId: this.owner.getId(),
				windowId: childWindowId,
				uiConfiguration: {
					...this.context.config
				},
				backgroundImage: this.newWindowBackgroundImage,
				blurredBackgroundImage: this.newWindowBlurredBackgroundImage,
				childWindowPageTitle: 'Application subview' // TODO
			};
			port.postMessage(JSON.stringify(initOkMessage));
			this.deferredExecutor.ready = true;
			this.listener.handleInitialized(this.windowId, this.initialViewInfo);
		} else if (e.data._type === 'VIEW_DROPPED') {
			this.listener.handleViewDroppedFromOtherWindow(e.data.sourceWindowId, e.data.targetWindowId, e.data.viewInfo, e.data.existingViewName, e.data.relativePosition);
		} else if (e.data._type === 'LOCAL_LAYOUT_CHANGED_BY_USER') {
			this.listener.handleLocalLayoutChangedByUser(e.data.windowId);
		} else if (e.data._type === 'REGISTER_COMPONENT') {
			const componentId = e.data.componentId;
			const teamappsType = e.data.teamappsType;
			// (this.context as TeamAppsUiContextInternalApi).registerClientObject(new Proxy({}, {
			// 	get: (target, property, receiver) => {
			// 		if (property === 'getId') {
			// 			return () => componentId;
			// 		} else {
			// 			let me = this;
			// 			return function () {
			// 				me.sendCommandToWindow(componentId, property.toString(), arguments);
			// 			}
			// 		}
			// 	}
			// }) as Component, componentId, teamappsType, ["TODO"], ["TODO"]);
		} else if (e.data._type === 'EVENT') {
			this.listener.handleUiEvent(e.data.eventObject);
		} else if (e.data._type === 'CHILD_WINDOW_CREATED') {
			let grandChildWindowId = e.data.childWindowId;
			let grandChildWindoMessagePort = e.ports[0];
			let viewInfo = e.data.viewInfo;
			this.listener.handleChildWindowCreated(grandChildWindowId, grandChildWindoMessagePort, viewInfo);
		} else if (e.data._type === 'CHILD_WINDOW_CREATION_FAILED') {
			let viewName = e.data.viewName;
			this.listener.handleChildWindowCreationFailed(viewName);
		} else if (e.data._type === 'WORKSPACELAYOUT_METHOD_INVOCATION_RESPONSE') {
			let callback = this.methodInvocationResponseCallbacksByCorrelationId[e.data.correlationId];
			if (callback) {
				if (e.data.successful) {
					callback.resolve(e.data.returnValue);
				} else {
					callback.reject(e.data.returnValue);
				}
			}
		}
	}

	refreshViewComponent(viewName: string, component: Component): void {
		ChildWindowViewContainer.logger.error("TODO #componentRef introduce windowIds..."); // TODO #componentRef
		// this.sendCommandToWindow(this.owner.getId(), "refreshViewComponent", arguments);
	}

	refreshViewAttributes(viewName: string, tabIcon: string, tabCaption: string, tabCloseable: boolean, visible: boolean): void {
		ChildWindowViewContainer.logger.error("TODO #componentRef introduce windowIds..."); // TODO #componentRef
		// this.sendCommandToWindow(this.owner.getId(), "refreshView", arguments);
	}

	addViewToTopLevel(newView: DtoWorkSpaceLayoutView, windowId: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: DtoSplitSizePolicy, referenceChildSize: number): void {
		console.warn("Sending: addViewToTopLevel");
		this._viewInfos[newView.viewName] = newView;
		this.sendCommandToWindow(this.owner.getId(), "addViewToTopLevel", arguments);
	}

	addViewRelativeToOtherView(newView: DtoWorkSpaceLayoutView, existingViewName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: DtoSplitSizePolicy, referenceChildSize: number): void {
		this._viewInfos[newView.viewName] = newView;
		this.sendCommandToWindow(this.owner.getId(), "addViewRelativeToOtherView", arguments);
	}

	addViewAsTab(newView: DtoWorkSpaceLayoutView, itemId: string): void {
		this._viewInfos[newView.viewName] = newView;
		this.sendCommandToWindow(this.owner.getId(), "addViewAsTab", arguments);
	}

	addViewAsNeighbourTab(newView: DtoWorkSpaceLayoutView, existingViewName: string): void {
		this._viewInfos[newView.viewName] = newView;
		this.sendCommandToWindow(this.owner.getId(), "addViewAsNeighbourTab", arguments);
	}

	removeView(viewName: string): void {
		delete this._viewInfos[viewName];
		this.sendCommandToWindow(this.owner.getId(), "removeView", arguments);
	}

	setViewVisible(viewName: string, visible: boolean): void {
		this.sendCommandToWindow(this.owner.getId(), "setViewVisible", arguments);
	}

	redefineLayout(newLayout: DtoWorkSpaceLayoutItem, addedViewConfigs: DtoWorkSpaceLayoutView[]): void {
		this.sendCommandToWindow(this.owner.getId(), "redefineLayout", arguments);
	}

	moveViewToTopLevel(viewName: string, windowId: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: DtoSplitSizePolicy, referenceChildSize: number): void {
		this.sendCommandToWindow(this.owner.getId(), "moveViewToTopLevel", arguments);
	}

	moveViewRelativeToOtherView(viewName: string, existingViewName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: DtoSplitSizePolicy, referenceChildSize: number): void {
		this.sendCommandToWindow(this.owner.getId(), "moveViewRelativeToOtherView", arguments);
	}

	moveViewToTab(viewName: string, existingViewName: string): void {
		this.sendCommandToWindow(this.owner.getId(), "moveViewToTab", arguments);
	}

	selectViewTab(viewName: string) {
		this.sendCommandToWindow(this.owner.getId(), "selectViewTab", arguments);
	}

	setViewGroupPanelState(viewGroupId: string): void {
		this.sendCommandToWindow(this.owner.getId(), "setViewGroupPanelState", arguments);
	}

	async getLayoutDescriptor(): Promise<WindowLayoutDescriptor> {
		console.warn("Sending: getLocalLayout");
		return this.invokeMethod("getLocalLayout");
	}

	private sendCommandToWindow(targetComponentId: string, methodName: string, args: IArguments | any[]) {
		if (!this.deferredExecutor.ready) {
			ChildWindowViewContainer.logger.debug(`Enqueueing method invocation to child window (${this.windowId}): ${methodName}(${args})`)
		}
		this.deferredExecutor.invokeWhenReady(() => {
			ChildWindowViewContainer.logger.debug(`Sending method invocation to child window (${this.windowId}): ${methodName}(${args})`);
			this.port.postMessage(JSON.stringify({
				_type: "COMMAND",
				componentId: targetComponentId,
				methodName: methodName,
				arguments: Array.prototype.slice.call(args)
			}));
		});
	}

	private async invokeMethod(methodName: string, ...args: any[]): Promise<any> {
		let correlationId = generateUUID();
		return await this.deferredExecutor.invokeWhenReady(() => {
			this.port.postMessage(JSON.stringify({
				_type: "WORKSPACELAYOUT_METHOD_INVOCATION",
				methodName: methodName,
				arguments: Array.prototype.slice.call(args),
				correlationId: correlationId
			}));
			return new Promise((resolve, reject) => {
				this.methodInvocationResponseCallbacksByCorrelationId[correlationId] = {resolve, reject}
			});
		});
	}
}
