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
import {isEmptyable, Panel, SplitDirection, SplitSizePolicy} from "teamapps-client-core-components";
import {DtoWorkspaceLayoutDndDataTransfer, WorkSpaceLayout} from "./WorkSpaceLayout";
import {getMicrosoftBrowserVersion} from "teamapps-client-core-components";
import {bind, Component, generateUUID, parseHtml} from "projector-client-object-api";
import {Toolbar} from "teamapps-client-core-components";
import {SplitPaneItem} from "./SplitPaneItem";
import {View} from "./View";
import {ItemTree, ItemTreeItem} from "./ItemTree";
import {TabPanelItem} from "./TabPanelItem";
import {ViewContainer, ViewContainerListener} from "./ViewContainer";
import {RelativeDropPosition} from "./RelativeDropPosition";
import {WindowLayoutDescriptor} from "./WindowLayoutDescriptor";
import {LayoutDescriptorApplyer} from "./LayoutDescriptorApplyer";
import {MultiProgressDisplay} from "teamapps-client-core-components";
import {computePosition, size} from "@floating-ui/dom";
import {
	createDtoWorkSpaceLayoutSplitItem, createDtoWorkSpaceLayoutViewGroupItem,
	DtoRelativeWorkSpaceViewPosition, DtoViewGroupPanelState,
	DtoWorkSpaceLayoutItem,
	DtoWorkSpaceLayoutView,
	DtoWorkSpaceLayoutViewGroupItem
} from "./generated";

export class LocalViewContainer implements ViewContainer {

	private static DND_MIME_TYPE: string = getMicrosoftBrowserVersion() ? 'text' : 'teamapps/uiworkspaceview';

	private itemTree = new ItemTree();
	private _toolbar: Toolbar;

	private $mainDiv: HTMLElement;
	private $toolbarContainer: HTMLElement;
	private $contentContainer: HTMLElement;
	private $dndActiveRectangle: HTMLElement;
	private $dndImage: HTMLElement;
	private lastDndEventType: string;

	private $maximizationContainerWrapper: HTMLElement;
	private $maximizationContainer: HTMLElement;
	private $normalContainerOfMaximizedTabPanel: HTMLElement;
	private $minimizedViewsBar: HTMLElement;
	private $progressContainer: HTMLElement;
	private multiProgressDisplay: MultiProgressDisplay;
	private viewEventsSuppressed: boolean;

	public readonly uuid = generateUUID();

