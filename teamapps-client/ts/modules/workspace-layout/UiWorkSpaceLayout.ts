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
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiConfigurationConfig} from "../../generated/UiConfigurationConfig";
import {
	UiWorkSpaceLayout_ChildWindowClosedEvent,
	UiWorkSpaceLayout_ChildWindowCreationFailedEvent,
	UiWorkSpaceLayout_LayoutChangedEvent,
	UiWorkSpaceLayout_ViewClosedEvent,
	UiWorkSpaceLayout_ViewDraggedToNewWindowEvent,
	UiWorkSpaceLayout_ViewGroupPanelStateChangedEvent,
	UiWorkSpaceLayout_ViewNeedsRefreshEvent,
	UiWorkSpaceLayout_ViewSelectedEvent,
	UiWorkSpaceLayoutCommandHandler,
	UiWorkSpaceLayoutConfig,
	UiWorkSpaceLayoutEventSource
} from "../../generated/UiWorkSpaceLayoutConfig";
import {UiWorkSpaceLayoutViewConfig} from "../../generated/UiWorkSpaceLayoutViewConfig";
import {UiWorkSpaceLayoutItemConfig} from "../../generated/UiWorkSpaceLayoutItemConfig";
import {AbstractUiComponent} from "../AbstractUiComponent";
import {TeamAppsUiContext, TeamAppsUiContextInternalApi} from "../TeamAppsUiContext";
import {UiSplitSizePolicy} from "../../generated/UiSplitSizePolicy";
import {UiRelativeWorkSpaceViewPosition} from "../../generated/UiRelativeWorkSpaceViewPosition";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {ViewInfo} from "./ViewInfo";
import {ViewContainer} from "./ViewContainer";
import {RelativeDropPosition} from "./RelativeDropPosition";
import {LocalViewContainer} from "./LocalViewContainer";
import {ChildWindowViewContainer} from "./ChildWindowViewContainer";
import {WindowLayoutDescriptor} from "./WindowLayoutDescriptor";
import {UiWorkSpaceLayoutViewGroupItemConfig} from "../../generated/UiWorkSpaceLayoutViewGroupItemConfig";
import {UiWorkSpaceLayoutSplitItemConfig} from "../../generated/UiWorkSpaceLayoutSplitItemConfig";
import {UiTemplateConfig} from "../../generated/UiTemplateConfig";
import {UiViewGroupPanelState} from "../../generated/UiViewGroupPanelState";
import {UiToolbar} from "../tool-container/toolbar/UiToolbar";
import {UiComponent} from "../UiComponent";
import {UiProgressDisplay} from "../UiProgressDisplay";
import {UiMultiProgressDisplay} from "../UiDefaultMultiProgressDisplay";

export type UiWorkspaceLayoutSubWindowProtocol_INIT_OK = {
	_type: 'INIT_OK',
	sessionId: string,
	workspaceLayoutId: string,
	windowId: string,
	uiConfiguration: UiConfigurationConfig,
	backgroundImage: string,
	blurredBackgroundImage: string,
	registeredTemplates: { [name: string]: UiTemplateConfig },
	childWindowPageTitle: string
}

