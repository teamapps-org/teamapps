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
import {ItemTreeItem} from "./ItemTree";
import {View} from "./View";
import {SplitPaneItem} from "./SplitPaneItem";
import {TabPanelItem} from "./TabPanelItem";
import {Component} from "projector-client-object-api";
import {isSplitPanelDescriptor, isTabPanelDescriptor} from "./WorkSpaceLayout";
import {
	ViewGroupPanelState,
	DtoWorkSpaceLayoutItem,
	DtoWorkSpaceLayoutSplitItem,
	DtoWorkSpaceLayoutView,
	DtoWorkSpaceLayoutViewGroupItem
} from "./generated";

export class LayoutDescriptorApplyer {

	private descriptorItemById: { [itemId: string]: DtoWorkSpaceLayoutItem } = {};
	private descriptorViewNames: string[] = [];

	private clientItemsById: { [itemId: string]: ItemTreeItem } = {};
	private clientViewsByName: { [viewName: string]: View } = {};

	private clientItemStash: { [itemId: string]: ItemTreeItem } = {};

	constructor(
		private $rootItemContainer: HTMLElement,
		private viewGroupFactory: (config: DtoWorkSpaceLayoutViewGroupItem, parent: SplitPaneItem) => TabPanelItem,
		private setViewGroupPanelStateFunction: (viewGroupItem: TabPanelItem, panelState: ViewGroupPanelState) => void
	) {
	}

	public apply(
		currentRootItem: ItemTreeItem,
		newLayoutDescriptor: DtoWorkSpaceLayoutItem,
		newViewConfigs: DtoWorkSpaceLayoutView[]
	): ItemTreeItem {
		this.descriptorItemById = {};
		this.descriptorViewNames = [];
		this.clientItemsById = {};
		this.clientViewsByName = {};
		this.clientItemStash = {};

		this.buildDescriptorDictionaries(newLayoutDescriptor);
		if (currentRootItem != null) {
			this.buildClientItemDictionaries(currentRootItem);
			this.cleanupUnknownClientItems(currentRootItem, newLayoutDescriptor, null);
		}
		return this.addNewStructure(newLayoutDescriptor, null, false, newViewConfigs);
	}

	private buildDescriptorDictionaries(descriptorItem: DtoWorkSpaceLayoutItem) {
		this.descriptorItemById[descriptorItem.id] = descriptorItem;
		if (isTabPanelDescriptor(descriptorItem)) {
			this.descriptorViewNames.push(...descriptorItem.viewNames);
		} else if (isSplitPanelDescriptor(descriptorItem)) {
			if (descriptorItem.firstChild != null) {
				this.buildDescriptorDictionaries(descriptorItem.firstChild);
			}
			if (descriptorItem.lastChild != null) {
				this.buildDescriptorDictionaries(descriptorItem.lastChild);
			}
		}
	}

	private buildClientItemDictionaries(item: ItemTreeItem) {
		this.clientItemsById[item.id] = item;
		if (item instanceof TabPanelItem) {
			item.tabs.forEach(tab => this.clientViewsByName[tab.viewName] = tab);
		} else if (item instanceof SplitPaneItem) {
			this.buildClientItemDictionaries(item.firstChild);
			this.buildClientItemDictionaries(item.lastChild);
		}
	}

	public cleanupUnknownClientItems(clientSideItem: ItemTreeItem, descriptorItem: DtoWorkSpaceLayoutItem, parent: SplitPaneItem | null) {
		// descriptorItem may be null for recursive executions of this method!
		if (descriptorItem != null && descriptorItem.id === clientSideItem.id) {
			if (clientSideItem instanceof SplitPaneItem) {
				this.cleanupUnknownClientItems(clientSideItem.firstChild, (descriptorItem as DtoWorkSpaceLayoutSplitItem).firstChild, clientSideItem);
				this.cleanupUnknownClientItems(clientSideItem.lastChild, (descriptorItem as DtoWorkSpaceLayoutSplitItem).lastChild, clientSideItem);
			} else if (clientSideItem instanceof TabPanelItem) {
				this.stashUnknownViews(clientSideItem, (descriptorItem as DtoWorkSpaceLayoutViewGroupItem).viewNames);
			}
		} else {
			let correspondingDescriptorItem = this.descriptorItemById[clientSideItem.id];
			if (correspondingDescriptorItem != null) {
				this.clientItemStash[clientSideItem.id] = clientSideItem;
				clientSideItem.component.getMainElement().remove();
				this.cleanupUnknownClientItems(clientSideItem, correspondingDescriptorItem, null);
			} else {
				// not referenced in the descriptor! however, descendants might well be referenced in the descriptor!
				if (clientSideItem instanceof TabPanelItem) {
					this.stashUnknownViews(clientSideItem, []);
				} else if (clientSideItem instanceof SplitPaneItem) {
					this.cleanupUnknownClientItems(clientSideItem.firstChild, null, clientSideItem);
					this.cleanupUnknownClientItems(clientSideItem.lastChild, null, clientSideItem);
				}
			}

			// remove the clientSideItem!
			if (parent != null) {
				if (clientSideItem === parent.firstChild) {
					parent.firstChild = null;
				} else {
					parent.lastChild = null;
				}
			} else {
				// this is the root item!
				clientSideItem.component.getMainElement().remove();
			}
		}
	}
	