	constructor(private workSpaceLayout: WorkSpaceLayout,
	            public readonly windowId: string,
	            viewConfigs: DtoWorkSpaceLayoutView[],
	            initialLayout: DtoWorkSpaceLayoutItem,
	            private listener: ViewContainerListener,
	            multiProgressDisplay: MultiProgressDisplay) {
		this.$mainDiv = parseHtml(`<div class="WorkSpaceLayout">
    <div class="toolbar-container"></div>
    <div class="content-container-wrapper">
	    <div class="content-container"></div>
		<div class="dnd-target-rectangle hidden"></div>
		<div class="dnd-drag-image"></div>
	</div>
	<div class="bottom-bar">
		<div class="minimized-tabpanel-button-container"></div>
		<div class="progress-container"></div>
	</div>
</div>`);

		this.$toolbarContainer = this.$mainDiv.querySelector<HTMLElement>(':scope .toolbar-container');
		this.$contentContainer = this.$mainDiv.querySelector<HTMLElement>(':scope .content-container');
		this.$dndActiveRectangle = this.$mainDiv.querySelector<HTMLElement>(':scope .dnd-target-rectangle');
		this.$dndImage = this.$mainDiv.querySelector<HTMLElement>(':scope .dnd-drag-image');
		this.$minimizedViewsBar = this.$mainDiv.querySelector<HTMLElement>(':scope .minimized-tabpanel-button-container');
		this.$progressContainer = this.$mainDiv.querySelector<HTMLElement>(':scope .progress-container');
		
		this.setMultiProgressDisplay(multiProgressDisplay);

		this.$maximizationContainerWrapper = parseHtml(`<div class="WorkSpaceLayout-maximization-container-wrapper"><div class="WorkSpaceLayout-maximization-container"></div></div>`);
		document.body.appendChild(this.$maximizationContainerWrapper);
		this.$maximizationContainer = this.$maximizationContainerWrapper.querySelector<HTMLElement>(':scope .DtoWorkSpaceLayout-maximization-container');

		if (initialLayout) {
			this.redefineLayout(initialLayout, viewConfigs);
		} else {
			// initialLayout will be null for child windows
			this.setRootItem(this.createTabPanelItem({id: generateUUID(), viewNames: []}, null));
			return this;
		}
		this.fireViewNeedsRefreshForAllEmptyVisibleLazyTabs();

		let srcTabPanel = null;

		this.$mainDiv.addEventListener('dragstart', (e) => {
			this.lastDndEventType = 'dragstart';

			let target = e.target as HTMLElement;

			const matchingTabPanel = this.findParentTabPanel(target);

			if (matchingTabPanel) {
				let viewName = this.getViewNameForDragTarget(target);
				if (viewName) {
					let view = this.itemTree.getViewByName(viewName);
					srcTabPanel = matchingTabPanel;
					try {
						let $tabButton = matchingTabPanel.component.getMainElement().querySelector<HTMLElement>(`:scope .tab-button[data-tab-name=${viewName}]`);
						if ($tabButton) {
							this.$dndImage.innerHTML = $tabButton.outerHTML;
							this.$dndImage.querySelector<HTMLElement>(':scope .tab-button').classList.remove('hidden');
							e.dataTransfer.setDragImage(this.$dndImage, 0, 0);
						}
					} catch (e) {
						// microsoft browsers do not support this...
					}
					e.dataTransfer.effectAllowed = 'move';
					let data: DtoWorkspaceLayoutDndDataTransfer = {
						// sourceUiSessionId: this.context.sessionId, // TODO replace with workspace layout uuid to uniquely identify the workspace layout, even inside a session!
						sourceWorkspaceLayoutId: this.uuid,
						sourceWindowId: this.windowId,
						viewName: viewName,
						tabIcon: view.tabIcon,
						tabCaption: view.tabCaption,
						tabCloseable: view.tabCloseable,
						lazyLoading: view.lazyLoading,
						visible: view.visible
					};
					e.dataTransfer.setData(LocalViewContainer.DND_MIME_TYPE, JSON.stringify(data));
				}
			}
		});
		this.$mainDiv.addEventListener('dragenter', (e) => {
			this.lastDndEventType = 'dragenter';
		});
		this.$mainDiv.addEventListener('dragover', (e) => {
			this.lastDndEventType = 'dragover';

			if (e.dataTransfer != null && e.dataTransfer.types != null && !e.dataTransfer.types.includes(LocalViewContainer.DND_MIME_TYPE)) {
				return; // detected wrong mime type
			}

			let dropPosition = this.determineDropPosition(e);

			if (dropPosition) {
				e.dataTransfer.dropEffect = 'move';
				this.$dndActiveRectangle.classList.remove("hidden");

				if (dropPosition.tabPanel) {
					let $tabPanelContentWrapper = dropPosition.tabPanel.component.getMainElement().querySelector<HTMLElement>(':scope .tabpanel-content-wrapper');
					let tabPanelContentRect = $tabPanelContentWrapper.getBoundingClientRect();

					if (dropPosition.relativeDropPosition === RelativeDropPosition.TAB) {
						this.cover($tabPanelContentWrapper, this.$dndActiveRectangle, "top", 1);
					} else if (dropPosition.relativeDropPosition === RelativeDropPosition.LEFT) {
						this.cover($tabPanelContentWrapper, this.$dndActiveRectangle, "left", .5);
					} else if (dropPosition.relativeDropPosition === RelativeDropPosition.RIGHT) {
						this.cover($tabPanelContentWrapper, this.$dndActiveRectangle, "right", .5);
					} else if (dropPosition.relativeDropPosition === RelativeDropPosition.TOP) {
						this.cover($tabPanelContentWrapper, this.$dndActiveRectangle, "top", .5);
					} else if (dropPosition.relativeDropPosition === RelativeDropPosition.BOTTOM) {
						this.cover($tabPanelContentWrapper, this.$dndActiveRectangle, "bottom", .5);
					}
				} else {
					let $workSpaceLayout = this.$contentContainer;
					let workSpaceLayoutRect = $workSpaceLayout.getBoundingClientRect();

					if (dropPosition.relativeDropPosition === RelativeDropPosition.LEFT) {
						this.cover($workSpaceLayout, this.$dndActiveRectangle, "left", .33);
					} else if (dropPosition.relativeDropPosition === RelativeDropPosition.RIGHT) {
						this.cover($workSpaceLayout, this.$dndActiveRectangle, "right", .33);
					} else if (dropPosition.relativeDropPosition === RelativeDropPosition.TOP) {
						this.cover($workSpaceLayout, this.$dndActiveRectangle, "top", .33);
					} else if (dropPosition.relativeDropPosition === RelativeDropPosition.BOTTOM) {
						this.cover($workSpaceLayout, this.$dndActiveRectangle, "bottom", .33);
					}
				}


			} else {
				this.$dndActiveRectangle.classList.add("hidden");
			}

			if (e.preventDefault) {
				e.preventDefault(); // Necessary. Allows us to drop.
			}
			return false;
		});
		this.$mainDiv.addEventListener('dragleave', (e) => {
			this.lastDndEventType = 'dragleave';
			this.$dndActiveRectangle.classList.add("hidden");
		});
		this.$mainDiv.addEventListener('drop', (e) => {
			this.$dndActiveRectangle.classList.add("hidden");

			let dropPosition = this.determineDropPosition(e);
			let dataTransferString = e.dataTransfer.getData(LocalViewContainer.DND_MIME_TYPE);
			if (dropPosition && dataTransferString != null) {
				let dataTransfer = JSON.parse(dataTransferString) as DtoWorkspaceLayoutDndDataTransfer;
				if ( /* TODO this.context.sessionId === dataTransfer.sourceUiSessionId && */ dataTransfer.sourceWorkspaceLayoutId === this.uuid) {
					if (dataTransfer.sourceWindowId === this.windowId) {
						if (dropPosition.tabPanel) {
							if (dropPosition.relativeDropPosition === RelativeDropPosition.TAB) {
								this.moveViewToTab(dataTransfer.viewName, dropPosition.tabPanel.tabs[0].viewName);
							} else {
								let uiRelativeWorkSpaceViewPosition = DtoRelativeWorkSpaceViewPosition[RelativeDropPosition[dropPosition.relativeDropPosition] as keyof typeof DtoRelativeWorkSpaceViewPosition];
								this.moveViewRelativeToOtherView(dataTransfer.viewName, dropPosition.tabPanel.tabs[0].viewName, uiRelativeWorkSpaceViewPosition, SplitSizePolicy.RELATIVE, .5);
							}
						} else {
							let uiRelativeWorkSpaceViewPosition = DtoRelativeWorkSpaceViewPosition[RelativeDropPosition[dropPosition.relativeDropPosition] as keyof typeof DtoRelativeWorkSpaceViewPosition];
							let isFirst = dropPosition.relativeDropPosition === RelativeDropPosition.LEFT || dropPosition.relativeDropPosition === RelativeDropPosition.TOP;
							this.moveViewToTopLevel(dataTransfer.viewName, this.windowId, uiRelativeWorkSpaceViewPosition, SplitSizePolicy.RELATIVE, isFirst ? .3 : .7);
						}
						this.listener.handleLocalLayoutChangedByUser(this.windowId);
					} else {
						if (this.uuid === dataTransfer.sourceWorkspaceLayoutId) {
							this.listener.handleViewDroppedFromOtherWindow(dataTransfer.sourceWindowId, this.windowId, {
								viewName: dataTransfer.viewName,
								tabIcon: dataTransfer.tabIcon,
								tabCaption: dataTransfer.tabCaption,
								tabCloseable: dataTransfer.tabCloseable,
								lazyLoading: dataTransfer.lazyLoading,
								visible: dataTransfer.visible
							}, dropPosition.tabPanel && dropPosition.tabPanel.tabs[0].viewName, dropPosition.relativeDropPosition);
						}
					}
				} else {
					console.warn("The user dropped a view from another DtoWorkSpaceLayout. Not accepting this.");
					return true;
				}
				return false;
			}
		}, false);
		this.$mainDiv.addEventListener('dragend', (e: DragEvent) => {
			this.$dndActiveRectangle.classList.add("hidden");
			const dropEffect = e.dataTransfer.dropEffect;
			const target = e.target as HTMLElement;
			const viewName = this.getViewNameForDragTarget(target);
			const dropSuccessful = dropEffect === 'move';
			let droppedOutsideWorkSpaceLayout = this.lastDndEventType === 'dragleave';
			if (droppedOutsideWorkSpaceLayout && !dropSuccessful) {
				// this.createSubWindow(viewName); TODO I had to comment this out since when canceling drag and drop using escape, there is not way to tell whether it was a drop outside the window or a canceled drop...
			} else if (droppedOutsideWorkSpaceLayout && dropSuccessful) {
				// The other window will request a view refresh. This component will intercept it and register the windowDelegate...
			} else {
				// Nothing to do. The move is made in the 'drop' handler.
			}
			this.lastDndEventType = 'dragend';
		}, false);
	}

