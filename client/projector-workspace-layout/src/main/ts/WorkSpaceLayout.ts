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
import {AbstractLegacyComponent, Component, ServerObjectChannel, TeamAppsEvent,} from "projector-client-object-api";
import {ViewInfo} from "./ViewInfo";
import {ViewContainer} from "./ViewContainer";
import {RelativeDropPosition} from "./RelativeDropPosition";
import {LocalViewContainer} from "./LocalViewContainer";
import {WindowLayoutDescriptor} from "./WindowLayoutDescriptor";
import {SplitSizePolicy, Toolbar} from "projector-client-core-components";
import {
	DtoRelativeWorkSpaceViewPosition,
	DtoViewGroupPanelState,
	DtoWorkSpaceLayout,
	DtoWorkSpaceLayout_ChildWindowClosedEvent,
	DtoWorkSpaceLayout_ChildWindowCreationFailedEvent,
	DtoWorkSpaceLayout_LayoutChangedEvent,
	DtoWorkSpaceLayout_ViewClosedEvent,
	DtoWorkSpaceLayout_ViewDraggedToNewWindowEvent,
	DtoWorkSpaceLayout_ViewGroupPanelStateChangedEvent,
	DtoWorkSpaceLayout_ViewNeedsRefreshEvent,
	DtoWorkSpaceLayout_ViewSelectedEvent,
	DtoWorkSpaceLayoutCommandHandler,
	DtoWorkSpaceLayoutEventSource,
	DtoWorkSpaceLayoutItem,
	DtoWorkSpaceLayoutSplitItem,
	DtoWorkSpaceLayoutView,
	DtoWorkSpaceLayoutViewGroupItem
} from "./generated";
import {MultiProgressDisplay, ProgressDisplay} from "projector-progress-display";

export type DtoWorkspaceLayoutDndDataTransfer = {
	// sourceUiSessionId: string,   // sourceUiSessionId: this.context.sessionId, // TODO replace with workspace layout uuid to uniquely identify the workspace layout, even inside a session!
	sourceWorkspaceLayoutId: string,
	sourceWindowId: string,
	targetWindowId?: string,
	viewName: string;
	tabIcon: string,
	tabCaption: string,
	tabCloseable: boolean,
	lazyLoading: boolean,
	visible: boolean
}

export class WorkSpaceLayout extends AbstractLegacyComponent<DtoWorkSpaceLayout> implements DtoWorkSpaceLayoutCommandHandler, DtoWorkSpaceLayoutEventSource {

	public readonly onLayoutChanged: TeamAppsEvent<DtoWorkSpaceLayout_LayoutChangedEvent> = new TeamAppsEvent();
	public readonly onViewDraggedToNewWindow: TeamAppsEvent<DtoWorkSpaceLayout_ViewDraggedToNewWindowEvent> = new TeamAppsEvent();
	public readonly onViewNeedsRefresh: TeamAppsEvent<DtoWorkSpaceLayout_ViewNeedsRefreshEvent> = new TeamAppsEvent();
	public readonly onChildWindowCreationFailed: TeamAppsEvent<DtoWorkSpaceLayout_ChildWindowCreationFailedEvent> = new TeamAppsEvent();
	public readonly onChildWindowClosed: TeamAppsEvent<DtoWorkSpaceLayout_ChildWindowClosedEvent> = new TeamAppsEvent();
	public readonly onViewSelected: TeamAppsEvent<DtoWorkSpaceLayout_ViewSelectedEvent> = new TeamAppsEvent();
	public readonly onViewClosed: TeamAppsEvent<DtoWorkSpaceLayout_ViewClosedEvent> = new TeamAppsEvent();
	public readonly onViewGroupPanelStateChanged: TeamAppsEvent<DtoWorkSpaceLayout_ViewGroupPanelStateChangedEvent> = new TeamAppsEvent();

	public static readonly ROOT_WINDOW_ID: string = "ROOT_WINDOW";

	public rootWindowMessagePort: MessagePort;
	public windowId: string = WorkSpaceLayout.ROOT_WINDOW_ID;

	private localViewContainer: LocalViewContainer;
	private viewContainersByWindowId: { [windowId: string]: ViewContainer } = {};