	private stashUnknownViews(viewGroup: TabPanelItem, descriptorViewNames: string[]) {
		let viewsNotFoundInDescriptorItem = viewGroup.tabs
			.filter(tab => descriptorViewNames.indexOf(tab.viewName) === -1);
		viewsNotFoundInDescriptorItem.forEach((tab: View) => {
			let viewIsReferencedAnywhereInRootDescriptor = this.descriptorViewNames.indexOf(tab.viewName) !== -1;
			if (viewIsReferencedAnywhereInRootDescriptor) {
				tab.component.getMainElement().remove();
				viewGroup.removeTab(tab);
			} else {
				// will not be used anymore in any way! just remove and destroy
				viewGroup.removeTab(tab);
			}
		});
	}

	private addNewStructure(descriptor: DtoWorkSpaceLayoutItem, parent: SplitPaneItem | null, firstChild: boolean, newViewConfigs: DtoWorkSpaceLayoutView[]) {
		if (descriptor == null) {
			return null;
		}
		let clientSideItem: ItemTreeItem<any> = this.clientItemsById[descriptor.id];
		let itemMovedToStash = this.clientItemStash[descriptor.id] != null;
		let item: ItemTreeItem<any>;
		if (clientSideItem != null) {
			item = clientSideItem;
			if (isSplitPanelDescriptor(descriptor)) {
				this.addNewStructure(descriptor.firstChild, clientSideItem as SplitPaneItem, true, newViewConfigs);
				this.addNewStructure(descriptor.lastChild, clientSideItem as SplitPaneItem, false, newViewConfigs);
			} else if (isTabPanelDescriptor(descriptor)) {
				this.addViews(clientSideItem as TabPanelItem, descriptor, newViewConfigs);
			}
		} else { // this is a descriptor for a new item
			item = this.createTreeItemFromLayoutDescriptor(descriptor, parent, newViewConfigs);
		}

		if (itemMovedToStash) {
			if (parent != null) {
				if (firstChild) {
					parent.firstChild = item;
				} else {
					parent.lastChild = item;
				}
				item.parent = parent;
			} else {
				this.$rootItemContainer.append(item.component.getMainElement());
			}
		}

		if (item instanceof SplitPaneItem && isSplitPanelDescriptor(descriptor)) {
			item.component.setSizePolicy(descriptor.sizePolicy);
			item.component.setReferenceChildSize(descriptor.referenceChildSize);
		} else if (item instanceof TabPanelItem && isTabPanelDescriptor(descriptor)) {
			this.setViewGroupPanelStateFunction(item, descriptor.panelState);
		}
		return item;
	}

	private createTreeItemFromLayoutDescriptor(descriptor: DtoWorkSpaceLayoutItem, parent: SplitPaneItem, newViewConfigs: DtoWorkSpaceLayoutView[]) {
		if (isTabPanelDescriptor(descriptor)) {
			let tabPanelItem = this.viewGroupFactory(descriptor, parent);
			this.addViews(tabPanelItem, descriptor, newViewConfigs);
			return tabPanelItem;
		} else if (isSplitPanelDescriptor(descriptor)) {
			let splitPaneItem = new SplitPaneItem(descriptor.id, parent, descriptor.splitDirection, descriptor.sizePolicy, descriptor.referenceChildSize);
			splitPaneItem.firstChild = this.addNewStructure(descriptor.firstChild, splitPaneItem, true, newViewConfigs);
			splitPaneItem.lastChild = this.addNewStructure(descriptor.lastChild, splitPaneItem, false, newViewConfigs);
			return splitPaneItem;
		}
	}

	private addViews(tabPanelItem: TabPanelItem, viewGroupDescriptor: DtoWorkSpaceLayoutViewGroupItem, newViewConfigs: DtoWorkSpaceLayoutView[]) {
		viewGroupDescriptor.viewNames.forEach((viewName, index) => {
			let selected = viewName === viewGroupDescriptor.selectedViewName || viewGroupDescriptor.selectedViewName == null && index === 0;
			let tabAlreadyInGroupItem = tabPanelItem.tabs.filter(tab => tab.viewName === viewName)[0];
			if (tabAlreadyInGroupItem) {
				tabPanelItem.moveTab(viewName, index);
				if (selected) {
					tabPanelItem.selectTab(viewName);
				}
			} else if (this.clientViewsByName[viewName]) {
				let view = this.clientViewsByName[viewName];
				tabPanelItem.addTab(view, selected, index);
			} else {
				let newViewConfig = newViewConfigs.filter(view => view.viewName === viewName)[0];
				if (newViewConfig != null) {
					let view = new View(newViewConfig.viewName, newViewConfig.tabIcon, newViewConfig.tabCaption, newViewConfig.tabCloseable, newViewConfig.lazyLoading, newViewConfig.visible, newViewConfig.component as Component);
					tabPanelItem.addTab(view, selected);
				} else {
					console.error("View item references non-existing view: " + viewName);
					return;
				}
			}
		});
	}
}