	private cover($tabPanelContentWrapper: HTMLElement, $floating: HTMLElement, alignment: "top" | "left" | "bottom" | "right", coverSize: number) {
		if (coverSize < 0 || coverSize > 1) {
			throw "size needs to be between 0 and 1";
		}
		let topOrBottom = alignment === 'top' || alignment === 'bottom';
		let leftOrRight = alignment === 'left' || alignment === 'right';
		computePosition($tabPanelContentWrapper, $floating, {
			placement: topOrBottom ? 'top-start' : 'left-start',
			strategy: 'absolute',
			middleware: [
				size({
					apply: ({rects, availableHeight, placement}) => {
						Object.assign($floating.style, {
							width: `${topOrBottom ? rects.reference.width : rects.reference.width * coverSize}px`,
							height: `${topOrBottom ? rects.reference.height * coverSize : rects.reference.height}px`,
						});
					}
				}),
			],
		}).then((values) => {
			Object.assign($floating.style, {
				left: `${values.x + (topOrBottom ? 0 : alignment === 'left' ? $floating.offsetWidth : $tabPanelContentWrapper.offsetWidth)}px`,
				top: `${values.y + (leftOrRight ? 0 : alignment === 'top' ? $floating.offsetHeight : $tabPanelContentWrapper.offsetHeight)}px`
			});
		});
	}