	constructor(config: DtoWorkSpaceLayout, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.localViewContainer = new LocalViewContainer(this, this.windowId, config.views, config.initialLayout, {
			handleChildWindowCreated: (childWindowId, messagePort, initialViewInfo) => this.handleChildWindowCreated(childWindowId, messagePort, initialViewInfo),
			handleChildWindowCreationFailed: (viewName: string) => this.handleChildWindowCreationFailed(viewName),
			handleViewDroppedFromOtherWindow: (sourceWindowId, targetWindowId, viewInfo, existingViewName, relativePosition) => this.handleViewDroppedFromOtherWindow(sourceWindowId, targetWindowId, viewInfo, existingViewName, relativePosition),
			handleLocalLayoutChangedByUser: (windowId: string) => this.handleLocalLayoutChangedByUser(windowId)
		}, config.multiProgressDisplay as MultiProgressDisplay);

		this.localViewContainer.setToolbar(config.toolbar as Toolbar);

		this.viewContainersByWindowId[this.windowId] = this.localViewContainer;
	}

	private handleChildWindowCreated(childWindowId: string, messagePort: MessagePort, initialViewInfo: ViewInfo) {
		// if (this.isRootWindow) {
		// 	let childWindow = window.open("", childWindowId);
		// 	let childWindowViewContainer = new ChildWindowViewContainer(childWindow, childWindowId, messagePort, initialViewInfo, this,
		// 		this.config.newWindowBackgroundImage, this.config.newWindowBlurredBackgroundImage, {
		// 			handleChildWindowCreated: (childWindowId, messagePort, initialViewInfo) => this.handleChildWindowCreated(childWindowId, messagePort, initialViewInfo),
		// 			handleChildWindowCreationFailed: (viewName: string) => this.handleChildWindowCreationFailed(viewName),
		// 			handleViewDroppedFromOtherWindow: (sourceWindowId, targetWindowId, viewInfo, existingViewName, relativePosition) => this.handleViewDroppedFromOtherWindow(sourceWindowId, targetWindowId, viewInfo, existingViewName, relativePosition),
		// 			handleLocalLayoutChangedByUser: (windowId: string) => this.handleLocalLayoutChangedByUser(windowId),
		// 			handleInitialized: async (windowId: string, initialViewInfo: ViewInfo) => {
		// 				await this.moveViewToTopLevel(initialViewInfo.viewName, childWindowId, null, null, null);
		// 				const layout = await this.getCurrentLayout();
		// 				this.onViewDraggedToNewWindow.fire({
		// 					windowId: windowId,
		// 					viewName: initialViewInfo.viewName,
		// 					layoutsByWindowId: layout
		// 				});
		// 			},
		// 			handleClosed: (childWindowViewContainer: ChildWindowViewContainer) => {
		// 				this.onChildWindowClosed.fire({
		// 					windowId: childWindowId
		// 				});
		// 				delete this.viewContainersByWindowId[childWindowViewContainer.windowId];
		// 			},
		// 			handleUiEvent: (uiEvent) => {
		// 				// On the one hand, this is an event that gets emitted by (or at least bypassed through) the workspaceLayout.
		// 				// But the workspace layout should not be seen as the emitter!
		// 				(this._context as any as TeamAppsUiContextInternalApi).sendEvent(uiEvent);
		// 			}
		// 		});
		// 	this.viewContainersByWindowId[childWindowId] = childWindowViewContainer;
		// } else {
		// 	this.rootWindowMessagePort.postMessage({
		// 		_type: 'CHILD_WINDOW_CREATED',
		// 		childWindowId: childWindowId,
		// 		viewInfo: initialViewInfo
		// 	}, [messagePort]);
		// }
	}

	private handleChildWindowCreationFailed(viewName: string) {
		if (this.isRootWindow) {
			this.onChildWindowCreationFailed.fire({
				viewName: viewName
			})
		} else {
			this.rootWindowMessagePort.postMessage({
				_type: 'CHILD_WINDOW_CREATION_FAILED',
				viewName
			});
		}
	}