export type UiWorkspaceLayoutDndDataTransfer = {
	sourceUiSessionId: string,
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

export class UiWorkSpaceLayout extends AbstractUiComponent<UiWorkSpaceLayoutConfig> implements UiWorkSpaceLayoutCommandHandler, UiWorkSpaceLayoutEventSource {

	public readonly onLayoutChanged: TeamAppsEvent<UiWorkSpaceLayout_LayoutChangedEvent> = new TeamAppsEvent(this);
	public readonly onViewDraggedToNewWindow: TeamAppsEvent<UiWorkSpaceLayout_ViewDraggedToNewWindowEvent> = new TeamAppsEvent(this);
	public readonly onViewNeedsRefresh: TeamAppsEvent<UiWorkSpaceLayout_ViewNeedsRefreshEvent> = new TeamAppsEvent(this);
	public readonly onChildWindowCreationFailed: TeamAppsEvent<UiWorkSpaceLayout_ChildWindowCreationFailedEvent> = new TeamAppsEvent(this);
	public readonly onChildWindowClosed: TeamAppsEvent<UiWorkSpaceLayout_ChildWindowClosedEvent> = new TeamAppsEvent(this);
	public readonly onViewSelected: TeamAppsEvent<UiWorkSpaceLayout_ViewSelectedEvent> = new TeamAppsEvent(this);
	public readonly onViewClosed: TeamAppsEvent<UiWorkSpaceLayout_ViewClosedEvent> = new TeamAppsEvent(this);
	public readonly onViewGroupPanelStateChanged: TeamAppsEvent<UiWorkSpaceLayout_ViewGroupPanelStateChangedEvent> = new TeamAppsEvent(this);

	public static readonly ROOT_WINDOW_ID: string = "ROOT_WINDOW";

	public rootWindowMessagePort: MessagePort;
	public windowId: string = UiWorkSpaceLayout.ROOT_WINDOW_ID;

	private localViewContainer: LocalViewContainer;
	private viewContainersByWindowId: { [windowId: string]: ViewContainer } = {};

	constructor(config: UiWorkSpaceLayoutConfig,
	            context: TeamAppsUiContext,
	            windowId = UiWorkSpaceLayout.ROOT_WINDOW_ID,
	            rootWindowMessagePort?: MessagePort) {
		super(config, context);

		this.windowId = windowId;
		this.rootWindowMessagePort = rootWindowMessagePort;

		this.localViewContainer = new LocalViewContainer(this, this.windowId, config.views, config.initialLayout, context, {
			handleChildWindowCreated: (childWindowId, messagePort, initialViewInfo) => this.handleChildWindowCreated(childWindowId, messagePort, initialViewInfo),
			handleChildWindowCreationFailed: (viewName: string) => this.handleChildWindowCreationFailed(viewName),
			handleViewDroppedFromOtherWindow: (sourceWindowId, targetWindowId, viewInfo, existingViewName, relativePosition) => this.handleViewDroppedFromOtherWindow(sourceWindowId, targetWindowId, viewInfo, existingViewName, relativePosition),
			handleLocalLayoutChangedByUser: (windowId: string) => this.handleLocalLayoutChangedByUser(windowId)
		}, config.multiProgressDisplay as UiProgressDisplay);

		this.localViewContainer.setToolbar(config.toolbar as UiToolbar);

		this.viewContainersByWindowId[this.windowId] = this.localViewContainer;
	}

	private handleChildWindowCreated(childWindowId: string, messagePort: MessagePort, initialViewInfo: ViewInfo) {
		if (this.isRootWindow) {
			let childWindow = window.open("", childWindowId);
			let childWindowViewContainer = new ChildWindowViewContainer(childWindow, childWindowId, messagePort, initialViewInfo, this,
				this._context, this._config.newWindowBackgroundImage, this._config.newWindowBlurredBackgroundImage, {
					handleChildWindowCreated: (childWindowId, messagePort, initialViewInfo) => this.handleChildWindowCreated(childWindowId, messagePort, initialViewInfo),
					handleChildWindowCreationFailed: (viewName: string) => this.handleChildWindowCreationFailed(viewName),
					handleViewDroppedFromOtherWindow: (sourceWindowId, targetWindowId, viewInfo, existingViewName, relativePosition) => this.handleViewDroppedFromOtherWindow(sourceWindowId, targetWindowId, viewInfo, existingViewName, relativePosition),
					handleLocalLayoutChangedByUser: (windowId: string) => this.handleLocalLayoutChangedByUser(windowId),
					handleInitialized: async (windowId: string, initialViewInfo: ViewInfo) => {
						await this.moveViewToTopLevel(initialViewInfo.viewName, childWindowId, null, null, null);
						const layout = await this.getCurrentLayout();
						this.onViewDraggedToNewWindow.fire({
							windowId: windowId,
							viewName: initialViewInfo.viewName,
							layoutsByWindowId: layout
						});
					},
					handleClosed: (childWindowViewContainer: ChildWindowViewContainer) => {
						this.onChildWindowClosed.fire({
							windowId: childWindowId
						});
						delete this.viewContainersByWindowId[childWindowViewContainer.windowId];
					},
					handleUiEvent: (uiEvent) => {
						// On the one hand, this is an event that gets emitted by (or at least bypassed through) the workspaceLayout.
						// But the workspace layout should not be seen as the emitter!
						(this._context as any as TeamAppsUiContextInternalApi).fireEvent(uiEvent);
					}
				});
			this.viewContainersByWindowId[childWindowId] = childWindowViewContainer;
		} else {
			this.rootWindowMessagePort.postMessage({
				_type: 'CHILD_WINDOW_CREATED',
				childWindowId: childWindowId,
				viewInfo: initialViewInfo
			}, [messagePort]);
		}
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
					this.moveViewRelativeToOtherView(viewInfo.viewName, existingViewName, UiRelativeWorkSpaceViewPosition[RelativeDropPosition[relativePosition] as keyof typeof UiRelativeWorkSpaceViewPosition], UiSplitSizePolicy.RELATIVE, .5);
				}
			} else {
				let isFirst = relativePosition === RelativeDropPosition.LEFT || relativePosition === RelativeDropPosition.TOP;
				this.moveViewToTopLevel(viewInfo.viewName, targetWindowId, UiRelativeWorkSpaceViewPosition[RelativeDropPosition[relativePosition] as keyof typeof UiRelativeWorkSpaceViewPosition], UiSplitSizePolicy.RELATIVE, isFirst ? .3 : .7);
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
		return this.windowId === UiWorkSpaceLayout.ROOT_WINDOW_ID;
	}

	async removeView(viewName: string) {
		let viewContainer = await this.getViewContainerByViewName(viewName);
		if (viewContainer != null) {
			viewContainer.removeView(viewName);
		}
	}

	redefineLayout(layoutsByWindowId: {[windowId: string]: UiWorkSpaceLayoutItemConfig}, addedViews: UiWorkSpaceLayoutViewConfig[]): void {
		Object.keys(layoutsByWindowId).forEach(windowId => {
			this.viewContainersByWindowId[windowId].redefineLayout(layoutsByWindowId[windowId], addedViews);
		});
	}

	async moveViewToTopLevel(viewName: string, targetWindowId: string, relativePosition: UiRelativeWorkSpaceViewPosition, sizePolicy: UiSplitSizePolicy, referenceChildSize: number) {
		let sourceViewContainer = await this.getViewContainerByViewName(viewName);
		let targetViewContainer = this.viewContainersByWindowId[targetWindowId];
		if (sourceViewContainer === targetViewContainer) {
			sourceViewContainer.moveViewToTopLevel(viewName, targetWindowId, relativePosition, sizePolicy, referenceChildSize);
		} else {
			let viewInfo = sourceViewContainer.getViewInfo(viewName);
			sourceViewContainer.removeView(viewName);
			targetViewContainer.addViewToTopLevel(UiWorkSpaceLayout.createEmptyViewConfig(viewInfo), targetWindowId, relativePosition, sizePolicy, referenceChildSize);
		}
	}

	async moveViewRelativeToOtherView(viewName: string, existingViewName: string, relativePosition: UiRelativeWorkSpaceViewPosition, sizePolicy: UiSplitSizePolicy, referenceChildSize: number) {
		let sourceViewContainer = await this.getViewContainerByViewName(viewName);
		let targetViewContainer = await this.getViewContainerByViewName(existingViewName);
		if (sourceViewContainer === targetViewContainer) {
			sourceViewContainer.moveViewRelativeToOtherView(viewName, targetViewContainer.windowId, relativePosition, sizePolicy, referenceChildSize);
		} else {
			let viewInfo = sourceViewContainer.getViewInfo(viewName);
			sourceViewContainer.removeView(viewName);
			targetViewContainer.addViewRelativeToOtherView(UiWorkSpaceLayout.createEmptyViewConfig(viewInfo), existingViewName, relativePosition, sizePolicy, referenceChildSize);
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
			targetViewContainer.addViewAsNeighbourTab(UiWorkSpaceLayout.createEmptyViewConfig(viewInfo), existingViewName, select);
		}
	}

	addViewToTopLevel(newView: UiWorkSpaceLayoutViewConfig, windowId: string, relativePosition: UiRelativeWorkSpaceViewPosition, sizePolicy: UiSplitSizePolicy, referenceChildSize: number): void {
		let targetViewContainer = this.viewContainersByWindowId[windowId];
		targetViewContainer.addViewToTopLevel(newView, windowId, relativePosition, sizePolicy, referenceChildSize);
	}

	async addViewRelativeToOtherView(newView: UiWorkSpaceLayoutViewConfig, existingViewName: string, relativePosition: UiRelativeWorkSpaceViewPosition, sizePolicy: UiSplitSizePolicy, referenceChildSize: number) {
		let targetViewContainer = await this.getViewContainerByViewName(existingViewName);
		targetViewContainer.addViewRelativeToOtherView(newView, existingViewName, relativePosition, sizePolicy, referenceChildSize);
	}

	async addViewAsTab(newView: UiWorkSpaceLayoutViewConfig, layoutItemId: string, select: boolean) {
		let targetViewContainer = await this.getViewContainerByItemId(layoutItemId);
		targetViewContainer.addViewAsTab(newView, layoutItemId, select);
	}

	async addViewAsNeighbourTab(newView: UiWorkSpaceLayoutViewConfig, existingViewName: string, select: boolean) {
		let targetViewContainer = await this.getViewContainerByViewName(existingViewName);
		targetViewContainer.addViewAsNeighbourTab(newView, existingViewName, select);
	}

	async refreshViewComponent(viewName: string, component: UiComponent) {
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

	async setViewGroupPanelState(viewGroupId: string, panelState: UiViewGroupPanelState) {
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

	private extractViewNamesFromLayoutDescriptor(item: UiWorkSpaceLayoutItemConfig): string[] {
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

	private extractItemIdsFromLayoutDescriptor(item: UiWorkSpaceLayoutItemConfig): string[] {
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

	private static createEmptyViewConfig(viewInfo: ViewInfo): UiWorkSpaceLayoutViewConfig {
		return {
			_type: "UiWorkSpaceLayoutView",
			viewName: viewInfo.viewName,
			tabIcon: viewInfo.tabIcon,
			tabCaption: viewInfo.tabCaption,
			tabCloseable: viewInfo.tabCloseable,
			component: null
		};
	}

	public setToolbar(toolbar: UiToolbar): void {
		this.localViewContainer.setToolbar(toolbar);
	}

	public async getCurrentLayout(): Promise<{[name: string]: UiWorkSpaceLayoutItemConfig}> {
		if (this.isRootWindow) {
			return Promise.all(this.viewContainers.map(vc => vc.getLayoutDescriptor()))
				.then((windowLayoutDescriptors: WindowLayoutDescriptor[]) => {
					let desciptorsByWindowId: {[windowId: string]: UiWorkSpaceLayoutItemConfig} = {};
					for (let windowLayoutDescriptor of windowLayoutDescriptors) {
						desciptorsByWindowId[windowLayoutDescriptor.windowId] = windowLayoutDescriptor.layout;
					}
					return desciptorsByWindowId;
				});
		} else {
			this.logger.warn("getCurrentLayout was called from non-root window!");
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

	fireViewGroupPanelStateChanged(viewGroupId: string, panelState: UiViewGroupPanelState) {
		this.onViewGroupPanelStateChanged.fire({
			viewGroupId: viewGroupId,
			panelState: panelState
		});
	}

	setMultiProgressDisplay(multiProgressDisplay: UiMultiProgressDisplay): void {
		this.localViewContainer.setMultiProgressDisplay(multiProgressDisplay);
	}
}

export function isTabPanelDescriptor(item: UiWorkSpaceLayoutItemConfig): item is UiWorkSpaceLayoutViewGroupItemConfig {
	return item._type === 'UiWorkSpaceLayoutViewGroupItem';
}

export function isSplitPanelDescriptor(item: UiWorkSpaceLayoutItemConfig): item is UiWorkSpaceLayoutSplitItemConfig {
	return item._type === 'UiWorkSpaceLayoutSplitItem';
}

TeamAppsUiComponentRegistry.registerComponentClass("UiWorkSpaceLayout", UiWorkSpaceLayout);