	public setToolbar(toolbar: Toolbar): void {
		if (this._toolbar) {
			this.$toolbarContainer.innerHTML = '';
		}
		this._toolbar = toolbar;
		if (toolbar) {
			this.$toolbarContainer.appendChild(this._toolbar.getMainElement());
			this._toolbar.onEmptyStateChanged.addListener(() => this.updateToolbarVisibility());
		}
		this.updateToolbarVisibility();
	}

	private updateToolbarVisibility() {
		this.$toolbarContainer.classList.toggle('hidden', this._toolbar == null || this._toolbar.empty);
	}

	public getViewInfo(viewName: string) {
		return this.itemTree.getViewByName(viewName).viewInfo;
	}

	get viewNames() {
		return this.itemTree.viewNames;
	}

	private createSubWindow(viewName: string | null) {
		let childWindowId = generateUUID();
		let childWindow = window.open("index.html" + (location.search ? location.search + "&" : "?") + "teamAppsContext=DtoWorkSpaceLayoutChildWindowTeamAppsUiContext", childWindowId, "height=600,width=800,location=0");

		if (!childWindow || childWindow.closed || typeof childWindow.closed === 'undefined') {
			console.warn("Popup window was blocked.");
			this.listener.handleChildWindowCreationFailed(viewName);
		} else {
			let onWindowLoaded = () => {
				const channel = new MessageChannel();

				childWindow.postMessage("", location.origin, [channel.port2]);

				this.listener.handleChildWindowCreated(childWindowId, channel.port1, this.itemTree.getViewByName(viewName).viewInfo);
			};

			if (getMicrosoftBrowserVersion()) {
				// IE and Edge only return from the window.open() method _after_ the page has been loaded
				onWindowLoaded();
			} else {
				childWindow.addEventListener('load', () => {
					onWindowLoaded();
				});
			}
		}
	}

	private getViewNameForDragTarget(target: HTMLElement) {
		let viewName: string | null;
		let isDraggablePanelHeadingElement = Panel.isDraggablePanelHeadingElement(target);
		if (isDraggablePanelHeadingElement) {
			viewName = target.closest('.tab-content-wrapper').getAttribute('data-tab-name');
		}
		let isTabButton = target.classList.contains('tab-button');
		if (isTabButton) {
			viewName = target.getAttribute('data-tab-name');
		}
		return viewName;
	}

	private determineDropPosition(e: MouseEvent): { tabPanel?: TabPanelItem, relativeDropPosition: RelativeDropPosition } {
		let workSpaceLayoutRect = this.$contentContainer.getBoundingClientRect();

		if (e.pageY - workSpaceLayoutRect.top < 12) {
			return {relativeDropPosition: RelativeDropPosition.TOP};
		} else if ((workSpaceLayoutRect.left + workSpaceLayoutRect.width) - e.pageX < 12) {
			return {relativeDropPosition: RelativeDropPosition.RIGHT};
		} else if ((workSpaceLayoutRect.top + workSpaceLayoutRect.height) - e.pageY < 12) {
			return {relativeDropPosition: RelativeDropPosition.BOTTOM};
		} else if (e.pageX - workSpaceLayoutRect.left < 12) {
			return {relativeDropPosition: RelativeDropPosition.LEFT};
		}

		const matchingTabPanel = this.findParentTabPanel(e.target as HTMLElement);

		if (matchingTabPanel != null) {
			let view = matchingTabPanel.tabs[0];
			let tabPanelContentWrapper = matchingTabPanel.component.getMainElement().querySelector<HTMLElement>(':scope .tabpanel-content-wrapper');
			let tabPanelContentRect = tabPanelContentWrapper.getBoundingClientRect();
			const relativeEventX = (e.pageX - tabPanelContentRect.left) / tabPanelContentRect.width;
			const relativeEventY = (e.pageY - tabPanelContentRect.top) / tabPanelContentRect.height;

			if (relativeEventY < 0 || e.pageY - tabPanelContentRect.top < 30) {
				return {tabPanel: matchingTabPanel, relativeDropPosition: RelativeDropPosition.TAB};
			} else if (relativeEventX > 0.2 && relativeEventX < 0.8 && relativeEventY > 0.2 && relativeEventY < 0.8) {
				return {tabPanel: matchingTabPanel, relativeDropPosition: RelativeDropPosition.TAB};
			} else if (relativeEventX < 0.5 && relativeEventY > relativeEventX && (1 - relativeEventY) > relativeEventX) {
				return {tabPanel: matchingTabPanel, relativeDropPosition: RelativeDropPosition.LEFT};
			} else if (relativeEventX > 0.5 && relativeEventY < relativeEventX && (1 - relativeEventY) < relativeEventX) {
				return {tabPanel: matchingTabPanel, relativeDropPosition: RelativeDropPosition.RIGHT};
			} else if (relativeEventY < 0.5 && relativeEventY < relativeEventX && relativeEventY < (1 - relativeEventX)) {
				return {tabPanel: matchingTabPanel, relativeDropPosition: RelativeDropPosition.TOP}
			} else { //if (relativeEventY > 0.5 && relativeEventY > relativeEventX && relativeEventY > (1 - relativeEventX)) {
				return {tabPanel: matchingTabPanel, relativeDropPosition: RelativeDropPosition.BOTTOM}
			}
		} else {
			return null;
		}
	}