	private handleViewDroppedFromOtherWindow(sourceWindowId: string, targetWindowId: string, viewInfo: ViewInfo, existingViewName: string, relativePosition: RelativeDropPosition) {
		if (this.isRootWindow) {
			if (existingViewName != null) {
				if (relativePosition === RelativeDropPosition.TAB) {
					this.moveViewToNeighbourTab(viewInfo.viewName, existingViewName, true);
				} else {
					this.moveViewRelativeToOtherView(viewInfo.viewName, existingViewName, DtoRelativeWorkSpaceViewPosition[RelativeDropPosition[relativePosition] as keyof typeof DtoRelativeWorkSpaceViewPosition], SplitSizePolicy.RELATIVE, .5);
				}
			} else {
				let isFirst = relativePosition === RelativeDropPosition.LEFT || relativePosition === RelativeDropPosition.TOP;
				this.moveViewToTopLevel(viewInfo.viewName, targetWindowId, DtoRelativeWorkSpaceViewPosition[RelativeDropPosition[relativePosition] as keyof typeof DtoRelativeWorkSpaceViewPosition], SplitSizePolicy.RELATIVE, isFirst ? .3 : .7);
			}
		} else {
			this.rootWindowMessagePort.postMessage({
				_type: 'VIEW_DROPPED',
				sourceWindowId: sourceWindowId,
				targetWindowId: targetWindowId,
				viewInfo: viewInfo,
				existingViewName: existingViewName,
				relativePosition: relativePosition,
			});
		}
		if (this.isRootWindow) {
			this.fireLayoutChanged()
		}
	}

	private handleLocalLayoutChangedByUser(windowId: string) {
		if (this.isRootWindow) {
			this.fireLayoutChanged()
		} else {
			this.rootWindowMessagePort.postMessage({
				_type: 'LOCAL_LAYOUT_CHANGED_BY_USER',
				windowId: windowId,
			});
		}
	}

	private get isRootWindow() {
		return this.windowId === WorkSpaceLayout.ROOT_WINDOW_ID;
	}

	async removeView(viewName: string) {
		let viewContainer = await this.getViewContainerByViewName(viewName);
		if (viewContainer != null) {
			viewContainer.removeView(viewName);
		}
	}

	redefineLayout(layoutsByWindowId: {[windowId: string]: DtoWorkSpaceLayoutItem}, addedViews: DtoWorkSpaceLayoutView[]): void {
		Object.keys(layoutsByWindowId).forEach(windowId => {
			this.viewContainersByWindowId[windowId].redefineLayout(layoutsByWindowId[windowId], addedViews);
		});
	}

	async moveViewToTopLevel(viewName: string, targetWindowId: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number) {
		let sourceViewContainer = await this.getViewContainerByViewName(viewName);
		let targetViewContainer = this.viewContainersByWindowId[targetWindowId];
		if (sourceViewContainer === targetViewContainer) {
			sourceViewContainer.moveViewToTopLevel(viewName, targetWindowId, relativePosition, sizePolicy, referenceChildSize);
		} else {
			let viewInfo = sourceViewContainer.getViewInfo(viewName);
			sourceViewContainer.removeView(viewName);
			targetViewContainer.addViewToTopLevel(WorkSpaceLayout.createEmptyViewConfig(viewInfo), targetWindowId, relativePosition, sizePolicy, referenceChildSize);
		}
	}

	async moveViewRelativeToOtherView(viewName: string, existingViewName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number) {
		let sourceViewContainer = await this.getViewContainerByViewName(viewName);
		let targetViewContainer = await this.getViewContainerByViewName(existingViewName);
		if (sourceViewContainer === targetViewContainer) {
			sourceViewContainer.moveViewRelativeToOtherView(viewName, targetViewContainer.windowId, relativePosition, sizePolicy, referenceChildSize);
		} else {
			let viewInfo = sourceViewContainer.getViewInfo(viewName);
			sourceViewContainer.removeView(viewName);
			targetViewContainer.addViewRelativeToOtherView(WorkSpaceLayout.createEmptyViewConfig(viewInfo), existingViewName, relativePosition, sizePolicy, referenceChildSize);
		}
	}

	async moveViewToNeighbourTab(viewName: string, existingViewName: string, select: boolean) {
		let sourceViewContainer = await this.getViewContainerByViewName(viewName);
		let targetViewContainer = await this.getViewContainerByViewName(existingViewName);
		if (sourceViewContainer === targetViewContainer) {
			sourceViewContainer.moveViewToTab(viewName, existingViewName, select);
		} else {
			let viewInfo = sourceViewContainer.getViewInfo(viewName);
			sourceViewContainer.removeView(viewName);
			targetViewContainer.addViewAsNeighbourTab(WorkSpaceLayout.createEmptyViewConfig(viewInfo), existingViewName, select);
		}
	}