	private findParentTabPanel(target: HTMLElement): TabPanelItem {
		let allTabPanelItems = this.itemTree.getAllTabPanelItems();
		let matchingTabPanel: TabPanelItem;
		while (target != null && target != this.$mainDiv) {
			matchingTabPanel = allTabPanelItems.filter(item => item.component.getMainElement() === target)[0];
			if (matchingTabPanel) {
				break;
			} else {
				target = target.parentElement;
			}
		}
		return matchingTabPanel;
	}

	private setRootItem(item: ItemTreeItem<Component>): void {
		if (this.itemTree.rootItem != null && isEmptyable(this.itemTree.rootItem.component)) {
			this.itemTree.rootItem.component.onEmptyStateChanged.removeListener(this.onRootItemEmptyStateChanged);
		}
		this.itemTree.rootItem = item;
		this.$contentContainer.appendChild(item.component.getMainElement());
		if (isEmptyable(item.component)) {
			item.component.onEmptyStateChanged.addListener(this.onRootItemEmptyStateChanged);
			this.onRootItemEmptyStateChanged(item.component.empty);
		}
	}

	@bind
	private onRootItemEmptyStateChanged(empty: boolean) {
		this.$contentContainer.classList.toggle("hidden", empty)
	}

	@bind
	private createTabPanelItem(config: DtoWorkSpaceLayoutViewGroupItem, parent: SplitPaneItem) {
		let tabPanelItem = new TabPanelItem(config.id, config.persistent, parent);
		tabPanelItem.onTabSelected.addListener(eventObject => this.tabSelected(eventObject, tabPanelItem));
		tabPanelItem.onTabNeedsRefresh.addListener(this.tabNeedsRefresh);
		tabPanelItem.onPanelStateChangeTriggered.addListener(panelState => this.setViewGroupPanelState2(tabPanelItem, panelState, true));
		tabPanelItem.onTabClosed.addListener(tabId => {
			if (tabPanelItem.tabs.length === 0) {
				this.setViewGroupPanelState2(tabPanelItem, DtoViewGroupPanelState.NORMAL, false);
			}
			this.workSpaceLayout.onViewClosed.fire({
				viewName: tabId
			});
		});
		return tabPanelItem;
	}

	private tabSelected(eventObject: { tabPanelItemId: string, tabId: string }, tabPanelItem: TabPanelItem) {
		let otherViewNames = tabPanelItem.tabs.map(tab => tab.viewName).filter(otherViewName => otherViewName !== eventObject.tabId);
		if (!this.viewEventsSuppressed) {
			this.workSpaceLayout.onViewSelected.fire({
				viewGroupId: eventObject.tabPanelItemId,
				viewName: eventObject.tabId,
				siblingViewNames: otherViewNames
			});
		}
	}

	@bind
	private tabNeedsRefresh(eventObject: { tabId: string }) {
		if (!this.viewEventsSuppressed) {
			this.workSpaceLayout.onViewNeedsRefresh.fire({
				viewName: eventObject.tabId
			});
		}
	}

	private createView(newViewConfig: DtoWorkSpaceLayoutView) {
		return new View(newViewConfig.viewName, newViewConfig.tabIcon, newViewConfig.tabCaption, newViewConfig.tabCloseable, newViewConfig.lazyLoading, newViewConfig.visible, newViewConfig.component as Component);
	}

	addViewToTopLevel(newViewConfig: DtoWorkSpaceLayoutView, windowId: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number): void {
		// windowId can be ignored here, since this method is only invoked if this is the target window!
		let view = this.createView(newViewConfig);
		this.addViewItemToNewPosition(view, null, relativePosition, sizePolicy, referenceChildSize);
	}