	addViewToTopLevel(newView: DtoWorkSpaceLayoutView, windowId: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number): void {
		let targetViewContainer = this.viewContainersByWindowId[windowId];
		targetViewContainer.addViewToTopLevel(newView, windowId, relativePosition, sizePolicy, referenceChildSize);
	}

	async addViewRelativeToOtherView(newView: DtoWorkSpaceLayoutView, existingViewName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number) {
		let targetViewContainer = await this.getViewContainerByViewName(existingViewName);
		targetViewContainer.addViewRelativeToOtherView(newView, existingViewName, relativePosition, sizePolicy, referenceChildSize);
	}

	async addViewAsTab(newView: DtoWorkSpaceLayoutView, layoutItemId: string, select: boolean) {
		let targetViewContainer = await this.getViewContainerByItemId(layoutItemId);
		targetViewContainer.addViewAsTab(newView, layoutItemId, select);
	}

	async addViewAsNeighbourTab(newView: DtoWorkSpaceLayoutView, existingViewName: string, select: boolean) {
		let targetViewContainer = await this.getViewContainerByViewName(existingViewName);
		targetViewContainer.addViewAsNeighbourTab(newView, existingViewName, select);
	}

	async refreshViewComponent(viewName: string, component: Component) {
		let viewContainter: ViewContainer = await this.getViewContainerByViewName(viewName);
		viewContainter.refreshViewComponent(viewName, component);
	}

	async refreshViewAttributes(viewName: string, tabIcon: string, tabCaption: string, tabCloseable: boolean, visible: boolean) {
		let viewContainter: ViewContainer = await this.getViewContainerByViewName(viewName);
		viewContainter.refreshViewAttributes(viewName, tabIcon, tabCaption, tabCloseable, visible);
	}

	async selectView(viewName: string) {
		let viewContainer = await this.getViewContainerByViewName(viewName);
		viewContainer.selectViewTab(viewName);
	}

	async setViewGroupPanelState(viewGroupId: string, panelState: DtoViewGroupPanelState) {
		let viewContainer = await this.getViewContainerByItemId(viewGroupId);
		viewContainer.setViewGroupPanelState(viewGroupId, panelState);
	}

	private async fireLayoutChanged() {
		this.onLayoutChanged.fire({
			layoutsByWindowId: await this.getCurrentLayout()
		});
	}

	private async getViewContainerByViewName(viewName: string): Promise<ViewContainer> {
		// TODO cache the child window layouts so this method does not have to communicate with the child windows all the time?
		let isInLocalViewContainer = this.localViewContainer.viewNames.indexOf(viewName) !== -1;
		if (isInLocalViewContainer) {
			return this.localViewContainer;
		} else {
			return new Promise<ViewContainer>((resolve, reject) => {
				let numberOfResponsesNotYetReceived = this.viewContainers.length;
				for (let viewContainer of this.viewContainers) {
					viewContainer.getLayoutDescriptor().then(descriptor => {
						let viewNames = this.extractViewNamesFromLayoutDescriptor(descriptor.layout);
						numberOfResponsesNotYetReceived--;
						if (viewNames.indexOf(viewName) !== -1) {
							resolve(viewContainer);
						} else if (numberOfResponsesNotYetReceived == 0) {
							reject(new Error(`Cannot find view ${viewName} in viewContainers!`));
						}
					}).catch(reason => {
						console.log(reason);
					})
				}
			});
		}
	}

	private extractViewNamesFromLayoutDescriptor(item: DtoWorkSpaceLayoutItem): string[] {
		if (isTabPanelDescriptor(item)) {
			return item.viewNames;
		} else if (isSplitPanelDescriptor(item)) {
			const firstChildViewNames = item.firstChild ? this.extractViewNamesFromLayoutDescriptor(item.firstChild) : [];
			const lastChildViewNames = item.lastChild ? this.extractViewNamesFromLayoutDescriptor(item.lastChild) : [];
			return [...firstChildViewNames, ...lastChildViewNames];
		}
	}

	private async getViewContainerByItemId(itemId: string): Promise<ViewContainer> {
		// TODO cache the child window layouts so this method does not have to communicate with the child windows all the time?
		let localLayoutDescriptor = await this.localViewContainer.getLayoutDescriptor();
		const isInLocalViewContainer = this.extractItemIdsFromLayoutDescriptor(localLayoutDescriptor.layout).indexOf(itemId) !== -1;
		if (isInLocalViewContainer) {
			return this.localViewContainer;
		} else {
			return new Promise<ViewContainer>((resolve, reject) => {
				let numberOfResponsesNotYetReceived = this.viewContainers.length;
				for (let viewContainer of this.viewContainers) {
					viewContainer.getLayoutDescriptor().then(descriptor => {
						let itemIds = this.extractItemIdsFromLayoutDescriptor(descriptor.layout);
						numberOfResponsesNotYetReceived--;
						if (itemIds.indexOf(itemId) !== -1) {
							resolve(viewContainer);
						} else if (numberOfResponsesNotYetReceived == 0) {
							reject();
						}
					}).catch(reason => {
						console.log(reason);
					})
				}
			});
		}
	}

	private extractItemIdsFromLayoutDescriptor(item: DtoWorkSpaceLayoutItem): string[] {
		if (isTabPanelDescriptor(item)) {
			return [item.id];
		} else if (isSplitPanelDescriptor(item)) {
			const firstChildItemIds = item.firstChild ? this.extractItemIdsFromLayoutDescriptor(item.firstChild) : [];
			const lastChildItemIds = item.lastChild ? this.extractItemIdsFromLayoutDescriptor(item.lastChild) : [];
			return [item.id, ...firstChildItemIds, ...lastChildItemIds];
		}
	}

	private get viewContainers(): ViewContainer[] {
		return Object.keys(this.viewContainersByWindowId)
			.map(windowId => this.viewContainersByWindowId[windowId]);
	}

	private static createEmptyViewConfig(viewInfo: ViewInfo): DtoWorkSpaceLayoutView {
		return {
			_type: "DtoWorkSpaceLayoutView",
			viewName: viewInfo.viewName,
			tabIcon: viewInfo.tabIcon,
			tabCaption: viewInfo.tabCaption,
			tabCloseable: viewInfo.tabCloseable,
			component: null
		};
	}

	public setToolbar(toolbar: Toolbar): void {
		this.localViewContainer.setToolbar(toolbar);
	}

	public async getCurrentLayout(): Promise<{[name: string]: DtoWorkSpaceLayoutItem}> {
		if (this.isRootWindow) {
			return Promise.all(this.viewContainers.map(vc => vc.getLayoutDescriptor()))
				.then((windowLayoutDescriptors: WindowLayoutDescriptor[]) => {
					let desciptorsByWindowId: {[windowId: string]: DtoWorkSpaceLayoutItem} = {};
					for (let windowLayoutDescriptor of windowLayoutDescriptors) {
						desciptorsByWindowId[windowLayoutDescriptor.windowId] = windowLayoutDescriptor.layout;
					}
					return desciptorsByWindowId;
				});
		} else {
			console.warn("getCurrentLayout was called from non-root window!");
			return null;
		}
	}

	public async getLocalLayout(): Promise<WindowLayoutDescriptor> {
		return this.localViewContainer.getLayoutDescriptor();
	}

	public doGetMainElement(): HTMLElement {
		return this.localViewContainer.getMainDomElement();
	}

	public destroy(): void {
		super.destroy();
		this.localViewContainer.destroy();
	}

	fireViewGroupPanelStateChanged(viewGroupId: string, panelState: DtoViewGroupPanelState) {
		this.onViewGroupPanelStateChanged.fire({
			viewGroupId: viewGroupId,
			panelState: panelState
		});
	}

	setMultiProgressDisplay(multiProgressDisplay: MultiProgressDisplay): void {
		this.localViewContainer.setMultiProgressDisplay(multiProgressDisplay);
	}
}

export function isTabPanelDescriptor(item: DtoWorkSpaceLayoutItem): item is DtoWorkSpaceLayoutViewGroupItem {
	return item._type === 'DtoWorkSpaceLayoutViewGroupItem';
}

export function isSplitPanelDescriptor(item: DtoWorkSpaceLayoutItem): item is DtoWorkSpaceLayoutSplitItem {
	return item._type === 'DtoWorkSpaceLayoutSplitItem';
}