	addViewRelativeToOtherView(newViewConfig: DtoWorkSpaceLayoutView, existingViewName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number): void {
		let view = this.createView(newViewConfig);
		this.addViewItemToNewPosition(view, existingViewName, relativePosition, sizePolicy, referenceChildSize);
	}

	addViewAsTab(newViewConfig: DtoWorkSpaceLayoutView, itemId: string, select: boolean): void {
		let view = this.createView(newViewConfig);
		this.itemTree.getTabPanelById(itemId).addTab(view, select);
		this.itemTree.updateIndex();
	}

	addViewAsNeighbourTab(newViewConfig: DtoWorkSpaceLayoutView, existingViewName: string, select: boolean): void {
		let view = this.createView(newViewConfig);
		this.itemTree.getViewByName(existingViewName).parent.addTab(view, select);
		this.itemTree.updateIndex();
	}

	private addViewItemToNewPosition(view: View, existingViewName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number) {
		if (existingViewName != null) {
			let siblingView: View = this.itemTree.getViewByName(existingViewName);
			let siblingTabPanelItem = siblingView.parent;
			let oldParent = siblingTabPanelItem.parent;
			let existingTabPanelIsPosition: 'ROOT' | 'FIRST' | 'LAST' = oldParent == null ? 'ROOT' : oldParent.firstChild === siblingTabPanelItem ? 'FIRST' : 'LAST';
			let newItemWillBeFirstChild = [DtoRelativeWorkSpaceViewPosition.LEFT, DtoRelativeWorkSpaceViewPosition.TOP].indexOf(relativePosition) !== -1;

			let isVerticalSplit = [DtoRelativeWorkSpaceViewPosition.LEFT, DtoRelativeWorkSpaceViewPosition.RIGHT].indexOf(relativePosition) !== -1;
			let newSplitPaneItem = new SplitPaneItem(generateUUID(), oldParent, isVerticalSplit ? SplitDirection.VERTICAL : SplitDirection.HORIZONTAL, sizePolicy, referenceChildSize);
			let newTabPanelItem = this.createTabPanelItem({id: generateUUID(), viewNames: []}, newSplitPaneItem);
			newTabPanelItem.addTab(view, true);

			if (existingTabPanelIsPosition === 'FIRST') {
				oldParent.component.firstChildComponent.getMainElement().remove();
				oldParent.firstChild = newSplitPaneItem;
			} else if (existingTabPanelIsPosition === 'LAST') {
				oldParent.component.lastChildComponent.getMainElement().remove();
				oldParent.lastChild = newSplitPaneItem;
			} else { // siblingTabPanelItem is root!
				siblingTabPanelItem.component.getMainElement().remove();
				this.setRootItem(newSplitPaneItem);
			}

			newSplitPaneItem.firstChild = newItemWillBeFirstChild ? newTabPanelItem : siblingTabPanelItem;
			newSplitPaneItem.lastChild = newItemWillBeFirstChild ? siblingTabPanelItem : newTabPanelItem;

			siblingTabPanelItem.parent = newSplitPaneItem
		} else {
			if (this.itemTree.viewCount === 0) {
				let rootTabPanelItem = this.itemTree.rootItem as TabPanelItem;
				rootTabPanelItem.addTab(view, true);
			} else {
				let isVerticalSplit = [DtoRelativeWorkSpaceViewPosition.LEFT, DtoRelativeWorkSpaceViewPosition.RIGHT].indexOf(relativePosition) !== -1;
				let splitPaneItem = new SplitPaneItem(generateUUID(), null, isVerticalSplit ? SplitDirection.VERTICAL : SplitDirection.HORIZONTAL, sizePolicy, referenceChildSize);
				let oldRootItem = this.itemTree.rootItem;
				oldRootItem.component.getMainElement().remove();
				this.setRootItem(splitPaneItem);

				let newTabPanelItem = this.createTabPanelItem({id: generateUUID(), viewNames: []}, splitPaneItem);
				newTabPanelItem.addTab(view, true);
				oldRootItem.parent = splitPaneItem;

				let newItemWillBeFirstChild = [DtoRelativeWorkSpaceViewPosition.LEFT, DtoRelativeWorkSpaceViewPosition.TOP].indexOf(relativePosition) !== -1;
				splitPaneItem.firstChild = newItemWillBeFirstChild ? newTabPanelItem : oldRootItem;
				splitPaneItem.lastChild = newItemWillBeFirstChild ? oldRootItem : newTabPanelItem;
			}
		}
		this.itemTree.updateIndex();
	}

	public removeView(viewName: string, updateIndex = true) {
		let view = this.itemTree.getViewByName(viewName);
		if (view == null) {
			console.warn(`Cannot remove view ${viewName}. View not found.`);
			return;
		}
		let tabPanelItem: TabPanelItem = view.parent;
		tabPanelItem.removeTab(view);
		if (tabPanelItem.tabs.length === 0 && !tabPanelItem.persistent) {
			this.removeEmptyTabPanelFromItemTree(tabPanelItem);
		}
		if (updateIndex) {
			this.itemTree.updateIndex();
		}
		if (this.windowId !== WorkSpaceLayout.ROOT_WINDOW_ID && this.itemTree.viewCount === 0) {
			window.close();
		}
	}

	private removeEmptyTabPanelFromItemTree(tabPanelItem: TabPanelItem) {
		let parentSplitPaneItem = tabPanelItem.parent;
		if (parentSplitPaneItem != null) { // else tabPanelItem is the rootItem, so do NOT remove it!
			// remove this tabPanel. The parent splitPane is now also needless...
			let tabPanelItemIsFirstChild = parentSplitPaneItem.firstChild === tabPanelItem;
			let siblingItem: ItemTreeItem<Component>;
			if (tabPanelItemIsFirstChild) {
				siblingItem = parentSplitPaneItem.lastChild;
				siblingItem.component.getMainElement().remove();
				parentSplitPaneItem.lastChild = null;
			} else {
				siblingItem = parentSplitPaneItem.firstChild;
				siblingItem.component.getMainElement().remove();
				parentSplitPaneItem.firstChild = null;
			}
			let grandParentSplitPaneItem = parentSplitPaneItem.parent;
			if (grandParentSplitPaneItem != null) {
				let parentSplitPaneItemIsFirstChild = grandParentSplitPaneItem.firstChild === parentSplitPaneItem;
				if (parentSplitPaneItemIsFirstChild) {
					grandParentSplitPaneItem.firstChild = siblingItem;
				} else {
					grandParentSplitPaneItem.lastChild = siblingItem;
				}
			} else {
				parentSplitPaneItem.component.destroy();
				parentSplitPaneItem.component.getMainElement().remove();
				this.setRootItem(siblingItem);
				siblingItem.parent = null;
			}
		}
	}

	moveViewToTopLevel(viewName: string, windowId: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number): void {
		this.moveViewRelativeToOtherView(viewName, null, relativePosition, sizePolicy, referenceChildSize);
	}

	moveViewRelativeToOtherView(viewName: string, newSiblingName: string, relativePosition: DtoRelativeWorkSpaceViewPosition, sizePolicy: SplitSizePolicy, referenceChildSize: number): void {
		let view = this.itemTree.getViewByName(viewName);
		if (viewName === newSiblingName && view.parent.tabs.length <= 1) {
			return; // would not have any effect anyway
		}
		view.component.getMainElement().remove();
		this.removeView(viewName, false);
		this.addViewItemToNewPosition(view, newSiblingName, relativePosition, sizePolicy, referenceChildSize);
	}

	moveViewToTab(viewName: string, newSiblingName: string): void {
		let view = this.itemTree.getViewByName(viewName);
		if (viewName === newSiblingName) {
			return; // would not have any effect anyway
		}
		view.component.getMainElement().remove();
		this.removeView(viewName, false);
		this.itemTree.getViewByName(newSiblingName).parent.addTab(view, true);
		this.itemTree.updateIndex();
	}

	public refreshViewComponent(viewName: string, component: Component) {
		let view = this.itemTree.getViewByName(viewName);
		view.component = component;
	}

	refreshViewAttributes(viewName: string, tabIcon: string, tabCaption: string, tabCloseable: boolean, visible: boolean): void {
		let view = this.itemTree.getViewByName(viewName);
		view.updateTabAttributes(tabIcon, tabCaption, tabCloseable, visible);
	}

	setViewVisible(viewName: string, visible: boolean): void {
		let view = this.itemTree.getViewByName(viewName);
		view.setVisible(visible);
	}

	redefineLayout(newLayout: DtoWorkSpaceLayoutItem, addedViewConfigs: DtoWorkSpaceLayoutView[]): void {
		let newRootItem = new LayoutDescriptorApplyer(
			this.$contentContainer,
			this.createTabPanelItem,
			(viewGroupItem, panelState) => this.setViewGroupPanelState(viewGroupItem.id, panelState)
		).apply(this.itemTree.rootItem, newLayout, addedViewConfigs);
		this.setRootItem(newRootItem)
	}

	selectViewTab(viewName: string) {
		let tabPanel = this.itemTree.getTabPanelForView(viewName);
		if (tabPanel) {
			tabPanel.selectTab(viewName);
		}
	}

	@bind
	setViewGroupPanelState(viewGroupId: string, panelState: DtoViewGroupPanelState): void {
		let tabPanel = this.itemTree.getTabPanelById(viewGroupId);
		if (tabPanel) {
			this.setViewGroupPanelState2(tabPanel, panelState, false);
		}
	}

	private setViewGroupPanelState2(viewGroup: TabPanelItem, panelState: DtoViewGroupPanelState, firePanelStateChangeEvent: boolean) {
		const oldPanelState = viewGroup.state;
		if (oldPanelState != panelState) {
			if (panelState === DtoViewGroupPanelState.MAXIMIZED) {
				this.maximizeTabPanel(viewGroup);
			} else if (panelState === DtoViewGroupPanelState.MINIMIZED) {
				this.minimizeTabPanel(viewGroup);
			} else {
				this.restoreTabPanel(viewGroup);
			}
			if (firePanelStateChangeEvent) {
				this.workSpaceLayout.fireViewGroupPanelStateChanged(viewGroup.id, panelState);
			}
		}
	}

	private minimizeTabPanel(tabPanelItem: TabPanelItem) {
		if (tabPanelItem.maximized) {
			this.restoreTabPanel(tabPanelItem);
		}
		tabPanelItem.state = DtoViewGroupPanelState.MINIMIZED;
		this.$minimizedViewsBar.append(tabPanelItem.$minimizedTrayButton);
	}

	private maximizeTabPanel(tabPanelItem: TabPanelItem) {
		const $element = tabPanelItem.component.getMainElement();
		this.$normalContainerOfMaximizedTabPanel = $element.parentElement;
		this.$maximizationContainerWrapper.classList.add("show");
		this.$maximizationContainer.append($element);
		this.$maximizationContainer.classList.add("animate__animated", "animate__zoomIn");
		tabPanelItem.state = DtoViewGroupPanelState.MAXIMIZED;
	}

	private restoreTabPanel(tabPanelItem: TabPanelItem) {
		if (tabPanelItem.state === DtoViewGroupPanelState.MAXIMIZED) {
			const $element = tabPanelItem.component.getMainElement();
			this.$maximizationContainerWrapper.classList.remove("show");
			this.$maximizationContainer.append($element);
			this.$maximizationContainer.classList.remove("animate__animated", "animate__zoomIn");
			this.$normalContainerOfMaximizedTabPanel.appendChild($element);
			tabPanelItem.state = DtoViewGroupPanelState.NORMAL;
		} else if (tabPanelItem.state === DtoViewGroupPanelState.MINIMIZED) {
			tabPanelItem.$minimizedTrayButton.remove();
			tabPanelItem.state = DtoViewGroupPanelState.NORMAL;
		}
	}

	destroy() {
		if (this.itemTree.rootItem.component) this.itemTree.rootItem.component.destroy();
		this.$maximizationContainerWrapper.remove();
	}

	getMainDomElement() {
		return this.$mainDiv;
	}

	private createLayoutDescriptor(item: ItemTreeItem<Component>): DtoWorkSpaceLayoutItem {
		if (item instanceof SplitPaneItem) {
			return createDtoWorkSpaceLayoutSplitItem(item.id, item.splitDirection, this.createLayoutDescriptor(item.firstChild), this.createLayoutDescriptor(item.lastChild), {
				sizePolicy: item.sizePolicy,
				referenceChildSize: item.referenceChildSize
			})
		} else if (item instanceof TabPanelItem) {
			return createDtoWorkSpaceLayoutViewGroupItem(item.id, item.tabs.map(view => view.viewName), {
				selectedViewName: item.component.getSelectedTabId(),
				panelState: item.state,
				persistent: item.persistent
			});
		}
	}

	async getLayoutDescriptor(): Promise<WindowLayoutDescriptor> {
		return {
			windowId: this.windowId,
			layout: this.createLayoutDescriptor(this.itemTree.rootItem)
		};
	}

	private fireViewNeedsRefreshForAllEmptyVisibleLazyTabs() {
		this.itemTree.getAllTabPanelItems()
			.forEach(tabPanelItem => {
				let selectedTabId: string = tabPanelItem.component.getSelectedTabId();
				if (selectedTabId != null) { // might be completely empty!
					let selectedView: View = tabPanelItem.tabs.filter(tab => tab.viewName === selectedTabId)[0];
					if (selectedView.component == null && selectedView.lazyLoading) {
						this.workSpaceLayout.onViewNeedsRefresh.fire({
							viewName: selectedTabId
						});
					}
				}
			});
	}

	setMultiProgressDisplay(multiProgressDisplay: MultiProgressDisplay) {
		this.multiProgressDisplay && this.multiProgressDisplay.getMainElement().remove();
		this.multiProgressDisplay = multiProgressDisplay;
		multiProgressDisplay && this.$progressContainer.appendChild(multiProgressDisplay.getMainElement());
	}
}
